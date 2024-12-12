package tagging.lemmatizer

import java.util.regex.Pattern

class WordReplacer {

  case class Replacement(val regex:String, val replaces:String)
  {
    val pattern = Pattern.compile(regex)
    def replace(text:String):(Boolean, String)={
      val matcher = pattern.matcher(text)
      (matcher.find(), matcher.replaceAll(replaces))
    }
  }

  var replacementList = Array[Replacement]()

  def addReplacement(pattern:String, replaces:String):this.type ={
    replacementList:+= Replacement(pattern, replaces)
    this
  }

  def process(token:String):Array[String]={
    val foundList = replacementList.map(replacement=> replacement.replace(token))
      .filter(_._1).map(_._2).distinct
    if(foundList.isEmpty) Array(token)
    else foundList
  }
}

object WordLemmaReplacer{

  val consonent = "[qwrtypğsdfghklşzxcvbnmç]"
  val vowel = "[euıoüaiö]"

  val c1 = "(" + consonent + ")("+vowel+")ğ$"

  def apply(): WordReplacer = {
    new WordReplacer().addReplacement(c1,"$1$2k")
  }

  def test(): Unit = {
    val replacer = WordLemmaReplacer()
    replacer.process("mutluluğ").foreach(result=> println(result))
  }

  def main(args: Array[String]): Unit = {
    test()
  }
}
