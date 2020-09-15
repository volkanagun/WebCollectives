package data.crawler.web

import java.util

class LookupSplit(ttype: String, llabal: String, splitRegex: String) extends LookupPattern(ttype, llabal, null) {

  override protected def getResults(propertyMap: util.Map[String, String], partial: String): util.List[String] = {

    val array = partial.split(splitRegex)
    if (isNth && nth < array.length) {
      var resultList = new util.ArrayList[String]()
      var i = nth
      while (i < mth) {
        val result = getReplaces(array(i))
        resultList.add(result)
        i += 1
      }

      resultList
    }
    else {
      util.Arrays.asList[String](array: _*)
    }

  }
}
