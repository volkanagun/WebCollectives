package data.crawler.sites;

import data.crawler.web.LookupOptions;
import data.crawler.web.LookupPattern;
import data.crawler.web.WebFlow;
import data.crawler.web.WebTemplate;

public class BasedIstanbul {
    public static WebFlow build() {
        String domain = "https://www.basedistanbul.com/";

        LookupPattern linkPattern = new LookupPattern(LookupOptions.URL, LookupOptions.MAINPAGE, "<div class=\"title\">", "</div>")
                .setStartEndMarker("<div", "</div>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a(.*?)href=\"", "\""));

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-links", domain)
                .addSeed("arts", "http://egazete.anadolu.edu.tr/kultur-sanat")
                .addSeed("sports", "http://egazete.anadolu.edu.tr/spor")
                .addSeed("education", "http://egazete.anadolu.edu.tr/acikogretim")
                .addSeed("education", "http://egazete.anadolu.edu.tr/kampus")
                .setDoFast(true)
                .setDoDeleteStart(true)
                .setNextPageSuffix("?p=").setNextPageStart(1).setNextPageSize(300)
                .setThreadSize(1)
                .setDomain(domain)
                .setMainPattern(linkPattern);

        LookupPattern articleLookup = new LookupPattern(LookupOptions.ARTICLE, LookupOptions.CONTAINER, "<div class=\"main\">", "</div>")
                .setStartEndMarker("<div", "</div>")
                .setNth(0)
                .addPattern(new LookupPattern(LookupOptions.SKIP, LookupOptions.CONTAINER, "<div class=\"title\">", "</div>")
                        .setStartEndMarker("<div", "</div>")
                        .setNth(0)
                        .setRemoveTags(true)
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1>", "</h1>"))
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.DATE, "<span class=\"date\">", "</span>")))
                .addPattern(new LookupPattern(LookupOptions.SKIP, LookupOptions.CONTAINER, "<div class=\"reporter\">", "</div>")
                        .setStartEndMarker("<div", "</div>").setNth(0)
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.AUTHOR, " <div class=\"name\">", "</div>")
                                .setStartEndMarker("<div", "</div>")
                                .setNth(0)
                                .setRemoveTags(true)))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLE, "<article>", "</article>")
                        .setNth(0)
                        .setRemoveTags(false)
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLEPARAGRAPH, "(<p>|\")", "(</p>|\")").setRemoveTags(true)));


        WebTemplate articleTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-text", domain)
                .setType(LookupOptions.ARTICLEDOC)
                .setLookComplete(true)
                .setThreadSize(1)
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
        build().execute();
    }
}
