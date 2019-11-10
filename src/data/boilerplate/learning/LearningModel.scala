package data.boilerplate.learning

import java.io.File

import data.boilerplate.learning.pipes.{CSSAttributeOp, PipeOp, TagNameOp, TextDensityOp, TokenCountOp, TokenHTMLDensityOp}
import data.boilerplate.structure.WebInstanceIO
import data.crawler.web.LookupOptions
import org.bytedeco.opencv.opencv_dnn.ConvolutionLayer
import org.deeplearning4j.nn.conf.{ComputationGraphConfiguration, NeuralNetConfiguration, layers}
import org.deeplearning4j.nn.conf.layers.recurrent.Bidirectional
import org.deeplearning4j.nn.conf.layers.{LSTM, RnnOutputLayer}
import org.deeplearning4j.nn.graph.ComputationGraph
import org.deeplearning4j.nn.weights.WeightInit
import org.deeplearning4j.optimize.listeners.PerformanceListener
import org.deeplearning4j.util.ModelSerializer
import org.nd4j.linalg.activations.Activation
import org.nd4j.linalg.learning.config.{AdaGrad, Adam}
import org.nd4j.linalg.lossfunctions.LossFunctions

object LearningModel extends Serializable {

  val featureModel = FeatureParams.buildPipeOp()
  val iterator = new WebInstanceIO(DirectoryParams.htmlFolder,Array(DirectoryParams.blogsTRFolder, DirectoryParams.articleTRFolder), Array())
    .iterator(featureModel)

  def loadGraph(): ComputationGraph = {
    val modelFilename = UniqueParams.modelFilename()
    println(s"Loading graph model from ${modelFilename}")
    if (new File(modelFilename).exists())
      ModelSerializer.restoreComputationGraph(new File(modelFilename))
    else null
  }

  def saveGraph(model: ComputationGraph): this.type = {
    val modelFilename = UniqueParams.modelFilename()
    val modelFile = new File(modelFilename)
    println(s"Saving graph model to ${modelFilename}")
    modelFile.getParentFile.mkdirs()
    modelFile.delete()
    ModelSerializer.writeModel(model, new File(modelFilename), true)
    this
  }

  def loadGraphModel(typ:String)={
    val modelGraph = loadGraph()
    if (modelGraph == null) {
      val modelGraphConf = computationalModel(typ)
      modelGraphConf.setEpochCount(LearningParams.epocs)

      val c = new ComputationGraph(modelGraphConf)
      c.init()

      c
    }
    else {
      modelGraph
    }
  }

  def computationalModel(typex:String):ComputationGraphConfiguration={
    val conf = new NeuralNetConfiguration.Builder().seed(12345)
      .l2(0.001) //l2 regularization on all layers
      .updater(new Adam.Builder().learningRate(0.004).build())
      .graphBuilder()
      .addInputs("input")
      .layer("0", new Bidirectional(Bidirectional.Mode.AVERAGE, new LSTM.Builder().name("lstm").weightInit(WeightInit.XAVIER).nIn(LearningParams.vocabSize)
        .nOut(LearningParams.hiddenSize).activation(Activation.TANH).build()), "input")
      .layer("1", new RnnOutputLayer.Builder(LossFunctions.LossFunction.MCXENT).nIn(LearningParams.hiddenSize).nOut(LearningParams.labelSize)
        .activation(Activation.SOFTMAX).build(), "0")
      .setOutputs("1")

    conf.build()
  }

  def train(): Unit ={
    val learningModel = loadGraphModel("")
    learningModel.addListeners(new PerformanceListener(1, true))
    for(epoci <- 1 to LearningParams.epocs) {
      println(s"Epoc: ${epoci}")
      learningModel.fit(iterator)
      saveGraph(learningModel)
      iterator.reset()
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
