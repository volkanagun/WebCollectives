package data.crawler.sites;

import data.crawler.web.LookupOptions;
import data.crawler.web.LookupPattern;
import data.crawler.web.WebFlow;
import data.crawler.web.WebTemplate;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Ergonomic implements Serializable {
    public static WebFlow build() {
        WebTemplate mainTemplate = new WebTemplate(LookupOptions.BLOGDIRECTORY, "blog-links", LookupOptions.EMPTYDOMAIN);

        LookupPattern linkPattern = new LookupPattern(LookupOptions.URL, LookupOptions.CONTAINER, "<div\\sclass\\=\"thumbnail\\soverlay\">", "</div>")
                .setStartEndMarker("<div", "</div>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a\\shref\\=\"", "\"").setNth(0));

        mainTemplate.setMainPattern(linkPattern);
        mainTemplate.setNextPageSuffix("/page/");
        mainTemplate.setNextPageSize(8);
        mainTemplate.setNextPageStart(2);

        mainTemplate.addSeed("cinema", "http://www.egonomik.com/category/sinema");
        mainTemplate.addSeed("pc-games", "http://www.egonomik.com/category/oyunlar/");
        mainTemplate.addSeed("web-design", "http://www.egonomik.com/category/web-tasarim");
        mainTemplate.addSeed("internet", "http://www.egonomik.com/category/internet");
        mainTemplate.addSeed("fun", "http://www.egonomik.com/category/eglencelik/");

        LookupPattern articlePattern = new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLETEXT, "<div\\sid=\"content\"", "</article>")
                .setStartEndMarker("<div", "</div>")
                .addPattern(new LookupPattern(LookupOptions.SKIP, LookupOptions.CONTAINER, "<span\\sclass=\"entry-cats\">", "</span>")
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.GENRE, "<h5>", "</h5>")))
                .addPattern(new LookupPattern(LookupOptions.SKIP, LookupOptions.CONTAINER, "<span\\sclass=\"author\">", "</span>")
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.AUTHORNAME, "<a\\shref.*?>", "</a>")))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1\\sclass=\"entry-title\">", "</h1>"))
                .addPattern(new LookupPattern(LookupOptions.CONTAINER, LookupOptions.ARTICLETEXT, "<div\\sclass=\"entry-content\">", "</div>")
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
        ExecutorService service = Executors.newCachedThreadPool();
        service.submit(build());
    }
}
