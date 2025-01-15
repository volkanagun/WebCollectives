package data.boilerplate.structure

import data.crawler.web.LookupOptions
import data.util.TextFile

import java.io.{File, PrintWriter}
import java.text.BreakIterator
import java.util.Locale
import scala.collection.parallel.CollectionConverters.ArrayIsParallelizable
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



  def extractDocument(extractFolder: String, minTextLength:Int = 200): XMLExtractor = {
    new File(extractFolder).mkdir()
    folderNames.foreach(folderName => {
      new File(folderName).listFiles().par.foreach(f => {
        println(s"Filename ${f.getName}")
        val fout = new File(extractFolder + f.getName)
        try {
          if(!fout.exists()) {
            val map = XMLParser.parseList(f.getPath)
            val textList = map.get(XMLParser.paragraphLabel)
            if (textList.isDefined) {
              val textArray = textList.get
              val file = new TextFile(fout)
              file.openBufferWrite()
              textArray.foreach(text=>{
                val fixText = reportFix(text, 80);
                if(fixText.length > minTextLength) {
                  file.writeNextLine(fixText)
                }
              })

              file.closeBufferWrite()

            }
          }
        }
        catch {
          case e : Throwable => {
            val d = 0
          }
        }
      })
    })

    this
  }
}

object XMLExtractor {

  val locale = new Locale("tr")

  def extractStories(): Unit = {
    new XMLExtractor(Array("resources/stories-turkish/"), 100000)
      .extractDocument("resources/story-texts/")
  }

  def extractWikipedia(): Unit = {
    new XMLExtractor(Array("resources/wikipedia/"), 1000000)
      .extractDocument("resources/wiki-texts/", 300)
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

    new XMLExtractor(Array("resources/blogs-turkish/", LookupOptions.BLOGTRYDIRECTORY), 100000000)
      .extractDocument("resources/txt/")

  }


  def extractSentences(filename: String, targetFilename: String): Unit = {
    val pw = new PrintWriter(s"resources/sentences/${targetFilename}.txt");
    new File(filename).listFiles().foreach(f => {
      Source.fromFile(f).getLines().foreach(line => {
        val sentenceIter = BreakIterator.getSentenceInstance(locale)
        sentenceIter.setText(line)
        var start = 0
        var end = sentenceIter.next()
        while(end != BreakIterator.DONE){
          pw.println(line.substring(start, end))
          pw.flush()
          start = end
          end = sentenceIter.next()
        }

      })
    })

    pw.close()
  }

  def extractSentences(text:String):Array[String]={
    val sentenceIter = BreakIterator.getSentenceInstance(locale)
    sentenceIter.setText(text)
    var start = 0
    var end = sentenceIter.next()
    var array = Array[String]()
    while(end != BreakIterator.DONE){
      val sentence = text.substring(start, end)
      if (sentence.length > 70) {
        array :+= sentence
      }
      start = end
      end = sentenceIter.next()
    }
    array
  }

  def extractSentences(filenames: Array[String], targetFilename: String): Unit = {
    val pw = new PrintWriter(s"resources/sentences/${targetFilename}");
    filenames.foreach(filename => new File(filename).listFiles().foreach(f => {
      Source.fromFile(f).getLines().foreach(line => {
        val sentenceIter = BreakIterator.getSentenceInstance(locale)
        sentenceIter.setText(line)
        var start = 0
        var end = sentenceIter.next()
        while(end != BreakIterator.DONE){
          pw.println(line.substring(start, end))
          pw.flush()
          start = end
          end = sentenceIter.next()
        }
      })
    }))

    pw.close()
  }

  def main(args: Array[String]): Unit = {
/*
    extractArticles()
    extractBlogs()
    extractStories()
    extractWikipedia()
*/

    val filenames = Array("resources/txt/", "resources/wiki-texts/", "resources/story-texts/")
    extractSentences(filenames, "sentences-december-v1-tr.txt")
  }
}
