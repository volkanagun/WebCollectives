package data.boilerplate.learning.graph

import data.boilerplate.learning.pipes.IntermediateResult
import data.boilerplate.structure.HTMLNode

case class Node(val processId:Int,  val features: Array[String], val label: String, var node: HTMLNode) {

  private var stats = Array.fill[Double](features.length)(0d)
  var count = 0d
  var map:Map[String, Array[Node]] = Map[String, Array[Node]]()
  var id = processId
  var rank_score = 0d
  def addNeigbour(label: String, node: Node): Node = {
    map = map.updated(label, map.getOrElse(label, Array[Node]()) :+ node)
    this
  }

  def getStats() = stats

  def equals(process:Processes):Boolean = {
      process.id == id
  }

  def newNode(stats:Array[Double], map:Map[String, Array[Node]]):Node={
    Node(id, features, label, node).setStats(stats)
      .setMap(map)
  }

  def score():Double = {
  stats.sum/stats.length
  }
  def featureId() = features.foldRight[Int](7){(item, main)=> main * 7 + item.hashCode}
  def htmlId() = node.htmlID()
  def childNodes() = node.childnodes

  def depth():Int = {
    if map.isEmpty then 1;
    else{
      val sub = map.toArray.flatMap(_._2).map(node=> node.depth()).max
      sub + 1
    }
  }

  def rank(rankMap:Map[Int, Double]):Double = {
    //tree negative log average sum
    val c_id = htmlId()
    if map.isEmpty && rankMap.contains(c_id) then {
      rankMap(c_id)
    }
    else if map.isEmpty then {
      rank_score = score();
    }
    else {
      val crr_score = rankMap.getOrElse(c_id, 0d)
      val scores = map.toArray.map((label, array)=>{
        val total = array.map(node=> node.score()).sum
        total
      })
      rank_score = crr_score + score() + scores.sum/scores.length
    }

    rank_score
  }

  def setMap(map: Map[String, Array[Node]]): this.type = {
    this.map = map
    this
  }
  def setProcessId(id: Int): this.type = {
    this.id = id
    this
  }

  def setStats(stats: Array[Double]): this.type = {
    if this.stats.exists(d=> d.isNaN) then{
      val d = 0;
    }
    this.stats = stats
    this
  }

  def process(htmlPaths: Array[IntermediateResult]): Array[Node] = {
    count += 1
    htmlPaths.map(result => {
      add(get(result))
    })
  }

  def z_score(threshold: Double): Boolean = {
    stats.forall(i => i >= threshold)
  }

  def z_score_filter(threshold: Double): Option[Node] = {
    val checkCurrent = z_score(threshold)
    val crrPositives = map.map((name, nodes) => name -> nodes.flatMap(node => node.z_score_filter(threshold)))
      .filter(_._2.nonEmpty)
    if checkCurrent && crrPositives.nonEmpty then {
      Some(Node(id, features, label, node).setMap(crrPositives).setStats(stats))
    }
    else if checkCurrent && map.isEmpty then{
      Some(Node(id, features, label, node).setStats(stats))
    }
    else{
      None
    }
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
    stats = stats.map(i => i / (count+1))
    this
  }
}
