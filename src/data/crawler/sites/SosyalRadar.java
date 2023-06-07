package data.crawler.sites;

import data.crawler.web.LookupOptions;
import data.crawler.web.LookupPattern;
import data.crawler.web.WebFlow;
import data.crawler.web.WebTemplate;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SosyalRadar implements Serializable {

    public static WebFlow build() {
        WebTemplate mainTemplate = new WebTemplate(LookupOptions.BLOGDIRECTORY, "blog-links", LookupOptions.EMPTYDOMAIN);

        LookupPattern linkPattern = new LookupPattern(LookupOptions.URL, LookupOptions.CONTAINER, "<h2\\sclass=\"post-title\">", "</h2>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a\\shref=\"", "\"").setNth(0));

        mainTemplate.setMainPattern(linkPattern);
        mainTemplate.setNextPageSuffix("/page/");
        mainTemplate.setNextPageSize(10);
        mainTemplate.setNextPageStart(2);
        mainTemplate.addSeed("social-media", "http://www.sosyalradar.com/k/sosyal-medya");
        mainTemplate.addSeed("web", "http://www.sosyalradar.com/k/web");
        mainTemplate.addSeed("technology", "http://www.sosyalradar.com/k/teknoloji");
        mainTemplate.addSeed("commerce", "http://www.sosyalradar.com/k/e-ticaret");
        mainTemplate.addSeed("enterprise", "http://www.sosyalradar.com/k/girisim");
        mainTemplate.addSeed("qa", "http://www.sosyalradar.com/k/soru-cevap");
        mainTemplate.addSeed("design", "http://www.sosyalradar.com/k/tasarim");


        LookupPattern articlePattern = new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLETEXT, "<article\\sclass=\".*?>", "</article>")
                //.setStartEndMarker("<div", "</div>")
                .addPattern(new LookupPattern(LookupOptions.LOOKUP, LookupOptions.GENRE, LookupOptions.GENRE))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1\\sclass=\"post-title\">", "</h1>").setNth(1))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.AUTHORNAME, "<a\\shref=.*?rel=\"author\">", "</a>").setNth(1))
                .addPattern(new LookupPattern(LookupOptions.CONTAINER, LookupOptions.ARTICLETEXT, "<div\\sclass=\"entry\">", "</div>")
                        .setStartEndMarker("<div", "</div>")
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLEPARAGRAPH, "<p>", "</p>")));

        WebTemplate articleTemplate = new WebTemplate(LookupOptions.BLOGDIRECTORY, "blog-text", LookupOptions.EMPTYDOMAIN);
        articleTemplate.setMainPattern(articlePattern)
                .setType(LookupOptions.BLOGDOC)
                .setHtmlSaveFolder(LookupOptions.HTMLDIRECTORY);

        mainTemplate.addNext(articleTemplate, LookupOptions.ARTICLELINK);
        WebFlow webFlow = new WebFlow(mainTemplate);
        return webFlow;

    }

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        WebFlow.submit(executorService, build());
    }
}
