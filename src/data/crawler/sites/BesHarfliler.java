package data.crawler.sites;

import data.crawler.web.LookupOptions;
import data.crawler.web.LookupPattern;
import data.crawler.web.WebFlow;
import data.crawler.web.WebTemplate;

import java.io.Serializable;

public class BesHarfliler implements Serializable {


    public static WebFlow build() {
        WebTemplate mainTemplate = new WebTemplate(LookupOptions.BLOGDIRECTORY, "blog-links", LookupOptions.EMPTYDOMAIN);

        LookupPattern linkPattern = new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLE, "<div\\sclass=\"grid_8\\somega\">", "</div>")
                .setStartEndMarker("<div", "</div>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a\\shref=\"", "\">").setNth(0));

        mainTemplate.setMainPattern(linkPattern)
                .setNextPageSuffix("page/")
                .setNextPageSize(5)
                .setNextPageStart(2)
                .addSeed("culture", "http://www.5harfliler.com/category/kultur/")
                .addSeed("history", "http://www.5harfliler.com/category/tarih/")
                .addSeed("art", "http://www.5harfliler.com/category/sanat/")
                .addSeed("social", "http://www.5harfliler.com/category/meydan/");


        LookupPattern articlePattern = new LookupPattern(LookupOptions.CONTAINER, LookupOptions.ARTICLE, "<section>", "</section>")
                .addPattern(new LookupPattern(LookupOptions.SKIP, LookupOptions.AUTHORNAME, "<div\\sclass=\"grid_4\\sauthor.*?>", "</div>")
                        .setStartEndMarker("<div", "</div>")
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.AUTHORNAME, "<h5>", "</h5>").setNth(0)))
                .addPattern(new LookupPattern(LookupOptions.LOOKUP, LookupOptions.GENRE, LookupOptions.GENRE))
                .addPattern(new LookupPattern(LookupOptions.SKIP, LookupOptions.ARTICLE, "<div\\sclass=\"grid_12\\sarticle(.*?)>", "</div>")
                        .setStartEndMarker("<div", "</div>")
                        .setNth(0)
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1>", "</h1>").setNth(0))
                        .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLETEXT, "<div\\sclass=\"post_content\">", "</div>")
                                .setStartEndMarker("<div", "</div")
                                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLEPARAGRAPH, "<p>", "</p>"))));


        WebTemplate articleTemplate = new WebTemplate(LookupOptions.BLOGDIRECTORY, "blog-text", "http://www.5harfliler.com/");
        articleTemplate.setMainPattern(articlePattern);
        articleTemplate.setType(LookupOptions.BLOGDOC);

        mainTemplate.addNext(articleTemplate, LookupOptions.ARTICLELINK);


        WebFlow webFlow = new WebFlow(mainTemplate);
        return webFlow;
    }
}
