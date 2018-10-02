package data.crawler.sites;

import data.crawler.web.LookupOptions;
import data.crawler.web.LookupPattern;
import data.crawler.web.WebFlow;
import data.crawler.web.WebTemplate;

import java.io.Serializable;

public class SonBirseyler implements Serializable {
    public static WebFlow build() {
        WebTemplate mainTemplate = new WebTemplate(LookupOptions.BLOGDIRECTORY, "blog-links", LookupOptions.EMPTYDOMAIN);

        LookupPattern linkPattern = new LookupPattern(LookupOptions.URL, LookupOptions.CONTAINER, "<div class=\"td-pb-span8 td-main-content\">", "</div>")
                .setStartEndMarker("<div", "</div>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINKCONTAINER, "<h3 itemprop=\"name\" class=\"entry-title td-module-title\">", "</h3>")
                        .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a.*?href=\"", "\"").setNth(1)));

        mainTemplate.setMainPattern(linkPattern);
        mainTemplate.setNextPageSuffix("page/");
        mainTemplate.setNextPageSize(50);
        mainTemplate.setNextPageStart(1);
        mainTemplate.addSeed("social", "http://www.sonbisey.com/category/gundem/");
        mainTemplate.addSeed("technology", "http://www.sonbisey.com/category/listeler/teknoloji/");
        mainTemplate.addSeed("entertainment", "http://www.sonbisey.com/category/listeler/kultursanat/");
        mainTemplate.addSeed("science", "http://www.sonbisey.com/category/listeler/bilim/");
        mainTemplate.addSeed("music", "http://www.sonbisey.com/category/listeler/sinematvmuzik/");


        LookupPattern articlePattern = new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLETEXT, "<article id=\"post-\\d+.*?>", "</article>")
                .addPattern(new LookupPattern(LookupOptions.LOOKUP, LookupOptions.GENRE, LookupOptions.GENRE))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1 itemprop=\"name\" class=\"entry-title\">", "</h1>").setNth(1))
                .addPattern(new LookupPattern(LookupOptions.SKIP, LookupOptions.AUTHORNAME, "<div class=\"td-post-author-name\">", "</div>")
                        .setStartEndMarker("<div", "</div>")
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.AUTHORNAME, "<a.*?>", "</a>")))
                .addPattern(new LookupPattern(LookupOptions.CONTAINER, LookupOptions.ARTICLETEXT, "<div class=\"td-post-content\">", "</div>")
                        .setStartEndMarker("<div", "</div>")
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLEPARAGRAPH, "<p>", "</p>").setNth(2).setMth(100)));

        WebTemplate articleTemplate = new WebTemplate(LookupOptions.BLOGDIRECTORY, "blog-text", LookupOptions.EMPTYDOMAIN);
        articleTemplate.setMainPattern(articlePattern);
        articleTemplate.setType(LookupOptions.BLOGDOC);

        mainTemplate.addNext(articleTemplate, LookupOptions.ARTICLELINK);
        WebFlow webFlow = new WebFlow(mainTemplate);
        return webFlow;

    }
}
