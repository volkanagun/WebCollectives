package data.boilerplate.structure

import java.io.{ObjectInputStream, ObjectOutputStream}

/**
 * @author Volkan Agun
 */
case class HTMLPath(var pathNodes : Array[HTMLNode]) extends Serializable{

  def id():Int={
    pathNodes.foldRight[Int](7){case(node, main) => node.nameID() + 7 * main}
  }

  def build():this.type = {
    pathNodes.foreach(node=> node.build())
    this
  }

  def read(stream: ObjectInputStream):this.type ={
    val count = stream.readInt()
    for(i<-0 until count) {
      pathNodes :+= new HTMLNode(-1, null).read(stream)
    }
    this
  }
  def write(stream: ObjectOutputStream):this.type ={
    stream.writeInt(pathNodes.size)
    pathNodes.foreach(pathNode => pathNode.write(stream))
    this
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
