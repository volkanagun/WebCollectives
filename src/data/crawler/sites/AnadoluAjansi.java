package data.crawler.sites;

import data.crawler.web.*;

import java.io.Serializable;

public class AnadoluAjansi implements Serializable {
    public static WebFlow build() {

        String domain = "https://www.aa.com.tr/tr";
        Integer pageCount = 1;

        //<a href="#!" class="button-daha text-center">
        //                Devam
        //            </a>
        WebFunctionCall clickCall = new WebButtonClickCall(5, ".button-daha.text-center").setWaitTime(1500);
        WebFunctionCall scrollCall = new WebFunctionScrollHeight(1).setWaitTime(1500);
        WebFunctionCall sequenceCall = new WebFunctionSequence(pageCount, clickCall, scrollCall)
                .setWaitBetweenCalls(2000L)
                .initialize()
                .setWaitTime(1000);

        LookupPattern linkPattern = new LookupPattern(LookupOptions.URL, LookupOptions.MAINPAGE, "<div class=\"konu-alt(.*?)>", "</div>")
                .setStartEndMarker("<div", "</div>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a(.*?)href=\"", "\""));

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-links", domain)
                .addSeed("economy", "https://www.aa.com.tr/tr/ekonomi")
                .addSeed("world", "https://www.aa.com.tr/tr/dunya")
                .addSeed("analysis", "https://www.aa.com.tr/tr/analiz")
                .addSeed("sports", "https://www.aa.com.tr/tr/spor")
                .addSeed("turkey", "https://www.aa.com.tr/tr/turkiye")
                .addSeed("covid", "https://www.aa.com.tr/tr/koronavirus")
                .setDoFast(false)
                .setSleepTime(2500L)
                .setDoDeleteStart(true)
                .setThreadSize(1)
                .setDomain(domain)
                .setFunctionCall(sequenceCall)
                .setMainPattern(linkPattern);

        LookupPattern articleLookup = new LookupPattern(LookupOptions.ARTICLE, LookupOptions.CONTAINER, "<main>", "</main>")
                .setNth(0)
                .addPattern(new LookupPattern(LookupOptions.SKIP, LookupOptions.CONTAINER, "<div class=\"detay-spot-category\">", "</div>")
                        .setNth(0).setStartEndMarker("<div", "</div>")
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1>", "</h1>")
                                .setRemoveTags(true))
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.AUTHOR, "<span(.*?)>", "</span>")
                                .setNth(0)
                                .setRemoveTags(true))
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.DATE, "<span(.*?)>", "</span>")
                                .setNth(1)
                                .setRemoveTags(true)))
                .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLETEXT, "<div class=\"detay-icerik\">", "</div>")
                        .setStartEndMarker("<div", "</div>").setNth(0)
                        .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLEPARAGRAPH, "<p>", "</p>")
                                .setRemoveTags(true)));

        WebTemplate articleTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-text", domain)
                .setType(LookupOptions.ARTICLEDOC)
                .setLookComplete(false).setSleepTime(2500L)
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

