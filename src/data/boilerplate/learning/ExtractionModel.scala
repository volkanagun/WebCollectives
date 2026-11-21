package data.boilerplate.learning

import java.io.File
import data.boilerplate.learning.features.{CSSAttributeOp, TagNameOp, TextDensityOp, TokenCountOp, TokenHTMLDensityOp}
import data.boilerplate.learning.pipes.{PipeOp, PipeResult}
import data.boilerplate.structure.WebInstanceIO
import org.deeplearning4j.nn.conf.{ComputationGraphConfiguration, NeuralNetConfiguration, layers}
import org.deeplearning4j.nn.conf.layers.recurrent.Bidirectional
import org.deeplearning4j.nn.conf.layers.{LSTM, RnnOutputLayer}
import org.deeplearning4j.nn.graph.ComputationGraph
import org.deeplearning4j.nn.weights.WeightInit
import org.deeplearning4j.optimize.listeners.PerformanceListener
import org.deeplearning4j.util.ModelSerializer
import org.nd4j.linalg.activations.Activation
import org.nd4j.linalg.dataset.api.iterator.MultiDataSetIterator
import org.nd4j.linalg.learning.config.{AdaGrad, Adam}
import org.nd4j.linalg.lossfunctions.LossFunctions

object ExtractionModel extends Serializable {

  val featureModel = FeatureParams.buildPipeOp()
  val model = new WebInstanceIO(DirectoryParams.htmlFolder, Array[String](DirectoryParams.articleTRFolder), Array[String]())


  def train(): Unit ={

    for(epoci <- 1 to LearningParams.epocs) {
      println(s"Epoc: ${epoci}")
      model.fit()
    }
  }

  def main(args: Array[String]): Unit = {
    train()
  }
}



object UniqueParams{
  var modelFolder = "resources/models/"

  def modelFilename():String = {
    modelFolder + obtainModelID()+".bin";
  }

  def obtainModelID():String={
    var r = 1
    r = 7 * r + LearningParams.hashCode()
    r = 7 * r + FeatureParams.hashCode()
    r.toString
  }
}

object LearningParams{
  var lrate = 0.001
  var windowLength = 1000
  var vocabSize = 1000
  var labelSize = 10
  var hiddenSize = 50
  var batchSize = 32
  var epocs = 4

}

object DirectoryParams{
  val resources = "resources/"
  val htmlFolder = resources + "htmls/"
  val articleTRFolder = resources + "articles-turkish/"
  val blogsTRFolder = resources + "blogs-turkish/"
  val blogsENFolder = resources + "blogs-english/"
  val fanficsFolder = resources + "fanfics/"
  val sikayetVarFolder = resources + "sikayetvar/"
  val tweetsFolder = resources + "tweets/"
  val allfolders = Array(articleTRFolder, blogsTRFolder, blogsENFolder, fanficsFolder, sikayetVarFolder, tweetsFolder)
}

object FeatureParams
{
  var extractCSS = true
  var extractAttributes = true
  var tokenCount = true
  var wordDensity = true
  var textDensity = true
  var linkDensity = true
  var extractID = true
  var extractTagName = true


  def buildPipeOp():PipeOp={
    val mainOp = PipeOp()
    if(extractCSS)  mainOp.op(CSSAttributeOp())
    if(tokenCount) mainOp.op(TokenCountOp())
    if(wordDensity) mainOp.op(TokenHTMLDensityOp())
    if(textDensity) mainOp.op(TextDensityOp())
    if(extractTagName) mainOp.op(TagNameOp())

    mainOp
  }
}
