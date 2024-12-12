package tagging.hmm

import tagging.lemmatizer.Table

import java.io.{File, FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream}
import scala.util.control.Breaks

class SequenceHMM() extends Serializable {


  val tableSym:Table = Table()
  val tableLabel:Table = Table()
  val tableSymLabel:Table = Table()
  var syms:Set[Probability] = Set()
  var lbs:Set[Probability] = Set()
  var eps = 0.00000000001d
  var dummy = "dummy"
  var start = "start"
  var end = "end"
  var labelMarker = "##"


  def merge(other:SequenceHMM):SequenceHMM = {
    other.syms.foreach(probability=>{
      if(syms.contains(probability)){
        val sym = syms.find(prob=> prob.equals(probability)).get
        sym.inc(probability.count)
      }
      else{
        syms = syms + probability
      }
    })

    other.lbs.foreach(probability=>{
      if(lbs.contains(probability)){
        val sym = lbs.find(prob => probability.equals(prob)).get
        sym.inc(probability.count)
      }
      else{
        lbs = lbs + probability
      }
    })

    tableSym.merge(other.tableSym)
    tableSymLabel.merge(other.tableSymLabel)
    tableLabel.merge(other.tableLabel)

    this
  }

  def save(filename:String):SequenceHMM = {
    println("Saving HMM")
    val stream = new ObjectOutputStream(new FileOutputStream(filename))

    stream.writeDouble(eps)
    stream.writeObject(dummy)
    stream.writeObject(start)
    stream.writeObject(end)
    stream.writeObject(labelMarker)

    stream.writeInt(syms.size)
    syms.foreach(sym=> sym.save(stream))
    stream.writeInt(lbs.size)
    lbs.foreach(sym=> sym.save(stream))

    tableSym.save(stream)
    tableLabel.save(stream)
    tableSymLabel.save(stream)
    stream.close()
    this

  }
  def load(filename:String):SequenceHMM = {
    if(new File(filename).exists()) {
      println("Loading HMM")
      val stream = new ObjectInputStream(new FileInputStream(filename))
      eps = stream.readDouble();
      dummy = stream.readObject().asInstanceOf[String]
      start = stream.readObject().asInstanceOf[String]
      end = stream.readObject().asInstanceOf[String]
      labelMarker = stream.readObject().asInstanceOf[String]

      val sz1 = stream.readInt()
      for (i <- 0 until sz1) {
        syms += Probability(stream)
      }

      val sz2 = stream.readInt()
      for (i <- 0 until sz2) {
        lbs += Probability(stream)
      }

      tableSym.load(stream)
      tableLabel.load(stream)
      tableSymLabel.load(stream)
      stream.close()
    }
    this
  }

  def updateLabels(item:String):Probability = {
    val probability = new Probability(item, lbs.size)

    if(lbs.contains(probability))
    {
      lbs.find(prob=>prob.equals(probability)).get.inc()
    }
    else{
      lbs = lbs + probability.inc()
      probability
    }

  }

  def updateSyms(item:String):Probability = {
    val probability = new Probability(item, syms.size)

    if(syms.contains(probability))
    {
      syms.find(prob=>prob.equals(probability)).get.inc()
    }
    else{
      syms = syms + probability.inc()
      probability
    }
  }

  def getSyms(item:String):Probability = {
    val probability = new Probability(item, syms.size, 1L, eps)

    if(syms.contains(probability))
    {
      syms.find(prob=> probability.equals(prob)).get
    }
    else{
      probability
    }

  }

  def getLabel(item:String):Probability = {

    val probability = new Probability(item, lbs.size, 1L, eps)

    if(lbs.contains(probability))
    {
      lbs.find(prob=> prob.equals(probability)).get
    }
    else{
      probability
    }

  }


