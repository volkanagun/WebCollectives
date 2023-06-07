package data.boilerplate.learning.features

import data.boilerplate.learning.pipes.{IntermediateResult, PatternOp}
import data.boilerplate.structure.HTMLNode
import org.jsoup.nodes.Element


case class HTMLPatternOp(regex: String) extends PatternOp(Array(), s"html-regex-op[${regex}]") {

  def exists(regex: String): HTMLPatternOp = {
    HTMLPatternOp(regex)
  }

  //count the frequency
  override def execute(leaf: HTMLNode): IntermediateResult = {
    val html = leaf.getFullHTML()
    val matches = rpatterns(regex, html)
    val map = Map(name -> matches.length.toDouble)
    IntermediateResult(leaf,map)
  }

  //boolean contains
  override def exists(leaf: HTMLNode): Double = {
    val html = leaf.node.asInstanceOf[Element].html()
    val matches = rpatterns(regex, html)
    if (matches.length > 0) 1.0 else 0.0
  }
}