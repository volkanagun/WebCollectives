package data.boilerplate.structure

import org.apache.pdfbox.cos.COSDocument
import org.apache.pdfbox.io.{RandomAccessFile, RandomAccessInputStream, RandomAccessRead}
import org.apache.pdfbox.pdfparser.PDFParser
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.text.{PDFTextStripper, PDFTextStripperByArea}

import java.awt.Rectangle
import java.io.{File, FileOutputStream, PrintWriter}
import java.nio.file.{Files, Path, StandardCopyOption}
import scala.collection.parallel.CollectionConverters.ArrayIsParallelizable
import scala.io.Source

class PDFExtractor(val sourceFolder: String, val destinationFile: String) {

  def extract(): this.type = {
    val pw = new PrintWriter(new FileOutputStream(destinationFile, false))
    extract(pw, new File(sourceFolder))
    pw.close()
    this
  }

  def fix(newFilename:String):this.type ={
    val lines = Source.fromFile(destinationFile).getLines()
      .flatMap(line=> XMLExtractor.extractSentences(line))
      .filter(sentence=> !sentenceHasCapital(sentence) && !sentenceHasCammelCase(sentence) && !sentenceHasLong(sentence)
        && !sentenceHasIllegal(sentence) && !sentenceHasDefinition(sentence))
      .map(sentence=> sentenceWithPunct(sentence))
      .map(sentence=> sentenceCleanRepetetion(sentence))
      .map(sentence=> sentenceClean(sentence))

    println("Lines filtered....")


    lines.sliding(100000, 100000).foreach(collection=>{
      val pw = new PrintWriter(new FileOutputStream(newFilename, true))
      collocate(collection).foreach(line=>{
        pw.println(line)
      })

      pw.close()
    })

    println("Lines fixed")
    this
  }

  def sentenceContains(sentence: String, regex: String): Boolean = {
    val foundIter = regex.r.findAllIn(sentence)
    foundIter.nonEmpty
  }

  def sentenceHasCapital(sentence: String): Boolean = {
    val tokens = sentence.split("\\s+");
    val count = tokens.count(token => token.matches("[A-ZÜĞİŞÇÖ\\p{Punct}]+"))
    count > 0
  }
  def sentenceHasCammelCase(sentence: String): Boolean = {
    val tokens = sentence.split("\\s+");
    val regex = "([A-ZÜĞİŞÇÖ\\p{Punct}][a-zğüışçö][A-ZÜĞİŞÇÖ\\p{Punct}]|[a-zğüışçö][A-ZÜĞİŞÇÖ\\p{Punct}][a-zğüışçö])+"
    val count = tokens.count(token => regex.r.findAllIn(token).nonEmpty)
    count > 0
  }

  def sentenceHasLong(sentence: String): Boolean = {
    val tokens = sentence.split("\\s+");
    val count = tokens.count(token => token.matches("\\p{Punct}{5,}"))
    count > 0
  }

  def sentenceHasIllegal(sentence: String): Boolean = {
    val tokens = sentence.split("\\s+");
    val count1 = tokens.count(token => token.matches("(»||\"°\\\\.)"))
    val bool1 = sentenceContains(sentence, "\\\\[a-zA-Z]")
    val bool2 = sentenceContains(sentence, "\\_+")
    count1 > 0 || bool1 || bool2
  }

  def sentenceHasDefinition(sentence: String): Boolean = {
    val bool = sentenceContains(sentence, "\\d+ [A-Za-zĞÜİŞÇÖğüşıöç]+\\: ")
    bool
  }

  def sentenceCleanRepetetion(sentence: String): String = {
    val tokens = sentence.split("\\s")
    var array = Array[String]()
    for (i <- 0 until tokens.length - 1) {
      val crr = tokens(i);
      if (!(crr.length == 1 && crr.equals(tokens(i + 1)))) {
        array = array :+ crr
      }
    }
    array = array :+ tokens.last
    array.mkString(" ")
  }

  def sentenceWithPunct(sentence: String): String = {
    val tokens = sentence.split("\\s+");
    if (tokens.head.matches("[\\p{Punct}\\d]{2,}"))
      tokens.tails.mkString(" ")
    else
      sentence
  }

  def sentenceClean(sentence: String): String = {
    var new_sentence = sentence
    new_sentence = new_sentence.replaceAll("·�", "")
    new_sentence = new_sentence.replaceAll("'�", "")
    new_sentence = new_sentence.replaceAll("�", "")
    new_sentence = new_sentence.replaceAll("\u00AD", "")
    new_sentence = new_sentence.replaceAll("°\\\\", "")
    new_sentence = new_sentence.replaceAll("\\,\\”", "\"")
    new_sentence = new_sentence.replaceAll("\\,\"", "\"")
    new_sentence = new_sentence.replaceAll("\\.\\,", "\\.")
    new_sentence = new_sentence.replaceAll("\\d+\\s+\\—", "-")
    new_sentence = new_sentence.replaceAll("\\’", "'")
    new_sentence = new_sentence.replaceAll("\\”", "\"")
    new_sentence = new_sentence.replaceAll("\\“", "\"")
    new_sentence = new_sentence.replaceAll("\\d+\\s+\\[\\-\\*]", "-")
    new_sentence = new_sentence.replaceAll("\\*\\s\\*\\s\\*", "")
    new_sentence = new_sentence.replaceAll("((\\,|\\;|\\:)){2,}", "$2")
    new_sentence = new_sentence.replaceAll("(\\'([A-ZÜĞİŞÇÖ]))", "$2")
    new_sentence = new_sentence.replaceAll("(\\')\\s+([a-zğüışçö]+)", "$1$2")
    new_sentence = new_sentence.replaceAll("(\\d+)\\s+([A-ZÜĞİŞÇÖ]+)", "$2")
    new_sentence = new_sentence.replaceAll("(\\d+)\\s+([a-zğüışçö]+)", "$2")
    new_sentence = new_sentence.replaceAll("(([a-zğüışçö]+)\\-)", "$2")

    new_sentence
  }

