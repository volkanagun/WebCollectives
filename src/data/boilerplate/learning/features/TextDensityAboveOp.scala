package data.boilerplate.learning.features

import data.boilerplate.learning.pipes.IntermediateResult
import data.boilerplate.structure.HTMLNode

class TextDensityAboveOp(val threshold:Float = 0.5f) extends TextDensityOp {
  
  override def execute(leaf: HTMLNode): IntermediateResult = {
    val html = getHtml(leaf)
    val text = getText(leaf)
    val density = (text.length.toDouble + 1E-12) / (html.length + 1E-12)
    val map =  if density > threshold then Map(name -> 1.0) else Map(name -> 0.0) 
    IntermediateResult(leaf, map)
  }
}
