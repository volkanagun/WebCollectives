package tagging.lemmatizer

import java.io.{FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream}
import scala.collection.parallel.CollectionConverters.ArrayIsParallelizable
import scala.io.Source
import scala.util.Random

class WordRanker(val lemmatizer: WordLemmatizer) extends Tagger {


  var skip = 7
  var iterations = 10
  val start = "start"
  var srcLinks = Map[Int, Array[Link]](start.hashCode -> Array())


  def setLinks(linkMap: Map[Int, Array[Link]]): this.type = {

    srcLinks = linkMap
    this
  }

  def add(srcID: Int, dstID: Int, catID: Int): Unit = {
    synchronized {
      val link = Link(srcID, dstID, catID)
      var array = srcLinks.getOrElse(srcID, Array[Link]())
      val index = array.indexOf(link)
      if (index == -1) {
        array = array :+ link
        srcLinks = srcLinks.updated(srcID, array)
      }
      else {
        array(index).increment()
      }
    }
  }

  def add(input: Array[String], distance: Int): Unit = {
    var src = start.hashCode

    input.filter(word => word != null)
      .sliding(distance, 1).foreach { window => {
      for(i<-0 until window.length - 1) {

        for (k <- 0 until window.length) {
          val dst = window(k).hashCode
          val key = "dist@" + k
          add(src, dst, key.hashCode)
        }

        src = window(i).hashCode
      }

    }
    }
  }

  def suffix(input: Array[Array[WordSpan]]): Array[Array[String]] = {
    input.map(array => array.flatMap(_.split("|").split("\\|")))
  }

  def label(input: Array[Array[WordSpan]]): Array[Array[String]] = {
    input.map(array => array.map(_.label))
  }

  def normalize(): this.type = {
    srcLinks.foreach { case (id, array) => {
      array.groupBy(_.catID).foreach { case (_, links) => {
        val total = links.map(_.count).sum
        links.foreach(link => link.normalize(total))
      }
      }
    }
    }

    this
  }

  def combinations(input: Array[WordGroup], result: Array[Array[WordSpan]] = Array[Array[WordSpan]](Array()), i: Int = 0): Array[Array[WordSpan]] = {

    if (i == input.length) result
    else {

      var crr = i;
      var array = Array[Array[WordSpan]]()
      for (k <- 0 until input(crr).size) {
        for (j <- 0 until result.length) {
          val current = result(j) :+ input(crr).getItem(k)
          array = array :+ current
        }
      }

      combinations(input, array, crr + 1)
    }
  }

  def count(input: Array[WordGroup]): this.type = {

    val combinateInput = combinations(input)
    val suffixes = suffix(combinateInput)
    val labels = label(combinateInput)

    suffixes.foreach(sequence => {
      add(sequence, skip)
    })

    labels.foreach(sequence => {
      add(sequence, skip)
    })

    this
  }

  def loglikelihood(srcID: Int, destination: String): (Int, Double) = {
    val dstID = destination.hashCode
    if (srcLinks.contains(srcID)) {
      val link = srcLinks(srcID).find(_.dstID == dstID)
      if (link.isDefined) {
        (dstID, link.get.logLikelihood())
      }
      else {
        (srcID, 0d)
      }
    }
    else {
      (srcID, 0d)
    }
  }

  def loglikelihood(srcID: Int, dstID: Int, key: String): (Int, Double) = {

    val catID = key.hashCode
    if (srcLinks.contains(srcID)) {
      val link = srcLinks(srcID).find(link => link.dstID == dstID && link.catID == catID)
      if (link.isDefined) {
        (dstID, link.get.logLikelihood())
      }
      else {
        (srcID, 0d)
      }
    }
    else {
      (srcID, 0d)
    }
  }

  def rankingSearch(input: Array[String], distance: Int, iterations: Int): Double = {
    var current = start.hashCode
    var i = 0;

    var scores = Range(0, input.length)
    var scoreMap = Map[Int, Double](0 -> 1d)
    var count = 0;
    while (count < iterations) {

      scores.foreach(i => {
        val srcID = current
        var (j, sum) = loglikelihood(current, input(i))
        var jsum = 0.85f * scoreMap.getOrElse(srcID, 0d) + 0.15f * sum;
        val min = Math.max(0, i - distance)
        for (k <- min until i) {
          val d = i - k
          val key =  "dist@" + d
          val (_, sumSkip) = loglikelihood(srcID, j, key)
          jsum += 0.85f * scoreMap.getOrElse(srcID, 0d) + 0.15f * sumSkip;
        }

        scoreMap = scoreMap.updated(j, jsum / iterations)
        current = j
      })

      count += 1
    }

    scoreMap.map(_._2).sum
  }

  def merge(wordRanker: WordRanker): this.type = {
    wordRanker.srcLinks.foreach { case (id, dstArray) => {
      var srcArray = srcLinks.getOrElse(id, Array[Link]())
      dstArray.foreach(dstLink => {
        val index = srcArray.indexOf(dstLink)
        if (index == -1) {
          srcArray = srcArray :+ dstLink
        }
        else {
          srcArray(index)
            .increment(dstLink.count)
        }
      })

      srcLinks = srcLinks.updated(id, srcArray)
    }
    }

    this
  }