  def collocate(collection: Seq[String]): Array[String] = {
    println("Collocation analysis...")
    val dictionary = collection.flatMap(sentence => sentence.split("\\s+").map(token => (token.hashCode, 1))).groupBy(_._1)
      .view.mapValues(_.length)

    collection.map(sentence => {
      var tokens = Array[String]()
      val sentenceTokens = sentence.split("\\s+")
      var i = 0
      var addedLast = false
      while (i < sentenceTokens.length - 1) {
        val collocate = sentenceTokens(i) + sentenceTokens(i + 1)
        val collocateHash = collocate.hashCode
        if (dictionary.contains(collocateHash)) {
          tokens :+= collocate
          addedLast = true
        }
        else {
          tokens :+= sentenceTokens(i)
          addedLast = false
        }

        i = i + 1
      }

      if (!addedLast) tokens :+= sentenceTokens(i)

      val new_sentence = tokens.mkString(" ")
      new_sentence
    }).toArray
  }


  def flatten(crrFolder: File): Unit = {
    if (crrFolder.isFile) {

      val parentFile = new File(crrFolder.getParent)
      val parentFolder = parentFile.getParent
      if (!parentFolder.equals(sourceFolder)) {
        val targetFile = new File(parentFolder + "/" + crrFolder.getName)
        Files.move(crrFolder.toPath, targetFile.toPath, StandardCopyOption.REPLACE_EXISTING)
      }
    }
    else {
      val files = crrFolder.listFiles()
      files.foreach(file => flatten(file))
      if (files.isEmpty) {
        crrFolder.delete()
      }
    }
  }

  def extract(pw: PrintWriter, crrFolder: File): Unit = {
    val folders = crrFolder.listFiles().filter(f => !f.isFile);
    folders.foreach(file => extract(pw, file))
    val subFiles = crrFolder.listFiles()
    if (subFiles.isEmpty) crrFolder.delete()
    else {
      val files = subFiles.filter(f => f.isFile);

      var count = 0
      files.sliding(24, 24).foreach(fileSeq => {
        val sentences = fileSeq.par.flatMap(file => {
          val fpdf = file.getName.endsWith(".pdf")
          if (!fpdf) {
            file.delete()
            Seq[String]()
          }
          else {
            val lines = extractSentences(file)
            if (lines.isEmpty) file.delete()
            else println("Sentence count: " + lines.length)
            lines.toSeq
          }
        }).toArray
        sentences.foreach(line => {
          pw.println(line)
          count += 1
        })
      })
      println("Lines: " + count)
    }
  }

  def extractSentences(file: File): Array[String] = {

    println("Extracting : " + file.getName)
    var sentences = Array[String]()

    try {

      val parser = new PDFParser(new RandomAccessFile(file, "r"))
      parser.parse()
      val cosDoc = parser.getDocument()

      val pdDoc = new PDDocument(cosDoc)
      val pdPages = pdDoc.getPages.iterator();
      var parsed = ""

      while (pdPages.hasNext) {
        val page = pdPages.next()
        val bounds = page.getBBox
        val rectangle = new Rectangle(0, 50, bounds.getWidth.toInt, bounds.getHeight.toInt)
        val stripper = new PDFTextStripperByArea()
        stripper.addRegion("text", rectangle)
        stripper.extractRegions(page)
        parsed += " " + stripper.getTextForRegion("text")
      }

      parsed = parsed.replaceAll("\n", " ")
      parsed = parsed.replaceAll("\\s+", " ")
      pdDoc.close()
      cosDoc.close()
      sentences = XMLExtractor.extractSentences(parsed)
    }
    catch {
      case e: Exception => println("Error in pdf file: " + file.getName)
    }
    sentences
  }

}

object PDFExtractor {
  val existingExtraction = "resources/sentences/novel-sentences-tr.txt"
  val fixExtraction = "resources/sentences/novel-fix-sentences-tr.txt"

  def test(): Unit = {
    val extractor = new PDFExtractor("/home/wolf/Downloads/Okuma Kitaplari/", existingExtraction)
    println(extractor.sentenceClean("\"Leydim\" F'lar isminin söylenmesini bekleye\u00AD rek duraksadı. 27 Kadın endişelice Lorduna göz attı."))
    println(extractor.sentenceClean("\"Hızlı büyüyorsun,\" diye alaycı ve hassas bir yıl\u00AD gıyla ekledi. 161 Ramoth felaket kaşındığını tekrar söyledi."))
  }

  def main(args: Array[String]): Unit = {
    val extractor = new PDFExtractor("/home/wolf/Downloads/Okuma Kitaplari/", existingExtraction)
    //extractor.fix(fixExtraction)
    test()
  }
}
