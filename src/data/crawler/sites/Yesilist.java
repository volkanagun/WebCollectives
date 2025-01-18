package data.crawler.sites;

import data.crawler.web.*;

public class Yesilist {
    public static WebFlow build() {

        String domain = "https://www.yesilist.com";
        Integer pageCount = 10;

        WebFunctionCall clickCall = new WebButtonClickCall(1, "li.previous > a")
                .setDoStopOnError(true)
                .setWaitTime(1000);

        WebFunctionCall sequenceCall = new WebFunctionAggregate(clickCall,pageCount)
                .setDoFirefox(true)
                .setWaitBetweenCalls(1000L)
                .setDoStopOnError(false)
                .initialize()
                .setWaitTime(1000);

        LookupPattern linkPattern = new LookupPattern(LookupOptions.ARTICLELINKCONTAINER, LookupOptions.ARTICLELINKCONTAINER, "<h2 class=\"entry-title\">", "</h2>")
                .setStartEndMarker("<h2","</h2>")
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLELINK, "<a(.*?)href=\"", "\"")
                        .setNth(0));

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-links", domain)
                .addSeed("trends", "https://www.yesilist.com/kategori/hayat/moda/")
               /* .addSeed("child", "https://www.yesilist.com/kategori/anne-ve-cocuk/")
                .addSeed("environment", "https://www.yesilist.com/kategori/ekoloji/")
                .addSeed("environment", "https://www.yesilist.com/kategori/gida/")
                .addSeed("city", "https://www.yesilist.com/kategori/kent/")
                .addSeed("health", "https://www.yesilist.com/kategori/hayat/")*/
                .setDoFast(false)
                .setSleepTime(2500L)
                .setDoDeleteStart(true)
                .setThreadSize(1)
                .setDomain(domain)
                .setFunctionCall(sequenceCall)
                .setMainPattern(linkPattern);

        LookupPattern articleLookup = new LookupPattern(LookupOptions.ARTICLE, LookupOptions.CONTAINER, "<article(.*?)>", "</article>")
                .setNth(0)
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1(.*?)>", "</h1>")
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.AUTHOR, "<span class=\"author vcard\">", "</span>")
                        .setNth(0)
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.SKIP, LookupOptions.CONTAINER, "<span class=\"posted-on\">", "</span>")
                        .setNth(1).addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.DATE, "<time(.*?)>", "</time>")
                                .setNth(0)
                                .setRemoveTags(true)))
                .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLETEXT, "<div class=\"entry-content clearfix\">", "</div>")
                        .setStartEndMarker("<div", "</div>")
                        .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLEPARAGRAPH, "<p>", "</p>")
                                .setRemoveTags(true)));

        WebTemplate articleTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-text", domain)
                .setType(LookupOptions.ARTICLEDOC)
                .setLookComplete(false).setSleepTime(2500L).setWaitTime(1000L)
                .setThreadSize(1)
                .setDoFast(false)
                .setDomain(domain)
                .setHtmlSaveFolder(LookupOptions.HTMLDIRECTORY)
                .setMainPattern(articleLookup)
                .setForceWrite(false);

        linkTemplate.addNext(articleTemplate, LookupOptions.ARTICLELINK);
        WebFlow flow = new WebFlow(linkTemplate);

        return flow;
    }

    public static void main(String[] args) {
        build().execute();
    }
}
