package data.boilerplate.learning.features

import data.boilerplate.learning.pipes.{ExecutableOp, IntermediateResult}
import data.boilerplate.structure.HTMLNode


case class CSSAttributeOp() extends ExecutableOp(Array(), "css-attr-op") {


  override def canApply(htmlNode: HTMLNode): Boolean = isElement(htmlNode)

  override def execute(leaf: HTMLNode): IntermediateResult = {
    val attr = leaf.node.attributes().getIgnoreCase("style")
    if (attr != null && !attr.isEmpty) {
      val mapping = attr.split("\\;\\s?").map(subcss => {
        val ssf = "style-" + subcss
        (ssf, 1.0)
      }).toMap
      IntermediateResult(mapping)
    }
    else IntermediateResult()
  }
}
