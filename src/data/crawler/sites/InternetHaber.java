package data.crawler.sites;

import data.crawler.web.LookupOptions;
import data.crawler.web.LookupPattern;
import data.crawler.web.WebFlow;
import data.crawler.web.WebTemplate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InternetHaber {

    public static WebFlow build(){

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-links", LookupOptions.EMPTY)
                //.addSeed("http://www.internethaber.com/politika")
                //.addSeed("http://www.internethaber.com/dunya")
                //.addSeed("http://www.internethaber.com/ekonomi")
                //.addSeed("http://www.internethaber.com/spor")
                .addSeed("http://www.internethaber.com/guncel-haberler")
                .setNextPageStart(12000)
                .setNextPageSize(1000)
                .setNextPageSuffix("?page=")
                .setThreadSize(4);

        LookupPattern linkPattern = new LookupPattern(LookupOptions.URL, LookupOptions.MAINPAGE, "<ul class=\"list\">", "</ul>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINKCONTAINER, "<li>", "</li>")
                        .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a href=\"", "\"")
                                .setNth(0)));

        linkTemplate.setMainPattern(linkPattern);



        //Article Download
        WebTemplate articleTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-text", LookupOptions.EMPTY)
                .setType(LookupOptions.ARTICLEDOC)
                .setLookComplete(false)
                .setThreadSize(6)
                .setForceWrite(true);

        LookupPattern articleLookup = new LookupPattern(LookupOptions.ARTICLE, LookupOptions.CONTAINER, "<div class=\"newsDetail\">", "</div>")
                .setStartEndMarker("<div","</div>")
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1>","</h1>").setNth(0))
                .addPattern(new LookupPattern(LookupOptions.TEXT,LookupOptions.DATE, "<meta content=\"","\">Eklenme"))
                .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLETEXT,"<div class=\"news-detail-content\">","</div>")
                        .setStartEndMarker("<div", "</div>")
                        .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLEPARAGRAPH,"<p>","</p>")));


        articleTemplate.setMainPattern(articleLookup);

        linkTemplate.addNext(articleTemplate, LookupOptions.ARTICLELINK);

        WebFlow flow = new WebFlow(linkTemplate);
        return flow;
    }

    public static void main(String[] args) {

        ExecutorService service = Executors.newFixedThreadPool(5);
        WebFlow.submit(service, build());
        service.shutdown();
    }
}
