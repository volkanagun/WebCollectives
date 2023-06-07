package data.crawler.sites;

import data.crawler.web.*;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GoogleWord {
    public static WebFlow build() {

        Calendar calendar = Calendar.getInstance();
        calendar.set(2021, 1, 1);
        Date startDate = calendar.getTime();
        Date endDate = new Date();

        //Main Download
        WebTemplate mainTemplate = new WebTemplate(LookupOptions.GOOGLEDIR, "search-links", "http://www.google.com.tr")
                .setSleepTime(2000L).setDoDeleteStart(true)
                .setSuffixGenerator(new WebWordGenerator("resources/dictionary/eval-dictionary.txt"));

        String mainSeed = "http://www.gazeteoku.com/tum-yazarlar.html?site=&date=";
        mainTemplate.addSeed(mainSeed)
                .setDoDeleteStart(true);

        LookupPattern yazarPattern = new LookupPattern(LookupOptions.URL, LookupOptions.MAINPAGE, "<div class=\"widget-author\">", "</div>")
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.AUTHORLINK, "<a\\shref\\=\"", "\" title").setNth(1));

        mainTemplate.setMainPattern(yazarPattern).setDoDeleteStart(true);

        //Link Download
        WebTemplate linkTemplate = new WebTemplate(LookupOptions.ARTICLEDIRECTORY, "author-links", "http://www.gazeteoku.com", "?page=")
                .setNextPageStart(1)
                .setNextPageSize(10).setSleepTime(2000L)
                .setDoDeleteStart(true);

        LookupPattern patternLinkArticle = new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLE, "<div class=\"article-others bordered-top\">", "</div>");
        LookupPattern patternLink = new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLELINK, "<a href=\"", "\"\\s");

        patternLinkArticle.addPattern(patternLink);
        linkTemplate.setMainPattern(patternLinkArticle);

        //Article Download
        WebTemplate articleTemplate = new WebTemplate(LookupOptions.ARTICLEDIRECTORY, "article-text", "http://www.gazeteoku.com")
                .setSleepTime(2000L)
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

    public static void main(String[] args)
    {
        ExecutorService service = Executors.newFixedThreadPool(15);
        WebFlow.submit(service, build());
        service.shutdown();
    }
}
