package data.boilerplate

import javax.swing.text.html.parser.DocumentParser

class WebInstance(val htmlFile:String, val xmlFile:String) extends Serializable {

  var extractions = Array("title","date","body", "author", "genre")
  var extractionText = Map[String, String]()

  def setExtractions(array:Array[String]):this.type ={
    this.extractions = array
    this
  }

  protected def parseXML():this.type ={
    this.extractionText = XMLParser.parse(xmlFile)
    this
  }

  protected def matching():this.type ={
    //parse html and map the tree path (including features) to corresponding extraction text
    this
  }


}


