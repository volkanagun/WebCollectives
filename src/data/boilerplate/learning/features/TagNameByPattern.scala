package data.boilerplate.learning.features

import data.boilerplate.learning.pipes.{IntermediateResult, PatternOp}
import data.boilerplate.structure.HTMLNode

case class TagNameByPattern(regex:String) extends PatternOp(Array(), "tag-name-"+regex) {
  override def canApply(htmlNode: HTMLNode): Boolean = true

  override def execute(leaf: HTMLNode): IntermediateResult = {

    val name = leaf.node.nodeName()
    if (name != null && name.matches(regex)) {
      IntermediateResult(leaf, Map(s"tagName-${name}" -> 1.0))
    }
    else IntermediateResult(leaf, Map(s"tagName-${name}" -> 0.0))

  }
}
