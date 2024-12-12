package data.crawler.sites;

import data.crawler.web.LookupOptions;
import data.crawler.web.LookupPattern;
import data.crawler.web.WebFlow;
import data.crawler.web.WebTemplate;

import java.io.Serializable;

public class Webrazzi implements Serializable {

    public static WebFlow build() {

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.BLOGTRYDIRECTORY, "blog-links", LookupOptions.EMPTYDOMAIN)
                .setDoDeleteStart(true);

        LookupPattern linkPattern = new LookupPattern(LookupOptions.ARTICLELINKCONTAINER, LookupOptions.ARTICLE, "<div class=\"post(.*?)\">", "</div>")
                .setStartMarker("<div")
                .setEndMarker("</div>")
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLELINK, "<a href=\"", "\""));

        linkTemplate.setMainPattern(linkPattern)
                .setSleepTime(1500L)
                .addSeed("https://webrazzi.com/haberler/")
                .addSeed("https://webrazzi.com/kategori/dijital/")
                .addSeed("https://webrazzi.com/kategori/yatirim/")
                .addSeed("https://webrazzi.com/kategori/e-ticaret/")
                .addSeed("https://webrazzi.com/kategori/mobil/")
                .addSeed("https://webrazzi.com/kategori/teknoloji/")
                .addSeed("https://webrazzi.com/kategori/girisimler/");

        LookupPattern articlePattern = new LookupPattern(LookupOptions.CONTAINER, LookupOptions.ARTICLE, "<article(.*?)>", "</article>")
                .setStartEndMarker("<article", "</article>")
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1 class=\"(.*?)\">", "</h1>"))
                .addPattern(new LookupPattern(LookupOptions.SKIP, LookupOptions.CONTAINER, "<div class=\"single-post-category(.*?)>", "</div>")
                        .setStartEndMarker("<div", "</div")
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.GENRE, "<a (.*?)>", "</a>").setNth(0)))
                .addPattern(new LookupPattern(LookupOptions.SKIP, LookupOptions.CONTAINER, "<span class=\"single-post-meta-text\">", "</span>")
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.AUTHOR, "<strong>","</strong>")))
                .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLETEXT, "<div class=\"single-post-content\">", "</div>")
                        .setStartEndMarker("<div", "</div")
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLEPARAGRAPH, "<p>", "</p>")));

        WebTemplate articleTemplate = new WebTemplate(LookupOptions.BLOGTRYDIRECTORY, "blog-text", "http://webrazzi.com");
        articleTemplate.setMainPattern(articlePattern)
                .setHtmlSaveFolder(LookupOptions.HTMLDIRECTORY)
                .setType(LookupOptions.BLOGDOC);

        linkTemplate.addNext(articleTemplate, LookupOptions.ARTICLELINK);
        WebFlow webFlow = new WebFlow(linkTemplate);
        return webFlow;

    }

    public static void main(String[] args) {
        build().execute();
    }

}
