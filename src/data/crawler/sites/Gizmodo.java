package data.crawler.sites;

import data.crawler.web.*;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Gizmodo {
    public static WebFlow build(String mainFolder){

        Calendar calendarStart = Calendar.getInstance();
        calendarStart.set(2019, 1, 1);
        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.set(2019, 3, 1);

        Date startDate = calendarStart.getTime();
        Date endDate = calendarEnd.getTime();


        WebTemplate linkTemplate = new WebTemplate(mainFolder, "blog-links", LookupOptions.EMPTY)
                .addSeed("https://earther.gizmodo.com/")
                .addSeed("https://gizmodo.com/c/design")
                .addSeed("https://paleofuture.gizmodo.com/")
                .addSeed("https://gizmodo.com/c/field-guide")
                .addSeed("https://io9.gizmodo.com/")
                .addSeed("https://gizmodo.com/c/review/cameras")
                .addSeed("https://gizmodo.com/c/review/smart-home")
                .addSeed("https://gizmodo.com/c/review/e-readers")
                .addSeed("https://gizmodo.com/c/review/smartphones")
                .addSeed("https://gizmodo.com/c/review/computer-components")
                .addSeed("https://gizmodo.com/c/review/headphones")
                .addSeed("https://gizmodo.com/c/review/laptops-tablets")
                .addSeed("https://gizmodo.com/c/review/wearables")
                .addSeed("https://gizmodo.com/c/review/computer-peripherals")
                .addSeed("https://gizmodo.com/c/review/home-entertainment")
                .addSeed("https://gizmodo.com/c/review/other-gadgets")
                .addSeed("https://gizmodo.com/c/review/uncategorized")
                .addSeed("https://gizmodo.com/c/review/drones")
                .addSeed("https://gizmodo.com/c/review/home-audio")
                .addSeed("https://gizmodo.com/c/review/bags")
                .addSeed("https://gizmodo.com/c/review/kitchen-gadgets")

                .setSuffixGenerator(new WebTimeGenerator("?startTime=",startDate, endDate))
                .setThreadSize(1);

        LookupPattern linkPattern = new LookupPattern(LookupOptions.URL, LookupOptions.MAINPAGE, "<article class(.*?)>", "</article>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINKCONTAINER, "<h1 class(.*?)>", "</h1>")
                        .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "href=\"", "\"")
                                .setNth(0)));

        linkTemplate.setMainPattern(linkPattern);



        //Article Download
        WebTemplate articleTemplate = new WebTemplate(mainFolder, "blog-text", LookupOptions.EMPTY)
                .setType(LookupOptions.BLOGDOC)
                .setLookComplete(false)
                .setThreadSize(1)
                .setSleepTime(500L)
                .setForceWrite(true);

        LookupPattern articleLookup = new LookupPattern(LookupOptions.ARTICLE, LookupOptions.CONTAINER, "<article(.*?)>", "</article>")
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
                .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLETEXT,"<div class=\"post-content(.*?)\">","</div>")
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
        WebFlow.submit(service, build(LookupOptions.BLOGENGDIRECTORY));
        service.shutdown();
    }
}
