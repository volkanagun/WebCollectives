package data.boilerplate

import org.jsoup.Jsoup
import org.jsoup.nodes.{Node, TextNode}

import scala.io.Source

/**
 * @author Volkan Agun
 */




object HTMLParser {

  def parseHTML(filename:String) : HTMLNode = {
    val htmlText = Source.fromFile(filename, "UTF-8").getLines().mkString("\n")
    val htmlDocument  = Jsoup.parse(htmlText)
    parseNode(htmlDocument)
  }

  def parseNode(node:Node):HTMLNode={
    val childNodes = node.childNodes()
      .toArray[Node](Array[Node]())
      .filter(cnode=> { !(cnode.isInstanceOf[TextNode] && cnode.asInstanceOf[TextNode].isBlank)})
      .map(cnode=> {
        parseNode(cnode)
      })

    new HTMLNode(node)
      .setChildNodes(childNodes)
  }

  def main(args: Array[String]): Unit = {
    val pathStrs = parseHTML("html.html").visit().map(path=>{
      path.toTagString()
    })

    println(pathStrs.mkString("\n"))
  }
}
