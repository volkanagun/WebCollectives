package data.crawler.sites;

import data.crawler.web.LookupOptions;
import data.crawler.web.LookupPattern;
import data.crawler.web.WebFlow;
import data.crawler.web.WebTemplate;

public class BilgiUstam {

    public static WebFlow build() {

        String domain = "http://www.bilgiustam.com";
        int count = 1;

        LookupPattern linkPattern = new LookupPattern(LookupOptions.SKIP, LookupOptions.MAINPAGE, "<h2 class=\"title\">", "</h2>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a(.*?)href=\"", "\""));

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.BLOGDIRECTORY, "blog-links", domain)
               .addSeed("technology", "https://www.bilgiustam.com/tek/")
               .addSeed("daily", "https://www.bilgiustam.com/gun/")
               .addSeed("people", "https://www.bilgiustam.com/kimdir-2/")
               .addSeed("science", "https://www.bilgiustam.com/biyoloji-2/")
               .addSeed("events", "https://www.bilgiustam.com/etkinlikler/")
               .addSeed("web", "https://www.bilgiustam.com/internet/")
               .addSeed("travel", "https://www.bilgiustam.com/ulasim/")
               .addSeed("daily", "https://www.bilgiustam.com/yasam/")
                .addSeed("science", "https://www.bilgiustam.com/bilim/")
                .addSeed("web", "https://www.bilgiustam.com/internet/")
                .addSeed("travel", "https://www.bilgiustam.com/turizm/")
                .addSeed("hobby", "https://www.bilgiustam.com/hobi-2/")
                .addSeed("health", "https://www.bilgiustam.com/saglik-2/")
                .addSeed("countries", "https://www.bilgiustam.com/ulkeler/")
                .addSeed("auto", "https://www.bilgiustam.com/otomobil/")
                .addSeed("culture", "https://www.bilgiustam.com/sanat/")
                .addSeed("economy", "https://www.bilgiustam.com/ekonomi-2/")

                .setDoFast(false)
                .setDoDeleteStart(true)
                .setSleepTime(1000L)
                .setNextPageSuffix("page/")
                .setNextPageSuffixAddition("/")
                .setNextPageStart(1)
                .setNextPageSize(count)
                .setThreadSize(1)
                .setDomain(domain)
                .setMainPattern(linkPattern);

        LookupPattern articleLookup = new LookupPattern(LookupOptions.ARTICLE, LookupOptions.CONTAINER, "<div class=\"single-container\"(.*?)>", "</div>")
                .setStartEndMarker("<div", "</div>")
                .setNth(0)
                .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLETEXT, "<div class=\"entry(.*?)\">", "</div>")
                        .setStartEndMarker("<div", "</div>").setNth(0)
                        .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLEPARAGRAPH, "<[ph]>", "</[ph]>")));

        WebTemplate articleTemplate = new WebTemplate(LookupOptions.BLOGDIRECTORY, "blog-text", domain)
                .setType(LookupOptions.BLOGDOC)
                .setMainContent(true)
                .setLookComplete(true).setDoDeleteStart(false)
                .setThreadSize(1)
                .setDoFast(false)
                .setSleepTime(1000L)
                .setDomain(domain)
                //.setDoRandomSeed(count)
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