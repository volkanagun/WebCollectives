package data.boilerplate

import java.util.Locale
import java.util.regex.Pattern

import scala.io.Source
import scala.xml.Node

object XMLParser {


  def parse(filename: String): Map[String, String] = {
    val text = Source.fromFile(filename, "UTF-8").getLines().mkString("\n")
    val cleanedText = cleanText(text)
    val xmlMain = scala.xml.XML.loadString(cleanedText);
    val xmlRoot = (xmlMain \\ "ROOT")
    var map = Map[String, String]()

    (xmlRoot \\ "RESULT").foreach(node => {
      val value = (node \ "@LABEL").text
      if (value.equals("AUTHORNAME")) {
        var authorName = node.text.trim
        if (authorName != null && !authorName.isEmpty) {
          val index: Int = authorName.indexOf("|")
          if (index > 0) {
            authorName = authorName.substring(0, index).trim.replaceAll("\n", "")
          }
          map = map.updated("author", authorName)
        }
      }
      else if (value.equals("ARTICLETITLE")) {
        map = map.updated("title", node.text.trim)
      }
      else if (value.equals("GENRE")) {
        var genre: String = node.text.trim
        if (genre != null && !genre.isEmpty) {
          map = map.updated("genre", genre)
        }
      }
      else if (value.equals("ARTICLETEXT")) {
        val content = node.text
        map = map.updated("body", content)
      }
      else if (value.equals("ARTICLEPARAGRAPH")) {
        val paragraph = node.text
        map = map.updated("paragraph", map.getOrElse("body", "") + "\n" + paragraph)
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
    ntext

  }
}
