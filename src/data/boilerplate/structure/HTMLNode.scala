package data.boilerplate.structure

import org.jsoup.nodes.{Node, TextNode}

/**
 * @author Volkan Agun
 */
class HTMLNode(val node:Node) extends Serializable{

  var childnodes = Array[HTMLNode]()
  var parent:HTMLNode = null

  def setParent(p:HTMLNode):HTMLNode={
    this.parent = p;
    this
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