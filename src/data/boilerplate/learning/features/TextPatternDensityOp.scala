package data.boilerplate.learning.features

import data.boilerplate.learning.pipes.{IntermediateResult, PatternOp}
import data.boilerplate.structure.HTMLNode
import org.jsoup.nodes.Element


case class TextPatternDensityOp(regex: String) extends PatternOp(Array(), s"text-pattern-density-op") {

  //count the frequency
  override def execute(leaf: HTMLNode): IntermediateResult = {
    val element = leaf.node.asInstanceOf[Element]
    val text = element.text()
    val html = element.html()

    val total = rpatternTotalCharLength(regex, html)
    val density = text.length.toDouble / total
    val map = Map(name -> density)
    IntermediateResult(leaf, map)
  }

}


case class TagTextDensityOp() extends PatternOp(Array(), s"tag-text-density-op") {

  //count the frequency
  override def execute(leaf: HTMLNode): IntermediateResult = {
    PatternTextDensityOp("<(.*?)>").execute(leaf)
  }

}