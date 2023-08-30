package data.boilerplate.structure

import java.util.Locale
import java.util.regex.Pattern
import scala.io.Source

object XMLParser {


  val paragraphLabel = "ARTICLEPARAGRAPH";
  val articleLabel = "ARTICLETEXT";
  val authorLabel = "AUTHORNAME";
  val titleLabel = "ARTICLETITLE";
  val genreLabel = "GENRE";

  def parse(filename: String): Map[String, String] = {

    val text = Source.fromFile(filename, "UTF-8").getLines().mkString("\n")
    val cleanedText = cleanText(text)
    val xmlMain = scala.xml.XML.loadString(cleanedText);
    val xmlRoot = (xmlMain \\ "ROOT")
    var map = Map[String, String]()

    (xmlRoot \\ "RESULT").foreach(node => {
      val value = (node \ "@LABEL").text
      if (value.equals(authorLabel)) {
        var authorName = node.text.trim
        if (authorName != null && !authorName.isEmpty) {
          val index: Int = authorName.indexOf("|")
          if (index > 0) {
            authorName = authorName.substring(0, index).trim.replaceAll("\n", "")
          }
          map = map.updated(authorLabel, authorName)
        }
      }
      else if (value.equals(titleLabel)) {
        map = map.updated(titleLabel, node.text.trim)
      }
      else if (value.equals(genreLabel)) {
        var genre: String = node.text.trim
        if (genre != null && !genre.isEmpty) {
          map = map.updated(genreLabel, genre)
        }
      }
      else if (value.equals(articleLabel)) {
        val content = cleanText(node.text)
        map = map.updated(articleLabel, content)
      }
      else if (value.equals(paragraphLabel)) {
        val paragraph =  cleanText(node.text)
        map = map.updated(paragraphLabel, map.getOrElse(paragraphLabel, "") + "\n" + paragraph)
      }
    })

    map
  }


  def cleanText(text: String): String = {
    //rewrite here vice versa text replace
    var ntext = text.replaceAll("\u001B", "")
    ntext = WebReplaces.htmlReplaces
      .foldLeft[String](ntext)((txt, replacement) => replacement.replaceAll(txt))
    ntext = ntext.replaceAll("&", "&amp;")
    ntext = Pattern.compile("(<a\\s(.*?)>)", Pattern.DOTALL).matcher(ntext).replaceAll("")
    ntext = cleanHTMLCodes(ntext)
    //ntext = normalizeCases(ntext)
    ntext
  }

