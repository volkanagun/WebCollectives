package data.boilerplate.learning.features

import data.boilerplate.learning.pipes.{IntermediateResult, PatternOp}
import data.boilerplate.structure.HTMLNode
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.jsoup.nodes.Element


case class PatternParentRatioOp(regex: String) extends PatternOp(Array(), s"parrent-pattern-ratio-op") {

  val analyzer = new StandardAnalyzer()

  //count the frequency
  override def execute(leaf: HTMLNode): IntermediateResult = {
    val parent = leaf.parent.node.asInstanceOf[Element]
    val current = leaf.node.asInstanceOf[Element]
    val parrentPatterns = rpatterns(regex, parent.html())
    val currentPatterns = rpatterns(regex, current.html())
    val density = currentPatterns.length.toDouble / parrentPatterns.length
    IntermediateResult(Map(name -> density))
  }

}