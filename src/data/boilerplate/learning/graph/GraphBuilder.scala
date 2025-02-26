package data.boilerplate.learning.graph

import data.boilerplate.learning.features.{ChildTagCountOp, ParentTextDensityOp, PatternTextDensityOp, PatternTokenRatioOp, PreviousByPattern, PreviousHasTagOp, TagNameOp, TagTextDensityOp}
import data.boilerplate.learning.pipes.{IntermediateResult, PipeOp}
import data.boilerplate.structure.{HTMLNode, HTMLPath}



class GraphBuilder {

  /**
   * Path of the node from top to bottom
   * @param features
   * @param id
   * @param label
   */

  def modelContent():Processes = {
    //Build processes model with PipeOps
    //Apply build for processes
    //Apply process for IntermediateResults
    val tagRegex = "<\\p{L}+(.*?)>"
    val mainOp = PipeOp()
      .op(TagTextDensityOp())
      .op(ChildTagCountOp("(p|h1|h2|span)"))

    val paragraphOp = PipeOp()
      .op(ParentTextDensityOp())
      .op(PreviousHasTagOp("(h1|h2)"))
      .op(PreviousByPattern(TagNameOp()))

    val processes = Processes("mainContent", mainOp)
    processes

  }

}
