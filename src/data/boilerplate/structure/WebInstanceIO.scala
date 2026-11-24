package data.boilerplate.structure

import java.io.{File, FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream}
import data.boilerplate.learning.LearningParams
import data.boilerplate.learning.graph.{GraphBuilder, Node}
import data.boilerplate.learning.pipes.{PipeOp, PipeResult}


import scala.collection.parallel.CollectionConverters.ArrayIsParallelizable

/**
 * @author Volkan Agun
 */
class WebInstanceIO(val htmlFolder: String, val xmlFolders: Array[String], val validTags: Array[String], val limit:Int = 5) extends Serializable {

  var binaryID = "resources/binary/instance-" + buildID().toString + ".bin"
  var readFile = false
  var instanceIter: Iterator[WebInstance] = null
  val model = GraphBuilder()

  def buildID(): Int = {
    var r = 7
    r = 3 * r + htmlFolder.hashCode
    r = hashCode(r, xmlFolders)
    r = hashCode(r, validTags)
    r
  }

  def hashCode(main: Int, array: Array[String]): Int = {
    var r = main
    array.foreach(item => {
      r = r * 3 + item.hashCode
    })

    r
  }

  def loadInstances(): Array[WebInstance] = {
    val f = new File(binaryID)
    if (f.exists() && readFile) {
      val reader = new ObjectInputStream(new FileInputStream(f));
      val count = reader.readInt()
      var instances = Array[WebInstance]()

      for (i <- 0 until count) {
        val webInstance = new WebInstance(null, null, Array()).read(reader)
        instances :+= webInstance
      }

      reader.close()
      instances
    }
    else if (!f.exists() && readFile) {
      val instances = build()
      val writer = new ObjectOutputStream(new FileOutputStream(f, false))
      writer.writeInt(instances.length)
      instances.foreach(instance => instance.write(writer))
      writer.close()
      instances
    }
    else {
      val instances = build()
      instances
    }
  }

  def build(): Array[WebInstance] = {
    val htmlFilenames = new File(htmlFolder).list().take(limit)
    val xmlFilenames = xmlFolders.flatMap(folderName => new File(folderName).list().map(fname => folderName + fname))

    val instanceList = htmlFilenames.par.map(htmlName => {
        val isfound = xmlFilenames.find(xmlName => xmlName.contains(htmlName))
        isfound match {
          case Some(xmlName) => Some(new WebInstance(htmlFolder + htmlName, xmlName, validTags).build())
          case None => None
        }
      }).flatten.toArray
      .filter(instance=> instance.valid)


    instanceList
  }

  def fit(): Array[Node] = {


    var array = Array[PipeResult]()
    var crr = 0

    val arr = loadInstances().iterator

    var cnt = 0
    var mainArray = Array[PipeResult]()
    while (arr.hasNext && cnt < LearningParams.batchSize) {
      cnt += 1
      val webInstance = arr.next()
      val htmlPaths = webInstance.paths.map { case (path) => {
        path
      }}.sortBy(_.depth())

      model.score(htmlPaths)
    }

    Array()

  }
}
