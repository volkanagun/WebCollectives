package data.boilerplate.learning.features

import data.boilerplate.learning.pipes.{IntermediateResult, PatternOp}
import data.boilerplate.structure.HTMLNode
import org.jsoup.nodes.Element

case class PatternTextDensityOp(regex: String) extends PatternOp(Array(), s"pattern-text-density-op") {

  //count the frequency
  override def execute(leaf: HTMLNode): IntermediateResult = {
    val element = leaf.node.asInstanceOf[Element]
    val text = element.text()
    val html = element.html()
    val patterns = rpatterns(regex, html)
    val textLength = text.length
    val density = (textLength.toDouble + 1E-12) / (patterns.length + 1E-12)
    IntermediateResult(leaf,Map(name -> density))
  }

}