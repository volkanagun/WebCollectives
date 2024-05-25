package data.boilerplate.structure

import data.util.TextFile

import java.io.{File, PrintWriter}
import scala.io.Source

class XMLExtractor(val folderNames: Array[String], val count: Int) extends Serializable {

  def reportCharacters(text: String): Boolean = {
    val list = "\\&(.*?);".r.findAllIn(text)
    val bool = list.isEmpty
    list.foreach(item => println(item))
    bool
  }


  def reportFix(text:String, minCharLength:Int = 100):String={
    var mtext = text;

    mtext = mtext.replaceAll("\\{(\\\\(\\p{L}+)\\s)+\\}", " ")
    mtext = mtext.replaceAll("Kaynak\\: Hürriyet", "")

    mtext = mtext.replaceAll("ý","ı")
    mtext = mtext.replaceAll("\\s+"," ")
    mtext = text.replaceAll("\\'\\'","'")
    mtext = text.replaceAll(" "," ")

    mtext = mtext.trim
    var lines = mtext.split("\n").map(line=> {
      val wordCount = "\\p{L}+".r.findAllIn(line).size + 1
      val symCount = "[\\{\\}\\[\\]\\&\\-\\*\\+\\/\\p{Punct}\\p{Digit}]+".r.findAllIn(line).size + 1
      (line, wordCount.toFloat/symCount)
    }).filter(pair => pair._2 > 3 && pair._1.length >= minCharLength)
      .filter(pair => pair._1.matches("(.*?)[\\.\\?\\!\"\\'\\]\\}\\)]$"))
      .map(pair => pair._1.trim)

    lines.mkString("\n")
  }



  def extractDocument(extractFolder: String): XMLExtractor = {
    new File(extractFolder).mkdir()
    folderNames.foreach(folderName => {
      new File(folderName).listFiles().take(count).foreach(f => {
        val fout = new File(extractFolder + f.getName)
        try {
          if(!fout.exists()) {
            val text = XMLParser.parse(f.getPath).get(XMLParser.paragraphLabel)
            if (text.isDefined && reportCharacters(text.get)) {
              val fixText = reportFix(text.get, 80);
              new TextFile(fout).writeFullText(fixText)
            }
          }
        }
        catch {
          case _: Throwable => {}
        }
      })
    })

    this
  }
}

object XMLExtractor {
  def extractStories(): Unit = {
    new XMLExtractor(Array("resources/stories-turkish/"), 100000)
      .extractDocument("resources/story-texts/")
  }

  def extractWikipedia(): Unit = {
    new XMLExtractor(Array("resources/wikipedia/"), 100000)
      .extractDocument("resources/wiki-texts/")
  }

  def extractArticles(): Unit = {
    new XMLExtractor(Array(
      "resources/articles/",
      "resources/articles-turkish/",
      "resources/articles-hurriyet/",
      "resources/articles-cumhuriyet/",
      "resources/articles-independent/",
      "resources/articles-sozcu/",
      "resources/articles-halktv/"), 100000000)
      .extractDocument("resources/txt/")
  }

  def extractBlogs(): Unit = {

    new XMLExtractor(Array("resources/blogs-turkish/"), 100000000)
      .extractDocument("resources/txt/")

  }

  def extractSentences(filename: String, targetFilename: String): Unit = {
    val pw = new PrintWriter(s"resources/sentences/${targetFilename}.txt");
    new File(filename).listFiles().foreach(f => {
      Source.fromFile(f).getLines().foreach(line => {
        pw.println(line)
        pw.flush()
      })
    })

    pw.close()
  }

  def extractSentences(filenames: Array[String], targetFilename: String): Unit = {
    val pw = new PrintWriter(s"resources/sentences/${targetFilename}");
    filenames.foreach(filename => new File(filename).listFiles().foreach(f => {
      Source.fromFile(f).getLines().foreach(line => {
        pw.println(line)
        pw.flush()
      })
    }))

    pw.close()
  }

  def main(args: Array[String]): Unit = {

    extractArticles()
    extractBlogs()
    extractStories()
    extractWikipedia()

    val filenames = Array("resources/txt/", "resources/wiki-texts/", "resources/story-texts/")
    extractSentences(filenames, "sentences-april-v2-tr.txt")
  }
}
