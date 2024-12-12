package tagging.lemmatizer

import java.io.{FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream}
import java.util.Locale
import scala.io.Source

class WordLemmatizer(val tagger:Tagger) extends Tagger {

  var forms = Map[String, EntryList]()
  val replacer = WordLemmaReplacer()

  val tr = new Locale("tr")

  override def process(sentence: String): Array[WordSpan] = {
    extract(sentence)
      .map(_.asInstanceOf[WordSpan])
  }

  def extract(sentence:String):Array[WordGroup]={
    val spans = tagger.process(sentence)
    lemmatize(spans, sentence)
  }

  def extractPruned(sentence:String):Array[WordGroup]={
    val wordGroups = extract(sentence)
    wordGroups.map(wordGroup=> wordGroup.prune())
  }

  override def init(sentences: Iterator[String]): WordLemmatizer.this.type = this

  case class Entry(lemma:String, label:String){
    override def equals(obj: Any): Boolean = {
      if(obj.isInstanceOf[Entry]){
        val other = obj.asInstanceOf[Entry]
        other.lemma == lemma && other.label == label
      }
      else {
        false
      }
    }
  }
  case class EntryList(var array:Array[Entry]){
    def this(entry:Entry) = this(Array(entry))
    def add(entry:Entry): this.type = {
      if(!array.contains(entry)) array = array :+ entry
      this
    }
  }

  def add(item: String,lemma:String, label: String): this.type = {
    if(forms.contains(item)){
      forms(item).add(Entry(lemma, label))
    }
    else {
      forms = forms + (item -> new EntryList(Entry(lemma, label)))
    }
    this
  }

  def contains(input: Array[String]): Boolean = {
    forms.contains(input.mkString(" "))
  }

  def contains(input: String): Boolean = {
    forms.contains(input)
  }

  def compile(text:String, input:Array[WordSpan], entryList: EntryList):WordGroup={
    val (start, end) = (input.head.start, input.last.end)
    val wordGroup = WordGroup(start, text.substring(start, end))

    entryList.array.foreach(entry=> {
      wordGroup.add(new WordSpan(wordGroup.start, wordGroup.end)
        .setValue(wordGroup.value)
        .setLemma(entry.lemma)
        .setLabel(entry.label))
    })

    wordGroup
  }

  def lemmatize(input: Array[WordSpan], text:String): Array[WordGroup] = {

    var i = 0

    var result = Array[WordSpan]()

    while (i < input.length) {
      val max = math.min(i + 5, input.length)
      var notFound = true
      var imax = i + 1
      for (j <- max until i by -1) {
        val wordseq = input.slice(i, j - 1)

        val prefix = wordseq.mkString(" ")
        val last = input(j - 1)
        val length = last.length
        val min = math.max(3, length - 10)

        for (k <- length to min by -1) {
          val slice = (prefix + " " + last.slice(0, k)).trim
          val lowercase = slice.toLowerCase(tr)
          val processed = replacer.process(lowercase)

          processed.foreach(candidate=>{
            if (contains(candidate)) {
              val entryList = forms(candidate)
              val wordGroup = compile(text, input.slice(i, j), entryList)
              result = result :+ wordGroup
              imax = math.max(imax, j)
              notFound = false
            }
          })
        }
      }

      if (notFound) {
        result = result :+ input(i).toWordGroup()
      }

      i = imax
    }

    //All combinations
    //val spans = result.flatMap(wordSpan => wordSpan.asInstanceOf[WordGroup].items)
    //WordGroup(results)
    WordGroup(result)

  }

}


object WordLemmatizer {

  var source = "resources/dictionary/lexicon.txt"
  var binary = "resources/binary/lemma.bin"
  val consonant = "bcçdfgğhjklmnprsştvyzxq"

  val p1 = s"([aı][${consonant}]+)E" + "$"
  val p2 = s"([ei][${consonant}]+)E" + "$"
  val p3 = s"([öü][${consonant}]+)E" + "$"
  val p4 = s"([ou][${consonant}]+)E" + "$"
  val p5 = s"K" + "$"
  val p6 = s"T" + "$"
  val z0 = s"&" + "$"
  val z1 = "[\\~\\$]"

  def apply(): WordLemmatizer = {

    load()
  }


  //use implicit or other staff
  def save(wordLemmatizer: WordLemmatizer): WordLemmatizer = {

    val writer = new ObjectOutputStream(new FileOutputStream(binary))
    writer.writeInt(wordLemmatizer.forms.size)
    wordLemmatizer.forms.foreach { case (word, entryList) => {
      writer.writeObject(word)
      writer.writeInt(entryList.array.length)
      for(i<-0 until entryList.array.length){
        writer.writeObject(entryList.array(i).lemma)
        writer.writeObject(entryList.array(i).label)
      }
    }}

    writer.close()
    wordLemmatizer
  }