  def train(tokens:Array[String], labels:Array[String]):SequenceHMM = {
    //update probabilities
    val output = dummy +: tokens
    val cats = start +: labels
    output.zip(cats).sliding(2, 1).foreach(array => {

      val (item1, label1) = array.head
      val (item2, label2) = array.last

      val in1 = updateSyms(item1)
      val in2 = updateSyms(item2)

      val lb1 = updateLabels(label1)
      val lb2 = updateLabels(label2)

      tableSym.update(in1.index, in2.index)
      tableSymLabel.update(in1.index, lb1.index)
      tableSymLabel.update(in2.index, lb2.index)
      tableLabel.update(lb1.index, lb2.index)

    })

    this
  }

  def normalize():SequenceHMM={
    val sumSym = syms.map(_.count).sum
    val sumLbs = lbs.map(_.count).sum

    syms.foreach(p => p.normalize(sumSym))
    lbs.foreach(p => p.normalize(sumLbs))

    tableSym.normalize(syms)
    tableSymLabel.normalize(syms, lbs)
    tableLabel.normalize(lbs)
    this
  }



  def minimum(predictions: Map[Pair, (Int, Double)], index:Int):Int = {
    val minimumPair = predictions.filter(pair=> pair._1.item == index).minBy(_._2._2)
    minimumPair._1.item2
  }

  def inferByTokens(tokens:Array[String], items:Array[(String, String)]):Array[String]={


    val results = infer(items.map(_._2))
    val tokenRepeats = items.map(_._1)
    val tokenResults = tokenRepeats.zip(results)
    val tokenPairs = tokenResults.map(pair=> (pair._1 , pair._2.split(labelMarker)(1)))
    var finalResult = Array[String]()
    var j = 0
    var i = 0
    while(i<tokens.length && j<tokenPairs.length){
      val crrToken = tokens(i)
      var oldPair = tokenPairs(j)
      val break = Breaks
      break.breakable {
        while (j < tokenPairs.length) {
          val newPair = tokenPairs(j)
          val newToken = newPair._1

          if (newToken.equals(crrToken)) {
            oldPair = newPair
            j+=1
          }
          else {
            break.break()
          }
        }
      }

      i+=1
      finalResult :+= crrToken + labelMarker + oldPair._2

    }

    finalResult
  }

  def infer(items:Array[String]):Array[String] = {

    val outputs =  dummy +:  items
    val dummyStart = getSyms(dummy)
    val startLabel = getLabel(start)
    var predictions = Map[Pair, (Int, Double)](Pair(0, startLabel.index) -> (startLabel.index, 0d))

    //Construct
    outputs.sliding(2, 1).zipWithIndex.toArray.foreach {
      case(array, t) => {

      val tt = t + 1

      val currentOutput = getSyms(array.last)
      val currentObservationLog = currentOutput.negLog()

      lbs.foreach {
          clabel => {
          val nextOutputLog = tableSymLabel.get(currentOutput.index, clabel.index).negLog()
          val nextProbabilityLog = currentObservationLog + nextOutputLog

          val minLabel = lbs.map{ plabel => {
            val previousScore = predictions.getOrElse(Pair(t, plabel.index), (dummyStart.index, 0d))._2
            val aij = tableLabel.get(plabel.index, clabel.index).negLog()
            val newScore = nextProbabilityLog + previousScore +  aij
            (plabel.index, newScore)
          }}.minBy(_._2)

          val pair = Pair(tt, clabel.index)
          predictions = predictions + (pair -> minLabel)
        }
      }}}


    //Decode
    var minLabelIndex  = minimum(predictions, outputs.length - 1)
    var result = Array[String]()
    outputs.zipWithIndex.tail.reverse.foreach{case(item, index)=> {
       val maximum = lbs.find(prob=> prob.index == minLabelIndex).get
       val sout = item + labelMarker + maximum.item
       result = result :+ sout
       minLabelIndex = predictions(Pair(index, minLabelIndex))._1
    }}

     result.reverse
    }

}

object SequenceHMM {


  def main(args: Array[String]): Unit = {

    new SequenceHMM()


  }
}

