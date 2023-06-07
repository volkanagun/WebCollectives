package data.crawler.sites;

import data.crawler.web.LookupOptions;
import data.crawler.web.LookupPattern;
import data.crawler.web.WebFlow;
import data.crawler.web.WebTemplate;

import java.io.Serializable;

public class AvazAvaz implements Serializable {
    public static WebFlow build() {
        WebTemplate mainTemplate = new WebTemplate(LookupOptions.BLOGDIRECTORY, "blog-links", LookupOptions.EMPTYDOMAIN);

        LookupPattern linkPattern = new LookupPattern(LookupOptions.URL, LookupOptions.CONTAINER, "<div\\sclass=\"brick-media\">", "</div>")
                .setStartEndMarker("<div", "</div>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a\\shref=\"", "\""));

        mainTemplate.setMainPattern(linkPattern);
        /*mainTemplate.setNextPageSuffix("/page/");
        mainTemplate.setNextPageSize(6);
        mainTemplate.setNextPageStart(2);*/
        mainTemplate.addSeed("music", "F:\\java-projects\\AuthorIdentification\\resources\\web\\avaz.html");


        LookupPattern articlePattern = new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLETEXT, "<div\\sclass=\"content-wrap\">", "</div>")
                .setStartEndMarker("<div", "</div>")
                .addPattern(new LookupPattern(LookupOptions.LOOKUP, LookupOptions.GENRE, LookupOptions.GENRE))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1\\sclass=\"entry-title\\spage-title\">", "</h1>"))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.AUTHORNAME, "<span\\sclass=\"author\\svcard\\sicon-user\">", "</span>"))
                .addPattern(new LookupPattern(LookupOptions.CONTAINER, LookupOptions.ARTICLETEXT, "<div\\sclass=\"entry-content\">", "</div>")
                        .setStartEndMarker("<div", "</div>")
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLEPARAGRAPH, "<p>", "</p>")));

        WebTemplate articleTemplate = new WebTemplate(LookupOptions.BLOGDIRECTORY, "blog-text", LookupOptions.EMPTYDOMAIN);
        articleTemplate.setMainPattern(articlePattern);
        articleTemplate.setType(LookupOptions.BLOGDOC);

        mainTemplate.addNext(articleTemplate, LookupOptions.ARTICLELINK);
        WebFlow webFlow = new WebFlow(mainTemplate);
        return webFlow;

    }
}
