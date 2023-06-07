package data.boilerplate.learning.features

import data.boilerplate.learning.pipes.{IntermediateResult, PipeOp}
import data.boilerplate.structure.HTMLNode

case class ChildTagCountOp(tagName:String) extends PipeOp(Array(), "child-tag-"+tagName){
  override def canApply(htmlNode: HTMLNode): Boolean = true

  override def execute(leaf: HTMLNode): IntermediateResult = {
    IntermediateResult(leaf, Map(name -> depthFirst(leaf.node, tagName)))
  }


}
