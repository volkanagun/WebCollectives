package data.crawler.sites;

import data.crawler.web.LookupOptions;
import data.crawler.web.LookupPattern;
import data.crawler.web.WebFlow;
import data.crawler.web.WebTemplate;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GazeteOku implements Serializable {
    public static WebFlow build() {

        //Main Download
        WebTemplate mainTemplate = new WebTemplate(LookupOptions.ARTICLEDIRECTORY, "article-links", "http://www.gazeteoku.com");
        String mainSeed = "http://www.gazeteoku.com/tum-yazarlar.html";
        mainTemplate.addSeed(mainSeed);

        LookupPattern yazarPattern = new LookupPattern(LookupOptions.URL, LookupOptions.MAINPAGE, "<div class=\"widget-author\">", "</div>")
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.AUTHORLINK, "<a\\shref\\=\"", "\" title").setNth(1));

        mainTemplate.setMainPattern(yazarPattern);

        //Link Download
        WebTemplate linkTemplate = new WebTemplate(LookupOptions.ARTICLEDIRECTORY, "author-links", "http://www.gazeteoku.com", "?page=")
                .setNextPageStart(1)
                .setNextPageSize(10);
        LookupPattern patternLinkArticle = new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLE, "<div class=\"article-others bordered-top\">", "</div>");
        LookupPattern patternLink = new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLELINK, "<a href=\"", "\"\\s");

        patternLinkArticle.addPattern(patternLink);
        linkTemplate.setMainPattern(patternLinkArticle);

        //Article Download
        WebTemplate articleTemplate = new WebTemplate(LookupOptions.ARTICLEDIRECTORY, "article-text", "http://www.gazeteoku.com")
                .setType(LookupOptions.ARTICLEDOC);
        LookupPattern nameLookup = new LookupPattern(LookupOptions.ARTICLE, LookupOptions.CONTAINER, "<div class=\"widget-author\">", "</div>")
                .setStartMarker("<div")
                .setEndMarker("</div>")
                .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.AUTHORNAME,"<span class=\"name\">","</span>"));

        LookupPattern titleLookup = new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLETITLE, "<h1>", "</h1>");
        LookupPattern genreeLookup = new LookupPattern(LookupOptions.TEXT, LookupOptions.GENRE, LookupOptions.GENREPOLITICS);
        LookupPattern paragraphLookup = new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLEPARAGRAPH, "<p>", "</p>");
        LookupPattern contentLookup = new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLETEXT, "<div class=\"content-text\">", "</div>")
                .setStartMarker("<div")
                .setEndMarker("</div>")
                .addPattern(paragraphLookup);

        LookupPattern articleLookup = new LookupPattern(LookupOptions.ARTICLE, LookupOptions.CONTAINER, "<article class=\"news-detail\">", "</div>")
                .setStartMarker("<div")
                .setEndMarker("</div")
                .addPattern(nameLookup)
                .addPattern(titleLookup)
                .addPattern(genreeLookup)
                .addPattern(contentLookup);

        articleTemplate.setMainPattern(articleLookup);
        articleTemplate.setType(LookupOptions.ARTICLEDOC);
        articleTemplate.setHtmlSaveFolder(LookupOptions.HTMLDIRECTORY);

        mainTemplate.addNext(linkTemplate, LookupOptions.AUTHORLINK);
        linkTemplate.addNext(articleTemplate, LookupOptions.ARTICLELINK);


        WebFlow flow = new WebFlow(mainTemplate);
        return flow;
    }

    public static void main(String[] args){
        ExecutorService service = Executors.newFixedThreadPool(5);
        WebFlow.submit(service, build());
        service.shutdown();
    }
}
