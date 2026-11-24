package data.boilerplate.learning.features

import data.boilerplate.learning.pipes.IntermediateResult
import data.boilerplate.structure.HTMLNode

class ChildTagAverageOp(tagName:String) extends ChildTagCountOp(tagName+"avg") {
  override def execute(leaf: HTMLNode): IntermediateResult = {
    IntermediateResult(leaf, Map(name -> depthFirst(leaf.node, tagName)/depthAll(leaf.node)))
  }
}
