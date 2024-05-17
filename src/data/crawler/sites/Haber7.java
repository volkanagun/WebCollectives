package data.crawler.sites;

import data.crawler.web.LookupOptions;
import data.crawler.web.LookupPattern;
import data.crawler.web.WebFlow;
import data.crawler.web.WebTemplate;

public class Haber7 {
    public static WebFlow build(){

        String domain = "https://www.haber7.com/";
        int pageCount = 5;
        LookupPattern linkPattern = new LookupPattern(LookupOptions.URL, LookupOptions.MAINPAGE, "<div class=\"row\">", "</div>")
                .setStartEndMarker("<div","</div>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a href=\"", "\""));

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-links", domain)
                .addSeed("politics","https://www.haber7.com/siyaset/")
                .addSeed("breaking","https://www.haber7.com/guncel/")
                .addSeed("world","https://www.haber7.com/dunya/")
                .setDoFast(false)
                .setNextPageSuffix("p")
                .setNextPageSize(pageCount)
                .setDoDeleteStart(true)
                .setSleepTime(100L)
                .setThreadSize(1).setMainPattern(linkPattern)
                .setDomain(domain).setDomainSame(true);


        LookupPattern articleLookup = new LookupPattern(LookupOptions.ARTICLE, LookupOptions.CONTAINER, "<div class=\"col-md-8\">", "</div>")
                .setStartEndMarker("<div","</div>")
                .setTagLowercase(true)
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.DATE, "<span class=\"date-item updated\">","</span>")
                        .setNth(0)
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.SKIP, LookupOptions.CONTAINER, "<div class=\"news-author-title\">", "</div>")
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.AUTHOR, "<span class=\"name\">","</span>")))
                .addPattern(new LookupPattern(LookupOptions.LOOKUP, LookupOptions.GENRE, LookupOptions.GENRE))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1 class=\"title\">","</h1>")
                        .setNth(0)
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLETEXT, "<div class=\"news-content\"(.*?)>","</div>")
                        .setStartEndMarker("<div","</div>")
                        .setRequiredNotEmpty(true)
                        .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLEPARAGRAPH,"<(b|p)>","</(p|b)>")
                                .setRemoveTags(true)));

        WebTemplate articleTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-text", domain)
                .setType(LookupOptions.ARTICLEDOC)
                .setLookComplete(true)
                .setThreadSize(1)
                .setDoFast(false)
                .setSleepTime(100L)
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
