package data.crawler.sites;

import data.crawler.web.*;

public class Fizikist {
    public static WebFlow build() {

        String domain = "https://www.fizikist.com";
        Integer pageCount = 500;

        WebFunctionCall clickCall = new WebButtonClickControl(1, "button.btn-load-more-posts")
                .setDoStopOnError(true)
                .setWaitTime(1000);

        WebFunctionCall scrollCall = new WebFunctionScrollHeight(1).setWaitTime(1500);
        WebFunctionCall sequenceCall = new WebFunctionSequence(pageCount, scrollCall, clickCall)
                .setDoFirefox(true)
                .setWaitBetweenCalls(1000L)
                .setDoStopOnError(false)
                .initialize()
                .setWaitTime(1000);

        LookupPattern linkPattern = new LookupPattern(LookupOptions.ARTICLELINKCONTAINER, LookupOptions.ARTICLELINKCONTAINER, "<h4 class=\"title\"(.*?)>", "</h4>")
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLELINK, "<a(.*?)href=\"", "\"")
                        .setNth(1));

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-links", domain)
                .addSeed("science", "https://www.fizikist.com/")
                .setDoFast(false)
                .setSleepTime(2500L)
                .setDoDeleteStart(true)
                .setThreadSize(1)
                .setDomain(domain)
                .setFunctionCall(sequenceCall)
                .setMainPattern(linkPattern);

        LookupPattern articleLookup = new LookupPattern(LookupOptions.ARTICLE, LookupOptions.CONTAINER, "<div class=\"col-lg-8\">", "</div>")
                .setStartEndMarker("<div","</div>")
                .setNth(0)
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1(.*?)>", "</h1>")
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.AUTHOR, "<h6 class=\"post-author-name\">", "</h6>")
                        .setNth(0)
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.SKIP, LookupOptions.CONTAINER, "<ul class=\"post-meta-list\">", "</ul>")
                        .setNth(1).addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.DATE, "<li>", "</li>")
                                .setNth(0)
                                .setRemoveTags(true)))
                .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLETEXT, "<div class=\"axil-post-details\">", "</div>")
                        .setStartEndMarker("<div", "</div>").setNth(0)
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
