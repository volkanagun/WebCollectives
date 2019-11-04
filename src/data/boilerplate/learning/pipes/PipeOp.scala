package data.boilerplate.learning.pipes

import data.boilerplate.structure.HTMLNode
import org.jsoup.nodes.{Document, Element, TextNode}

/**
 * @author Volkan Agun
 */
//The goal is to construct a pipeline feature extractor from tree paths
class PipeOp(var subpipes:Array[PipeOp], var name:String) extends Serializable {

  def this() = {
    this(Array(),"ROOT")
  }

  def op(pipeOp: PipeOp):this.type ={
    subpipes :+= pipeOp
    this
  }

  def sum(item:PipeOp):PipeOp = ???
  //generate a count operations class
  //diverge to different classes for operations build a tree like structure
  def count(item:PipeOp):PipeOp = {
    op(CountOp(items(item)).asInstanceOf[PipeOp])
    this
  }
  def max(item:PipeOp):PipeOp= ???
  def pattern(regexPattern:String):PipeOp= {
    op(HTMLPatternOp(regexPattern))
  }
  def ++(pipe: PipeOp):PipeOp= ???
  def exists(pipeOperations: PipeOp):PipeOp= {
    op(new ExistOp(items(pipeOperations)))
  }
  def operate():PipeResult= ???

  def items(item:PipeOp):Array[PipeOp]={
    item.newName(this)
    item.subpipes = item.subpipes.map(innerItem => innerItem.newName(item))
    Array(item)
  }

  def newName(parent:PipeOp):PipeOp={
    this.name = parent.name + "#" + this.name
    this
  }

  //operations
  def sum(irs:Array[IntermediateResult]):IntermediateResult={
    var gmap = Map[String, Double]()
    irs.foreach(ir=>{
      ir.map.foreach{case(nn, score)=> {
        gmap = gmap.updated(nn, gmap.getOrElse(nn, 0.0)+score)
      }}
    })

    IntermediateResult(gmap)
  }

  def normalize(irs:Array[IntermediateResult]):IntermediateResult={
    var gmap = Map[String, Double]()
    irs.foreach(ir=>{
      ir.map.foreach{case(nn, score)=> {
        gmap = gmap.updated(nn, score)
      }}
    })

    IntermediateResult(gmap)
  }

}

object PipeOp extends PipeOp(){
  def apply():PipeOp= {
    new PipeOp()
  }

  override def count(item:PipeOp)={
    super.count(item)
  }
}


class PipeResult extends Serializable{

}

case class IntermediateResult(var map:Map[String, Double] = Map()) extends PipeResult {

  def divide(by:Double):IntermediateResult={
    map = map.map{case(item, score)=> (item, score/by)}
    this
  }
}

abstract class ExecutableOp(pipes:Array[PipeOp], name:String) extends PipeOp(pipes, name){

  override def sum(item: PipeOp): PipeOp = ???

  override def max(item: PipeOp): PipeOp = ???

  override def pattern(regexPattern: String): PipeOp = ???

  override def ++(pipe: PipeOp): PipeOp = ???

  override def exists(pipeOperations: PipeOp): PipeOp = ???

  override def operate(): PipeResult = ???

  def sequence(leafSequence: Array[HTMLNode]): Array[IntermediateResult] = {
    leafSequence.map(leafNode=> execute(leafNode))
  }

  def execute(leaf:HTMLNode):IntermediateResult

}

class ExistOp(subpipes:Array[PipeOp], name:String) extends ExecutableOp(subpipes, name){

  def this(subpipes:Array[PipeOp]) = this(subpipes, "exist-op")



  def exists(leaf:HTMLNode):Double={
    val boolean = subpipes.map(pipe=> pipe match {
      case e:ExistOp => (pipe.name , e.exists(leaf))
      case _ => (pipe.name, 0.0)
    }).exists(_._2 > 0)

    if(boolean) 1.0 else 0.0

  }

  override def execute(leaf:HTMLNode):IntermediateResult = {
    val pairs = subpipes.map(pipe=> pipe match {
      case e:ExistOp => (pipe.name , e.exists(leaf))
      case _ => (pipe.name, 0.0)
    }).toMap

    IntermediateResult(pairs)
  }
}


class AvgOp(subpipes:Array[PipeOp], name:String) extends ExecutableOp(subpipes, name){

  def this(subpipes:Array[PipeOp]) = this(subpipes, "exist-op")


  override def execute(leaf:HTMLNode):IntermediateResult = {
    val pairs = subpipes.map(pipe=> pipe match {
      case e:AvgOp => Some((pipe.name , e.execute(leaf)))
      case _ => None
    }).flatten

    val size = pairs.length
    val iirss = pairs.map{case(name, ir)=> ir.divide(size)}
    normalize(iirss)
  }
}

case class RecursivePatternOp(regex:String, subs:Array[PipeOp]) extends ExistOp(subs, s"recursive-op[${regex}]"){

  //count the frequency inside all regex
  override def execute(leaf: HTMLNode): IntermediateResult = {
    val html = leaf.node.asInstanceOf[Element].html()
    val matches = regex.r.findAllIn(html)
    val irss = matches.map(matchString=>{
      val element = new HTMLNode(new Document(leaf.node.baseUri()).html(matchString))
      val counts = subs.map(subpipe=> {
        subpipe match {
          case e:ExistOp=> Some(e.execute(element))
          case _ => None
        }
      }).flatten

      normalize(counts)
    }).toArray

    sum(irss)
  }
}


case class HTMLPatternOp(regex:String) extends ExistOp(Array(), s"html-regex-op[${regex}]"){

  def exists(regex:String):HTMLPatternOp={
    HTMLPatternOp(regex)
  }

  //count the frequency
  override def execute(leaf: HTMLNode): IntermediateResult = {
    val html = leaf.node.asInstanceOf[Element].html()
    val matches = regex.r.findAllIn(html)
    val map = Map(name -> matches.length.toDouble)
    IntermediateResult(map)
  }

  //boolean contains
  override def exists(leaf: HTMLNode): Double = {
    val html = leaf.node.asInstanceOf[Element].html()
    val matches = regex.r.findAllIn(html)
    if(matches.length > 0) 1.0 else 0.0
  }

}

case class CountOp(pipes:Array[PipeOp]) extends ExecutableOp(pipes, "count-op"){

  override def execute(leaf: HTMLNode): IntermediateResult = {
    val map = subpipes.map(op=> {
      op match {
        case patternOp:ExistOp => {
          (patternOp.name, patternOp.exists(leaf))
        }
        case _ => (op.name, 0.0)
      }
    }).groupBy(_._1).map(pair=> (pair._1, pair._2.map(_._2).sum))

    IntermediateResult(map)
  }
}

object OpTester{
  def main(args: Array[String]): Unit = {
    PipeOp.count(HTMLPatternOp("[ab]"))
      .exists(HTMLPatternOp("[p]"))
      .sum(PipeOp.exists(HTMLPatternOp("[p]")))
  }
}
