package data.boilerplate.structure

import java.io.File

/**
 * @author Volkan Agun
 */
class WebInstanceReader(val htmlFolder:String, val xmlFolders:Array[String], val validTags:Array[String]) extends Serializable {

  def read():Array[WebInstance]={
    val htmlFilenames = new File(htmlFolder).list()
    val xmlFilenames = xmlFolders.flatMap(folderName=> new File(folderName).list())
    htmlFilenames.map(htmlName=> {
      xmlFilenames.find(xmlName=> xmlName.contains(htmlName)) match{
        case Some(xmlName)=> new WebInstance(htmlName, xmlName, validTags)
      }
    })
  }


}
