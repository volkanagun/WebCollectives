package data.crawler.sites;

import data.crawler.web.LookupOptions;
import data.crawler.web.LookupPattern;
import data.crawler.web.WebFlow;
import data.crawler.web.WebTemplate;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GezenlerKulubu implements Serializable {
    public static WebFlow build() {
        WebTemplate mainTemplate = new WebTemplate(LookupOptions.BLOGDIRECTORY, "blog-links", LookupOptions.EMPTYDOMAIN);

        LookupPattern linkPattern = new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLE, "<article\\sclass=\"post\".*?>", "</article>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a href=\"", "\"\\stitle="));

        mainTemplate.setMainPattern(linkPattern);

        mainTemplate.setNextPageSuffix("?page=")
                .setNextPageSize(57)
                .setNextPageStart(2)
                .addSeed("http://www.cokgezenlerkulubu.com/");

        LookupPattern articlePattern = new LookupPattern(LookupOptions.CONTAINER, LookupOptions.ARTICLE, "<div\\sclass=\"postdetail\">", "</div>")
                .setStartEndMarker("<div", "</div>")
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1>", "</h1>").setNth(0))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.GENRE, "travel"))
                .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLETEXT, "<div\\sclass=\"text\">", "</div>")
                        .setStartEndMarker("<div", "</div")
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLEPARAGRAPH, "<p>", "</p>")))
                .addPattern(new LookupPattern(LookupOptions.SKIP, LookupOptions.AUTHORNAME, "<div\\sclass=\"editor\"> ", "</div>")
                        .setStartEndMarker("<div", "</div>")
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.AUTHORNAME, "<span>", "</span>")));


        WebTemplate articleTemplate = new WebTemplate(LookupOptions.BLOGDIRECTORY, "blog-text", "http://www.cokgezenlerkulubu.com/");
        articleTemplate
                .setMainPattern(articlePattern)
                .setType(LookupOptions.BLOGDOC)
                .setHtmlSaveFolder(LookupOptions.HTMLDIRECTORY);

        mainTemplate.addNext(articleTemplate, LookupOptions.ARTICLELINK);
        WebFlow webFlow = new WebFlow(mainTemplate);
        return webFlow;
    }

    public static void main(String[] args) {
        ExecutorService service = Executors.newCachedThreadPool();
        service.submit(build());
    }
}
