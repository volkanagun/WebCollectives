package data.boilerplate.learning.graph

import data.boilerplate.learning.features.{ChildTagCountOp, CombineOrGateOp, NextHasTagOp, ParentTextDensityOp, PatternTextDensityOp, PatternTokenRatioOp, PreviousByPattern, PreviousHasTagOp, TagNameOp, TagTextDensityOp, TextDensityAboveOp}
import data.boilerplate.learning.pipes.{IntermediateResult, PipeOp}
import data.boilerplate.structure.{HTMLNode, HTMLPath}



class GraphBuilder {


  val processes = build()
  /**
   * Path of the node from top to bottom
   * @param features
   * @param id
   * @param label
   */

  def build():Processes = {
    //Build processes model with PipeOps
    //Apply build for processes
    //Apply process for IntermediateResults
    val tagRegex = "<\\p{L}+(.*?)>"
    val mainOp = PipeOp()
      .op(TagTextDensityOp())
      .op(ChildTagCountOp("(p|h1|h2|span)"))

    val paragraphOp = PipeOp()
      .op(TextDensityAboveOp())
      .op(CombineOrGateOp("continue",Array(PreviousHasTagOp("(h1|h2|p)"), NextHasTagOp("(p)"))))

    val processes = Processes("mainContent", mainOp)
    processes
  }

  def score(paths:Array[HTMLPath]): Unit = {
    val executionMap = processes.execute(paths)
    val executionResults = executionMap.values.toArray
    val nodes = processes.process(executionResults).map(node=> node.normalize())
    val new_nodes = z_scores(nodes)
    val filteredNodes = new_nodes.filter(node=> node.z_score(0.5))


    filteredNodes

  }

  def z_scores(nodes:Array[Node]):Array[Node]={
    val f_size = nodes.head.stats.length
    var mean = Array.fill[Double](f_size)(0.0)
    var std = Array.fill[Double](f_size)(0.0)

    mean = nodes.foldRight[Array[Double]](mean){(node, array)=>{
      array.zip(node.stats).map((m, n)=> m + n/nodes.length)
    }}

    std = nodes.foldRight[Array[Double]](std){(node, array)=>{
      array.zip(node.stats).zip(mean).map{case ((st, n), m) => (st + ((n-m)/nodes.length))}
    }}

    nodes.map(node=> {
      node.stats = node.stats.zipWithIndex.map((value, index)=> (value-mean(index))/std(index))
      node
    })
  }

}
