package data.crawler.sites;

import data.crawler.web.LookupOptions;
import data.crawler.web.LookupPattern;
import data.crawler.web.WebFlow;
import data.crawler.web.WebTemplate;

import java.io.Serializable;

public class Webrazzi implements Serializable {

    public static WebFlow build() {

        WebTemplate mainTemplate = new WebTemplate(LookupOptions.BLOGTRYDIRECTORY, "blog-links", LookupOptions.EMPTYDOMAIN);

        LookupPattern linkPattern = new LookupPattern(LookupOptions.ARTICLELINKCONTAINER, LookupOptions.ARTICLE, "<div class=\"post-title\">", "</div>")
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLELINK, "href=\"", "\"\\s"));

        mainTemplate.setMainPattern(linkPattern)
                .setSleepTime(1500L)
                .addSeed("http://webrazzi.com/");

        LookupPattern articlePattern = new LookupPattern(LookupOptions.CONTAINER, LookupOptions.ARTICLE, "<div class=\"post(.*?)>", "</div>")
                .setStartEndMarker("<div", "</div>")
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<header class=\"post-title\">", "</header>"))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.GENRE, "<a href=(.*?)rel=\"category\">", "</a>"))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.AUTHORNAME, "<span class=\"post-info-author\">", "</span>"))
                .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLETEXT, "<div class=\"post-content\">", "</div>")
                        .setStartEndMarker("<div", "</div")
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLEPARAGRAPH, "<p>", "</p>")));

        WebTemplate articleTemplate = new WebTemplate(LookupOptions.BLOGTRYDIRECTORY, "blog-text", "http://webrazzi.com");
        articleTemplate.setMainPattern(articlePattern)
                .setHtmlSaveFolder(LookupOptions.HTMLDIRECTORY)
                .setType(LookupOptions.BLOGDOC);

        mainTemplate.addNext(articleTemplate, LookupOptions.ARTICLELINK);
        WebFlow webFlow = new WebFlow(mainTemplate);
        return webFlow;

    }

    public static void main(String[] args) {
        build().execute();
    }

}
