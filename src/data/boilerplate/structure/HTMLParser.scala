package data.boilerplate.structure

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
    parseNode(1, 0, htmlDocument)
  }

  def parseNode(parentID:Int, position:Int, node:Node):HTMLNode={
    val id = (parentID * 7 + position) * 7 + node.hashCode()
    val childNodes = node.childNodes()
      .toArray[Node](Array[Node]()).zipWithIndex
      .filter((cnode, index) => { !(cnode.isInstanceOf[TextNode] && cnode.asInstanceOf[TextNode].isBlank)})
      .map((cnode, index) => {
        parseNode(id,index, cnode)
      })

    childNodes.zipWithIndex.foreach{case(node, index)=> {
      if(index > 0) node.setPrevious(childNodes(index-1))
      if(index < childNodes.length-1) node.setNext(childNodes(index+1))
    }}


    new HTMLNode(id, node)
      .setChildNodes(childNodes).build()
  }

  def main(args: Array[String]): Unit = {
    val pathStrs = parseHTML("html.html").visit().map(path=>{
      path.toTagString()
    })

    println(pathStrs.mkString("\n"))
  }
}
