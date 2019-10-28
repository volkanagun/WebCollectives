package data.boilerplate.structure

import java.io.File

import org.nd4j.linalg.dataset.api.iterator.MultiDataSetIterator

/**
 * @author Volkan Agun
 */
class WebInstanceIO(val htmlFolder:String, val xmlFolders:Array[String], val validTags:Array[String]) extends Serializable {

  def build():Array[WebInstance] = {
    val htmlFilenames = new File(htmlFolder).list()
    val xmlFilenames = xmlFolders.map(folderName=> new File(folderName).list())
    htmlFilenames.map(htmlName=> {
      xmlFilenames.find(xmlName=> xmlName.contains(htmlName)) match{
        case Some(xmlName) => Some(new WebInstance(htmlName, xmlName.head, validTags).build())
        case None => None
      }
    }).flatten
  }

  def iterator(batchSize:Int):MultiDataSetIterator={
    null
  }
}
