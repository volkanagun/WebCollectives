package data.crawler.sites;

import data.crawler.web.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FanFictionNet {
    public static WebFlow build() {


        WebTemplate bookTemplate = new WebTemplate(LookupOptions.FANFICDIRECTORY, "fanfic", LookupOptions.EMPTYDOMAIN);
        LookupPattern bookPattern = new LookupPattern(LookupOptions.URL, LookupOptions.BOOKLINK, "<a\\shref\\=\"/book/", "\"");

        bookTemplate.setMainPattern(bookPattern);
        bookTemplate.setDomain("https://www.funfiction.net/book/")
                .setForceWrite(false)
                .setThreadSize(20)
                .setSeedSizeLimit(50L)
                .addSeed("https://www.fanfiction.net/book");


        LookupPattern linkPattern = new LookupPattern(LookupOptions.SKIP, LookupOptions.CONTAINER, "<div id=content_wrapper_inner(.*?)>", "</div>")
                .setStartEndMarker("<div", "</div>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a\\s+class=stitle\\s+href=\"", "\""));

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.FANFICDIRECTORY, "book-links", LookupOptions.EMPTYDOMAIN);
        linkTemplate.setMainPattern(linkPattern)
                .setDomain("https://www.fanfiction.net/book/")
                .setForceWrite(false)
                .setSuffixGenerator(new WebCountGenerator(1, 200, "?&p="))
                .setThreadSize(20);


        LookupPattern articlePattern = new LookupPattern(LookupOptions.CONTAINER, LookupOptions.ARTICLE, "<div id=content_wrapper_inner(.*?)>", "</div>")
                .setStartEndMarker("<div", "</div")
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.GENRE, "<a class=xcontrast_txt href=\"/book/", "/\">"))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.AUTHOR, "<a class='xcontrast_txt' href='/u/(\\d+?)/", "'>"))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.LANGUAGE, "<span class='xgray xcontrast_txt'>", "</span>")
                        .setLookupFilter(new LookupFilterSet(new String[]{"French","English","Italian","Spanish"})))
                .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLETEXT, "<div class='storytext xcontrast_txt nocopy' id='storytext'>", "</div>")
                        .setStartEndMarker("<div", "</div>")
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLEPARAGRAPH, "<p(.*?)>", "</p>")
                                .setRemoveTags(true)));

        WebTemplate articleTemplate = new WebTemplate(LookupOptions.FANFICDIRECTORY, "article-text", LookupOptions.EMPTYDOMAIN);
        articleTemplate.setMainPattern(articlePattern).setForceWrite(true)
                .setDomain("https://www.fanfiction.net")
                .setLookComplete(true)
                .setThreadSize(24).setDoFast(true)
                .setType(LookupOptions.ARTICLEDOC);


        bookTemplate.addNext(linkTemplate, LookupOptions.BOOKLINK);
        linkTemplate.addNext(articleTemplate, LookupOptions.ARTICLELINK);
        WebFlow flow = new WebFlow(bookTemplate);
        return flow;
    }

    public static void main(String[] args) {
        ExecutorService service = Executors.newFixedThreadPool(5);
        WebFlow.submit(service, build());
        service.shutdown();
    }
}
