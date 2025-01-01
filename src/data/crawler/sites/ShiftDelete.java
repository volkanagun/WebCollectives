package data.crawler.sites;

import data.crawler.web.*;

public class ShiftDelete {

    public static WebFlow build() {

        String domain = "https://shiftdelete.net/";
        int pageStart = 1;
        int pageCount = 10;

        //int randomCount = 10;


        LookupPattern linkPattern = new LookupPattern(LookupOptions.SKIP, LookupOptions.TEXT, "<h4>", "</h4>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a(.*?)href=\"", "\""));

        LookupPattern shortArticle = new LookupPattern(LookupOptions.SKIP, LookupOptions.TEXT, "<div class=\"row\">", "</div>")
                .setStartEndMarker("<div","</div>")
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.AUTHOR, "<span class=\"post-author\">", "</span>")
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1(.*?)", "</h1>"))
                .addPattern(new LookupPattern(LookupOptions.ARTICLEDOC, LookupOptions.CONTAINER, "<div class=\"post-content(.*?)\">","</div>")
                        .setStartEndMarker("<div","</div")
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETEXT, "<p(.*?)", "</p>")));

        WebTemplate articleTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-text", domain)
                .setMainPattern(shortArticle);

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-links", domain)
                .addSeed("military", "https://shiftdelete.net/savunma-sanayi/")
                .addSeed("science", "https://shiftdelete.net/populer-bilim/")
                .addSeed("gaming", "https://shiftdelete.net/oyun/")
                .addSeed("web", "https://shiftdelete.net/sosyal-medya/")
                .addSeed("web", "https://shiftdelete.net/internet/")
                .addSeed("web", "https://shiftdelete.net/yazilim/")
                .addSeed("tech", "https://shiftdelete.net/technology/")
                .addSeed("tech", "https://shiftdelete.net/mobil/")
                .addSeed("tech", "https://shiftdelete.net/galeri/")
                .addSeed("auto", "https://shiftdelete.net/otomobil/")
                .addSeed("mobile", "https://shiftdelete.net/android/")
                .addSeed("hardware", "https://shiftdelete.net/donanim/")
                .addSeed("shopping", "https://shiftdelete.net/rehber/")
                .setSuffixGenerator(new WebCountGenerator(1, pageCount, "page/"))
                .setDoFast(Boolean.FALSE)
                .setDoDeleteStart(Boolean.TRUE)
                .setSleepTime(2500L)
                .setNextPageSize(pageCount)
                .setNextPageStart(pageStart)
                .setThreadSize(1)
                .setDomain(domain)
                .setMainPattern(linkPattern)
                .addExtraTemplate(articleTemplate, articleTemplate.getName());

        linkTemplate.addNext(articleTemplate, LookupOptions.ARTICLELINK);
        WebFlow flow = new WebFlow(linkTemplate);
        return flow;

    }

    public static void main(String[] args) {
        build().execute();
    }
}


