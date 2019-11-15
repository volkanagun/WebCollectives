package data.boilerplate.learning.features

import data.boilerplate.learning.pipes.{IntermediateResult, PatternOp, PipeOp}
import data.boilerplate.structure.HTMLNode
import org.jsoup.nodes.{Document, Element}


case class RecursivePatternOp(regex: String, subs: Array[PipeOp]) extends PatternOp(subs, s"recursive-op[${regex}]") {


  override def canApply(htmlNode: HTMLNode): Boolean = {
    isElement(htmlNode)
  }

  //count the frequency inside all regex
  override def execute(leaf: HTMLNode): IntermediateResult = {
    val html = leaf.node.asInstanceOf[Element].html()
    val matches = rpatterns(regex, html)
    val irss = matches.map(matchString => {
      val element = new HTMLNode(new Document(leaf.node.baseUri()).html(matchString))
      val counts = subs.map(subpipe => {
        subpipe match {
          case e: PatternOp => Some(e.execute(element))
          case _ => None
        }
      }).flatten

      normalize(counts)
    })

    sum(irss)
  }

}