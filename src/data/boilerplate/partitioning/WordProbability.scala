package tagging.hmm

import java.io.{ObjectInputStream, ObjectOutputStream}

class Probability(var item:String, var index:Int, var count:Long = 0L, var prob:Double = 0d){

  def inc():Probability= {
    count = count + 1
    this
  }

  def inc(sz:Long):Probability={
    count = count + sz
    this
  }

  def normalize(sum:Long):Probability={
    prob = (count.toDouble + 1) / (count + sum)
    this
  }

  def log():Double={
    Math.log(prob)
  }

  def negLog():Double={
    -Math.log(prob)
  }

  def clone(newProb:Double):Probability= {
    new Probability(item, index, count, newProb)
  }

  override def equals(obj: Any): Boolean = obj.asInstanceOf[Probability].item.equals(item)


  override def hashCode(): Int = {
    val state = Seq(item)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }

  def load(stream:ObjectInputStream):this.type = {
    item = stream.readObject().asInstanceOf[String]
    index = stream.readInt()
    count = stream.readLong()
    prob = stream.readDouble()
    this
  }

  def save(stream:ObjectOutputStream):this.type ={
    stream.writeObject(item)
    stream.writeInt(index)
    stream.writeLong(count)
    stream.writeDouble(prob)
    this
  }

  override def toString = s"$item : $prob"

}

object Probability{
  def apply(stream:ObjectInputStream):Probability ={
    new Probability("", 0, 0).load(stream)
  }
}
