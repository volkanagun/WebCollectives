package data.boilerplate.learning.features

import data.boilerplate.learning.pipes.{IntermediateResult, PatternOp}
import data.boilerplate.structure.{HTMLNode, HTMLPath}

case class NextHasTagOp(val tagNameRegex:String) extends PatternOp(Array(), "next-has-"+tagNameRegex) {

  override def execute(leaf: HTMLNode): IntermediateResult = {
    val crrName = leaf.node.nodeName()
    val count = 0
    val ndo = leaf

    if (leaf.hasNext()) {
      val nextcheck = leaf.getNextSiblings().exists(node => node.tagName().matches(tagNameRegex))
      val nextHas = leaf.next.hasTag(tagNameRegex)
      val parentHas = leaf.parent.hasNext() && leaf.parent.next.hasTag(tagNameRegex)

      if (nextcheck || nextHas || parentHas) {
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
