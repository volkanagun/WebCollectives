package tagging.lemmatizer

import scala.util.control.Breaks

class WordGroup(override val start:Int, override val end:Int) extends WordSpan(start, end) {
  var items = Array[WordSpan]()
  def add(wordSpan:WordSpan):this.type ={
    if(wordSpan.isInstanceOf[WordGroup]){
      wordSpan.asInstanceOf[WordGroup].items.foreach(subSpan=> add(subSpan))
    }
    else if(!items.contains(wordSpan)) items = items :+ wordSpan

    this
  }

  def add(wordSpans: Array[WordSpan]):this.type ={
    wordSpans.foreach(wordSpan=> add(wordSpan))
    this
  }
  def prune():WordGroup={
    val sortList = items
    val labelMap = sortList.groupBy(wordSpan=> wordSpan.label)
    val newList = labelMap.map(pair=> pair._2.head).toArray
    val newGroup = new WordGroup(start, end).add(newList)
    newGroup
  }


  override def toString: String = {
    items.mkString("[",",","]")
  }

  def getItems() = items
  def getItem(j:Int) = items(j)

  def size = items.size
  def lemmaList():Array[String]={
    items.map(wordSpan=> {
      wordSpan.getLemma()
    })
  }

  def lemmaSplitList(split:String):Array[String]={
    items.map(wordSpan => {
      wordSpan.split(split)
    })
  }

  /*def toWordSpan():WordSpan = {
    val value = items.map(word=> word.value).mkString(" ")
    val lemma = items.map(word=> word.lemma).mkString(" ")
    new WordSpan(start, end)
      .setValue(value)
      .setLemma(lemma)
      .setLabel(label)
  }*/
}


object WordGroup{

  def dummy(start: Int): WordGroup = {
    new WordGroup(start, start + "dummy".length)
      .setLabel("dummy")
      .setLemma("dummy")
      .setValue("dummy")
  }

  def apply(start:Int, text: String): WordGroup = {
    new WordGroup(start, start + text.length)
      .setLabel("NULL")
      .setLemma("NULL")
      .setValue(text)
  }


  def apply(array: Array[WordSpan]): Array[WordGroup] = {
    val spans = array.sorted
    var selected = Array[WordGroup]()
    var crrIndex = 0
    while (crrIndex < spans.length) {
      val crrSpan = spans(crrIndex).toWordGroup()
      var nextIndex = crrIndex + 1
      val breaks = new Breaks()
      breaks.breakable {
        while (nextIndex < spans.length) {
          val nextSpan = spans(nextIndex)

          if (!crrSpan.contains(nextSpan)) {
            breaks.break()
          }
          else if(crrSpan.equalsByStart(nextSpan)){
            crrSpan.add(nextSpan)
            crrIndex += 1
          }
          else{
            crrIndex +=1
          }

          nextIndex += 1
        }
      }

      selected = selected :+ crrSpan
      crrIndex += 1
    }
    selected
  }
}


