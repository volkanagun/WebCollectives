package data.boilerplate.learning.graph

import data.boilerplate.learning.features.{ChildTagCountOp, CombineOrGateOp, NextHasTagOp, ParentTextDensityOp, PatternTextDensityOp, PatternTokenRatioOp, PreviousByPattern, PreviousHasTagOp, TagExistsByPattern, TagNameOp, TagTextDensityOp, TextDensityAboveOp}
import data.boilerplate.learning.pipes.{IntermediateResult, PipeOp}
import data.boilerplate.structure.{HTMLNode, HTMLPath}


class GraphBuilder {


  val processes = build()
  val main = processes.head

  /**
   * Path of the node from top to bottom
   *
   * @param features
   * @param id
   * @param label
   */

  def build(): Array[Processes] = {
    //Build processes model with PipeOps
    //Apply build for processes
    //Apply process for IntermediateResults
    val tagRegex = "<\\p{L}+(.*?)>"

    val paragraphOp = PipeOp("CHILD")
      //  .op(TextDensityAboveOp())
      .op(TagExistsByPattern("(h1|h2|p)"))
      .op(CombineOrGateOp("continue", Array(PreviousHasTagOp("(h1|h2|p)"), NextHasTagOp("(p)"))))

    val mainOp = PipeOp("PARENT")
      //.op(TagTextDensityOp())
      .op(ChildTagCountOp("(p|h1|h2)"))

    val processChild = Processes("CHILD", paragraphOp)
    val processParent = Processes("PARENT", mainOp).child(processChild)


    Array(processParent)


  }

  def score(paths: Array[HTMLPath]): Unit = {
    //Execute the processes for each path separately.
    //Combine into single by processes hierarchy.

    val executionResults = processes.flatMap(processes => processes.execute(paths))
    val nodes = main.process(executionResults).map(node => node.normalize())
    val new_nodes = z_scores(nodes)
    val structurals = new_nodes.filter(node => node.equals(main))
    val filteredNodes = structurals.flatMap(node => node.z_score_filter(0.0))

    filteredNodes.foreach(node => {
      println(node.node.text)
    })

    filteredNodes

  }

  def z_scores(nodes: Array[Node]): Array[Node] = {
    nodes.groupBy(_.featureId()).view
      .mapValues(sub_nodes => z_scores_by(sub_nodes)).toArray.flatMap(_._2)
  }

  def z_scores_by(nodes: Array[Node]): Array[Node] = {
    val f_size = nodes.head.getStats().length
    var mean = Array.fill[Double](f_size)(0.0)
    var std = Array.fill[Double](f_size)(0.0)

    mean = nodes.foldRight[Array[Double]](mean) { (node, array) => {
      array.zip(node.getStats()).map((m, n) => m + n / nodes.length)
    }
    }

    std = nodes.foldRight[Array[Double]](std) { (node, array) => {
      array.zip(node.getStats()).zip(mean).map { case ((st, n), m) => (st + ((n - m) / nodes.length)) }
    }
    }

    nodes.map(node => {
      val new_stats = node.getStats().zipWithIndex.map((value, index) => {
        if std(index) == 0 then 0.0
        else (value - mean(index)) / std(index)
      })
      val new_map = node.map.map((label, array) => label -> z_scores(array))
      node.newNode(new_stats, new_map)
    })
  }

}
