package data.boilerplate.learning.graph

import data.boilerplate.learning.pipes.IntermediateResult
import data.boilerplate.structure.HTMLNode

case class Node(val features: Array[String], val label: String, var node: HTMLNode) {

  var stats = Array.fill[Double](features.length)(0d)
  var count = 0d
  var map = Map[String, Array[Node]]()

  def addNeigbour(label: String, node: Node): Node = {
    map = map.updated(label, map.getOrElse(label, Array[Node]()) :+ node)
    this
  }

  def process(htmlPaths: Array[IntermediateResult]): Array[Node] = {
    count += 1
    htmlPaths.map(result => {
      add(get(result))
    })
  }

  def process(result: IntermediateResult): Node = {
    count += 1
    add(get(result))
  }

  def get(intermediateResult: IntermediateResult): Array[Double] = {
    features.map(item => intermediateResult.get(item))
  }

  def add(array: Array[Double]): this.type = {
    stats = stats.zip(array).map(pair => pair._1 + pair._2)
    this
  }

  def normalize(): this.type = {
    stats = stats.map(i => i / count)
    this
  }
}
