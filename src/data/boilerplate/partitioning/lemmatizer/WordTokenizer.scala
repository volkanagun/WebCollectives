package tagging.lemmatizer

import java.io._
import java.util.Locale
import java.util.regex.Pattern
import scala.io.Source

/**
 * @Author Dr. Hayri Volkan Agun
 * @Date 15.03.2022 16:07
 * @Project BigLanguage
 * @Version 1.0
 */

@SerialVersionUID(1000L)
class WordTokenizer(val modelFilename: String = "/resources/dictionary/dictionary.bin", windowSize: Int = 3) extends Serializable {

  val locale = new Locale("tr")
  val txtFilename = new File("").getAbsoluteFile().getAbsolutePath + modelFilename
  val ioFilename = new File("").getAbsoluteFile().getAbsolutePath + modelFilename + "-" + windowSize + ".io"

  val regexWord = "([abcçdefgğhıijklmnoöprsştuüvyzwqABCÇDEFGĞHIİJKLMNOÖPRSŞTUÜVYZQWX\\p{L}]+)"
  val regexLongNum1 = "(\\d+[\\/\\-\\.\\\\]\\d+([\\/\\-\\.\\\\]?))+"
  val regexLongNum2 = "(\\d+[\\/\\-\\.\\\\]\\s\\d+\\s([\\/\\-\\.\\\\]?))+"
  val regexNum = "(\\d+)"
  val regexParanthesisOpen = "([\\(\\[\\{\"\\<\\`])"
  val regexParanthesisClose = "([\\}\\]\\)\"\\>\\`])"
  val regexSTOP = "([\\?\\.\\:\\!])"
  val regexSEP = "((\\_|\\-|\\&\\&|~|¨|\\'|\\|\\|\\,\\;))"
  val regexSYM = "([\\^\\%\\$\\#\\€\\Â\\*\\£\\=\\@])"
  val regexSPACE = "(\\s+)"

  val regexArray = Array(regexWord, regexLongNum1, regexLongNum2, regexNum, regexParanthesisOpen,
    regexParanthesisClose, regexSTOP, regexSEP, regexSYM)
  val patternArray = regexArray.map(Pattern.compile(_, Pattern.UNICODE_CHARACTER_CLASS))

  var frequency = Map[String, Double]()
  var frequencyBin = Map[String, Double]()
  var countSum = 0L


  def maskSymbols(sentence: String): String = {
    var masked = regexLongNum1.r.replaceAllIn(sentence, " NUM ")
    masked = regexLongNum2.r.replaceAllIn(masked, " NUM ")
    masked = regexParanthesisOpen.r.replaceAllIn(masked, " OPEN ")
    masked = regexParanthesisClose.r.replaceAllIn(masked, " CLOSE ")
    masked = regexNum.r.replaceAllIn(masked, " NUM ")
    masked = regexSEP.r.replaceAllIn(masked, " - ")
    masked
  }

  def freqConstruct(filename: String): WordTokenizer = {

    Source.fromFile(filename).getLines().foreach(sentence => {
      val tokens = standardTokenizer(sentence)
      val sentences = Array(tokens.mkString(" "))
      sentences.foreach(sentence => {
        val splited = sentence.split("\\s+")
        Range(1, 3).toArray.flatMap(s => splited.sliding(s).map(items => items.mkString(" ")).toArray)
          .foreach(item => {
            frequency = frequency.updated(item, frequency.getOrElse(item, 0d) + 1d)
            countSum = countSum + 1
          })
      })
    })

    frequency = frequency.filter { case (item, count) => count > 2 }

    save()
  }

  def freqConstructBySentence(sentence: String, cutoff: Int = 2): WordTokenizer = {
    val tokens = standardTokenizer(sentence)
    val sentences = Array(tokens.mkString(" "))

    sentences.foreach(sentence => {
      val splited = sentence.split("\\s+")
      Range(1, 3).toArray.flatMap(s => splited.sliding(s).map(items => items.mkString(" ")).toArray)
        .foreach(item => {
          frequency = frequency.updated(item, frequency.getOrElse(item, 0d) + 1d)
          countSum = countSum + 1
        })
    })

    frequency = frequency.filter { case (item, count) => count > cutoff }

    save()

  }


  def build(cutoff: Int = 5): this.type = {

    frequency = frequency.filter { case (item, count) => count >= cutoff }
    binCountUpdate()

    frequency = Map[String, Double]()
    countSum = 0
    this
  }

  def binCountUpdate(count: Int = 100000): this.type = {

    val values = frequency.map(_._2)
    if (!values.isEmpty) {
      val max = values.max
      val min = values.min
      val step = (max - min) / count
      val newBin = frequency.map { case (item, value) => {
        (item, value / step + 1d)
      }
      }

      newBin.foreach { case (item, value) => frequencyBin = frequencyBin.updated(item, frequencyBin.getOrElse(item, 0d) + value) }
    }

    this
  }

  def save(): WordTokenizer = {
    println("Saving tokenizer....")
    val outputStream = new ObjectOutputStream(new FileOutputStream(txtFilename))
    val array = frequency.toArray
    outputStream.writeInt(array.size)

    for (i <- 0 until array.size) {
      val item = array(i)
      outputStream.writeObject(item)
    }

    outputStream.close()
    this
  }


