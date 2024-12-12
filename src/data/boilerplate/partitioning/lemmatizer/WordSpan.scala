package tagging.lemmatizer

class WordSpan(val start: Int, val end: Int) extends Ordered[WordSpan] {

  var lemma: String = null
  var label: String = null
  var value: String = null

  def toWordGroup():WordGroup = {

    if(this.isInstanceOf[WordGroup]) this.asInstanceOf[WordGroup]
    else {
      val crrGroup = new WordGroup(start, end)
        .setValue(value)
        .setLemma(lemma)
        .setLabel(label)
        .add(this)
      crrGroup
    }
  }

  def getLemma(): String = {
    if (lemma == null) value
    else lemma
  }

  def matchLabel(regex:String):Boolean={
    label.matches(regex)
  }

  def split(split: String): String = {
    if (lemma == null || lemma.length == value.length) {
      value
    }
    else {
      value.substring(0, lemma.length) + split + value.substring(lemma.length)
    }
  }

  def setLemma(lemma: String): this.type = {
    this.lemma = lemma
    this
  }

  def setLabel(label: String): this.type = {
    this.label = label
    this
  }

  def setValue(value: String): this.type = {
    this.value = value
    this
  }

  def contains(other: WordSpan): Boolean = {
    start <= other.start && end >= other.end
  }

  def intersect(other: WordSpan): Boolean = {
    !contains(other) && ((other.start >= start && other.start < end) || (other.end >= start && other.end <= end))
  }

  def length = end - start

  def slice(start: Int, end: Int): String = {
    value.slice(start, end)
  }

  override def compare(that: WordSpan): Int = {
    if (start < that.start) -1
    else if (start == that.start && end > that.end) -1
    else +1
  }

  override def toString = if (lemma == null) value else lemma

  private def canEqual(other: Any): Boolean = other.isInstanceOf[WordSpan]

  override def equals(other: Any): Boolean = other match {
    case that: WordSpan =>
      that.canEqual(this) && lemma == that.lemma && label == that.label && start == that.start && end == that.end
    case _ => false
  }

  def equalsByStart(other:Any):Boolean={
    other match {
      case that:WordSpan => start == that.start
      case that:WordGroup => start == that.start
      case _=> false
    }
  }

  override def hashCode(): Int = {
    val state = Seq(lemma, label, start, end)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}

object WordSpan {
  def compile(array: Array[WordSpan]) = new WordSpan(array.head.start, array.last.end)
    .setValue(array.mkString(" "))

  def dummy(start:Int):WordSpan={
    new WordSpan(start, start+"dummy".length)
      .setValue("dummy")
      .setLabel("dummy")
      .setLemma("dummy")
  }
}

object WordSpanTest {

  def sort(array: Array[WordSpan]): Unit = {
    new RegexTokenizer().consume(array).foreach(ws => println(ws))
  }

  def main(args: Array[String]): Unit = {
    val w1 = new WordSpan(100, 200).setValue("w1")
    val w2 = new WordSpan(150, 165).setValue("w2")

    println("Intersects w1 && w2" + w1.intersect(w2))
    println("Intersects w2 && w1" + w2.intersect(w1))

    val w3 = new WordSpan(100, 190).setValue("w3")
    val w4 = new WordSpan(170, 225).setValue("w4")

    println("Intersects w3 && w4" + w3.intersect(w4))
    println("Intersects w4 && w3" + w4.intersect(w3))

    val w5 = new WordSpan(190, 220).setValue("w5")
    val w6 = new WordSpan(75, 125).setValue("w6")

    println("Intersects w5 && w6" + w5.intersect(w6))
    println("Intersects w6 && w5" +  w6.intersect(w5))

    val array = Array(w1, w2, w3, w4, w5, w6).sorted
    println("Sorted")
    array.foreach(wordSpan => println(wordSpan))
    println("Consumed")
    sort(array)

  }
}