  def load(): WordLemmatizer = {
    val wordLemmatizer = new WordLemmatizer(RegexTokenizer())
    val reader = new ObjectInputStream(new FileInputStream(binary))
    val size = reader.readInt()

    for (i <- 0 until size) {
      val word = reader.readObject().asInstanceOf[String]
      val length = reader.readInt()
      for(j<-0 until length) {
        val lemma = reader.readObject().asInstanceOf[String]
        val label = reader.readObject().asInstanceOf[String]
        wordLemmatizer.add(word,lemma, label)
      }
    }

    reader.close()
    wordLemmatizer
  }

  def buildWiki(): WordLemmatizer = {
    val wordLemmatizer = buildLexicon()
    val processNo = (text: String) => text
    val processMakmek = (text: String) => {
      text.replaceAll("(mak|mek)$", "")
    }

    buildWiki(wordLemmatizer, "resources/dictionary/wiki-adjectives.txt", "adjective", processNo)
    buildWiki(wordLemmatizer, "resources/dictionary/wiki-nouns.txt", "noun", processNo)
    buildWiki(wordLemmatizer, "resources/dictionary/wiki-adverbs.txt", "adverb", processNo)
    buildWiki(wordLemmatizer, "resources/dictionary/wiki-names.txt", "noun", processNo)
    buildWiki(wordLemmatizer, "resources/dictionary/wiki-particles.txt", "particle", processNo)
    buildWiki(wordLemmatizer, "resources/dictionary/wiki-phrases.txt", "phrase", processNo)
    buildWiki(wordLemmatizer, "resources/dictionary/wiki-verbs.txt", "verb", processMakmek)
    buildWiki(wordLemmatizer, "resources/dictionary/wiki-conjunctions.txt", "conjunction", processNo)
    buildWiki(wordLemmatizer, "resources/dictionary/wiki-determiners.txt", "determiner", processNo)
    buildWiki(wordLemmatizer, "resources/dictionary/wiki-interjections.txt", "interjections", processNo)
    buildWiki(wordLemmatizer, "resources/dictionary/wiki-postposition.txt", "postposition", processNo)
    buildWiki(wordLemmatizer, "resources/dictionary/wiki-preposition.txt", "preposition", processNo)
    buildWiki(wordLemmatizer, "resources/dictionary/wiki-pronouns.txt", "pronoun", processNo)

    save(wordLemmatizer)
  }

  def buildWiki(wordLemmatizer: WordLemmatizer, filename:String, label:String, process:(String=>String)): WordLemmatizer = {
    Source.fromFile(filename).getLines().map(line=> line.trim).filter(line=> line.nonEmpty && line.length > 1)
      .map(line=> process(line))
      .foreach(item=> wordLemmatizer.add(item, item, label))
    wordLemmatizer
  }

  def buildLexicon(): WordLemmatizer = {

    val pr = new WordLemmatizer(RegexTokenizer())
    Source.fromFile(source).getLines().foreach(line => {
      val lineSplit = line.split("\t")
      val (word, pos) = (lineSplit(0), lineSplit(2))
      val lemma = clean(word)
      val wordSet = convert(word)
      wordSet.foreach(item => {
        pr.add(item, lemma, pos)
      })
    })

    save(pr)
  }

  def clean(entry:String):String={
    var z = entry.replaceAll(z0, "")
    z = z.replaceAll(z1, "")
    z = z.replaceAll(p1, "$1a")
    z = z.replaceAll(p2, "$1e")
    z = z.replaceAll(p5, "k")
    z = z.replaceAll(p6, "t")
    z
  }

  def convert(entry: String): Set[String] = {
    val zz0 = entry.replaceAll(z0, "")
    val zz1 = zz0.replaceAll(z1, "")
    val c11 = zz1.replaceAll(p1, "$1ı")
    val c12 = zz1.replaceAll(p1, "$1a")
    val c21 = zz1.replaceAll(p2, "$1i")
    val c22 = zz1.replaceAll(p2, "$1e")
    val c3 = zz1.replaceAll(p3, "$1ü")
    val c4 = zz1.replaceAll(p4, "$1u")
    val c51 = zz1.replaceAll(p5, "ğ")
    val c52 = zz1.replaceAll(p5, "k")
    val c61 = zz1.replaceAll(p6, "d")
    val c62 = zz1.replaceAll(p6, "t")

    Set(c11, c12, c21, c22, c3, c4, c51, c52, c61, c62)
      .filter(item => !item.matches("(.*?)[ETK]$"))
  }

  def main(args: Array[String]): Unit = {
    //println(convert("ağlE"))

    buildWiki()
    test()
  }

  def test(): Unit = {
    val wordLemmatizer = load()
    val regexTokenizer = RegexTokenizer()
    Array("mutluluğunda", "yüzerek karşı kıyıya geçti.","candan bezerek ağlamaya başladı.", "yeter akşamlık sabahlık.",
      "1 haftadan kisaysa tsi̇ i̇le yaşayin jet lag ile başetmek prof. dr. aksu’nun önerileri şöyle: eğer yolculuk kısa sürecekse hareket edilen ülkenin saatine uygun olarak yaşamak en etkili yöntem.").foreach(sentence => {
      val spans = wordLemmatizer.lemmatize(regexTokenizer.process(sentence), sentence)
      println(spans.mkString("|"))
    })
  }
}