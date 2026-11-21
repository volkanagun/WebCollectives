package data.crawler.sites;

import data.crawler.web.*;

import java.io.Serializable;

public class NTV implements Serializable {

    public static WebFlow build() {

        String domain = "https://www.ntv.com.tr";
        Integer pageCount = 2;

        WebFunctionCall closeCall = new WebButtonClickCall(3, "img.pgnn-d-vignette-close-button")
                .setDoStopOnError(false);

        WebFunctionCall scrollCall = new WebFunctionScrollHeight(1).setMinusHeight(0).setWaitTime(500);
        WebFunctionCall clickCall = new WebButtonClickCall(3, "a.infinite-link").setWaitTime(500);

        WebFunctionCall sequenceCall = new WebFunctionSequence(pageCount, closeCall, scrollCall, clickCall)
                .setTryOnError(3)
                .setDoFirefox(false).setWaitBetweenCalls(200L)
                .setDoStopOnError(true)
                .initialize()
                .setWaitTime(100);

        LookupPattern linkPattern = new LookupPattern(LookupOptions.URL, LookupOptions.MAINPAGE, "<div class=\"col-span-1 md:col-span-3 min-w-0\">", "</div>")
                .setStartEndMarker("<div", "</div>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a(.*?)href=\"", "\""));

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-links", domain)
                .addSeed("breaking", "https://www.ntv.com.tr/son-dakika")
                .addSeed("economy", "https://www.ntv.com.tr/ekonomi")
                .addSeed("world", "https://www.ntv.com.tr/dunya")
                .addSeed("turkiye", "https://www.ntv.com.tr/turkiye")
                .addSeed("trending", "https://www.ntv.com.tr/son-dakika")
                .addSeed("travel", "https://www.ntv.com.tr/seyahat")
                .addSeed("technology", "https://www.ntv.com.tr/teknoloji")
                .addSeed("general", "https://www.ntv.com.tr/yasam")
                .addSeed("health", "https://www.ntv.com.tr/saglik")
                .addSeed("arts", "https://www.ntv.com.tr/sanat")
                .addSeed("auto", "https://www.ntv.com.tr/otomobil")
                .addSeed("education", "https://www.ntv.com.tr/egitim")
                .setDoFast(false)
                .setSleepTime(1500L)
                .setDoDeleteStart(true).setNextPageSuffix("?sayfa=")
                .setThreadSize(1)
                .setDomain(domain)
                .setNextPageStart(1).setNextPageSize(pageCount)
                //.setFunctionCall(sequenceCall)
                .setMainPattern(linkPattern);

        LookupPattern articleLookup = new LookupPattern(LookupOptions.ARTICLE, LookupOptions.CONTAINER, "<div class=\"w-full container-infinity relative\">", "</div>")
                .setStartEndMarker("<div", "</div>")
                .setNth(0)
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1(.*?)>", "</h1>")
                        .setNth(0)
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.AUTHOR, "<p>", "</p>")
                        .setNth(0)
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.DATE, "<span>", "</span>")
                        .setNth(0)
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.CONTAINER, "<div class=\"w-full flex(.*?)\">","</div>")
                        .setStartEndMarker("<div","</div>")
                        .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLEPARAGRAPH, "<p>", "</p>")
                                .setRemoveTags(true)));

        WebTemplate articleTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-text", domain)
                .setType(LookupOptions.ARTICLEDOC)
                .setLookComplete(false).setSleepTime(500L)
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
