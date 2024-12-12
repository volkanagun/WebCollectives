package data.boilerplate.partitioning

import tagging.hmm.SequenceHMM
import tagging.lemmatizer.RegexTokenizer

import scala.collection.parallel.CollectionConverters.ArrayIsParallelizable
import scala.io.Source

object SentenceExtractor {

  val binaryFilename = "resources/binary/sentence.bin"
  val maxConfidenceLength = 120
  val maxSamples = 5000
  val sequenceHMM = new SequenceHMM().load(binaryFilename)
  val tokenizer = RegexTokenizer()

  def tokenize(sentenceLine:String):Array[String]={
    tokenizer.process(sentenceLine).map(wordSpan => sentenceLine.substring(wordSpan.start, wordSpan.end))
  }

  def train(tokens:Array[String]): Unit = {
    val labels = (0 until tokens.length).toArray.map(_=> "OTHER")
    labels(labels.length-1) = sequenceHMM.end
    sequenceHMM.train(tokens, labels)
  }

  def train(filename:String): Unit = {
    Source.fromFile(filename).getLines().filter(_.length < maxConfidenceLength).take(maxSamples).toArray
      .par.map(sentenceLine => tokenize(sentenceLine))
      .toArray.foreach(tokens=> {
        train(tokens)
      })

    sequenceHMM.normalize().save(binaryFilename)
  }

  def train(): Unit = {
    val sentenceFilename = "resources/sentences/sentences-april-v2-tr.txt"
    train(sentenceFilename)
  }

  def partition(text:String):Array[String]={
    val tokens = tokenize(text)
    sequenceHMM.infer(tokens)
  }

  def main(args: Array[String]): Unit = {
    train()
    partition("Deneme yapalim mi diye sordu Ali.").foreach(println)
  }
}
