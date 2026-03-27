package data.crawler.sites;

import data.crawler.web.*;

import java.io.Serializable;
import java.util.regex.Pattern;

public class HaberSitesi implements Serializable {

    public static WebFlow build() {
        int startIndice = 1;
        int pageCount = 100;

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-links", "https://www.habersitesi.com")
                .addSeed("ekonomi","https://www.habersitesi.com/ekonomi-haberleri-14hk")
                .addSeed("sports","https://www.habersitesi.com/spor-haberleri-13hk")
                .addSeed("breaking","https://www.habersitesi.com/gundem-haberleri-12hk")
                .addSeed("world","https://www.habersitesi.com/dunya-haberleri-15hk")
                .addSeed("politics","https://www.habersitesi.com/politika-haberleri-17hk")
                .addSeed("education","https://www.habersitesi.com/egitim-haberleri-16hk")
                .addSeed("security","https://www.habersitesi.com/asayis-haberleri-87hk")
                .addSeed("technology","https://www.habersitesi.com/teknoloji-haberleri-19hk")
                .addSeed("health","https://www.habersitesi.com/saglik-haberleri-20hk")
                .addSeed("arts","https://www.habersitesi.com/kultur-sanat-haberleri-22hk")
                .addSeed("turkey","https://www.habersitesi.com/ulke-haberleri-85hk")
                .addSeed("news","https://www.habersitesi.com/yerel-haberler-haberleri-18hk")
                .setNextPageStart(startIndice)
                .setNextPageSize(pageCount)
                .setWaitTimeAfter(100000L)
                .setWaitTime(500L)
                .setSleepTime(500L)
                .setDoFast(true)
                .setDoDeleteStart(true)
                .setNextPageSuffix("-p")
                .setNextPageSize(pageCount)
                .setNextPageSuffixAddition(".htm")
                .setThreadSize(8);

        LookupPattern linkPattern = new LookupPattern(LookupOptions.URL, LookupOptions.MAINPAGE, "<div class=\"post-item ia\">", "</div>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a href=\"", "\"")
                                .setNth(0));


        linkTemplate.setMainPattern(linkPattern);
        linkTemplate.setDomainSame(true);

        //Article Download
        WebTemplate articleTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-text", "https://www.habersitesi.com")
                .setType(LookupOptions.ARTICLEDOC)
                .setLookComplete(true)
                .setThreadSize(1)
                .setSleepTime(1500L)
                .setDoFast(Boolean.FALSE)
                .setHtmlSaveFolder(LookupOptions.HTMLDIRECTORY)
                .setForceWrite(false);

        LookupPattern articleLookup = new LookupPattern(LookupOptions.ARTICLE, LookupOptions.CONTAINER, "<div class=\"col-12 col-lg-8 article-detail news-detail\">", "</div>")
                .setNth(0)
                .setStartEndMarker("<div","</div>")
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1(.*?)>","</h1>").setNth(0))
                .addPattern(new LookupPattern(LookupOptions.TEXT,LookupOptions.DATE, "<time datetime=\"","\">"))
                .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLETEXT,"<div class=\"article-content\">","</div>")
                        .setStartEndMarker("<div", "</div>")
                        .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLEPARAGRAPH,"(<p>)","(</p>)")));

        articleTemplate.setMainPattern(articleLookup);
        linkTemplate.addNext(articleTemplate, LookupOptions.ARTICLELINK);
        WebFlow flow = new WebFlow(linkTemplate);

        return flow;
    }

    public static void main(String[] args) {
        build().execute();
    }
}