  def cleanHTMLCodes(text: String): String = {
    var mtext = text
    mtext = mtext.replaceAll("â", "a")
    mtext = mtext.replaceAll("\\&amp;#8211;", "-")
    mtext = mtext.replaceAll("\\&#199;", "Ç")
    mtext = mtext.replaceAll("\\&#231;", "ç")
    mtext = mtext.replaceAll("\\&#246;", "ö")
    mtext = mtext.replaceAll("\\&#214;", "Ö")
    mtext = mtext.replaceAll("\\&#252;", "ü")
    mtext = mtext.replaceAll("\\&#220;", "Ü")

    mtext = mtext.replaceAll("\\&#8211;", "-")
    mtext = mtext.replaceAll("\\&#13;", "\n")
    mtext = mtext.replaceAll("\\&#35;", "#")
    mtext = mtext.replaceAll("\\&#36;", "$")
    mtext = mtext.replaceAll("\\&#37;", "%")
    mtext = mtext.replaceAll("\\&#38;", "&")
    mtext = mtext.replaceAll("\\&#39;", "'")
    mtext = mtext.replaceAll("\\&#40;", "(")
    mtext = mtext.replaceAll("\\&#41;", ")")
    mtext = mtext.replaceAll("\\&#42;", "*")
    mtext = mtext.replaceAll("\\&#43;", "+")
    mtext = mtext.replaceAll("\\&#45;", "-")
    mtext = mtext.replaceAll("\\&#47;", "/")
    mtext = mtext.replaceAll("\\&#60;", "<")
    mtext = mtext.replaceAll("\\&#61;", "=")
    mtext = mtext.replaceAll("\\&#62;", ">")
    mtext = mtext.replaceAll("\\&#63;", "?")
    mtext = mtext.replaceAll("\\&#64;", "@")
    mtext = mtext.replaceAll("\\&#91;", "[")
    mtext = mtext.replaceAll("\\&#92;", "\\")
    mtext = mtext.replaceAll("\\&#93;", "]")

    mtext = mtext.replaceAll("\\&#94;", "^")
    mtext = mtext.replaceAll("\\&#95;", "_")
    mtext = mtext.replaceAll("\\&#96;", "`")

    mtext = mtext.replaceAll("\\&#123;", "{")
    mtext = mtext.replaceAll("\\&#124;", "|")
    mtext = mtext.replaceAll("\\&#125;", "}")
    mtext = mtext.replaceAll("\\&#126;", "~")
    mtext = mtext.replaceAll("\\&#163;", "£")
    mtext = mtext.replaceAll("\\&#169;", "©")
    mtext = mtext.replaceAll("\\&#177;", "±")

    mtext = mtext.replaceAll("\\&#163;", "£")
    mtext = mtext.replaceAll("\\&#165;", "¥")
    mtext = mtext.replaceAll("\\&#38;", "&")
    mtext = mtext.replaceAll("\\&#160;", " ")
    mtext = mtext.replaceAll("\\&#214;", "Ö")
    mtext = mtext.replaceAll("\\&#215;", "×")
    mtext = mtext.replaceAll("\\&#246;", "ö")

    mtext = mtext.replaceAll("\\&#8471;", "℗")
    mtext = mtext.replaceAll("\\&#8364;", "€")
    mtext = mtext.replaceAll("\\&#8480;", "℠")
    mtext = mtext.replaceAll("\\&#8482;", "™")

    mtext = mtext.replaceAll("\\&#945;", "α")
    mtext = mtext.replaceAll("\\&#946;", "β")
    mtext = mtext.replaceAll("\\&#947;", "γ")
    mtext = mtext.replaceAll("\\&#948;", "δ")
    mtext = mtext.replaceAll("\\&#949;", "ε")
    mtext = mtext.replaceAll("\\&#950;", "ζ")
    mtext = mtext.replaceAll("\\&#951;", "η")
    mtext = mtext.replaceAll("\\&#952;", "θ")
    mtext = mtext.replaceAll("\\&#953;", "ι")
    mtext = mtext.replaceAll("\\&#954;", "κ")
    mtext = mtext.replaceAll("\\&#955;", "λ")
    mtext = mtext.replaceAll("\\&#956;", "μ")
    mtext = mtext.replaceAll("\\&#957;", "ν")
    mtext = mtext.replaceAll("\\&#958;", "ξ")
    mtext = mtext.replaceAll("\\&#959;", "ο")
    mtext = mtext.replaceAll("\\&#960;", "π")
    mtext = mtext.replaceAll("\\&#961;", "ρ")
    mtext = mtext.replaceAll("\\&#963;", "σ")
    mtext = mtext.replaceAll("\\&#964;", "τ")
    mtext = mtext.replaceAll("\\&#965;", "υ")
    mtext = mtext.replaceAll("\\&#966;", "φ")
    mtext = mtext.replaceAll("\\&#967;", "χ")
    mtext = mtext.replaceAll("\\&#968;", "ψ")
    mtext = mtext.replaceAll("\\&#969;", "ω")
    mtext = mtext.replaceAll("\\&#915;", "Γ")
    mtext = mtext.replaceAll("\\&#916;", "Δ")
    mtext = mtext.replaceAll("\\&#917;", "Ε")
    mtext = mtext.replaceAll("\\&#918;", "Ζ")
    mtext = mtext.replaceAll("\\&#919;", "Η")
    mtext = mtext.replaceAll("\\&#920;", "Θ")
    mtext = mtext.replaceAll("\\&#921;", "Ι")
    mtext = mtext.replaceAll("\\&#922;", "Κ")
    mtext = mtext.replaceAll("(\\&#923;|\\&Lambda;)", "Λ")
    mtext = mtext.replaceAll("(\\&#924;|\\&Mu;)", "Μ")
    mtext = mtext.replaceAll("(\\&#925;|\\&Nu;)", "Ν")
    mtext = mtext.replaceAll("(\\&#926;|\\&Xi;)", "Ξ")
    mtext = mtext.replaceAll("(\\&#927;|\\&Omicron;)", "Ο")
    mtext = mtext.replaceAll("(\\&#928;|\\&Pi;)", "Π")
    mtext = mtext.replaceAll("(\\&#929;|\\&Rho;)", "Ρ")
    mtext = mtext.replaceAll("(\\&#931;|\\&Sigma;)", "Σ")
    mtext = mtext.replaceAll("(\\&#932;|\\&Tau;)", "Τ")
    mtext = mtext.replaceAll("(\\&#933;|\\&Upsilon)", "Υ")
    mtext = mtext.replaceAll("(\\&#934;|\\&Phi;)", "Φ")
    mtext = mtext.replaceAll("(\\&#935;|\\&Chi;)", "Χ")
    mtext = mtext.replaceAll("(\\&#936;|\\&Psi;)", "Ψ")
    mtext = mtext.replaceAll("(\\&#937;|\\&Omega;)", "Ω")

    mtext = mtext.replaceAll("\\&amp;", "&")
    mtext
  }

  def normalizeCases(text:String):String={
    var mtext = normalizeCammelCase(text, "((\\p{Ll}+)(\\p{Lu}\\p{L}+))")
    mtext = normalizeCase(text, "((\\p{Lu})(\\p{Lu}+))")
    mtext = normalizeCase(mtext, "((\\p{Ll})(\\p{Lu}{2,}}))")
    mtext = normalizeCase(mtext, "($(\\p{Ll})(\\p{L}+))")

    mtext
  }

  def normalizeCase(text:String, pattern:String ) : String = {
    var mtext = text
    val locale = new Locale("tr")
    val matcher = Pattern.compile(pattern).matcher(text)
    while(matcher.find()){
      val replaceFound = matcher.group()
      val gstr1 = matcher.group(2).toUpperCase(locale)
      val gstr2 = matcher.group(3).toLowerCase(locale)
      mtext = mtext.replaceAll(replaceFound, gstr1+gstr2)
    }

    mtext
  }

  def normalizeCammelCase(text:String, pattern:String ) : String = {
    var mtext = text
    val locale = new Locale("tr")
    val matcher = Pattern.compile(pattern).matcher(text)
    while(matcher.find()){
      val replaceFound = matcher.group()
      val gstr1 = matcher.group(2)
      val gstr2 = matcher.group(3).toLowerCase(locale)
      mtext = mtext.replaceAll(replaceFound, gstr1+gstr2)
    }

    mtext
  }

  def main(args: Array[String]): Unit = {
    val txt = "ali DÜN gece uĞramadı."
    println(normalizeCases(txt))
  }
}
