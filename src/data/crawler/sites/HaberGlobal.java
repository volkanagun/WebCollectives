package data.crawler.sites;

import data.crawler.web.LookupOptions;
import data.crawler.web.LookupPattern;
import data.crawler.web.WebFlow;
import data.crawler.web.WebTemplate;

public class HaberGlobal {


    public static WebFlow build() {
        String domain = "https://haberglobal.com.tr";
        int size = 10;
        int start = 10;
                                                      //Type of pattern  //XML tag in the result           //start regex               //end regex
        LookupPattern linkPattern = new LookupPattern(LookupOptions.URL, LookupOptions.MAINPAGE, "<div class=\"row\">", "</div>")
                 //same marker of start and end regex definitions
                .setStartEndMarker("<div", "</div>")
                //Sub pattern linkPattern must contains this subpattern
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a href=\"", "\""));

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.ARTICLEDIRECTORY, "article-links", domain)
                .addSeed("news", "https://haberglobal.com.tr/gundem")
                .addSeed("news", "https://haberglobal.com.tr/dunya")
                .addSeed("breaking", "https://haberglobal.com.tr/son-dakika-haberler")
                .addSeed("economy", "https://haberglobal.com.tr/ekonomi")
                .addSeed("sports", "https://haberglobal.com.tr/spor")
                .addSeed("health", "https://haberglobal.com.tr/saglik")
                .addSeed("health", "https://haberglobal.com.tr/yasam")
                .addSeed("trends", "https://haberglobal.com.tr/magazin")
                .addSeed("science", "https://haberglobal.com.tr/bilim-teknoloji")
                .addSeed("culture", "https://haberglobal.com.tr/kultur-sanat")
                .addSeed("auto", "https://haberglobal.com.tr/otomobil")
                .setLinkPattern("(.*?)\\d+",null)
                .setDoFast(false)
                .setDoDeleteStart(true)
                .setSleepTime(1000L)
                .setThreadSize(1)
                .setDomain(domain)
                .setNextPageSuffix("/")
                .setNextPageStart(start)
                .setNextPageSize(size)
                .setMainPattern(linkPattern);

        LookupPattern articleLookup = new LookupPattern(LookupOptions.ARTICLE, LookupOptions.CONTAINER, "<div class=\"post-detail-box\"(.*?)>", "</div>")
                .setStartEndMarker("<div","</div>")
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.DATE, "<time(.*?)>", "</time>")
                        .setNth(0)
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1(.*?)>", "</h1>")
                        .setNth(0)
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLETEXT, "<div class=\"content-text\"(.*?)>", "</div>")
                        .setStartEndMarker("<div", "</div>").setNth(0)
                        .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLEPARAGRAPH, "<(p|h2(.*?))>", "</(p|h2)>")));


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
