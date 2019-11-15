package data.boilerplate.learning.features

import data.boilerplate.learning.pipes.{ExecutableOp, IntermediateResult}
import data.boilerplate.structure.HTMLNode
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute
import org.jsoup.nodes.Element


case class TokenCountOp() extends ExecutableOp(Array(), s"token-count-op") {

  val analyzer = new StandardAnalyzer()


  override def canApply(htmlNode: HTMLNode): Boolean = isElement(htmlNode)

  //count the frequency
  override def execute(leaf: HTMLNode): IntermediateResult = {
    val element = leaf.node.asInstanceOf[Element]
    val tokenLength = tokenize(element.text()).length
    val count = tokenLength.toDouble
    IntermediateResult(Map(name -> count))
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