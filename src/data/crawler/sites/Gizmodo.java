package data.crawler.sites;

import data.crawler.web.*;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Gizmodo {
    public static WebFlow build(){

        Calendar calendarStart = Calendar.getInstance();
        calendarStart.set(2018, 12, 1);
        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.set(2019, 1, 26);

        Date startDate = calendarStart.getTime();
        Date endDate = calendarEnd.getTime();


        WebTemplate linkTemplate = new WebTemplate(LookupOptions.BLOGENGDIRECTORY, "blog-links", LookupOptions.EMPTY)
                .addSeed("https://earther.gizmodo.com/")
                .addSeed("https://gizmodo.com/c/review/cameras")
                .addSeed("https://gizmodo.com/c/review/smart-home")
                .addSeed("https://gizmodo.com/c/review/e-readers")
                .addSeed("https://gizmodo.com/c/review/smartphones")
                .setSuffixGenerator(new WebTimeGenerator("?startTime=",startDate, endDate))
                .setThreadSize(4);

        LookupPattern linkPattern = new LookupPattern(LookupOptions.URL, LookupOptions.MAINPAGE, "<article class(.*?)>", "</article>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINKCONTAINER, "<h1 class(.*?)>", "</h1>")
                        .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "href=\"", "\"")
                                .setNth(0)));

        linkTemplate.setMainPattern(linkPattern);



        //Article Download
        WebTemplate articleTemplate = new WebTemplate(LookupOptions.BLOGENGDIRECTORY, "blog-text", LookupOptions.EMPTY)
                .setType(LookupOptions.BLOGDOC)
                .setLookComplete(false)
                .setThreadSize(4)
                .setForceWrite(true);

        LookupPattern articleLookup = new LookupPattern(LookupOptions.ARTICLE, LookupOptions.CONTAINER, "<article class=(.*?)>", "</article>")
                .setStartEndMarker("<article","</article>")
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.GENRE,"Filed to: <span>","</span>"))
                /*.addPattern(new LookupPattern(LookupOptions.GENRE, LookupOptions.CONTAINER, "<div class=\"storytype(.*?)\">","</div>")
                        .setStartEndMarker("<div","</div>")
                        .setType(LookupOptions.SKIP)
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.GENRE, "<a(.*?)>","</a>")
                                .setNth(0)))*/
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1 class=\"(.*?)\">","</h1>")
                        .setNth(0))
                .addPattern(new LookupPattern(LookupOptions.TEXT,LookupOptions.AUTHOR, "<div class=\"meta__byline js_meta-byline author \">","</div>")
                        .setStartEndMarker("<div","</div>")
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.TEXT,LookupOptions.DATE, "<time class=\"(.*?)\" datetime=\"","\">")
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLETEXT,"<div class=\"post-content(.*?)>","</div>")
                        .setStartEndMarker("<div", "</div>")
                        .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLEPARAGRAPH,"<p>","</p>")
                                .setRemoveTags(true)));


        articleTemplate.setMainPattern(articleLookup);
        articleTemplate.setLookComplete(true);

        linkTemplate.addNext(articleTemplate, LookupOptions.ARTICLELINK);

        WebFlow flow = new WebFlow(linkTemplate);
        return flow;
    }

    public static void main(String[] args){
        ExecutorService service = Executors.newFixedThreadPool(1);
        WebFlow.submit(service, build());
        service.shutdown();
    }
}
