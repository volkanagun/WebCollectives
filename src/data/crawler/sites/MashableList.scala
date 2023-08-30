package data.crawler.sites

import data.crawler.web.{LookupJSON, LookupOptions, LookupPattern, LookupSplit, WebFlow, WebTemplate}

import java.util.concurrent.{ExecutorService, Executors}


object MashableList {

  def build(mainFolder: String): WebFlow = {
    val articleTemplate: WebTemplate = new WebTemplate(mainFolder, "blog-text", LookupOptions.EMPTYDOMAIN)
    val mainPattern: LookupPattern = new LookupJSON(LookupOptions.CONTAINER, LookupOptions.ARTICLE, "new").addPattern(new LookupJSON(LookupOptions.TEXT, LookupOptions.DATE, "post_date")).addPattern(new LookupJSON(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "title")).addPattern(new LookupJSON(LookupOptions.TEXT, LookupOptions.GENRE, "channel")).addPattern(new LookupJSON(LookupOptions.TEXT, LookupOptions.AUTHOR, "author")).addPattern(new LookupJSON(LookupOptions.ARTICLE, LookupOptions.ARTICLE, "content").setType(LookupOptions.SKIP).addPattern(new LookupJSON(LookupOptions.ARTICLE, LookupOptions.ARTICLETEXT, "plain").addPattern(new LookupSplit(LookupOptions.TEXT, LookupOptions.ARTICLEPARAGRAPH, "\n\n"))))
    articleTemplate.addSeed("https://mashable.com/stories.json?").setNextPageSuffix("page=").setType("BLOG-DOC").setMultipleIdentifier(LookupOptions.ARTICLETITLE).setNextPageSize(100).setNextPageStart(1).setThreadSize(4).setLookComplete(true).setHtmlSaveFolder(LookupOptions.HTMLDIRECTORY).setMainPattern(mainPattern)
    val flow: WebFlow = new WebFlow(articleTemplate)
    return flow
  }

  def main(args: Array[String]): Unit = {
    val service: ExecutorService = Executors.newFixedThreadPool(5)
    WebFlow.submit(service, build(LookupOptions.BLOGENGDIRECTORY))
    service.shutdown()
  }
}