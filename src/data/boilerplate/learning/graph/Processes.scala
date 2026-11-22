package data.boilerplate.learning.graph

import data.boilerplate.learning.pipes.{IntermediateResult, PipeOp}
import data.boilerplate.structure.{HTMLNode, HTMLPath}

import scala.collection.parallel.CollectionConverters.ArrayIsParallelizable

case class Processes(val label: String, val model: PipeOp) {

  val PARENT = "PARENT"
  val NEXT = "NEXT"
  val PREVIOUS = "PREVIOUS"
  val CHILD = "CHILD"

  var labelMap = Map[String, Processes]()
  val id = label.hashCode * 7 + model.id()

  def add(label: String, pipeOp: PipeOp): this.type = {
    labelMap = labelMap.updated(label, Processes(label, pipeOp))
    this
  }

  def add(label: String, process: Processes): this.type = {
    labelMap = labelMap.updated(label, process)
    this
  }

  def parent(pipeOp: PipeOp): this.type = {
    add(PARENT, pipeOp)
  }

  def next(pipeOp: PipeOp): this.type = {
    add(NEXT, pipeOp)
  }

  def previous(pipeOp: PipeOp): this.type = {
    add(PREVIOUS, pipeOp)
  }

  def child(pipeOp: PipeOp): this.type = {
    add(CHILD, pipeOp)
  }

  def child(processes: Processes): this.type = {
    add(CHILD, processes)
  }

  override def toString: String = {
    label + ":[" + model.toString() + "]" + s"{${labelMap.mkString(",")}}"
  }

  def execute(paths: Array[HTMLPath]): Array[IntermediateResult] = {
    paths.map(htmlPath => (model.execute(htmlPath).setProcessId(id))) ++ labelMap.toArray.flatMap { (label, process) => {
      process.execute(paths)
    }
    }
  }

  def execute(node: HTMLNode): Array[IntermediateResult] = {
    model.execute(node) +: labelMap.toArray.flatMap { (label, process) => {
      process.execute(node)
    }}
  }


  def process(array: Array[IntermediateResult]): Array[Node] = {

    val nodeArray = array.map { result => {
      Node(result.processId, result.map.keys.toArray, label, result.node)
        .process(result)
    }}

    val idMap = nodeArray.map(item => {
      item.htmlId() -> item
    }).toMap

    nodeArray.foreach { case (mainNode) => {
      val htmlNode = mainNode.node
      labelMap.foreach { case (label, subprocess) => {

        if (PARENT == label) {
          //go parent
          val nodeNode = idMap(htmlNode.parentID())
          val nodeResult = subprocess.execute(nodeNode.node)
          nodeNode.process(nodeResult)
          mainNode.addNeigbour(label, nodeNode)
        }
        else if (CHILD == label) {
          //go child

          htmlNode.getAllChildNodes().filter(node => idMap.contains(node.htmlID())).foreach(childNode => {
            val nodeNode = idMap(childNode.htmlID())
            val nodeResult = subprocess.execute(childNode)
            nodeNode.process(nodeResult)
            mainNode.addNeigbour(label, nodeNode)
          })
        }
        else if (NEXT == label && idMap.contains(htmlNode.next.htmlID())) {
          //go next
          htmlNode.getNextSiblings().filter(node => idMap.contains(node.htmlID())).foreach(childNode => {
            val nodeNode = idMap(childNode.htmlID())
            val nodeResult = subprocess.model.execute(childNode)
            nodeNode.process(nodeResult)
            mainNode.addNeigbour(label, nodeNode)
          })
        }
        else if (PREVIOUS == label && idMap.contains(htmlNode.previous.htmlID())) {
          //go previous
          htmlNode.getPreviousSiblings().filter(node => idMap.contains(node.htmlID())).foreach(childNode => {
            val nodeNode = idMap(childNode.htmlID())
            val nodeResult = subprocess.model.execute(childNode)
            nodeNode.process(nodeResult).normalize()
            mainNode.addNeigbour(label, nodeNode)
          })
        }

        mainNode
      }
      }
    }}

    nodeArray
  }
}