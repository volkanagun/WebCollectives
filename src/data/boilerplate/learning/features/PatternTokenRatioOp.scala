package data.boilerplate.learning.features

import data.boilerplate.learning.pipes.{IntermediateResult, PatternOp}
import data.boilerplate.structure.HTMLNode
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute
import org.jsoup.nodes.Element


case class PatternTokenRatioOp(regex: String) extends PatternOp(Array(), s"pattern-token-density-op") {

  val analyzer = new StandardAnalyzer()

  //count the frequency
  override def execute(leaf: HTMLNode): IntermediateResult = {
    val element = leaf.node.asInstanceOf[Element]
    val html = element.html()
    val patterns = rpatterns(regex, html)
    val tokenLength = tokenize(element.text()).length
    val density = tokenLength.toDouble / patterns.length
    IntermediateResult(leaf, Map(name -> density))
  }

  protected def tokenize(text: String): Array[String] = {
    val tokenStream = analyzer.tokenStream("TEXT", text)
    val attr = tokenStream.addAttribute(classOf[CharTermAttribute])
    tokenStream.reset()
    var array = Array[String]()
    while (tokenStream.incrementToken)
      array :+= attr.toString

    array
  }

}
