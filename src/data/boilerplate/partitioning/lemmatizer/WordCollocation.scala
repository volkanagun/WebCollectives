package tagging.lemmatizer

import java.io._
import java.util.Locale
import scala.collection.parallel.CollectionConverters.ArrayIsParallelizable
import scala.io.Source
import scala.util.control.Breaks

class WordCollocation(val lemmatizer: WordLemmatizer, val window: Int) extends Tagger {
  val tr = new Locale("tr")
  var counts = Map[Int, Long]()
  var sum = 0L

  override def process(sentence: String): Array[WordSpan] = {
    val spans = lemmatizer.process(sentence)
    collocate(spans)
  }

  def extract(sentence: String): Array[String] = {
    val spans = lemmatizer.extract(sentence)
    extract(spans).map(_.value)
  }

  override def init(sentences: Iterator[String]): this.type = {
    sentences.foreach(sentence => {
      val spans = lemmatizer.process(sentence)
      update(spans)
    })
    this
  }

  def merge(wordCollocation: WordCollocation): this.type = {
    wordCollocation.counts.foreach { case (id, cnt) => add(id, cnt) }
    this
  }

  def add(item: String, count: Long): this.type = {
    counts = counts.updated(item.hashCode, count)
    sum = sum + 1
    this
  }

  def add(id: Int, count: Long): this.type = {
    counts = counts.updated(id, count)
    sum = sum + 1
    this
  }

  def setSum(sum: Long): this.type = {
    this.sum = sum
    this
  }

  def collocate(spans: Array[WordSpan]): Array[WordSpan] = {
    var array = spans
    Range(1, window).foreach(_ => {
      var result = Array[WordSpan]()
      array = array :+ WordSpan.dummy(spans.last.end + 1)
      array.sliding(2, 1).foreach { case (Array(w1, w2)) => {
        if (test(w1, w2) > 0.5) {
          val lemma = w1.lemma + " " + w2.lemma
          val value = w1.value + " " + w2.value
          val label = w2.label
          result = result :+ new WordSpan(w1.start, w2.end)
            .setLabel(label)
            .setLemma(lemma)
            .setValue(value)
        }
        else {
          result = result :+ w1
        }
      }
      }
      array = result
    })

    array
  }

  def extract(spans: Array[WordGroup]): Array[WordGroup] = {
    var array = spans
    Breaks.breakable {

      for (k <- 0 until window) {
        var result = Array[WordGroup]()
        var i = 0
        while (i < array.length - 1) {
          val w1 = array(i)
          val w2 = array(i + 1)
          val score = test(w1, w2)
          if (score >= 0.7) {
            val value = w1.value + " " + w2.value
            result = result :+ new WordGroup(w1.start, w2.end).setValue(value)
              .add(w1)
              .add(w2)
            i += 1
          }

          i += 1
        }

        if (result.nonEmpty) array = result
        else Breaks.break()
      }
    }

    array
  }

  def update(spans: Array[WordSpan]): Array[WordSpan] = {
    var array = spans
    Range(1, window).foreach(_ => {
      var result = Array[WordSpan]()
      array = array :+ WordSpan.dummy(spans.last.end + 1)
      array.sliding(2, 1).foreach { case (Array(w1, w2)) => {
        val value = w1.value + " " + w2.value
        val span = new WordSpan(w1.start, w2.end)
          .setValue(value)
          .toWordGroup()

        counts = counts.updated(w1.value.hashCode, counts.getOrElse(w1.value.hashCode, 0L) + 1L)
        counts = counts.updated(w2.value.hashCode, counts.getOrElse(w2.value.hashCode, 0L) + 1L)
        counts = counts.updated(value.hashCode, counts.getOrElse(value.hashCode, 0L) + 1L)
        result = result :+ span

      }}

      array = result
    })

    sum = sum + 1
    array
  }

  def test(word1: WordSpan, word2: WordSpan): Double = {
    val crrValue = word1.value + " " + word2.value
    val w1freq = counts.getOrElse(word1.value.hashCode, 1L)
    val w2freq = counts.getOrElse(word2.value.hashCode, 1L)
    val joint = counts.getOrElse(crrValue.hashCode, 1L).toDouble
    2 * joint / (w1freq * w2freq)
  }
}

object WordCollocation {

  val window = 4
  val nthreads = 1024
  val njobs = nthreads * 96
  val sentenceFilename = "resources/text/sentences-tr.txt"
  val collocationFilename = "resources/dictionary/collocations.txt"

  val lemmaTagger = WordLemmatizer()

  def apply(): WordCollocation = {
    load()
  }

  def build(): Unit = {
    val sentences = Source.fromFile(sentenceFilename).getLines()
    val mainCollocation = new WordCollocation(lemmaTagger, window)
    sentences.take(100000).sliding(njobs, njobs).zipWithIndex.foreach(pair => {
      val seq = pair._1
      println("ZipIndex: " + pair._2)
      seq.sliding(nthreads, nthreads).toArray.par.map(seq => new WordCollocation(lemmaTagger, window).init(seq.iterator))
        .toArray.foreach(wordCollocation => mainCollocation.merge(wordCollocation))
    })

    save(mainCollocation)
  }

  def extract(): Unit = {
    val wordCollocation = load()
    val sentences = Source.fromFile(sentenceFilename).getLines()

    var set = Set[String]()
    sentences.take(100000).sliding(njobs, njobs).zipWithIndex.foreach(pair => {
      val seq = pair._1
      println("ZipIndex: " + pair._2)
      seq.sliding(nthreads, nthreads).toArray.par.map(seq => seq.flatMap(s => wordCollocation.extract(s)))
        .toArray.foreach(collocation => {
        set = set ++ collocation
      })
    })

    new PrintWriter(collocationFilename) {
      set.foreach(collocation => println(collocation))
    }.close()

  }


  def load(): WordCollocation = {
    val inputObjectStream = new ObjectInputStream(new FileInputStream(s"resources/binary/collocator-$window.bin"))
    val sum = inputObjectStream.readLong()
    val length = inputObjectStream.readInt()
    val wordCollocation = new WordCollocation(WordLemmatizer(), window)
    for (i <- 0 until length) {
      val id = inputObjectStream.readInt()
      val cnt = inputObjectStream.readLong()
      wordCollocation.add(id, cnt)
    }
    inputObjectStream.close()
    wordCollocation.setSum(sum)

  }

  def save(wordCollocation: WordCollocation): Unit = {
    val map = wordCollocation.counts
    val outputObjectStream = new ObjectOutputStream(new FileOutputStream(s"resources/binary/collocator-$window.bin"))
    outputObjectStream.writeLong(wordCollocation.sum)
    outputObjectStream.writeInt(map.size)
    map.foreach { case (id, cnt) => {
      outputObjectStream.writeInt(id)
      outputObjectStream.writeLong(cnt)
    }
    }

    outputObjectStream.close()
  }

  def main(args: Array[String]): Unit = {
    //build()
    extract()

  }
}
