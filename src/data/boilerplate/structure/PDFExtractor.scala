package data.boilerplate.structure

import org.apache.pdfbox.cos.COSDocument
import org.apache.pdfbox.io.{RandomAccessFile, RandomAccessInputStream, RandomAccessRead}
import org.apache.pdfbox.pdfparser.PDFParser
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.text.{PDFTextStripper, PDFTextStripperByArea}

import java.awt.Rectangle
import java.io.{File, FileOutputStream, PrintWriter}
import scala.collection.parallel.CollectionConverters.ArrayIsParallelizable

class PDFExtractor(val sourceFolder: String, val destinationFile: String) {

  def extract(): Unit = {
    val pw = new PrintWriter(new FileOutputStream(destinationFile, false))
    extract(pw, new File(sourceFolder))
    pw.close()
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

  def main(args: Array[String]): Unit = {
    val extractor = new PDFExtractor("/home/wolf/Downloads/Okuma Kitaplari/", "resources/sentences/novel-sentences-tr.txt")
    extractor.extract()
  }
}
