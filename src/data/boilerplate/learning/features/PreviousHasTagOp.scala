package data.boilerplate.learning.features

import data.boilerplate.learning.pipes.{IntermediateResult, PatternOp, PipeOp}
import data.boilerplate.structure.{HTMLNode, HTMLPath}

case class PreviousHasTagOp(tagNameRegex:String) extends PatternOp(Array(), "previous-has-"+tagNameRegex){
  override def execute(leaf: HTMLNode): IntermediateResult = {
    val crrName = leaf.node.nodeName()
    val count = 0
    val ndo = leaf
    if (leaf.hasPrevious()) {
      val precheck = leaf.previous.node.nodeName().matches(tagNameRegex)
      val previousHas = leaf.previous.childnodes.exists(subnode=> subnode.node.nodeName().matches(tagNameRegex))
      if (precheck || previousHas) {
        IntermediateResult(ndo, Map(name -> 1d))
      }
      else {
        IntermediateResult(ndo, Map(name -> 0d))
      }
    }
    else {
      IntermediateResult(ndo, Map(name -> 0d))
    }
  }

  override def execute(path: HTMLPath): IntermediateResult = {
    execute(path.pathNodes.last)
  }
}
