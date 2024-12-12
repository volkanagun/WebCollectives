package data.boilerplate.learning.graph

import data.boilerplate.learning.pipes.{IntermediateResult, PipeOp}
import data.boilerplate.structure.HTMLPath

import scala.collection.parallel.CollectionConverters.ArrayIsParallelizable

case class Processes(val label: String, val model: PipeOp) {

  val PARENT = "PARENT"
  val NEXT = "NEXT"
  val PREVIOUS = "PREVIOUS"
  val CHILD = "CHILD"

  var labelMap = Map[String, Processes]()

  def add(label: String, pipeOp: PipeOp): this.type = {
    labelMap = labelMap.updated(label, Processes(label, pipeOp))
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

  def build(paths: Array[HTMLPath]): Map[HTMLPath, IntermediateResult] = {
    paths.par.map(htmlPath => (htmlPath -> model.execute(htmlPath)))
      .toArray
      .toMap
  }

  def process(array: Array[IntermediateResult]): Array[Node] = {

    val nodeMap = array.map { result => {
      result.node -> Node(result.map.keys.toArray, label, result.node)
        .process(result)
    }
    }.toMap

    val idMap = nodeMap.map(pair => pair._1.htmlID() -> pair._2)

    nodeMap.map { case (htmlNode, mainNode) => {
      labelMap.foreach { case (label, pipeop) => {

        if (PARENT == label) {
          //go parent
          val nodeNode = idMap(htmlNode.parentID())
          val nodeResult = pipeop.model.execute(nodeNode.node)
          nodeNode.process(nodeResult)
          mainNode.addNeigbour(label, nodeNode)
        }
        else if (CHILD == label) {
          //go child
          htmlNode.childnodes.foreach(childNode => {
            val nodeNode = idMap(childNode.htmlID())
            val nodeResult = pipeop.model.execute(childNode)
            nodeNode.process(nodeResult)
            mainNode.addNeigbour(label, nodeNode)
          })
        }
        else if (NEXT == label) {
          //go sibling
          val nodeNode = idMap(htmlNode.next.htmlID())
          val nodeResult = pipeop.model.execute(htmlNode.next)
          nodeNode.process(nodeResult)
          mainNode.addNeigbour(label, nodeNode)
        }
        else if (PREVIOUS == label) {
          //go previous
          val nodeNode = idMap(htmlNode.previous.htmlID())
          val nodeResult = pipeop.model.execute(htmlNode.previous)
          nodeNode.process(nodeResult)
          mainNode.addNeigbour(label, nodeNode)
        }
      }
      }

      mainNode

    }
    }.toArray

  }
}