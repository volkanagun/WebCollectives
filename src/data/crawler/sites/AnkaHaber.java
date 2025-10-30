package data.crawler.sites;

import data.crawler.web.*;

public class AnkaHaber {
    public static WebFlow build() {
        String domain = "https://ankahaber.net";
        int pageCount = 15;

        WebFunctionCall functionCall = new WebButtonClickCall(1,"a.btn")
                .setDoStopOnError(false)
                .setWaitTime(500);

        WebFunctionCall scrollCall = new WebFunctionScroll(1,"a.btn").setWaitTime(500);

        WebFunctionCall sequenceCall = new WebFunctionSequence(pageCount, scrollCall, functionCall)
                .setDoFirefox(true)
                .setWaitBetweenCalls(100L)
                .initialize()
                .setWaitTime(100);

        WebFunctionCall scrollJS = new WebFunctionScroll(1,"a.btn").setWaitTime(500);

        WebFunctionCall sequenceScrollCall = new WebFunctionSequence(1, scrollJS)
                .setDoFirefox(true)
                .setWaitBetweenCalls(100L)
                .initialize()
                .setWaitTime(100);

        LookupPattern linkPattern = new LookupPattern(LookupOptions.URL, LookupOptions.MAINPAGE, "<div class=\"container\">", "</div>")
                .setStartEndMarker("<div", "</div>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a(.*?)href=\"", "\""));

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.ARTICLEDIRECTORY, "article-links", domain)
                .addSeed("economy", "https://ankahaber.net/haber/kategori/Ekonomi")
                .addSeed("culture", "https://ankahaber.net/haber/kategori/Bulten")
                .addSeed("turkey", "https://ankahaber.net/haber/kategori/Yurt")
                .addSeed("world", "https://ankahaber.net/haber/kategori/Dis")
                .addSeed("breaking", "https://ankahaber.net/haber/kategori/Gundem")
                .setLinkPattern("(.*?\\d+)", null)
                .setDoFast(false)
                .setDoDeleteStart(true)
                .setSleepTime(1000L)
                .setFunctionCall(sequenceCall)
                .setThreadSize(1)
                .setDomain(domain)
                .setMainPattern(linkPattern);

        LookupPattern articleLookup = new LookupPattern(LookupOptions.ARTICLE, LookupOptions.CONTAINER, "<div class=\"col-lg-12 col-md-12(.*?)\">", "</div>")
                .setStartEndMarker("<div", "</div>")
                .setNth(1)
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.DATE, "<div(.*?)id=\"HaberTarihi\">", "</div>")
                        .setStartEndMarker("<div", "</div>")
                        .setNth(0)
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h3(.*?)>", "</h3>")
                        .setNth(0)
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLETEXT, "<p id=\"HaberDetay\">", "</p>")
                        .setStartEndMarker("<p", "</p>").setNth(0)
                        .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLEPARAGRAPH, "<(p(.*?))>", "</(p|h2)>")
                                .setRemoveTags(true)));

        WebTemplate articleTemplate = new WebTemplate(LookupOptions.ARTICLEDIRECTORY, "article-text", domain)
                .setType(LookupOptions.ARTICLEDOC)
                .setMainContent(true)
                .setLookComplete(true).setDoDeleteStart(false)
                .setThreadSize(1)
                .setFunctionCall(sequenceScrollCall)
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
