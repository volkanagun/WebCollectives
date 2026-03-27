package data.crawler.sites;

import data.crawler.web.*;

public class Balkangunlugu {
    public static WebFlow build() {

        String domain = "https://balkangunlugu.com.tr";
        Integer pageCount = 1;

        WebFunctionCall clickCall = new WebButtonClickControl(1, "a.categoryLoadMore")
                .setDoStopOnError(true)
                .setWaitTime(1000);

        WebFunctionCall scrollCall = new WebFunctionScrollHeight(1).setWaitTime(1500);
        WebFunctionCall sequenceCall = new WebFunctionSequence(pageCount,  scrollCall, clickCall)
                .setDoFirefox(true)
                .setWaitBetweenCalls(1000L)
                .setDoStopOnError(false)
                .initialize()
                .setWaitTime(1000);

        LookupPattern linkPattern = new LookupPattern(LookupOptions.ARTICLELINKCONTAINER, LookupOptions.ARTICLELINKCONTAINER, "<div class=\"left(.*?)\">","</div>")
                .setStartEndMarker("<div","</div>")
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLELINK, "<a(.*?)href=\"", "\""));

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-links", domain)
                .addSeed("economy", "https://balkangunlugu.com.tr/kategori/ekonomi/")                
                .setDoFast(false)
                .setSleepTime(2500L)
                .setDoDeleteStart(true)
                .setThreadSize(1)
                .setDomain(domain)
                .setFunctionCall(sequenceCall)
                .setMainPattern(linkPattern);

        LookupPattern articleLookup = new LookupPattern(LookupOptions.ARTICLE, LookupOptions.CONTAINER, "<div class=\"haberSingle active\">", "</div>")
                .setStartEndMarker("<div", "</div>")
                .setNth(0)
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.DATE, "<u(.*?)>", "</u>")
                        .setNth(0)
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1>", "</h1>")
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLETEXT, "<div class=\"detail\">", "</div>")
                        .setNth(0).setStartEndMarker("<div", "</div>")
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
