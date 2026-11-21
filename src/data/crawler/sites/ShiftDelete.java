package data.crawler.sites;

import data.crawler.web.*;

public class ShiftDelete {

    public static WebFlow build() {

        String domain = "https://shiftdelete.net/";
        int pageStart = 1;
        int pageEnd = 20;


        LookupPattern linkPattern = new LookupPattern(LookupOptions.SKIP, LookupOptions.TEXT, "<h[34](.*?)>", "</h[34]>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a(.*?)href=\"", "\""));

        LookupPattern shortArticle = new LookupPattern(LookupOptions.SKIP, LookupOptions.TEXT, "<div class=\"sd-w g\">", "</div>")
                .setStartEndMarker("<div","</div>")
                .setNth(0)
                .addPattern(new LookupPattern(LookupOptions.SKIP, LookupOptions.CONTAINER, "<div class=\"by\">", "</div>")
                        .addPattern(new LookupPattern(LookupOptions.TEXT,LookupOptions.AUTHOR, "<a(.*?)>","</a>")
                                .setNth(0).setRemoveTags(true))
                        .addPattern(new LookupPattern(LookupOptions.TEXT,LookupOptions.DATE, "<time(.*?)>","</time>")
                                .setNth(0).setRemoveTags(true)))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1(.*?)>", "</h1>"))
                .addPattern(new LookupPattern(LookupOptions.ARTICLEDOC, LookupOptions.CONTAINER, "<div class=\"bd sd-prose\">","</div>")
                        .setStartEndMarker("<div","</div")
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETEXT, "<p(.*?)", "</p>")));

        WebTemplate articleTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-text", domain)
                .setMainPattern(shortArticle)
                .setDoFast(true)
                .setThreadSize(24)
                .setSleepTime(20L);

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-links", domain)
                .addSeed("military", "https://shiftdelete.net/savunma-sanayi/")
                .addSeed("science", "https://shiftdelete.net/populer-bilim/")
                .addSeed("gaming", "https://shiftdelete.net/oyun/")
                .addSeed("web", "https://shiftdelete.net/sosyal-medya/")
                .addSeed("web", "https://shiftdelete.net/internet/")
                .addSeed("web", "https://shiftdelete.net/yazilim/")
                .addSeed("tech", "https://shiftdelete.net/yapay-zeka/")
                .addSeed("tech", "https://shiftdelete.net/mobil/")
                .addSeed("tech", "https://shiftdelete.net/galeri/")
                .addSeed("auto", "https://shiftdelete.net/otomobil/")
                .addSeed("mobile", "https://shiftdelete.net/android/")
                .addSeed("hardware", "https://shiftdelete.net/donanim/")
                .addSeed("shopping", "https://shiftdelete.net/rehber/")
                .setSuffixGenerator(new WebCountGenerator(1, pageEnd, "page/"))
                .setDoFast(Boolean.TRUE)
                .setDoDeleteStart(Boolean.TRUE)
                .setSleepTime(10L)
                .setNextPageSize(pageEnd)
                .setNextPageStart(pageStart)
                .setThreadSize(24)
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


