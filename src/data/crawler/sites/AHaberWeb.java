package data.crawler.sites;

import data.crawler.web.*;

import java.io.Serializable;


public class AHaberWeb implements Serializable {

    public static WebFlow build() {

        String domain = "https://ahaber.com.tr";
        int pageCount = 100;
        int randomCount = 100;

        WebFunctionCall clickCall = new WebButtonClickCall(1, "button.btn.btn-load-more").setDoFirefox(true)
                .setWaitTime(1000);

        WebFunctionCall scrollCall = new WebFunctionScrollHeight(1)
                .setWaitTime(1000);

        WebFunctionCall sequenceCall = new WebFunctionSequence(pageCount, scrollCall/*, clickCall*/)
                .setWaitBetweenCalls(2000L).setDoFirefox(true)
                .initialize();

        LookupPattern linkPattern = new LookupPattern(LookupOptions.URL, LookupOptions.MAINPAGE, "<div data-search-item(.*?)>", "</div>")
                .setStartEndMarker("<div", "</div>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a(.*?)href=\"", "\""));

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-links", domain)
                .addSeed("economy", "https://www.ahaber.com.tr/arama?category=ekonomi")
                .addSeed("world", "https://www.ahaber.com.tr/arama?category=dunya")
                .addSeed("trending", "https://www.ahaber.com.tr/arama?category=gundem")
                .addSeed("sports", "https://www.ahaber.com.tr/arama?category=spor")
                .addSeed("magazine", "https://www.ahaber.com.tr/arama?category=magazin")
                .addSeed("technology", "https://www.ahaber.com.tr/arama?category=teknoloji")
                .addSeed("general", "https://www.ahaber.com.tr/arama?category=yasam")
                .addSeed("health", "https://www.ahaber.com.tr/arama?category=saglik")
                .addSeed("local-news", "https://www.ahaber.com.tr/arama?category=ozel-haberler")
                .addSeed("auto", "https://www.ahaber.com.tr/arama?category=otomobil")
                .setDoFast(false)
                .setDoDeleteStart(true)
                .setSleepTime(3500L)
                .setFunctionCall(sequenceCall)
                .setThreadSize(1)
                .setDoRandomSeed(randomCount)
                .setDomain(domain)
                .setLinkPattern(null,"^https://www.ahaber.com.tr/galeri(.*?)$")
                .setMainPattern(linkPattern);

        LookupPattern articleLookup = new LookupPattern(LookupOptions.TEXT, LookupOptions.CONTAINER, "<section class=\"item\">", "</section>")
                .setStartEndMarker("<section", "</section>")
                .setNth(0)
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1 class=\"(.*?)\">", "</h1>")
                        .setNth(0)
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.SKIP, LookupOptions.CONTAINER, "<div class=\"detailSpot(.*?)\">", "</div>")
                        .setStartEndMarker("<div", "</div>")
                        .setNth(0).addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.DATE, "<span>","</span>").setNth(0)
                                .setRemoveTags(true).setReplaces(new String[]{"\"\\s", "Giri(.*?)\\:\\s?"}, new String[]{"",""})))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.AUTHOR, "AHABER"))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETEXT, "<div class=\"textFrame\">", "</div>")
                        .setStartEndMarker("<div", "</div>").setNth(0)
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLEPARAGRAPH, "<(p|h2(.*?))>", "</(p|h2)>")
                                .setRemoveTags(true)));

        WebTemplate articleTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-text", domain)
                .setType(LookupOptions.ARTICLEDOC)
                .setLookComplete(false)
                .setThreadSize(1)
                .setDoFast(false).setSleepTime(2000L)
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
