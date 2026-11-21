package data.boilerplate.learning.features

import data.boilerplate.learning.pipes.{ExecutableOp, IntermediateResult, PipeOp}
import data.boilerplate.structure.HTMLNode

class CombineOrGateOp(name:String, val list:Array[PipeOp]) extends ExecutableOp(list,"combine-or-"+name) {
  override def execute(leaf: HTMLNode): IntermediateResult = {
    val found = list.exists(op=> op.execute(leaf).map.forall((name, score)=> score != 0.0))
    val map = (if found then Map(name->1.0) else Map(name-> 0.0))
    IntermediateResult(leaf, map)  
  }
}
