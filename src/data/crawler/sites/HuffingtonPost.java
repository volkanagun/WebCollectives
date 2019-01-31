package data.crawler.sites;

import data.crawler.web.*;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HuffingtonPost implements Serializable {
    public static WebFlow build() {
        //
        Calendar calendarStart = Calendar.getInstance();
        calendarStart.set(2002, 1, 1);
        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.set(2008, 1, 1);

        Date startDate = calendarStart.getTime();
        Date endDate = calendarEnd.getTime();

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.BLOGENGDIRECTORY, "blog-links", LookupOptions.EMPTYDOMAIN)
                .setSuffixGenerator(new WebDateGenerator("yyyy-MM-dd", startDate, endDate))
                .setThreadSize(4)
                .addSeed("https://www.huffingtonpost.com/archive/");

        LookupPattern linkPattern = new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLELINK, null, null)
                .setSingleGroup(2).setSingleRegex("<a\\sclass\\=\"card__link\\syr\\-card\\-headline\"\\s(.*?)href\\=\"(.*?)\">");

        linkTemplate.setMainPattern(linkPattern);

        WebTemplate mainTemplate = new WebTemplate(LookupOptions.BLOGENGDIRECTORY, "blog-text", "https://www.huffingtonpost.com/")
                .setDomain("https://www.huffingtonpost.com/")
                .setThreadSize(4)
                .setSleepTime(30000L);

        LookupPattern mainPattern = new LookupPattern(LookupOptions.CONTAINER, LookupOptions.ARTICLE, "<div\\sid\\=\"main\"\\srole\\=\"main\">", "</div>")
                .setStartEndMarker("<div", "</div>")
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1 class\\=\"headline__title\">", "</h1>")
                        .setNth(0))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.AUTHOR, "<div class\\=\"(author-card__name)\">", "</div>")
                        .setRemoveTags(true).setNth(1))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.DATE, "<span\\sclass\\=\"timestamp__date--published\">", "</span>"))
                .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLETEXT, "<div class\\=\"entry__text js-entry-text yr-entry-text\"(.*?)>", "</div>")
                        .setStartEndMarker("<div", "</div>").addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLEPARAGRAPH, "<p>", "</p>")))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.GENRE, "<a class=\"tag yr-tag\"(.*?)>", "</a>").setNth(1));

        mainTemplate.setType("BLOG-DOC")
                .setMainPattern(mainPattern).setForceWrite(true);

        linkTemplate.addNext(mainTemplate, LookupOptions.ARTICLELINK);
        WebFlow flow = new WebFlow(linkTemplate);
        return flow;
    }

    public static void main(String[] args){
        ExecutorService service = Executors.newFixedThreadPool(5);
        WebFlow.submit(service, build());
        service.shutdown();
    }
}
