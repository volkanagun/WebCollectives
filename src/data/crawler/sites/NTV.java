package data.crawler.sites;

import data.crawler.web.LookupOptions;
import data.crawler.web.LookupPattern;
import data.crawler.web.WebFlow;
import data.crawler.web.WebTemplate;

import java.io.Serializable;

public class NTV implements Serializable {
    public static WebFlow build() {
        String domain = "https://www.ntv.com.tr";

        LookupPattern linkPattern = new LookupPattern(LookupOptions.URL, LookupOptions.MAINPAGE, "<p class=\"card-text\">", "</p>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a(.*?)href=\"", "\""));

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-links", domain)
                .addSeed("economy", "https://www.ntv.com.tr/ekonomi")
                .addSeed("world", "https://www.ntv.com.tr/dunya")
                .addSeed("trending", "https://www.ntv.com.tr/son-dakika")
                .addSeed("travel", "https://www.ntv.com.tr/seyahat")
                .addSeed("technology", "https://www.ntv.com.tr/teknoloji")
                .addSeed("general", "https://www.ntv.com.tr/yasam")
                .addSeed("health", "https://www.ntv.com.tr/saglik")
                .addSeed("arts", "https://www.ntv.com.tr/sanat")
                .addSeed("news", "https://www.ntv.com.tr/turkiye")
                .addSeed("auto", "https://www.ntv.com.tr/otomobil")
                .addSeed("education", "https://www.ntv.com.tr/egitim")
                .setDoFast(true)
                .setWaitTime(5000L).setWaitTimeAfter(20000L)
                .setDoDeleteStart(true)
                .setThreadSize(1)
                .setDomain(domain)
                .setNextPageSuffix("?sayfa=")
                .setNextPageStart(1)
                .setNextPageSize(200)
                .setMainPattern(linkPattern);

        LookupPattern articleLookup = new LookupPattern(LookupOptions.ARTICLE, LookupOptions.CONTAINER, "<div class=\"category-detail-left(.*?)>", "</div>")
                .setStartEndMarker("<div", "</div>")
                .setNth(0)
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1 class=\"category-detail-title\"(.*?)>", "</h1>")
                        .setNth(0)
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.SKIP, LookupOptions.CONTAINER, "<p class=\"news-info-text\">", "</p>")
                        .setNth(0)
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.AUTHOR, "<span>", "</span>")
                                .setNth(0)
                                .setRemoveTags(true))
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.DATE, "<span>", "</span>")
                                .setNth(1)
                                .setRemoveTags(true)))
                .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLETEXT, "<div class=\"category-detail-content\">", "</div>")
                        .setStartEndMarker("<div", "</div>").setNth(0)
                        .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLEPARAGRAPH, "<p>", "</p>")
                                .setRemoveTags(true)));

        WebTemplate articleTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-text", domain)
                .setType(LookupOptions.ARTICLEDOC)
                .setLookComplete(false)
                .setThreadSize(1)
                .setDoFast(true)
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
