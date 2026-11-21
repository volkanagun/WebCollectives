package data.crawler.sites;

import data.crawler.web.*;

public class PopularScience {
    public static WebFlow build() {
        Integer pageCount = 50;
        String domain = "https://bmag.com.tr/";
        
        WebFunctionCall scroller = new WebFunctionScrollHeight(1, 20).setWaitTime(500);
        WebFunctionCall seq = new WebFunctionSequence(pageCount, scroller).setWaitBetweenCalls(500L).setWaitTime(1000).initialize();
        LookupPattern linkPattern = new LookupPattern(LookupOptions.CONTAINER, LookupOptions.MAINPAGE, "<div class=\"relative mb-4 md:mb-6\"(.*?)>", "</div>")
                .addPattern(new LookupPattern(LookupOptions.ARTICLELINK,LookupOptions.ARTICLELINK,"<a(.*?)href=\"","\"")
                        .setNth(0));
                
                

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-links", domain)
                .addSeed("auto", "https://bmag.com.tr/otomobil")
                .addSeed("travel", "https://bmag.com.tr/doga-seyahat")
                .addSeed("science", "https://bmag.com.tr/bilim")
                .addSeed("history", "https://bmag.com.tr/tarih")
                .addSeed("economy", "https://bmag.com.tr/ekonomi")
                .addSeed("culture", "https://bmag.com.tr/yasam")
                .addSeed("culture", "https://bmag.com.tr/mutfak-lezzet")
                .addSeed("technology", "https://bmag.com.tr/oyun-teknoloji")                
                .setDoFast(true)
                .setSleepTime(1000L)
                .setDoDeleteStart(false)
                .setFunctionCall(seq)
                .setThreadSize(12)
                .setDomain(domain)
                .setMainPattern(linkPattern);

        LookupPattern articleLookup = new LookupPattern(LookupOptions.ARTICLE, LookupOptions.CONTAINER, "<div class=\"col-span-12 md:col-span-8 md:col-start-3\"(.*?)>", "</div>")
                .setStartEndMarker("<div","</div>")
                .setTagLowercase(true)
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1(.*?)>", "</h1>")
                        .setRemoveTags(true)).setRequiredNotEmpty(true)
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.DATE, "<span data-v-a8fe0a61=\"\">Oluşturma Tarihi\\:\\s", "</span>")
                        .setRemoveTags(true)
                        .setRequiredNotEmpty(false))
                .addPattern(new LookupPattern(LookupOptions.CONTAINER, LookupOptions.ARTICLE, "<div class=\"news-content\"(.*?)>", "</div>")
                        .setStartEndMarker("<div", "</div>")
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLEPARAGRAPH, "\n", "\n")
                                .setRemoveTags(true)));

        WebTemplate articleTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-text", domain)
                .setType(LookupOptions.ARTICLEDOC)
                .setLookComplete(true)
                .setThreadSize(1).setSleepTime(2500L)
                .setDoFast(false)
                .setDomain(domain).setFunctionCall(new WebEmptyFunctionCall().setWaitTime(5000).initialize())
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
