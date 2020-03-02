package data.boilerplate.learning.pipes

import com.sun.xml.internal.bind.api.impl.NameConverter.Standard
import data.boilerplate.learning.features.{CSSAttributeOp, HTMLPatternOp, ParentTextDensityOp, TagNameOp, TagTextDensityOp, TextDensityOp}
import data.boilerplate.structure.{HTMLNode, HTMLParser, HTMLPath}
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute
import org.jsoup.nodes.{Attribute, Comment, DataNode, Document, Element, TextNode}

/**
 * @author Volkan Agun
 */
//The goal is to construct a pipeline feature extractor from tree paths
class PipeOp(var subpipes: Array[PipeOp], var name: String) extends Serializable {

  def this() = {
    this(Array(), "ROOT")
  }

  def canApply(htmlNode: HTMLNode): Boolean = {
    true
  }

  def op(pipeOp: PipeOp): this.type = {
    subpipes :+= pipeOp
    this
  }

  def op(pipeOps: Array[PipeOp]): this.type = {
    subpipes ++= pipeOps
    this
  }

  def op(pipeOps: PipeOp*): this.type = {
    subpipes ++= pipeOps
    this
  }

  def execute(leaf: HTMLNode): IntermediateResult = {
    val iirs = subpipes.filter(op => op.canApply(leaf)).map(op => op.execute(leaf))
    sum(iirs)
  }

  def execute(path: HTMLPath): IntermediateResult = {
    val leaf = path.pathNodes.last
    val iirs = subpipes.filter(op => op.canApply(leaf))
      .map(op => op.execute(leaf))

    sum(iirs)
  }

