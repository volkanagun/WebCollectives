package data.boilerplate.learning.pipes

import data.boilerplate.structure.HTMLNode
import org.jsoup.nodes.{Document, Element, TextNode}

/**
 * @author Volkan Agun
 */
//The goal is to construct a pipeline feature extractor from tree paths
class PipeOp(var subpipes: Array[PipeOp], var name: String) extends Serializable {

  def this() = {
    this(Array(), "ROOT")
  }

  def op(pipeOp: PipeOp): this.type = {
    subpipes :+= pipeOp
    this
  }

  def op(pipeOps: Array[PipeOp]): this.type = {
    subpipes ++= pipeOps
    this
  }

  def op(pipeOp: PipeOp, opName: String): PipeOp = {
    (this, pipeOp, opName) match {
      case (t: ExistsOp, n: ExistsOp, "exists") => {
        t.op(n.pipes)
      }
      case (t: SumOp, n: SumOp, "sum") => {
        t.op(n.pipes)
      }
      case (t: AvgOp, n: AvgOp, "avg") => {
        t.op(n.pipes)
      }
      case (t: PipeOp, n: PipeOp, _) => {
        t.op(n)
        t
      }
      case (_, _, _) => throw new UnsupportedOperationException()
    }
  }

  def charString(charCount: Int): String = {

    name +
      (if (!subpipes.isEmpty) {
        val space = Range(0, charCount).toArray.map(i => " ").mkString("")
        "[" + "\n" + subpipes.map(oo => space + oo.charString(charCount + name.length)).mkString("\n") + "]"
      } else {
        ""
      })
  }

  override def toString(): String = {
    charString(name.length)
  }


  def sum(item: PipeOp): PipeOp = {
    op(SumOp(Array(item)), "sum")
  }

  def sum(items: PipeOp*): PipeOp = {
    op(SumOp(items.toArray), "sum")
  }


  //generate a count operations class
  //diverge to different classes for operations build a tree like structure

  def max(item: PipeOp): PipeOp = ???

  def pattern(regexPattern: String): PipeOp = {
    op(HTMLPatternOp(regexPattern))
  }

  def pattern(patternOp: PatternOp): PipeOp = {
    op(patternOp)
  }

  def ++(pipe: PipeOp): PipeOp = ???

  def exists(pipeOp: PatternOp): PipeOp = {
    op(ExistsOp(Array(pipeOp)), "exists")
  }

  def operate(): PipeResult = ???


  def newName(parent: PipeOp): PipeOp = {
    this.name = parent.name + "#" + this.name
    this.subpipes = this.subpipes.map(subPipe => subPipe.newName(this))
    this
  }

  //operations
  def sum(irs: Array[IntermediateResult]): IntermediateResult = {
    var gmap = Map[String, Double]()
    irs.foreach(ir => {
      ir.map.foreach { case (nn, score) => {
        gmap = gmap.updated(nn, gmap.getOrElse(nn, 0.0) + score)
      }
      }
    })

    IntermediateResult(gmap)
  }

  def normalize(irs: Array[IntermediateResult]): IntermediateResult = {
    var gmap = Map[String, Double]()
    irs.foreach(ir => {
      ir.map.foreach { case (nn, score) => {
        gmap = gmap.updated(nn, score)
      }
      }
    })

    IntermediateResult(gmap)
  }

}

object PipeOp extends PipeOp() {
  def apply(): PipeOp = {
    new PipeOp()
  }

  override def sum(items: PipeOp): PipeOp = {
    val itemArray = Array(items)
    SumOp(itemArray)
  }

  override def exists(pipeOp: PatternOp): PipeOp = ExistsOp(Array(pipeOp))
}


class PipeResult extends Serializable {

}

case class IntermediateResult(var map: Map[String, Double] = Map()) extends PipeResult {

  def divide(by: Double): IntermediateResult = {
    val nmap = map.map { case (item, score) => (item, score / by) }
    IntermediateResult(nmap)
  }

  def trimPrefix(name: String): String = {
    val index = name.indexOf("/")
    if (index > -1) name.substring(index + 1)
    else name

  }

  def addPefix(name: String): IntermediateResult = {
    val nmap = map.map { case (item, score) => (name + "/" + item, score) }
    IntermediateResult(nmap)
  }

  def add(ii: IntermediateResult): IntermediateResult = {
    val union = map.keySet ++ ii.map.keySet
    val nmap = union.map(item => {
      val fscore = map.getOrElse(item, 0.0) + ii.map.getOrElse(item, 0.0)
      (item, fscore)
    }).toMap

    IntermediateResult(nmap)
  }

  def add(ii: IntermediateResult, prefix: String): IntermediateResult = {
    val nii = ii.addPefix(prefix)
    val union = map.keySet ++ nii.map.keySet
    val nmap = union.map(item => {
      val fscore = map.getOrElse(item, 0.0) + nii.map.getOrElse(item, 0.0)
      (item, fscore)
    }).toMap

    IntermediateResult(nmap)
  }


  def add(iis: Array[IntermediateResult]): IntermediateResult = {
    val intermediateResult = IntermediateResult()
    iis.foreach(ii => intermediateResult.add(ii))
    intermediateResult
  }

  def add(iis: Array[IntermediateResult], prefix: String): IntermediateResult = {
    val intermediateResult = IntermediateResult()
    iis.foreach(ii => intermediateResult.add(ii, prefix))
    intermediateResult
  }

  def avgOp(iis: Array[IntermediateResult], prefix: String): IntermediateResult = {
    add(iis, prefix).avgOp(prefix)
  }

  def avgOp(prefix: String): IntermediateResult = {
    val keys = map.keySet.filter(item => item.startsWith(prefix))
    val nmap = map.filter { case (item, score) => !keys.contains(item) } ++ map.filter { case (item, score) => keys.contains(item) }
      .map { case (item, score) => (trimPrefix(item), score / keys.size) }
    IntermediateResult(nmap)
  }

  def sumOp(iis: Array[IntermediateResult], prefix: String, name: String): IntermediateResult = {
    add(iis, prefix).sumOp(prefix, name)
  }

  def sumOp(prefix: String, newName: String): IntermediateResult = {
    val keys = map.keySet.filter(item => item.startsWith(prefix))
    val sum = keys.map(item => {
      map.getOrElse(item, 0.0)
    }).sum

    val nmap = map.filter { case (item, score) => !keys.contains(item) } + (newName -> sum)
    IntermediateResult(nmap)
  }

  def sumAvgOp(prefix: String, newName: String): IntermediateResult = {
    val keys = map.keySet.filter(item => item.startsWith(prefix))
    val sum = keys.map(item => {
      map.getOrElse(item, 0.0)
    }).sum

    val nmap = map.filter { case (item, _) => !keys.contains(item) } + (newName -> sum / keys.size)
    IntermediateResult(nmap)
  }
}

