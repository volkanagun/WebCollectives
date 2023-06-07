package data.crawler.sites;

import data.crawler.web.LookupOptions;
import data.crawler.web.LookupPattern;
import data.crawler.web.WebFlow;
import data.crawler.web.WebTemplate;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BoingBlogList implements Serializable {

    public static WebFlow build(String mainFolder) {

        WebTemplate yazarTemplate = new WebTemplate(mainFolder, "author-links", LookupOptions.EMPTYDOMAIN);
        LookupPattern authorPattern = new LookupPattern(LookupOptions.URL, LookupOptions.AUTHORLINK, "<a class=\"byline\" href=\"https://boingboing.net/author/", "\">");
        yazarTemplate.setMainPattern(authorPattern);

        yazarTemplate.setDomain("https://boingboing.net/author/")
                .setForceWrite(false)
                .setNextPageSuffix("/page/")
                .setNextPageStart(1)
                .setNextPageSize(50)
                .setSleepTime(500L)
                .setThreadSize(2)
                .addSeed("https://boingboing.net/grid");

        WebTemplate linkTemplate = new WebTemplate(mainFolder, "blog-links", LookupOptions.EMPTYDOMAIN);
        LookupPattern linkPattern = new LookupPattern(LookupOptions.URL, LookupOptions.CONTAINER, "<h2\\sclass\\=\"entry-title\">", "</h2>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a\\shref\\=\"", "\"\\s"));

        linkTemplate.setMainPattern(linkPattern)
                .setDomain("https://boingboing.net/author/")
                .setForceWrite(false).setNextPageSuffix("/page/").setDoDeleteStart(true)
                .setNextPageStart(1)
                .setNextPageSize(100)
                .setSleepTime(500L)
                .setThreadSize(2);

        LookupPattern topPattern = new LookupPattern(LookupOptions.CONTAINER, LookupOptions.ARTICLE, LookupOptions.EMPTY)
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.DATE, "<meta\\sproperty\\=\"article\\:published\\_time\"\\scontent\\=\"", "\"(.*?)/>"));

        LookupPattern articlePattern = new LookupPattern(LookupOptions.CONTAINER, LookupOptions.ARTICLE, "<div\\sid\\=\"container\">", "</div>")
                .setType(LookupOptions.SKIP)
                .setStartEndMarker("<div", "</div>")
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1>", "</h1>")
                        .setNth(0))
                .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLETEXT, "<div\\sid\\=\"story\">", "</div>")
                        .setStartEndMarker("<div", "</div>")
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLEPARAGRAPH, "<p>", "</p>")))
                .addPattern(new LookupPattern(LookupOptions.CONTAINER, LookupOptions.AUTHOR, "<div\\sid\\=\"share-author\"(.*?)>", "</div>")
                        .setStartEndMarker("<div", "</div>").setType(LookupOptions.SKIP)
                        .setNth(0).addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.AUTHORNAME, "<h2>", "</h2>")
                                .setNth(0)))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.GENRE, "<h3\\sclass\\=\"thetags\"(.*?)>", "</h3>")
                        .setRemoveTags(true));

        WebTemplate articleTemplate = new WebTemplate(mainFolder, "boing-text", LookupOptions.EMPTYDOMAIN);
        topPattern.addPattern(articlePattern);
        articleTemplate.setMainPattern(topPattern)
                .setForceWrite(true)
                .setDomain("https://boingboing.net")
                .setThreadSize(2)
                .setSleepTime(1000L)
                .setHtmlSaveFolder(LookupOptions.HTMLDIRECTORY)
                .setType(LookupOptions.BLOGDOC);

        yazarTemplate.addNext(linkTemplate, LookupOptions.AUTHORLINK);
        linkTemplate.addNext(articleTemplate, LookupOptions.ARTICLELINK);
        WebFlow flow = new WebFlow(yazarTemplate);
        return flow;

    }

    public static void main(String[] args){

        ExecutorService service = Executors.newFixedThreadPool(5);
        WebFlow.submit(service, build(LookupOptions.BLOGENGDIRECTORY));
        service.shutdown();

    }
}
