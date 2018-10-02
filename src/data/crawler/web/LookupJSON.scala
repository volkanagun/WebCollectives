package data.crawler.web

import java.util

import org.json4s.{DefaultFormats, JObject}
import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.jackson.Serialization.{read,write}

class UNAPPLY[T] {
  def unapply(a: Any): Option[T] = Some(a.asInstanceOf[T])
}

object M extends UNAPPLY[Map[String, List[String]]]

object L extends UNAPPLY[List[String]]

//May be a xquery fashion implemented dynamically
class LookupJSON(val ttype: String, val llabel: String, val jsonTag: String) extends LookupPattern(ttype, llabel, null) {

  override protected def getResults(propertyMap: util.Map[String, String], partial: String): util.List[String] = {
    implicit val formats = DefaultFormats
    var resultList: util.List[String] = new util.ArrayList[String]()

    if (isValue) {
      if (LookupOptions.TEXT.equals(ttype)) {
        resultList.add(value)
      }
      else if (LookupOptions.LOOKUP.equals(ttype)) {
        if (value != null && propertyMap.containsKey(value)) resultList.add(propertyMap.get(value))
      }
    }
    else {
      val jobject = parse(partial).asInstanceOf[JObject]
      val array = (jobject \ jsonTag)

      val arrayString = array match {
        case a:JString => Array(a.s)
        case b:JArray => array.children.map(jval => write(jval)).toArray
        case c:JObject=> Array(write(c))
        case _ => Array[String]()
      }

      resultList = util.Arrays.asList[String](arrayString:_*)
    }

    if (isNth && nth < resultList.size()) {
      val subList = new util.ArrayList[String]
      var i = nth
      while (i < Math.min(mth, resultList.size)) {
        val result = getReplaces(resultList.get(i))
        subList.add(result)
        i += 1;
      }

      resultList.clear()
      resultList.addAll(subList)
    }

    resultList

  }
}

