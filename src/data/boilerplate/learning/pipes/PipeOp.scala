package data.boilerplate.learning.pipes

import data.boilerplate.structure.HTMLNode

/**
 * @author Volkan Agun
 */
//The goal is to construct a pipeline feature extractor from tree paths
class PipeOp(var subpipes:Array[PipeOp], var name:String) extends Serializable {

  def this() = {
    this(Array(),"ROOT")
  }

  def sum(item:PipeOp):PipeOp = ???
  //generate a count operations class
  //diverge to different classes for operations build a tree like structure
  def count(item:PipeOp):PipeOp = {
    subpipes :+= CountOp(items(item))
    this
  }
  def max(item:PipeOp):PipeOp= ???
  def pattern(regexPattern:String):PipeOp= ???
  def ++(pipe: PipeOp):PipeOp= ???
  def exists(pipeOperations: PipeOp):PipeOp= ???
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

case class IntermediateResult(var map:Map[String, Double]) extends PipeResult {

}

abstract class ExecutableOp(pipes:Array[PipeOp], name:String) extends PipeOp(pipes, name){

  override def sum(item: PipeOp): PipeOp = ???

  override def max(item: PipeOp): PipeOp = ???

  override def pattern(regexPattern: String): PipeOp = ???

  override def ++(pipe: PipeOp): PipeOp = ???

  override def exists(pipeOperations: PipeOp): PipeOp = ???

  override def operate(): PipeResult = ???

  def sequence(leafSequence: Array[HTMLNode]): Array[IntermediateResult] = {
    leafSequence.map(leafNode=> single(leafNode))
  }

  def single(leaf:HTMLNode):IntermediateResult


}

abstract class ExistOp(subpipes:Array[PipeOp], name:String) extends ExecutableOp(subpipes, name){

  def exists(leaf:HTMLNode):Double

  override def single(leaf:HTMLNode):IntermediateResult = {
    val pairs = subpipes.map(pipe=> pipe match {
      case e:ExistOp => (pipe.name , e.exists(leaf))
      case _ => (pipe.name, 0.0)
    }).toMap

    IntermediateResult(pairs)
  }

}

case class PatternOp(regex:String) extends ExistOp(Array(), "pattern-op"){

  def exists(regex:String):PatternOp={
    PatternOp(regex)
  }

  override def single(leaf: HTMLNode): IntermediateResult = ???

  override def exists(leaf: HTMLNode): Double = ???
}

case class CountOp(pipes:Array[PipeOp]) extends ExecutableOp(pipes, "count-op"){

  override def single(leaf: HTMLNode): IntermediateResult = {
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
    PipeOp.count(PatternOp("[ab]"))
      .exists(PatternOp("[p]"))
      .sum(PipeOp.exists(PatternOp("[p]")))
  }
}
