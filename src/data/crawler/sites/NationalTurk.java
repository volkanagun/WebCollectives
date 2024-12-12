package data.crawler.sites;

import data.crawler.web.*;

public class NationalTurk {
    public static WebFlow build() {
        String domain = "https://www.nationalturk.com";
        int pageCount = 20;

        WebFunctionCall functionCall = new WebButtonClickCall(1,"a.show-more-button")
                .setDoStopOnError(false)
                .setWaitTime(500);

        WebFunctionCall scrollCall = new WebFunctionScroll(1,"a.show-more-button")
                .setWaitTime(500);

        WebFunctionCall sequenceCall = new WebFunctionSequence(pageCount, scrollCall, functionCall)
                .setDoFirefox(true)
                .setWaitBetweenCalls(500L)
                .initialize()
                .setWaitTime(500);

        LookupPattern linkPattern = new LookupPattern(LookupOptions.URL, LookupOptions.MAINPAGE, "<div class=\"masonry-grid-wrapper masonry-with-spaces\">", "</div>")
                .setStartEndMarker("<div", "</div>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a(.*?)href=\"", "\""));

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.ARTICLEDIRECTORY, "article-links", domain)
                .addSeed("economy", "https://www.nationalturk.com/ekonomi/")
                .addSeed("culture", "https://www.nationalturk.com/kultur/")
                .addSeed("technology", "https://www.nationalturk.com/gelecek/")
                .addSeed("life", "https://www.nationalturk.com/yasam/")
                .addSeed("travel", "https://www.nationalturk.com/seyahat/")
                .addSeed("sports", "https://www.nationalturk.com/spor/")
                .addSeed("news", "https://www.nationalturk.com/haberler/")

                .setDoFast(false)
                .setDoDeleteStart(true)
                .setSleepTime(1000L)
                .setFunctionCall(sequenceCall)
                .setThreadSize(1)
                .setDomain(domain)
                .setMainPattern(linkPattern);

        LookupPattern articleLookup = new LookupPattern(LookupOptions.ARTICLE, LookupOptions.CONTAINER, "<div class=\"main-content(.*?)\">", "</div>")
                .setStartEndMarker("<div", "</div>")
                .setNth(1)
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.DATE, "<span class=\"date meta-item tie-icon\">", "</div>")
                        .setStartEndMarker("<div", "</div>")
                        .setNth(0)
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1 class=\"post-title entry-title\">", "</h1>")
                        .setNth(0)
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLETEXT, "<div class=\"entry-content entry clearfix\">", "</div>")
                        .setStartEndMarker("<div", "</div>")
                        .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLEPARAGRAPH, "<(p|h2)>", "</(p|h2)>")
                                .setRemoveTags(true)));

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
