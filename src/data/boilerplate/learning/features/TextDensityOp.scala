package data.boilerplate.learning.features

import data.boilerplate.learning.pipes.{IntermediateResult, PatternOp}
import data.boilerplate.structure.HTMLNode
import org.jsoup.nodes.{Element, TextNode}


case class TextDensityOp() extends PatternOp(Array(), s"text-density-op") {

  //count the frequency
  override def execute(leaf: HTMLNode): IntermediateResult = {
    val html = getParentHtml(leaf)
    val text = getText(leaf)
    val density = (text.length.toDouble + 1E-12) / (html.length + 1E-12)
    val map = Map(name -> density)
    IntermediateResult(leaf, map)
  }

}
