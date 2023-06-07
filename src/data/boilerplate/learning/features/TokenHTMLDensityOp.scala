package data.boilerplate.learning.features

import data.boilerplate.learning.pipes.{ExecutableOp, IntermediateResult}
import data.boilerplate.structure.HTMLNode
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute
import org.jsoup.nodes.Element


case class TokenHTMLDensityOp() extends ExecutableOp(Array(), s"token-html-density-op") {

  val analyzer = new StandardAnalyzer()


  override def canApply(htmlNode: HTMLNode): Boolean = isElement(htmlNode)

  //count the frequency
  override def execute(leaf: HTMLNode): IntermediateResult = {
    val element = leaf.node.asInstanceOf[Element]
    val html = element.html()
    val tokenLength = tokenize(element.text()).length
    val density = tokenLength.toDouble / html.length
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