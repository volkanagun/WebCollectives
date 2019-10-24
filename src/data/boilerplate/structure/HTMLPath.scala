package data.boilerplate.structure

/**
 * @author Volkan Agun
 */
case class HTMLPath(var pathNodes : Array[HTMLNode]) extends Serializable{
  def preappend(htmlNode: HTMLNode):this.type ={
    pathNodes = htmlNode +:pathNodes
    this
  }

  def toTextString():Option[String]={
    pathNodes.last.tagText()
  }

  def toTagString():String={
    "[" + pathNodes.map(node=> node.tagName()).mkString(",") + "]"
  }
}
