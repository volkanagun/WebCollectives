package data.boilerplate.learning.features

import data.boilerplate.learning.pipes.{IntermediateResult, PatternOp}
import data.boilerplate.structure.HTMLNode
import org.jsoup.nodes.Element


case class PatternCharAvgOp(regex: String) extends PatternOp(Array(), s"pattern-char-avg-op") {

  //count the frequency
  override def execute(leaf: HTMLNode): IntermediateResult = {
    val element = leaf.node.asInstanceOf[Element]
    val average = rpatternAvgCharLength(regex, element.html())
    val map = Map(name -> average)
    IntermediateResult(leaf, map)
  }

}