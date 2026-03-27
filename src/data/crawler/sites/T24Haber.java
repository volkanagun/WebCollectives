package data.crawler.sites;

import data.crawler.web.*;

public class T24Haber {
    public static WebFlow build() {

        String domain = "https://t24.com.tr";
        int pageCount = 1;

        WebFunctionCall scrollCall1 = new WebFunctionScrollHeight(1, 2500).setWaitTime(500);
        WebButtonClickCall clickcall = new WebButtonClickCall(1, "div.adh-close-1000073");
        WebFunctionCall sequenceCall = new WebFunctionSequence(pageCount, clickcall, scrollCall1)
                .setDoFirefox(true)
                .setWaitBetweenCalls(5000L)
                .initialize()
                .setWaitTime(100);

        LookupPattern linkPattern = new LookupPattern(LookupOptions.URL, LookupOptions.MAINPAGE, "<div class=\"container\"", "</div>")
                .setStartEndMarker("<div", "</div>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a href=\"", "\""));

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-links", domain)
                .addSeed("news", "https://t24.com.tr/politika/")
                /*.addSeed("news", "https://t24.com.tr/gundem/")
                .addSeed("sports", "https://t24.com.tr/spor/")
                .addSeed("economy", "https://t24.com.tr/ekonomi/")
                .addSeed("world", "https://t24.com.tr/dunya/")
                .addSeed("culture", "https://t24.com.tr/kultur-sanat/")*/
                .setFunctionCall(sequenceCall)
                .setDoFast(false)
                .setDoDeleteStart(true)
                .setSleepTime(2500L)
                .setThreadSize(4)
                .setDomain(domain)
                .setMainPattern(linkPattern);

        LookupPattern articleLookup =
                new LookupPattern(LookupOptions.CONTAINER, LookupOptions.ARTICLE, LookupOptions.EMPTY)
                        .addPattern(new LookupPattern(LookupOptions.SKIP, LookupOptions.CONTAINER, "<div class=\"col-md-8 col-xs-12\">", "</div>")
                                .setStartEndMarker("<div", "</div>")
                                .setNth(0)
                                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1(.*?)>", "</h1>")
                                        .setNth(0)))
                        .addPattern(new LookupPattern(LookupOptions.SKIP, LookupOptions.CONTAINER, "<div class=\"_392lz\">", "</div>")
                                .setStartEndMarker("<div", "</div>")
                                .setNth(0)
                                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.DATE, "<p>", "</p>").setNth(0)))
                        .addPattern(new LookupPattern(LookupOptions.ARTICLEDOC, LookupOptions.CONTAINER, "<div class=\"_392lz\"(.*?)>", "</div>")
                                .setStartEndMarker("<div", "</div>")
                                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLEPARAGRAPH, "<p>", "</p>")
                                        .setRemoveTags(true)));

        WebTemplate articleTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-text", domain)
                .setType(LookupOptions.ARTICLEDOC)
                .setLookComplete(false)
                .setThreadSize(1)
                .setDoFast(false)
                .setSleepTime(2000L)
                .setDomain(domain)
                .setFunctionCall(scrollCall1.initialize())
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
