package data.boilerplate.structure

import java.io.{ObjectInputStream, ObjectOutputStream}

class WebInstance(var htmlFile: String, var xmlFile: String, var xmlLabels: Array[String]) extends Serializable {

  var extractionText = Map[String, String]()
  var pathLabels = Array[(HTMLPath, String)]()
  var paths = Array[HTMLPath]()
  var similarityThreshold = 0.8d
  var valid = true

  var labelNONE = "NONE"

  def setLabels(array: Array[String]): this.type = {
    this.xmlLabels = array
    this
  }

  def getLabels(): Array[String] = {
    xmlLabels
  }

  def write(objectOutput: ObjectOutputStream): this.type = {
    objectOutput.writeObject(htmlFile)
    objectOutput.writeObject(xmlFile)
    objectOutput.writeInt(xmlLabels.length)
    xmlLabels.foreach(label => objectOutput.writeObject(label))

    objectOutput.writeInt(extractionText.size)
    extractionText.foreach(pair => {
      objectOutput.writeObject(pair._1)
      objectOutput.writeObject(pair._2)
    })

    objectOutput.writeInt(pathLabels.size)
    pathLabels.foreach{case(path, str) => {
      path.write(objectOutput)
      objectOutput.writeObject(str)
    }}

    objectOutput.writeInt(paths.size)
    paths.foreach{case(path) => {
      path.write(objectOutput)
    }}

    this

  }

  def read(stream: ObjectInputStream): this.type = {
    htmlFile = stream.readObject().asInstanceOf[String]
    xmlFile = stream.readObject().asInstanceOf[String]

    var count = stream.readInt()
    for (i<-0 until count)
      xmlLabels :+= stream.readObject().asInstanceOf[String]

    count = stream.readInt()
    for (i<-0 until count) {
      extractionText += (stream.readObject().asInstanceOf[String] ->
        stream.readObject().asInstanceOf[String])
    }

    count = stream.readInt()
    for (i<-0 until count) {
      pathLabels :+= (new HTMLPath(Array()).read(stream),
        stream.readObject().asInstanceOf[String])
    }

    count = stream.readInt()
    for (i<-0 until count) {
      paths :+= new HTMLPath(Array()).read(stream)
    }

    this

  }

  protected def parseXML(): this.type = {
    this.extractionText = XMLParser.parse(xmlFile)
    this
  }

  protected def windowSimilarity(mainText: String, searchText: String): Double = {
    val scores = searchText.sliding(10, 10).map(randomized => mainText.contains(randomized)).map(item => if (item) 1.0 else 0.0)
    scores.sum / scores.length
  }

  protected def labelMatch(searchText: String): Option[String] = {
    val scores = extractionText.map { case (label, mainText) => {
      (label, windowSimilarity(mainText, searchText))
    }
    }.filter(_._2 > similarityThreshold)

    if (scores.isEmpty) Some(labelNONE)
    else Some(scores.maxBy(_._2)._1)
  }

  protected def labelMatch(searchText: Option[String]): Option[String] = {
    searchText match {
      case None => Some(labelNONE)
      case Some(text) => labelMatch(text)
    }
  }

  protected def matching(): this.type = {
    paths = HTMLParser.parseHTML(htmlFile).visit().map(path=> path.build())
    pathLabels = paths.map(path => (path, labelMatch(path.toTextString())))
      .filter(pair => pair._2.isDefined).map(pair => (pair._1, pair._2.get))
    this
  }

  def build(): this.type = {
    try {
      parseXML()
      matching()
    }
    catch {
      case e: Throwable => {
        valid = false
      }
    }
    this
  }


}


