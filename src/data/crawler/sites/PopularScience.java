package data.crawler.sites;

import data.crawler.web.LookupOptions;
import data.crawler.web.LookupPattern;
import data.crawler.web.WebFlow;
import data.crawler.web.WebTemplate;

public class PopularScience {
    public static WebFlow build() {

        String domain = "https://popsci.com.tr/";
        int pageCount = 10;

        LookupPattern linkPattern = new LookupPattern(LookupOptions.URL, LookupOptions.MAINPAGE, "<div class=\"td-ss-main-content\">", "</div>")
                .setStartEndMarker("<div", "</div>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a href=\"", "\""));

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-links", domain)
                .addSeed("millitary", "https://popsci.com.tr/kategori/konular/askeri/")
                .addSeed("science", "https://popsci.com.tr/kategori/konular/bilim/")
                .addSeed("environment", "https://popsci.com.tr/kategori/konular/cevre/")
                .addSeed("entertainment", "https://popsci.com.tr/kategori/konular/eglence/")
                .addSeed("technology", "https://popsci.com.tr/kategori/konular/elektronik/")
                .addSeed("physics", "https://popsci.com.tr/kategori/konular/enerji/")
                .addSeed("air", "https://popsci.com.tr/kategori/konular/havacilik/")
                .addSeed("nature", "https://popsci.com.tr/kategori/konular/doga/")
                .addSeed("culture", "https://popsci.com.tr/kategori/kendin-yap/")
                .addSeed("auto", "https://popsci.com.tr/kategori/konular/otomobil/")
                .addSeed("health", "https://popsci.com.tr/kategori/konular/saglik/")
                .addSeed("technology", "https://popsci.com.tr/kategori/konular/teknoloji/")
                .addSeed("physics", "https://popsci.com.tr/kategori/konular/uzay/")
                .setDoFast(false)
                .setSleepTime(1000L)
                .setDoDeleteStart(false)
                .setNextPageSize(pageCount)
                .setNextPageStart(1)
                .setNextPageJump(1)
                .setThreadSize(1).setNextPageSuffix("page/")
                .setDomain(domain).setDomainSame(true)
                .setMainPattern(linkPattern);

        LookupPattern articleLookup = new LookupPattern(LookupOptions.ARTICLE, LookupOptions.CONTAINER, "<div class=\"td-pb-row\">", "</div>")
                .setStartEndMarker("<div", "</div")
                .setTagLowercase(true).setNth(0)
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1 class=\"entry-title\">", "</h1>")
                        .setRemoveTags(true)).setRequiredNotEmpty(true)
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.DATE, "<time class=\"(.*?)\" datetime=\"", "\">")
                        .setRemoveTags(true)
                        .setRequiredNotEmpty(false))
                .addPattern(new LookupPattern(LookupOptions.SKIP, LookupOptions.CONTAINER, "<div class=\"td-ss-main-content\">", "</div>")
                        .setStartEndMarker("<div", "</div>")
                        .setTagLowercase(false)
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.AUTHOR, "<p><em>(Yazar|Kaynak): ", "(</em>)")
                                .setTagLowercase(false)
                                .setRequiredNotEmpty(false)
                                .setRemoveTags(true)))
                .addPattern(new LookupPattern(LookupOptions.CONTAINER, LookupOptions.ARTICLE, "<div class=\"td-post-content tagdiv-type\">", "</div>")
                        .setStartEndMarker("<div", "</div>")
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLEPARAGRAPH, "<(b|p)>", "</(p|b)>")
                                .setRemoveTags(true)));

        WebTemplate articleTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-text", domain)
                .setType(LookupOptions.ARTICLEDOC)
                .setLookComplete(true)
                .setThreadSize(1).setSleepTime(2500L)
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
        WebFlow flow = build();
        flow.execute();
    }
}
