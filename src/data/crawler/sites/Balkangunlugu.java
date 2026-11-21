package data.crawler.sites;

import data.crawler.web.*;

public class Balkangunlugu {
    public static WebFlow build() {

        String domain = "https://balkangunlugu.com.tr";
        Integer pageCount = 2;

        WebFunctionCall clickCall = new WebButtonClickControl(1, "button.btn-load-more")
                .setDoStopOnError(false)
                .setWaitTime(100);

        WebFunctionCall scrollCall = new WebFunctionScrollHeight(1).setDoStopOnError(false).setWaitTime(500);
        WebFunctionCall sequenceCall = new WebFunctionSequence(pageCount,  scrollCall, clickCall)
                .setTryOnError(5)
                .setDoFirefox(false)
                .setWaitBetweenCalls(50L)
                .setDoStopOnError(true)
                .initialize()
                .setWaitTime(50);

        LookupPattern linkPattern = new LookupPattern(LookupOptions.ARTICLELINKCONTAINER, LookupOptions.ARTICLELINKCONTAINER, "<div class=\"post-item\">","</div>")
                .setStartEndMarker("<div","</div>")
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLELINK, "<a(.*?)href=\"", "\""));

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-links", domain)
                .addSeed("economy", "https://balkangunlugu.com.tr/kategori/ekonomi/")
                .addSeed("education", "https://balkangunlugu.com.tr/kategori/egitim/")
                .addSeed("politics", "https://balkangunlugu.com.tr/kategori/siyaset/")
                .addSeed("technology", "https://balkangunlugu.com.tr/kategori/teknoloji-haberler/")
                .addSeed("health", "https://balkangunlugu.com.tr/kategori/saglik-haberler/")
                .addSeed("news", "https://balkangunlugu.com.tr/kategori/gundem/")
                .addSeed("sports", "https://balkangunlugu.com.tr/kategori/spor/")

                .setDoFast(false)
                .setSleepTime(500L)
                .setDoDeleteStart(true)
                .setThreadSize(1)
                .setDomain(domain)
                .setFunctionCall(sequenceCall)
                .setMainPattern(linkPattern);

        LookupPattern articleLookup = new LookupPattern(LookupOptions.ARTICLE, LookupOptions.CONTAINER, "<div class=\"post-content\">", "</div>")
                .setStartEndMarker("<div", "</div>")
                .setNth(0)
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.DATE, "<div class=\"item-meta item-meta-date\">", "</div>")
                        .setNth(0)
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1(.*?)>", "</h1>")
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLETEXT, "<div class=\"post-text mt-4\">", "</div>")
                        .setNth(0).setStartEndMarker("<div", "</div>")
                        .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLEPARAGRAPH, "<p>", "(</p>|\n)")
                                .setRemoveTags(true)));

        WebTemplate articleTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-text", domain)
                .setType(LookupOptions.ARTICLEDOC)
                .setLookComplete(false).setSleepTime(500L).setWaitTime(1000L)
                .setThreadSize(1)
                //.setFunctionCall(emptyCall)
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
