package data.boilerplate.learning.graph

import data.boilerplate.learning.features.{ChildTagAverageOp, ChildTagCountOp, CombineOrGateOp, NextHasTagOp, ParentTextDensityOp, PatternTextDensityOp, PatternTokenRatioOp, PreviousByPattern, PreviousHasTagOp, TagExistsByPattern, TagNameOp, TagTextDensityOp, TextDensityAboveOp, TextDensityOp}
import data.boilerplate.learning.pipes.{IntermediateResult, PipeOp}
import data.boilerplate.structure.{HTMLNode, HTMLPath}

import scala.collection.parallel.CollectionConverters.ArrayIsParallelizable


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
      .op(TextDensityOp())
      .op(TagExistsByPattern("(h1|h2|p)"))
      //.op(CombineOrGateOp("continue", Array(PreviousHasTagOp("(h1|h2|p)"), NextHasTagOp("(p|h1|h2|span)"))))

    val mainOp = PipeOp("PARENT")
      //.op(TagTextDensityOp())
      .op(ChildTagAverageOp("(p|h1|h2)"))

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
    val filteredNodes = structurals.flatMap(node => node.z_score_filter(0.000001))

    filteredNodes.foreach(node => {
      println(node.node.text)
    })

    filteredNodes

  }

  def z_scores(nodes: Array[Node]): Array[Node] = {
    nodes.groupBy(_.featureId()).toArray.par
      .map { case (featureId, sub_nodes) => featureId -> z_scores_by(sub_nodes) }
      .seq.toArray.flatMap(_._2)
  }

  def z_scores_by(nodes: Array[Node]): Array[Node] = {
    val f_size = nodes.head.getStats().length
    val featureIndices = Array.tabulate(f_size)(index => index)

    val mean = featureIndices.par.map(index =>
      nodes.foldRight(0.0)((node, total) =>
        total + node.getStats()(index) / nodes.length
      )
    ).seq.toArray

    val std = featureIndices.par.map(index =>
      nodes.foldRight(0.0)((node, total) =>
        total + ((node.getStats()(index) - mean(index)) / nodes.length)
      )
    ).seq.toArray

    nodes.par.map(node => {
      val new_stats = node.getStats().zipWithIndex.map((value, index) => {
        if std(index) == 0 then 0.0
        else (value - mean(index)) / std(index)
      })
      val new_map = node.map.map((label, array) => label -> z_scores(array))
      node.newNode(new_stats, new_map)
    }).seq.toArray
  }

}