  def execute(paths: Array[HTMLPath]): Array[IntermediateResult] = {
    paths.map(path => {
      execute(path)
    })
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

  def extractHTMLorData(htmlNode: HTMLNode): String = {
    if (isElement(htmlNode)) htmlNode.node.asInstanceOf[Element].html()
    else if (isData(htmlNode)) htmlNode.node.asInstanceOf[DataNode].getWholeData
    else if (isComment(htmlNode)) htmlNode.node.asInstanceOf[Comment].getData
    else ""
  }

  def isElement(htmlNode: HTMLNode): Boolean = {
    htmlNode.node.isInstanceOf[Element]
  }

  def isComment(htmlNode: HTMLNode): Boolean = {
    htmlNode.node.isInstanceOf[Comment]
  }

  def isData(htmlNode: HTMLNode): Boolean = {
    htmlNode.node.isInstanceOf[DataNode]
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


class PipeResult(val vocabSize: Int = 1000, val labelSize:Int=100) extends Serializable {
  var vocabulary = Array[String]("UNK")
  var labels = Array[String]("UNK")

  def append(label:String*):Array[Int]={
    label.foreach(ll=> {
      val ii = labels.indexOf(ll)
      if(ii == -1){
        labels :+= ll
      }

    })

    label.map(ll=> labels.indexOf(ll)).toArray
  }

  def append(iir: IntermediateResult): Array[(Int, Double)] = {
    val items = updateVocabulary(iir.map.keySet)
    items.map{case (key, indice)=> {
      (indice, iir.map(key))
    }}.toArray
  }

  def updateVocabulary(keySet: Set[String]): Set[(String, Int)] = {
    keySet.foreach(key => if (!vocabulary.contains(key) && vocabulary.length < vocabSize) vocabulary :+= key)
    keySet.map(key => {
      val index = vocabulary.indexOf(key);
      val nindex = if(index == -1) 0
      else index

      (key, nindex)
    })
  }

}

case class IntermediateResult(var map: Map[String, Double] = Map()) extends PipeResult() {


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
    map = union.map(item => {
      val fscore = map.getOrElse(item, 0.0) + nii.map.getOrElse(item, 0.0)
      (item, fscore)
    }).toMap

    this
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
    val sum = keys.toArray.map(item => {
      map.getOrElse(item, 0.0)

    }).sum

    val nmap = map.filter { case (item, score) => !keys.contains(item) } + (newName -> sum)
    IntermediateResult(nmap)
  }

  def sumAvgOp(prefix: String, newName: String): IntermediateResult = {
    val keys = map.keySet.filter(item => item.startsWith(prefix))
    val sum = keys.toArray.map(item => {
      map.getOrElse(item, 0.0)
    }).sum

    val nmap = map.filter { case (item, _) => !keys.contains(item) } + (newName -> sum / keys.size)
    IntermediateResult(nmap)
  }

  override def toString: String = {
    map.map { case (name, score) => name + "-->" + score.toString }.mkString("\n")
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


}

class PatternOp(subpipes: Array[PipeOp], name: String) extends ExecutableOp(subpipes, name) {

  def this(subpipes: Array[PipeOp]) = this(subpipes, "exist-op")

  override def canApply(htmlNode: HTMLNode): Boolean = {
    isElement(htmlNode)
  }

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


  def rpatterns(regex: String, text: String): Array[String] = {
    var array = Array[String]()
    regex.r.findAllMatchIn(text).foreach(matching => array :+= matching.group(0))
    array
  }

  def rpatternAvgCharLength(regex: String, text: String): Double = {
    var totalSize = 0
    var cnt = 0;
    rpatterns(regex, text).foreach(matching => {
      totalSize += matching.length
      cnt += 1
    })

    totalSize.toDouble / cnt

  }

  def rpatternTotalCharLength(regex: String, text: String): Double = {
    var totalSize = 0
    rpatterns(regex, text).foreach(matching => {
      totalSize += matching.length
    })

    totalSize.toDouble

  }

}


case class ExistsOp(pipes: Array[PipeOp], opname: String = "exist-op") extends ExecutableOp(pipes, opname) {


  override def canApply(htmlNode: HTMLNode): Boolean = true

  override def execute(leaf: HTMLNode): IntermediateResult = {
    val mapping = subpipes.filter(_.canApply(leaf)).map(pipe => pipe match {
      case e: PatternOp => Some((e.name, e.exists(leaf)))
      case _ => None
    }).flatten.toMap

    IntermediateResult(mapping)

  }

}

case class AvgOp(pipes: Array[PipeOp], opname: String) extends ExecutableOp(pipes, opname) {

  def this(subpipes: Array[PipeOp]) = this(subpipes, "avg-op")


  override def canApply(htmlNode: HTMLNode): Boolean = true

  override def execute(leaf: HTMLNode): IntermediateResult = {
    val iirs = subpipes.filter(_.canApply(leaf)).map(pipe => pipe match {
      case e: ExecutableOp => Some(e.execute(leaf))
      case _ => None
    }).flatten

    val fiir = IntermediateResult()
    fiir.avgOp(iirs, name)
  }

}

case class SumOp(pipes: Array[PipeOp], opname: String = "sum-op") extends ExecutableOp(pipes, opname) {


  override def canApply(htmlNode: HTMLNode): Boolean = true

  override def execute(leaf: HTMLNode): IntermediateResult = {
    val prefix = "sum-op"
    val pairs = subpipes.filter(_.canApply(leaf)).map(pipe => pipe match {
      case e: ExecutableOp => Some(e.execute(leaf))
      case _ => None
    }).flatten

    IntermediateResult().sumOp(pairs, name, toString)
  }


}







object OpTester {

  def model1():PipeOp={
    PipeOp().op(CSSAttributeOp()).op(TagNameOp())
      .sum(HTMLPatternOp("<p(\\s|>)"), HTMLPatternOp("<div(\\s|>)"))
      .op(TextDensityOp(), ParentTextDensityOp(), TagTextDensityOp())
      .op(HTMLPatternOp("<p(\\s|>)"))
  }

  def main(args: Array[String]): Unit = {

    val node = HTMLParser.parseHTML("resources/demo/html.html")

    println(model1().execute(node.visit()).mkString("\n\n--------------------------------------\n"))

  }
}


