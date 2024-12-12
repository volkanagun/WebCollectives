package tagging.lemmatizer

import scala.collection.parallel.CollectionConverters.ArrayIsParallelizable
import scala.util.control.Breaks


abstract class Tagger extends Serializable{
  def process(sentence:String):Array[WordSpan]
  def init(sentences:Iterator[String]):this.type
}

class RuleTagger extends Tagger {

  var regexSpan = Array[(String, String)]()

  override def init(sentences:Iterator[String]) = this

  def add(regex: String, label: String): this.type = {
    regexSpan = regexSpan :+ (regex, label)
    this
  }

  def matching(sentence: String, regex: String, label: String): Array[WordSpan] = {
    val m = regex.r.pattern.matcher(sentence)
    var array = Array[WordSpan]()

    while (m.find()) {
      val start = m.start()
      val end = m.end()
      array = array :+ new WordSpan(start, end)
        .setLabel(label)
        .setLemma(m.group())
        .setValue(m.group())
    }

    array
  }

  override def process(sentence: String): Array[WordSpan] = {
    //use transducers
    regexSpan.flatMap { case (regex, label) => {
      matching(sentence, regex, label)
    }}
  }

  def partition(sentence:String):Array[String]={
    process(sentence).map(_.value)
  }
}

class RegexTokenizer extends RuleTagger {
  var subTaggers = Array[RuleTagger]()

  def add(regexTagger: RuleTagger): this.type = {
    subTaggers = subTaggers :+ regexTagger
    this
  }

  def consume(array: Array[WordSpan]): Array[WordSpan] = {
    val spans = array.sorted
    var selected = Array[WordSpan]()
    var crrIndex = 0
    while (crrIndex < spans.length) {
      val crrSpan = spans(crrIndex)
      var nextIndex = crrIndex + 1
      val breaks = new Breaks()
      breaks.breakable {
        while (nextIndex < spans.length) {
          val nextSpan = spans(nextIndex)
          if (!crrSpan.contains(nextSpan)) {
            breaks.break()
          }
          else {
            crrIndex += 1
          }

          nextIndex += 1
        }
      }

      selected = selected :+ crrSpan
      crrIndex += 1
    }
    selected
  }

  override def process(sentence: String): Array[WordSpan] = {
    val spans = subTaggers.par.flatMap(tagger => tagger.process(sentence))
      .toArray
    consume(spans)
  }

}

object RegexTokenizer extends RegexTokenizer{
  def apply(): RegexTokenizer = {
    new RegexTokenizer()
      .add(new DateTagger())
      .add(new NumTagger())
      .add(new SymTagger())
      .add(new LetterTagger())
      .add(new PuncTagger())
      .add(new TimeTagger())
      .add(new SepTagger())
      .add(new MoneyTagger())
      .add(new AbbrTagger())
      .add(new AdjTagger())
  }

  def test(): Unit = {
    val s1 = "İstiklal'le taban tabana zıt bir yer."
    val spans = RegexTokenizer().process(s1)
    println(spans.mkString("\n"))
  }

  def main(args: Array[String]): Unit = {
    test()
  }
}

class DateTagger(val label: String = "DATE") extends RuleTagger {
  add("\\d{2}}\\.\\d{2}\\.\\d{4}", label)
}

class TimeTagger(val label: String = "TIME") extends RuleTagger {
  add("\\d{2}\\:\\d{2} (a\\.m\\.||p\\.m\\.)", label)
  add("\\d{2}\\:\\d{2}", label)
}

class LetterTagger(val label: String = "WORD") extends RuleTagger {
  add("([abcçdefgğhıijklmnoöprsştuüvyzwqABCÇDEFGĞHIİJKLMNOÖPRSŞTUÜVYZQWX]+)", label)
}

class PuncTagger(val label: String = "STOP") extends RuleTagger {
  add("([\\?\\.\\:\\!]+)", label)
}

class SymTagger(val label: String = "SYM") extends RuleTagger {
  val regexSYM = "([\\^\\%\\$\\#\\€\\Â\\*\\£\\=\\@])"
  add(regexSYM, label)
}


class MoneyTagger(val label: String = "MONEY") extends RuleTagger {
  val moneySYM = "(\\d+\\,\\d+\\.\\d+|\\d+\\.\\d+|\\d+)([\\$\\€\\£\\¥\\₹\\₱\\₺]|Kč|[ABCÇDEFGĞHIİJKLMNOÖPRSŞTUÜVYZXWQ]+)"
  add(moneySYM, label)
}

class SepTagger(val label: String = "SEP") extends RuleTagger {
  val regexSEP = "(\\_|\\-|\\&\\&|~|¨|\\'|\\|\\|\\,\\;)"
  add(regexSEP, label)
}

class NumTagger(val label: String = "NUM") extends RuleTagger {
  add("((\\d+\\,\\d+\\.\\d+)|(\\d+))", label)
  add("((\\d+\\.\\d+))", label)
}

class AbbrTagger(val label: String = "ABBR") extends RuleTagger {
  add("(([ABCÇDEFGĞHIİJKLMNOÖPRSŞTUÜVYZXWQ]\\.)+)", label)
  add("([ABCÇDEFGĞHIİJKLMNOÖPRSŞTUÜVYZXWQ]+)", label)
}

class AdjTagger(val label:String = "Adj") extends RuleTagger{
  add("(\\d+)('?)(inci|ıncı|uncu|üncü|nci|ncı|ncu|ncü|lar|ler\\.)", label)
}

