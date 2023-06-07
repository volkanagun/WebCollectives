package data.boilerplate.learning.features

import data.boilerplate.learning.pipes.{IntermediateResult, PatternOp}
import data.boilerplate.structure.{HTMLNode, HTMLPath}

case class SiblingSameTag() extends PatternOp(Array(), "same-tag") {

  override def execute(leaf: HTMLNode): IntermediateResult = {
    val crrName = leaf.node.nodeName()
    val count = 0
    val ndo = leaf
    if(leaf.hasNext() && leaf.hasPrevious()){
      val precheck = leaf.previous.node.nodeName().equals(crrName)
      val nextcheck = leaf.next.node.nodeName().equals(crrName)
      if(precheck && nextcheck) {IntermediateResult(ndo,Map(name -> 1d))}
      else {IntermediateResult(ndo)}
    }
    else if(leaf.hasNext()){
      val nextcheck = leaf.next.node.nodeName().equals(crrName)
      if(nextcheck) IntermediateResult(ndo, Map(name -> 1d))
      else IntermediateResult(ndo)
    }
    else if(leaf.hasPrevious()){
      val precheck = leaf.previous.node.nodeName().equals(crrName)
      if(precheck) IntermediateResult(ndo, Map(name -> 1d))
      else IntermediateResult(ndo)
    }
    else{
      IntermediateResult(ndo)
    }
  }

  override def execute(path: HTMLPath): IntermediateResult = {
    execute(path.pathNodes.last)
  }
}
