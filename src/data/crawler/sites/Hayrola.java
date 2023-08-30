package data.crawler.sites;

import data.crawler.web.LookupOptions;
import data.crawler.web.LookupPattern;
import data.crawler.web.WebFlow;
import data.crawler.web.WebTemplate;

import java.io.Serializable;

public class Hayrola implements Serializable {
    public static WebFlow build() {
        WebTemplate mainTemplate = new WebTemplate(LookupOptions.BLOGDIRECTORY, "blog-links", "http://hayro.la");

        LookupPattern linkPattern = new LookupPattern(LookupOptions.URL, LookupOptions.CONTAINER, "<ul class=\"archive-list\">", "</ul>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINKCONTAINER, "<li>", "</li>")
                        .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a href=\"", "\">")
                                .setNth(1)));

        mainTemplate.setMainPattern(linkPattern);
        mainTemplate.setNextPageSuffix("page/");
        mainTemplate.setNextPageSize(5);
        mainTemplate.setNextPageStart(2);
        mainTemplate.addSeed("interesting", "http://hayro.la/kategori/ilgi-cekici/");
        mainTemplate.addSeed("woman", "http://hayro.la/kategori/kadin/");
        mainTemplate.addSeed("technology", "http://hayro.la/kategori/teknoloji/");
        mainTemplate.addSeed("funny", "http://hayro.la/kategori/komik/");


        LookupPattern articlePattern = new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLETEXT, "<div\\sid=\"content-wrapper\">", "</div>")
                .setStartEndMarker("<div", "</div>")
                .addPattern(new LookupPattern(LookupOptions.LOOKUP, LookupOptions.GENRE, LookupOptions.GENRE))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1\\sclass=\"post-title\".*?>", "</h1>").setNth(1))
                .addPattern(new LookupPattern(LookupOptions.SKIP, LookupOptions.AUTHORNAME, "<span\\sclass=\"author_name\".*?>", "</span>")
                        .setStartEndMarker("<span", "</span>")
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.AUTHORNAME, "<a\\shref=.*?>", "</a>")))
                .addPattern(new LookupPattern(LookupOptions.CONTAINER, LookupOptions.ARTICLETEXT, "<div\\sid=\"content_without_ad\">", "</div>")
                        .setStartEndMarker("<div", "</div>")
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLEPARAGRAPH, "<p>", "</p>")));

        WebTemplate articleTemplate = new WebTemplate(LookupOptions.BLOGDIRECTORY, "blog-text", "http://hayro.la");
        articleTemplate.setMainPattern(articlePattern)
                .setType(LookupOptions.BLOGDOC)
                .setHtmlSaveFolder(LookupOptions.HTMLDIRECTORY);

        mainTemplate.addNext(articleTemplate, LookupOptions.ARTICLELINK);
        WebFlow webFlow = new WebFlow(mainTemplate);
        return webFlow;

    }
}