  override def process(sentence: String): Array[WordSpan] = {
    val wordSpans = lemmatizer.extract(sentence)
    val wordCombinations = combinations(wordSpans)
    val suffxInput = suffix(wordCombinations)
    val labelInput = label(wordCombinations)

    val sorted = suffxInput.zip(labelInput).zipWithIndex.map { case ((input, labels), index) => {
      val suffixScore = rankingSearch(input, skip, iterations)
      val labelScore = rankingSearch(labels, skip, iterations)
      (0.85 * suffixScore + 0.15 * labelScore, index)
    }
    }.sortBy(_._1)

    val (_, i) = sorted.last
    wordCombinations(i)
  }

  override def init(sentences: Iterator[String]): WordRanker.this.type = {
    sentences.foreach(sentence => {
      println("Processing: " + sentence)
      val wordGroups = lemmatizer.extract(sentence)
      count(wordGroups)
    })

    this
  }


}

case class Link(srcID: Int, dstID: Int, val catID: Int, var count: Long = 0L) {

  var prob: Double = 0

  def increment(): this.type = {
    count = count + 1
    this
  }

  def increment(total: Long): this.type = {
    count = count + total
    this
  }

  def setProb(value: Double): this.type = {
    this.prob = value
    this
  }

  override def hashCode(): Int = {
    val array = Array(srcID, dstID, catID)
    array.foldRight[Int](3) { case (id, main) => 7 * main + id }
  }

  override def equals(obj: Any): Boolean = {
    val other = obj.asInstanceOf[Link]
    other.srcID == srcID && other.dstID == dstID && other.catID == catID
  }

  override def clone(): Link = {
    Link(srcID, dstID, catID, count)
  }

  def normalize(total: Long): Link = {
    prob = count.toDouble / total
    this
  }

  def logLikelihood(): Double = {
    Math.log(1.0 + prob)
  }
}

object WordRanker {

  val sentenceFilename = "resources/text/sentences-tr.txt"
  val binaryFilename = "resources/binary/ranker.bin"
  val lemmaTagger = WordLemmatizer()
  val njobs = 1048
  val nthreads = 24
  val maxSentences = 10000000
  val maxSentenceLength = 170

  def apply(): WordRanker = {
    load(binaryFilename)
  }

  def test(): Unit = {
    val wordRanker = WordRanker()
    Array(
      "yaşamak için elinden geleni yapıyordu.",
      "yüzerek karşı kıyaya geçti."
    ).map(sentence => wordRanker.process(sentence))
      .foreach(spans => {
        val str = spans.map(span => {
          span.lemma + ":" + span.label
        }).mkString(" ")
        println(str)
      })
  }

  def build(): Unit = {
    val sentences = Source.fromFile(sentenceFilename).getLines()
    val mainRanker = WordRanker()
    val random = new Random()
    sentences.filter(sentence => sentence.length < maxSentenceLength)
      .filter(_ => random.nextBoolean())
      .take(maxSentences).sliding(njobs, njobs).zipWithIndex.foreach(pair => {
      val seq = pair._1
      println("ZipIndex: " + pair._2)
      seq.sliding(nthreads, nthreads).toArray.par.map(seq => {
        new WordRanker(lemmaTagger)
          .init(seq.iterator)
      }).toArray
        .foreach(wordRanker => mainRanker.merge(wordRanker))
    })

    mainRanker.normalize()
    save(binaryFilename, mainRanker)
  }

  def load(filename: String): WordRanker = {
    val objectInputStream = new ObjectInputStream(new FileInputStream(filename))
    val size = objectInputStream.readInt()
    var map = Map[Int, Array[Link]]()
    for (i <- 0 until size) {
      val id = objectInputStream.readInt()
      val length = objectInputStream.readInt()
      var array = Array[Link]()
      for (j <- 0 until length) {
        val srcID = objectInputStream.readInt()
        val dstID = objectInputStream.readInt()
        val catID = objectInputStream.readInt()
        val count = objectInputStream.readLong()
        val prob = objectInputStream.readDouble()
        val crrLink = Link(srcID, dstID, catID, count)
          .setProb(prob)
        array = array :+ crrLink
      }
      map = map.updated(id, array)
    }
    objectInputStream.close()
    new WordRanker(lemmaTagger)
      .setLinks(map)
  }

  def save(filename: String, wordRanker: WordRanker): Unit = {
    val objectOutputStream = new ObjectOutputStream(new FileOutputStream(filename))
    val map = wordRanker.srcLinks
    objectOutputStream.writeInt(map.size)
    map.foreach { case (id, array) => {
      objectOutputStream.writeInt(id)
      objectOutputStream.writeInt(array.length)

      array.foreach(link => {
        objectOutputStream.writeInt(link.srcID)
        objectOutputStream.writeInt(link.dstID)
        objectOutputStream.writeInt(link.catID)
        objectOutputStream.writeLong(link.count)
        objectOutputStream.writeDouble(link.prob)
      })
    }
    }

    objectOutputStream.close()
  }


  def main(args: Array[String]): Unit = {
    build()
    test()
  }
}
