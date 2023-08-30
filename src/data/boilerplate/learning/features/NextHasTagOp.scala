package data.boilerplate.learning.features

import data.boilerplate.learning.pipes.{IntermediateResult, PatternOp}
import data.boilerplate.structure.{HTMLNode, HTMLPath}

case class NextHasTagOp(strname:String, val tagNameRegex:String) extends PatternOp(Array(), "next-has-"+strname) {

  override def execute(leaf: HTMLNode): IntermediateResult = {
    val crrName = leaf.node.nodeName()
    val count = 0
    val ndo = leaf

    if (leaf.hasNext()) {
      val nextcheck = leaf.next.node.nodeName().matches(tagNameRegex)
      val nextHas = leaf.next.childnodes.exists(subnode => subnode.node.nodeName().matches(tagNameRegex))
      if (nextcheck || nextHas) {
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
