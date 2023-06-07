package data.boilerplate.learning.features

import data.boilerplate.learning.pipes.{IntermediateResult, PipeOp}
import data.boilerplate.structure.{HTMLNode, HTMLPath}

case class PreviousByPattern(var patternOp : PipeOp ) extends PipeOp {

  def setOp(patternOp: PipeOp): this.type = {
    this.patternOp = patternOp
    this
  }

  override def execute(leaf: HTMLNode): IntermediateResult = {
    if(leaf.hasPrevious()) {
      val iirs = patternOp.execute(leaf.previous)
      sum(Array(iirs))
    }
    else{
      IntermediateResult(leaf)
    }
  }

  override def execute(path: HTMLPath): IntermediateResult = {
    val leaf = path.pathNodes.last

    if(leaf.hasPrevious()) {
      val iirs = patternOp.execute(leaf.previous)
      sum(Array(iirs))
    }
    else{
      IntermediateResult(leaf)
    }
  }

}
