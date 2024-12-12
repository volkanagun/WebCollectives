package tagging.lemmatizer

import tagging.hmm.{Pair, Probability}

import java.io.{ObjectInputStream, ObjectOutputStream}

case class Table() extends Serializable {
  var map = Map[Int, Probability]()
  var eps = 0.00000000001d

  def get(item:Int, item2:Int):Probability = {
    val id = Pair(item, item2).hashCode()
    if(map.contains(id)) map(id)
    else new Probability(item.toString, item, 1L, eps)
  }

  def update(item:Int, item2:Int):Table={
    val id = Pair(item, item2).hashCode()
    if(map.contains(id)) map(id).inc()
    else map = map.updated(id, new Probability(item.toString, item, 1L))
    this
  }

  def normalize(array:Set[Probability]): Unit ={
    array.foreach(item1 => {
      val cnt = item1.count
      array.foreach(item2 => {
        val pair = Pair(item1.index, item2.index).hashCode()
        if(map.contains(pair)) map(pair).normalize(cnt)
      })
    })
  }

  def normalize(array1:Set[Probability], array2:Set[Probability]): Unit ={
    array1.foreach(item1 => {
      val cnt = item1.count
      array2.foreach(item2 => {
        val pair = Pair(item1.index, item2.index).hashCode()
        if(map.contains(pair)) map(pair).normalize(cnt)
      })
    })
  }

  def load(stream:ObjectInputStream):this.type ={
    val sz = stream.readInt()
    for(i<-0 until sz){
      val index = stream.readInt()
      val prob = Probability(stream)
      map = map + (index -> prob)
    }
    this
  }

  def save(stream:ObjectOutputStream):this.type ={
    val sz = map.size
    stream.writeInt(sz)
    map.foreach{case(index, probability)=>{
      stream.writeInt(index)
      probability.save(stream)
    }}
    this
  }

  def merge(other: Table):Table = {
    other.map.foreach{case(index, prob)=>{
      if(map.contains(index)){
        map(index).inc(prob.count)
      }
      else{
        map = map + (index-> prob)
      }
    }}
    this
  }
}
