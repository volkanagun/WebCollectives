package data.crawler.sites;

import data.crawler.web.*;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Wikipedia implements Serializable {

    public static WebFlow build() {

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.WIKIDIRECTORY, "wiki-links", LookupOptions.EMPTYDOMAIN)
                .setThreadSize(1)
                .setSeedSizeLimit(1000L)
                .setForceWrite(true)
                .setDoDeleteStart(true)
                .setDoFast(false)
                .setDomain("https://tr.wikipedia.org")
                .addSeedDuplicate("https://tr.wikipedia.org/wiki/Anasayfa")
                .addSeedDuplicate("https://tr.wikipedia.org/wiki/%C3%96zel:Rastgele")
                .addSeedDuplicate("https://tr.wikipedia.org/wiki/%C3%96zel:Rastgele")
                .addSeedDuplicate("https://tr.wikipedia.org/wiki/%C3%96zel:Rastgele")
                .addSeedDuplicate("https://tr.wikipedia.org/wiki/%C3%96zel:Rastgele")
                .addSeedDuplicate("https://tr.wikipedia.org/wiki/%C3%96zel:Rastgele")
                .addSeedDuplicate("https://tr.wikipedia.org/wiki/%C3%96zel:Rastgele")
                .addSeedDuplicate("https://tr.wikipedia.org/wiki/%C3%96zel:Rastgele")
                .addSeedDuplicate("https://tr.wikipedia.org/wiki/%C3%96zel:Rastgele")
                .addSeedDuplicate("https://tr.wikipedia.org/wiki/%C3%96zel:Rastgele")
                .addSeedDuplicate("https://tr.wikipedia.org/wiki/%C3%96zel:Rastgele")
                .addSeedDuplicate("https://tr.wikipedia.org/wiki/%C3%96zel:Rastgele");

        LookupPattern linkPattern = new LookupPattern(LookupOptions.ARTICLELINKCONTAINER, LookupOptions.ARTICLE, "<div class=\"mw-content-container\">", "</div>")
                .setStartEndMarker("<div", "</div")
                //.addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.CONTAINER, "<p|li>","</p|li>")
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLELINK, "<a href=\"", "\"(((\\s+)(title|class))|(\\>))")
                        .setRemoveTags(true));

        linkTemplate.setMainPattern(linkPattern)
                .setLinkPattern("https://tr\\.wikipedia(.*)", "(.*?)(((new|image|nw-redirect|link|redlink|section)\\=\\d+)|\\.svg|\\.jpg|\\.png)");

        LookupPattern wikiPattern = new LookupPattern(LookupOptions.CONTAINER, LookupOptions.ARTICLE, "<main id=\"content\"(.*?)>", "</main>")
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1.*?>", "</h1>"))
                .addPattern(new LookupPattern(LookupOptions.CONTAINER, LookupOptions.ARTICLETEXT, "<div id=\"bodyContent\"(.*?)>", "</div>")
                        .setStartEndMarker("<div", "</div").setRemoveTags(false)
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLEPARAGRAPH, "<p(.*?)>", "</p>")))
                .addPattern(new LookupPattern(LookupOptions.CONTAINER, LookupOptions.CATEGORYLIST, "<div id=\"catlinks\"(.*?)>", "</div>")
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.CATEGORY, "<a href(.*?)>", "</a>")));

        WebTemplate articleTemplate = new WebTemplate(LookupOptions.WIKIDIRECTORY, "article-text", "https://tr.wikipedia.org")
                .setType(LookupOptions.ARTICLEDOC)
                .setLookComplete(true).setDoDeleteStart(false)
                .setThreadSize(1)
                .setMainContent(true)
                .setDoFast(false)
                .setSleepTime(300L)
                .setDomain("https://tr.wikipedia.org")
                .setHtmlSaveFolder(LookupOptions.HTMLDIRECTORY)
                .setMainPattern(wikiPattern)
                .setForceWrite(true)
                .setLinkPattern("https://tr\\.wikipedia(.*)", "(.*?)(((new|image|nw-redirect|link|redlink|section)\\=\\d+)|\\.svg|\\.jpg|\\.png)");


        linkTemplate.addNext(articleTemplate, LookupOptions.ARTICLELINK);
        articleTemplate.addExtraTemplate(linkTemplate, LookupOptions.ARTICLELINK);

        WebFlow flow = new WebFlow(linkTemplate);
        return flow;
    }

    public static void main(String[] args) {
        build().execute();
    }
}
