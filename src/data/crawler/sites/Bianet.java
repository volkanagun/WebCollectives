package data.crawler.sites;

import data.crawler.web.LookupOptions;
import data.crawler.web.LookupPattern;
import data.crawler.web.WebFlow;
import data.crawler.web.WebTemplate;

public class Bianet {
    public static WebFlow build() {
        String domain = "https://bianet.org";
        int pageCount = 1000;
        long limitSize = 100;
        int pageStart = 1;

        LookupPattern linkPattern = new LookupPattern(LookupOptions.URL, LookupOptions.MAINPAGE, "<div class=\"col-12 col-lg-8\">", "</div>")
                .setStartEndMarker("<div", "</div>")
                .addPattern(new LookupPattern(LookupOptions.SKIP, LookupOptions.CONTAINER,"<div class=\"col-12 col-sm-6\">","</div>")
                        .setStartEndMarker("<div","</div")
                        .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a(.*?)href=", "\\s")));

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-links", domain)
                .addSeed("news", "https://bianet.org/haberler")
                .setDoFast(true)
                .setDoDeleteStart(true)
                .setSleepTime(1000L)
                .setNextPageSuffix("?p=")
                .setNextPageSize(pageCount)
                .setNextPageStart(pageStart)
                .setThreadSize(8)
                .setSeedSizeLimit(limitSize)
                .setDomain(domain)
                .setMainPattern(linkPattern);

        LookupPattern articleLookup = new LookupPattern(LookupOptions.ARTICLE, LookupOptions.CONTAINER, "<section(.*?)>", "</section>")
                .setNth(0)
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.AUTHOR, "<a class=\"ccard(.*?)\">", "</a>")
                        .setNth(0)
                        .setRemoveTags(true)
                        .setRequiredNotEmpty(false))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.DATE, "<span class=value-desktop>", "</span>")
                        .setNth(0)
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1 class=headline(.*?)>", "</h1>")
                        .setNth(0)
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLETEXT, "<div class=content-wrapper>", "</div>")
                        .setStartEndMarker("<div", "</div>").setNth(0)
                        .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLEPARAGRAPH, "<(p|h2(.*?))>", "</(p|h2)>")));

        WebTemplate articleTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-text", domain)
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
        //articleTemplate.addExtraTemplate(linkTemplate, LookupOptions.ARTICLELINK);

        WebFlow flow = new WebFlow(linkTemplate);
        return flow;
    }

    public static void main(String[] args) {
        new Bianet().build().execute();
    }
}
