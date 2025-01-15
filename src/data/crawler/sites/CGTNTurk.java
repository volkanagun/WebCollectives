package data.crawler.sites;

import data.crawler.web.*;

public class CGTNTurk {
    public static WebFlow build() {
        String domain = "https://www.cgtnturk.com";
        int pageCount = 5;

        WebFunctionCall functionCall = new WebButtonClickCall(1, "a[rel=\"next\"]")
                .setDoStopOnError(false)
                .setWaitTime(100);

        WebFunctionCall scrollCall = new WebFunctionScroll(1, "a.page-link");
        WebFunctionCall sequenceCall = new WebFunctionSequence(1, scrollCall, functionCall)
                .setDoFirefox(true)
                .setWaitTime(1500);

        WebFunctionCall aggregateCall = new WebFunctionAggregate(sequenceCall, pageCount)
                .setWaitBetweenCalls(500L)
                .setDoFirefox(true)
                .initialize()
                .setDoStopOnError(true);


        LookupPattern linkPattern = new LookupPattern(LookupOptions.URL, LookupOptions.MAINPAGE, "<div class=\"col-lg-8 mb-3\">", "</div>")
                .setStartEndMarker("<div", "</div>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a(.*?)href=\"", "\""));

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.CUMHURIYETDIRECTORY, "article-links", domain)
                .addSeed("turkey", "https://www.cgtnturk.com/turkiye")
                .addSeed("world", "https://www.cgtnturk.com/dunya")
                .addSeed("china", "https://www.cgtnturk.com/cin")
                .addSeed("economy", "https://www.cgtnturk.com/ekonomi")
                .addSeed("news", "https://www.cgtnturk.com/yurt")
                .addSeed("sports", "https://www.cgtnturk.com/spor")
                .addSeed("health", "https://www.cgtnturk.com/yasam")
                .addSeed("health", "https://www.cgtnturk.com/saglik")
                .addSeed("culture", "https://www.cgtnturk.com/kultur")
                .addSeed("technology", "https://www.cgtnturk.com/bilim-teknoloji")
                .addSeed("news", "https://www.cgtnturk.com/ozel-haber")
                .setDoFast(false)
                .setDoDeleteStart(true)
                .setSleepTime(1000L)
                .setFunctionCall(aggregateCall)
                .setThreadSize(1)
                .setDomain(domain)
                .setMainPattern(linkPattern);

        LookupPattern articleLookup = new LookupPattern(LookupOptions.ARTICLE, LookupOptions.CONTAINER, "<main class=\"single\">", "</main>")
                .setStartEndMarker("<main", "</main>")
                .setNth(0)
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.AUTHOR, "cgtnturk"))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.DATE, "<time(.*?)>", "</time>")
                        .setNth(0)
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1(.*?)>", "</h1>")
                        .setNth(0)
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLETEXT, "<div class=\"col-lg-8\">", "</div>")
                        .setStartEndMarker("<div", "</div>").setNth(0)
                        .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLEPARAGRAPH, "<(p|h2(.*?))>", "</(p|h2)>").setRemoveTags(true)));

        WebTemplate articleTemplate = new WebTemplate(LookupOptions.ARTICLEDIRECTORY, "article-text", domain)
                .setType(LookupOptions.ARTICLEDOC)
                .setMainContent(true)
                .setLookComplete(true).setDoDeleteStart(false)
                .setThreadSize(1)
                .setDoFast(false)
                .setSleepTime(1000L)
                .setDomain(domain)
                .setHtmlSaveFolder(LookupOptions.HTMLDIRECTORY)
                .setMainPattern(articleLookup)
                .setForceWrite(true);

        linkTemplate.addNext(articleTemplate, LookupOptions.ARTICLELINK);

        WebFlow flow = new WebFlow(linkTemplate);
        return flow;
    }

    public static void main(String[] args) {
        build().execute();
    }
}