  def load(): WordTokenizer = {

    println("Loading tokenizer ...")

    if (new File(txtFilename).exists()) {

      val inputStream = new ObjectInputStream(new FileInputStream(txtFilename))
      val size = inputStream.readInt()
      var array = Array[(String, Double)]()
      println("Reading size: " + size)

      for (i <- 0 until size) {
        println("Reading " + i + "/" + size)
        val item = inputStream.readObject().asInstanceOf[(String, Double)]
        array = array :+ item
      }

      inputStream.close()
      this.frequency = array.toMap
      this
    }
    else {
      this
    }
  }

  def saveBinary(): WordTokenizer = {
    println("Saving binary tokenizer....")
    val outputStream = new ObjectOutputStream(new FileOutputStream(ioFilename))
    outputStream.writeObject(this)
    outputStream.close()
    this
  }

  def loadBinary(): WordTokenizer = {

    if (new File(ioFilename).exists()) {
      println("Loading binary tokenizer....")
      try {
        val inputStream = new ObjectInputStream(new FileInputStream(ioFilename))
        val readBinary = inputStream.readObject().asInstanceOf[WordTokenizer]
        frequency = readBinary.frequency
        frequencyBin = readBinary.frequencyBin
        countSum = readBinary.countSum
        inputStream.close()
        println("Tokenizer is loaded...")
      }
      catch {
        case e: Exception => e.printStackTrace()
      }
    }

    this
  }

  def merge(freqWordTokenizer: WordTokenizer): WordTokenizer = {
    freqWordTokenizer.frequency.foreach { case (item, value) => {
      frequency = frequency.updated(item, frequency.getOrElse(item, 0d) + value)
    }
    }

    countSum = countSum + freqWordTokenizer.countSum
    this
  }

  def characterTokenizer(sentence: String): Array[Array[String]] = {

    standardTokenizer(sentence.toLowerCase(new Locale("tr")))
      .sliding(windowSize, windowSize).toArray
      .flatMap(tokens => {
        tokens.map(token => {
          val characters = token.toCharArray.map(_.toString)
            .filter(_.nonEmpty) :+ "#"
          characters
        })
      })
  }


  //separate everything
  def standardTokenizer(sentence: String, pattern: Pattern, start: Int): Option[(String, Int, Int)] = {

    val matcher = pattern.matcher(sentence)
    if (matcher.find(start)) {
      val (s, e) = (matcher.start(), matcher.end())
      val group = sentence.substring(s, Math.min(sentence.length, Math.max(e, s + 1))).trim
      if (!group.isEmpty) {
        return Some((group, s, e))
      }
    }

    None
  }

  def standardReplacer(sentence: String): String = {
    var masked = maskSymbols(sentence)
    patternArray.foreach(pattern => {
      masked = pattern.pattern().r.replaceAllIn(masked, " $1 ")
    })
    masked.replaceAll(regexSPACE, " ").trim
  }

  def wordTokenizer(sentence: String, start: Int): Option[(String, Int, Int)] = {
    val res = patternArray.flatMap(p => standardTokenizer(sentence, p, start)).toArray
      .sortBy(tuple => tuple._3)
    if (res.isEmpty) None
    else Some(res.head)
  }


  def standardTokenizer(sentence: String): Array[String] = {

    val presentence = " " + sentence + " "
    var start = 0
    val masked = standardReplacer(presentence)
    var found = wordTokenizer(masked, start)
    var array = Array[String]()
    while (found.isDefined) {
      val (group, start, end) = found.get
      array = array :+ group
      found = wordTokenizer(masked, end)
    }

    array
  }

  protected def combinatoric(input: Array[Array[String]], result: Array[Array[String]], i: Int = 0): Array[Array[String]] = {

    if (i >= input.length) result
    else {

      var crr = i;
      var array = Array[Array[String]]()

      for (k <- 0 until input(crr).length) {
        for (i <- 0 until result.length) {
          val current = result(i) :+ input(crr)(k)
          array = array :+ current
        }
      }

      combinatoric(input, array, crr + 1)
    }
  }

  def ngramCombinations(sentence: String, stemLength: Int = 5): Array[String] = {
    //find the root
    val arrays = standardTokenizer(sentence).map(token => token.sliding(stemLength, 1).toArray)
    combinatoric(arrays, Array(Array[String]())).map(tokens => tokens.mkString(" "))
  }


  def ngramStemPartition(token: String): Array[String] = {
    val removes = Array(0, 1, 2, 3, 4, 5, 9).reverse

    removes.map(cut => {
      val max = Math.min(Math.max(token.length - cut, 3), token.length)
      token.substring(0, max) + "#" + token.substring(max)
    }).distinct

  }

}

object WordTokenizer {

  val sentenceFilename = "resources/text/sentences-tr.txt";
  //val sentenceFilename = "resources/text/wiki-text.txt";
  val dictionaryPhrasals = "resources/dictionary/phrasals.txt";
  val locale = new Locale("tr")
  val windowSize = 12

  def tokenize(sentence: String): Array[String] = {
    val tokenizer = new WordTokenizer().loadBinary()
    tokenizer.standardTokenizer(sentence)
  }


  def saveBinary(): Unit = {
    new WordTokenizer().loadBinary().build().saveBinary()
  }

  def loadBinary(): WordTokenizer = {
    new WordTokenizer().loadBinary()
  }

  def main(args: Array[String]): Unit = {

  }
}
