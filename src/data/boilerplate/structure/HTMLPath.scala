package data.boilerplate.structure

/**
 * @author Volkan Agun
 */
case class HTMLPath(var pathNodes : Array[HTMLNode]) extends Serializable{

  def id():Int={
    pathNodes.foldRight[Int](7){case(node, main) => node.nameID() + 7 * main}
  }

  override def hashCode(): Int = id()

  def last():Option[HTMLNode]={
    if(pathNodes.nonEmpty) Some(pathNodes.last)
    else None
  }

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
