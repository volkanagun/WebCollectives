package data.boilerplate.learning.features

import data.boilerplate.learning.pipes.{IntermediateResult, PatternOp, PipeOp}
import data.boilerplate.structure.{HTMLNode, HTMLPath}

case class ThresholdFilterOp(ops:Array[PipeOp], threshold:Double = 0.5d) extends PatternOp(ops, "threshold-op"){

  override def execute(leaf: HTMLNode): IntermediateResult = {
    val validated = ops.map(pop=> (pop.name, pop.execute(leaf)))
      .forall{case(name, result)=> result.get(name) >= threshold}
    val score = (if(validated) 1d else 0d)
    IntermediateResult(leaf, Map(name->score))
  }

  override def execute(path: HTMLPath): IntermediateResult = {
    execute(path.pathNodes.last)
  }

}
