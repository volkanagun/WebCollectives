package data.crawler.sites;

import data.crawler.web.*;

import java.io.Serializable;


public class AHaberWeb implements Serializable {

    public static WebFlow build() {

        String domain = "https://ahaber.com.tr";
        int pageCount = 10;

        WebFunctionCall clickAdvertisementCall = new WebButtonClickCall(1, "div.gravityMasterSkip").setDoFirefox(true)
                .setWaitTime(1000);

        WebFunctionCall clickCall = new WebButtonClickCall(1, "div.id_d_po_close").setDoFirefox(true)
                .setWaitTime(1000);

        WebFunctionCall scrollCall = new WebFunctionScrollHeight(1, 1500)
                .setWaitTime(1000);

        WebFunctionCall sequenceCall = new WebFunctionSequence(pageCount, scrollCall)
                .setWaitBetweenCalls(2000L).setDoFirefox(true)
                .initialize();

        WebFunctionCall mainCall = new WebFunctionSequence(1,/*clickAdvertisementCall, clickCall, */sequenceCall)
                .setWaitBetweenCalls(2000L).setDoFirefox(true)
                .initialize();

        LookupPattern linkPattern = new LookupPattern(LookupOptions.URL, LookupOptions.MAINPAGE, "<div class=\"news-card(.*?)\">", "</div>")
                .setStartEndMarker("<div", "</div>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a(.*?)href=\"", "\""));

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-links", domain)
                .addSeed("economy", "https://www.ahaber.com.tr/ekonomi/")
                .addSeed("world", "https://www.ahaber.com.tr/dunya/")
                .addSeed("trending", "https://www.ahaber.com.tr/gundem/")
                .addSeed("sports", "https://www.ahaber.com.tr/spor")
                .addSeed("magazine", "https://www.ahaber.com.tr/magazin")
                .addSeed("technology", "https://www.ahaber.com.tr/teknoloji")
                .addSeed("general", "https://www.ahaber.com.tr/yasam")
                .addSeed("health", "https://www.ahaber.com.tr/saglik")
                .addSeed("local-news", "https://www.ahaber.com.tr/ozel-haberler")
                .addSeed("auto", "https://www.ahaber.com.tr/otomobil")
                .setDoFast(false)
                .setDoDeleteStart(true)
                .setSleepTime(3500L)
                .setFunctionCall(mainCall)
                .setThreadSize(1)
                .setDomain(domain)
                .setLinkPattern(null,"^https://www.ahaber.com.tr/galeri(.*?)$")
                .setMainPattern(linkPattern);

        LookupPattern articleLookup = new LookupPattern(LookupOptions.TEXT, LookupOptions.CONTAINER, "<article(.*?)>", "</article>")
                .setStartEndMarker("<article", "</article>")
                .setNth(0)
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1(.*?)>", "</h1>")
                        .setNth(0)
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.DATE, "<time(.*?)>","</time>").setNth(0)
                                .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.AUTHOR, "AHABER"))
                .addPattern(new LookupPattern(LookupOptions.CONTAINER, LookupOptions.ARTICLETEXT, "<div(.*?)class=\"(.*?)article-detail-content-body(.*?)\"(.*?)>", "</div>")
                        .setStartEndMarker("<div", "</div>")/*.setNth(0)*/
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
