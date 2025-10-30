package data.boilerplate.structure

import java.io.{File, FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream}
import data.boilerplate.learning.LearningParams
import data.boilerplate.learning.pipes.{PipeOp, PipeResult}
import org.nd4j.linalg.dataset
import org.nd4j.linalg.dataset.api.{MultiDataSet, MultiDataSetPreProcessor}
import org.nd4j.linalg.dataset.api.iterator.MultiDataSetIterator
import org.nd4j.linalg.factory.Nd4j

import scala.collection.parallel.CollectionConverters.ArrayIsParallelizable

/**
 * @author Volkan Agun
 */
class WebInstanceIO(val htmlFolder: String, val xmlFolders: Array[String], val validTags: Array[String]) extends Serializable {

  var binaryID = "resources/binary/instance-" + buildID().toString + ".bin"
  var readFile = false
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

      for (i<-0 until count){
        val webInstance = new WebInstance(null, null, Array()).read(reader)
        instances :+= webInstance
      }

      reader.close()
      instances
    }
    else if(!f.exists() && readFile){
      val instances = build()
      val writer = new ObjectOutputStream(new FileOutputStream(f, false))
      writer.writeInt(instances.length)
      instances.foreach(instance=> instance.write(writer))
      writer.close()
      instances
    }
    else{
      val instances = build()
      instances
    }
  }

  def build(): Array[WebInstance] = {
    val htmlFilenames = new File(htmlFolder).list()
    val xmlFilenames = xmlFolders.flatMap(folderName => new File(folderName).list().map(fname => folderName + fname))

    htmlFilenames.par.map(htmlName => {
      val isfound = xmlFilenames.find(xmlName => xmlName.contains(htmlName))
      isfound match {
        case Some(xmlName) => Some(new WebInstance(htmlFolder + htmlName, xmlName, validTags).build())
        case None => None
      }
    }).flatten.toArray
      .filter(_.valid)
  }

  def iterator(pipeOp: PipeOp): MultiDataSetIterator = {


    new MultiDataSetIterator {
      var crr = 0
      var pipeResult = new PipeResult(LearningParams.vocabSize, LearningParams.labelSize)
      val arr = loadInstances()
      var iter = arr.toIterator

      override def next(i: Int): MultiDataSet = {
        var cnt = 0
        var mainArray = Array[(Array[(Int, Double)], Array[Int])]()
        while (hasNext && cnt < LearningParams.batchSize) {
          cnt += 1
          val webInstance = iter.next()
          mainArray ++= webInstance.pathLabels.take(LearningParams.windowLength).map { case (path, label) => {
            (pipeResult.append(pipeOp.execute(path)), pipeResult.append(label))
          }
          }
        }

        val input = Nd4j.create(mainArray.length, pipeResult.vocabSize, LearningParams.windowLength)
        val output = Nd4j.create(mainArray.length, LearningParams.windowLength)

        mainArray.zipWithIndex.foreach { case ((inputArray, outputArray), batchIndex) => {

          inputArray.zipWithIndex.foreach { case ((index, value), windex) => {
            input.putScalar(Array(batchIndex, index, windex), value)
          }
          }

          outputArray.zipWithIndex.foreach { case (lindex, windex) => {
            output.putScalar(Array(batchIndex, windex), lindex)
          }
          }

        }
        }

        new dataset.MultiDataSet(input, output)

      }

      override def setPreProcessor(multiDataSetPreProcessor: MultiDataSetPreProcessor): Unit = ???

      override def getPreProcessor: MultiDataSetPreProcessor = ???

      override def resetSupported(): Boolean = true

      override def asyncSupported(): Boolean = true

      override def reset(): Unit = {
        crr = 0
        iter = arr.toIterator
      }

      override def hasNext: Boolean = iter.hasNext

      override def next(): MultiDataSet = {
        next(0)
      }
    }
  }
}
