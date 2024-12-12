package tagging.hmm

case class Pair(item:Int, item2:Int) extends Serializable{
  override def hashCode(): Int = {
    var result = 7
    result = item + 7 * result
    result = item2 + 7 * result
    result
  }

  override def equals(obj: Any): Boolean = {
    val objPair = obj.asInstanceOf[Pair]
    objPair.item.equals(item) && objPair.item2.equals(item2)
  }

  override def toString: String = item + "->" + item2
}
