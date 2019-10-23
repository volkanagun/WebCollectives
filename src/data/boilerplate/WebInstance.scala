package data.boilerplate

import javax.swing.text.html.parser.DocumentParser

class WebInstance(val htmlFile:String, val xmlFile:String, var xmlLabels:Array[String]) extends Serializable {

  var extractionText = Map[String, String]()
  var pathLabels = Array[(HTMLPath, String)]()
  var paths = Array[HTMLPath]()
  var similarityThreshold = 0.5

  var labelNONE = "NONE"
  def setLabels(array:Array[String]):this.type ={
    this.xmlLabels = array
    this
  }

  def getLabels():Array[String] = {
    xmlLabels
  }


  protected def parseXML():this.type = {
    this.extractionText = XMLParser.parse(xmlFile)
    this
  }

  protected def windowSimilarity(mainText:String, searchText:String):Double={
    val scores = searchText.sliding(10, 10).map(randomized=> mainText.contains(randomized)).map(item=> if(item) 1.0 else 0.0)
    scores.sum / scores.length
  }

  protected def labelMatch(searchText:String):Option[String]={
    val scores = extractionText.map{case(label, mainText)=> {
      (label, windowSimilarity(mainText, searchText))
    }}.filter(_._2>similarityThreshold)

    if(scores.isEmpty) Some(labelNONE)
    else Some(scores.maxBy(_._2)._1)
  }

  protected def labelMatch(searchText:Option[String]):Option[String]={
    searchText match {
      case None => Some(labelNONE)
      case Some(text)=> labelMatch(text)
    }
  }

  protected def matching():this.type ={
    paths = HTMLParser.parseHTML(htmlFile).visit()
    pathLabels = paths.map(path=> (path, labelMatch(path.toTextString())))
      .filter(pair=> pair._2.isDefined).map(pair=> (pair._1, pair._2.get))
    this
  }


}


