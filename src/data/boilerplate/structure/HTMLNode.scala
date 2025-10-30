package data.boilerplate.structure

import org.jsoup.nodes.{Element, Node, TextNode}
import org.w3c.dom

import java.io.{ObjectInputStream, ObjectOutputStream}
import scala.xml.Comment

/**
 * @author Volkan Agun
 */
class HTMLNode(var id:Int, var node:Node) extends Serializable{

  var childnodes = Array[HTMLNode]()
  var previous, next:HTMLNode = null;
  var parent:HTMLNode = null
  var html:String = null
  var text:String = null

  def build():this.type = {
    html =  node.outerHtml()
    if (node.isInstanceOf[Element]){
      text = node.asInstanceOf[Element].text()
    }
    else if(node.isInstanceOf[TextNode]){
      text = node.asInstanceOf[TextNode].text()
    }
    this
  }

  def read(stream:ObjectInputStream):this.type={
    id = stream.readInt()
    html = stream.readObject().asInstanceOf[String]
    text = stream.readObject().asInstanceOf[String]

    val count = stream.readInt()
    for (i<-0 until count) {
      childnodes:+= new HTMLNode(-1, null).read(stream)
    }
    this
  }

  def write(stream:ObjectOutputStream):this.type={
    stream.writeInt(id)
    stream.writeObject(html)
    stream.writeObject(text)

    stream.writeInt(childnodes.length)
    childnodes.foreach(subNode=> subNode.write(stream))
    this
  }

  def nameID():Int={
    node.nodeName().hashCode
  }

  def parentID():Int={
    if(parent !=null) parent.htmlID()
    else htmlID()
  }
  def childID():Array[Int]={
    if(childnodes.isEmpty) Array(htmlID())
    else childnodes.map(_.htmlID())
  }

  def htmlID():Int={
    nameID()*7 + id
  }

  def hasNext():Boolean={
    next != null
  }

  def hasPrevious():Boolean={
    previous !=null
  }

  def setPrevious(previous:HTMLNode):this.type ={
    this.previous = previous
    this
  }
  def setNext(next:HTMLNode):this.type ={
    this.next = next
    this
  }

  def setParent(p:HTMLNode):HTMLNode={
    this.parent = p;
    this
  }

  def getParent():HTMLNode={
    if(hasParent()) parent
    else this
  }

  def hasParent():Boolean={
    this.parent != null
  }

  def setChildNodes(nodes:Array[HTMLNode]):this.type ={
    childnodes = nodes
    childnodes.foreach(childNode=> childNode.setParent(this))
    this
  }

  def getChildNodes():Array[HTMLNode]={
    childnodes
  }

  def tagName():String={
    node.nodeName()
  }
  def isLeaf():Boolean={
    node.isInstanceOf[TextNode] && !node.asInstanceOf[TextNode].isBlank
  }

  def isElement():Boolean={
    node !=null && (node.isInstanceOf[Element] || node.isInstanceOf[TextNode])
  }

  def isComment():Boolean={
    node!=null && (node.isInstanceOf[org.jsoup.nodes.Comment])
  }

  def getFullHTML(): String = {
    if(node!=null && node.isInstanceOf[TextNode]){
      node.asInstanceOf[TextNode].parent().outerHtml();
    }
    else if(node!=null && node.isInstanceOf[Element]){
      node.asInstanceOf[Element].outerHtml()
    }
    else{
      "NULL"
    }
  }

  def hasMatches(pattern:String):Boolean={
    val html = getFullHTML()
    pattern.r.findAllMatchIn(html).nonEmpty
  }

  def tagText():Option[String]={
    if(isLeaf()) Some(node.asInstanceOf[TextNode].text())
    else None
  }

  protected def visit(current:HTMLNode):Array[HTMLPath] = {
    val left2Right = current.childnodes.map(cnode=> visit(cnode))
    if(left2Right.isEmpty) Array(HTMLPath(Array(current)))
    else{
      left2Right.foldRight[Array[HTMLPath]](Array()){
        case(paths, main)=> {
          main ++ paths.map(path=> path.preappend(current))
        }
      }
    }
  }

  def visit():Array[HTMLPath]={
    visit(this)
  }
}