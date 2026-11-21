package data.boilerplate.learning.features

import data.boilerplate.learning.pipes.IntermediateResult
import data.boilerplate.structure.HTMLNode

class AvgTokenLengthOp(val threshold:Float = 7) extends TokenCountOp() {

  override def execute(leaf: HTMLNode): IntermediateResult = {
    val text = leaf.text
    val tokens = tokenize(text)
    val tokenCount = tokens.length
    val sumLength = tokens.map(token=> token.length).sum
    val count = tokenCount.toDouble
    val avg = sumLength / count
    val score = (if avg <= threshold then 1.0 else 0.0)
    IntermediateResult(leaf, Map(name -> score))
  }
}
