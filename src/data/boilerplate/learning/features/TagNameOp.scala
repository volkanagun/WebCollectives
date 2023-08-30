package data.boilerplate.learning.features

import data.boilerplate.learning.pipes.{ExecutableOp, IntermediateResult}
import data.boilerplate.structure.HTMLNode


case class TagNameOp() extends ExecutableOp(Array(), "tag-name-op") {


  override def canApply(htmlNode: HTMLNode): Boolean = isElement(htmlNode)

  override def execute(leaf: HTMLNode): IntermediateResult = {
    val name = leaf.node.nodeName()
    if (name != null) {
      IntermediateResult(leaf, Map(s"tagName-${name}" -> 1.0))
    }
    else IntermediateResult(leaf)
  }
}