abstract class ExecutableOp(pipes: Array[PipeOp], name: String) extends PipeOp(pipes, name) {


  override def max(item: PipeOp): PipeOp = ???

  override def pattern(regexPattern: String): PipeOp = ???

  override def ++(pipe: PipeOp): PipeOp = ???

  override def operate(): PipeResult = ???

  def sequence(leafSequence: Array[HTMLNode]): Array[IntermediateResult] = {
    leafSequence.map(leafNode => execute(leafNode))
  }

  def execute(leaf: HTMLNode): IntermediateResult


}

class PatternOp(subpipes: Array[PipeOp], name: String) extends ExecutableOp(subpipes, name) {

  def this(subpipes: Array[PipeOp]) = this(subpipes, "exist-op")


  def exists(leaf: HTMLNode): Double = {
    val boolean = subpipes.map(pipe => pipe match {
      case e: PatternOp => (pipe.name, e.exists(leaf))
      case _ => (pipe.name, 0.0)
    }).exists(_._2 > 0)

    if (boolean) 1.0 else 0.0

  }

  override def execute(leaf: HTMLNode): IntermediateResult = {
    val pairs = subpipes.map(pipe => pipe match {
      case e: PatternOp => (pipe.name, e.exists(leaf))
      case _ => (pipe.name, 0.0)
    }).toMap

    IntermediateResult(pairs)
  }


}


case class ExistsOp(pipes: Array[PipeOp], opname: String = "exist-op") extends ExecutableOp(pipes, opname) {

  override def execute(leaf: HTMLNode): IntermediateResult = {
    val mapping = subpipes.map(pipe => pipe match {
      case e: PatternOp => Some((e.name, e.exists(leaf)))
      case _ => None
    }).flatten.toMap

    IntermediateResult(mapping)

  }

}

case class AvgOp(pipes: Array[PipeOp], opname: String) extends ExecutableOp(pipes, opname) {

  def this(subpipes: Array[PipeOp]) = this(subpipes, "avg-op")


  override def execute(leaf: HTMLNode): IntermediateResult = {
    val iirs = subpipes.map(pipe => pipe match {
      case e: ExecutableOp => Some(e.execute(leaf))
      case _ => None
    }).flatten

    val fiir = IntermediateResult()
    fiir.avgOp(iirs, name)
  }

}

case class SumOp(pipes: Array[PipeOp], opname: String = "sum-op") extends ExecutableOp(pipes, opname) {


  override def execute(leaf: HTMLNode): IntermediateResult = {
    val prefix = "sum-op"
    val pairs = subpipes.map(pipe => pipe match {
      case e: ExecutableOp => Some(e.execute(leaf))
      case _ => None
    }).flatten

    IntermediateResult().sumOp(pairs, name, toString)
  }


}

case class RecursivePatternOp(regex: String, subs: Array[PipeOp]) extends PatternOp(subs, s"recursive-op[${regex}]") {

  //count the frequency inside all regex
  override def execute(leaf: HTMLNode): IntermediateResult = {
    val html = leaf.node.asInstanceOf[Element].html()
    val matches = regex.r.findAllIn(html)
    val irss = matches.map(matchString => {
      val element = new HTMLNode(new Document(leaf.node.baseUri()).html(matchString))
      val counts = subs.map(subpipe => {
        subpipe match {
          case e: PatternOp => Some(e.execute(element))
          case _ => None
        }
      }).flatten

      normalize(counts)
    }).toArray

    sum(irss)
  }
}


case class HTMLPatternOp(regex: String) extends PatternOp(Array(), s"html-regex-op[${regex}]") {

  def exists(regex: String): HTMLPatternOp = {
    HTMLPatternOp(regex)
  }

  //count the frequency
  override def execute(leaf: HTMLNode): IntermediateResult = {
    val html = leaf.node.asInstanceOf[Element].html()
    val matches = regex.r.findAllIn(html)
    val map = Map(name -> matches.length.toDouble)
    IntermediateResult(map)
  }


  //boolean contains
  override def exists(leaf: HTMLNode): Double = {
    val html = leaf.node.asInstanceOf[Element].html()
    val matches = regex.r.findAllIn(html)
    if (matches.length > 0) 1.0 else 0.0
  }

}


object OpTester {
  def main(args: Array[String]): Unit = {
    val op = PipeOp().pattern(HTMLPatternOp("[ab]"))
      .exists(HTMLPatternOp("[p]"))
      .sum(PipeOp.exists(HTMLPatternOp("[p]")), PipeOp.exists(HTMLPatternOp("[div]")))
    println(op.toString())
  }
}
