package data.crawler.sites;

import data.crawler.web.LookupOptions;
import data.crawler.web.LookupPattern;
import data.crawler.web.WebFlow;
import data.crawler.web.WebTemplate;

import java.io.Serializable;

public class AcikEkonomi implements Serializable {

    public static WebFlow build() {
        WebTemplate mainTemplate = new WebTemplate(LookupOptions.BLOGDIRECTORY, "blog-links", LookupOptions.EMPTYDOMAIN);

        LookupPattern linkPattern = new LookupPattern(LookupOptions.URL, LookupOptions.CONTAINER, "<h1\\sclass=\"entry-title\">", "</h1>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a\\shref=\"", "\""));

        mainTemplate.setMainPattern(linkPattern);
        mainTemplate.setNextPageSuffix("/page/");
        mainTemplate.setNextPageSize(6);
        mainTemplate.setNextPageStart(2);
        mainTemplate.addSeed("economy", "acikekonomi.com");

        LookupPattern articlePattern = new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLETEXT, "<div\\sid=\"content\"(.*?)>", "</div>")
                .setStartEndMarker("<div", "</div>")
                .addPattern(new LookupPattern(LookupOptions.LOOKUP, LookupOptions.GENRE, LookupOptions.GENRE))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1\\sclass=\"entry-title\">", "</h1>").setNth(0))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.AUTHORNAME, "<a\\sclass=\"author\"(.*?)>", "</a>"))
                .addPattern(new LookupPattern(LookupOptions.CONTAINER, LookupOptions.ARTICLETEXT, "<div\\sclass=\"entry-content\">", "</div>")
                        .setStartEndMarker("<div", "</div>")
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLEPARAGRAPH, "<p>", "</p>")
                                .setNth(1)
                                .setMth(100)));

        WebTemplate articleTemplate = new WebTemplate(LookupOptions.BLOGDIRECTORY, "blog-text", LookupOptions.EMPTYDOMAIN);
        articleTemplate
                .setMainPattern(articlePattern)
                .setType(LookupOptions.BLOGDOC)
                .setHtmlSaveFolder(LookupOptions.HTMLDIRECTORY);

        mainTemplate.addNext(articleTemplate, LookupOptions.ARTICLELINK);
        WebFlow webFlow = new WebFlow(mainTemplate);
        return webFlow;

    }
}
