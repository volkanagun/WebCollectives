package data.boilerplate.learning.features

import data.boilerplate.learning.pipes.{IntermediateResult, PatternOp}
import data.boilerplate.structure.HTMLNode
import org.jsoup.nodes.{Element, TextNode}


case class ParentTextDensityOp() extends PatternOp(Array(), s"parent-text-density-op") {

  //count the frequency
  override def execute(leaf: HTMLNode): IntermediateResult = {
    val parentText = getParentText(leaf)
    val currentText = getText(leaf)
    val density = (currentText.length.toDouble + 1E-12) / (parentText.length + 1E-12)
    val map = Map(name -> density)
    IntermediateResult(leaf,map)
  }

}