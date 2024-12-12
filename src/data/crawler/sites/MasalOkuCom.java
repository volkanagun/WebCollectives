package data.crawler.sites;

import data.crawler.web.LookupOptions;
import data.crawler.web.LookupPattern;
import data.crawler.web.WebFlow;
import data.crawler.web.WebTemplate;

public class MasalOkuCom {

    public static WebFlow build(){

        String domain = "https://masaloku.com.tr/";

        LookupPattern linkPattern = new LookupPattern(LookupOptions.URL, LookupOptions.MAINPAGE, "<div class=entry>", "</div>")
                .setStartEndMarker("<div","</div>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a class=more-link href=", ">"));

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.TURKISHSTORYDIRECTORY, "story-links", domain)
                .addSeed("masal","https://masaloku.com.tr/turkce-masallar/")
                .setDoFast(false)
                .setSleepTime(1000L)
                .setDoDeleteStart(false)
                .setNextPageSize(30)
                .setNextPageStart(1)
                .setNextPageJump(1)
                .setThreadSize(1).setNextPageSuffix("page/")
                .setDomain(domain).setDomainSame(true)
                .setMainPattern(linkPattern);

        LookupPattern articleLookup = new LookupPattern(LookupOptions.ARTICLE, LookupOptions.CONTAINER, "<div class=post-inner>", "</div>")
                .setStartEndMarker("<div","</div>")
                .setTagLowercase(true)
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1 .*?>","</h1>")
                        .setRemoveTags(true)).setRequiredNotEmpty(true)
                .addPattern(new LookupPattern(LookupOptions.CONTAINER, LookupOptions.ARTICLE, LookupOptions.EMPTY)
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLEPARAGRAPH,"<(b|p)>","</(p|b)>")
                                .setRemoveTags(true)));

        WebTemplate articleTemplate = new WebTemplate(LookupOptions.TURKISHSTORYDIRECTORY, "story-text", domain)
                .setType(LookupOptions.ARTICLEDOC)
                .setLookComplete(true)
                .setThreadSize(1).setSleepTime(2500L)
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
        WebFlow flow = (new MasalOkuCom()).build();
        flow.execute();
    }
}
