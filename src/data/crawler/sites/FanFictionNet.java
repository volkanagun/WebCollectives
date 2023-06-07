package data.crawler.sites;

import data.crawler.web.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FanFictionNet {

    private static final long bookSizeLimit = 10L;
    private static final long catoonSizeLimit = 10L;
    private static final int maxPage = 3;

    public static WebFlow buildBook(){
        return buildBook(LookupOptions.FANFICDIRECTORY);
    }

    public static List<WebFlow> build(){
        List list = new ArrayList<WebFlow>();
        //list.add(buildBook(LookupOptions.FANFICDIRECTORY));
        list.add(buildCartoon(LookupOptions.FANFICDIRECTORY));
        return list;
    }

    public static WebFlow buildBook(String mainDirectory) {

        WebFunctionCall functionCall= new WebButtonClickCall(1, "a[href^=\"top\"]").setDoFirefox(true).initialize();
        WebTemplate bookTemplate = new WebTemplate(mainDirectory, "fanfic", LookupOptions.EMPTYDOMAIN);
                //.setFunctionCall(functionCall);

        LookupPattern bookPattern = new LookupPattern(LookupOptions.URL, LookupOptions.BOOKLINK, "<a\\shref\\=\"/(book)/", "\"");

        bookTemplate.setMainPattern(bookPattern);
        bookTemplate.setDomain("https://www.fanfiction.net/book/")
                .setForceWrite(false)
                .setDoDeleteStart(true)
                .setThreadSize(1)
                .setSeedSizeLimit(bookSizeLimit)
                .setSleepTime(1000L)
                .addSeed("https://www.fanfiction.net/book");

        LookupPattern linkPattern = new LookupPattern(LookupOptions.SKIP, LookupOptions.CONTAINER, "<div id=content_wrapper_inner(.*?)>", "</div>")
                .setStartEndMarker("<div", "</div>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a\\s+class=stitle\\s+href=\"", "\""));

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.FANFICDIRECTORY, "book-links", LookupOptions.EMPTYDOMAIN);
        linkTemplate.setMainPattern(linkPattern)
                .setDoFast(false)
                .setSleepTime(1000L)
                .setDoDeleteStart(true)
                .setDomain("https://www.fanfiction.net/book/")
                .setForceWrite(false)
                .setSuffixGenerator(new WebCountGenerator(1, maxPage, "?&p="))
                .setThreadSize(1);

        LookupPattern articlePattern = new LookupPattern(LookupOptions.CONTAINER, LookupOptions.ARTICLE, "<div id=content_wrapper_inner(.*?)>", "</div>")
                .setStartEndMarker("<div", "</div")
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.GENRE, "<a class=xcontrast_txt href=\"/book/", "/\">"))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.AUTHOR, "<a class='xcontrast_txt' href='/u/(\\d+?)/", "'>"))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.LANGUAGE, "<span class='xgray xcontrast_txt'>", "</span>")
                        .setLookupFilter(new LookupFilterSet(new String[]{"English"})))
                .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLETEXT, "<div class='storytext xcontrast_txt nocopy' id='storytext'>", "</div>")
                        .setStartEndMarker("<div", "</div>")
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLEPARAGRAPH, "<p(.*?)>", "</p>")
                                .setRemoveTags(true)));

        WebTemplate articleTemplate = new WebTemplate(LookupOptions.FANFICDIRECTORY, "article-text", LookupOptions.EMPTYDOMAIN);
        articleTemplate.setMainPattern(articlePattern).setForceWrite(true)
                .setDomain("https://www.fanfiction.net")
                .setLookComplete(true)
                .setThreadSize(1)
                .setDoFast(true)
                .setType(LookupOptions.ARTICLEDOC);


        bookTemplate.addNext(linkTemplate, LookupOptions.BOOKLINK);
        linkTemplate.addNext(articleTemplate, LookupOptions.ARTICLELINK);
        WebFlow flow = new WebFlow(bookTemplate);
        return flow;

    }


    public static WebFlow buildCartoon(String mainDirectory) {

        WebFunctionCall loadCall = new WebEmptyFunctionCall()
                .setDoFirefox(true)
                .setWaitTime(2500)
                .initialize();

        WebTemplate cartoonTemplate = new WebTemplate(mainDirectory, "fanfic", LookupOptions.EMPTYDOMAIN);
        LookupPattern cartoonPattern = new LookupPattern(LookupOptions.URL, LookupOptions.BOOKLINK, "<a\\shref\\=\"/cartoon/", "\"");

        cartoonTemplate.setMainPattern(cartoonPattern);
        cartoonTemplate.setDomain("https://www.funfiction.net/")
                .setForceWrite(false)
                .setThreadSize(1)
                .setDoFast(false)
                .setSleepTime(1000L).setFunctionCall(loadCall)
                .setSeedSizeLimit(catoonSizeLimit)
                .addSeed("https://www.fanfiction.net/cartoon/");

        LookupPattern linkPattern = new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a\\s+class=\"stitle\" href=\"", "\"(.*?)>");

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.FANFICDIRECTORY, "cartoon-links", LookupOptions.EMPTYDOMAIN);
        linkTemplate.setMainPattern(linkPattern)
                .setDoFast(false)
                .setDomain("https://www.fanfiction.net/cartoon/")
                .setForceWrite(false).setFunctionCall(loadCall)
                .setSleepTime(1500L).setWaitTime(1500L)
                .setSuffixGenerator(new WebCountGenerator(1, maxPage, "?&srt=1&r=103&p="))
                .setThreadSize(1);

        LookupPattern articlePattern = new LookupPattern(LookupOptions.CONTAINER, LookupOptions.ARTICLE, "<div id=\"content_wrapper\".*?>", "</div>")
                .setStartEndMarker("<div", "</div")
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.AUTHOR, "<a class=\"xcontrast_txt\"(.*?)>", "\">").setNth(0))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.GENRE, "<a class=\"xcontrast_txt\"(.*?)>", "\">").setNth(1))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.LANGUAGE, "<span class='xgray xcontrast_txt'>", "</span>")
                        .setLookupFilter(new LookupFilterSet(new String[]{"English"})))
                .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLETEXT, "<div class=\"storytext xcontrast_txt nocopy\" id=\"storytext\">", "</div>")
                        .setStartEndMarker("<div", "</div>")
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLEPARAGRAPH, "<p(.*?)>", "</p>")
                                .setRemoveTags(true)));

        WebTemplate articleTemplate = new WebTemplate(LookupOptions.FANFICDIRECTORY, "article-text", LookupOptions.EMPTYDOMAIN);
        articleTemplate.setMainPattern(articlePattern).setForceWrite(true)
                .setDomain("www.fanfiction.net")
                .setLookComplete(true)
                .setThreadSize(1)
                .setDoFast(false).setFunctionCall(loadCall)
                .setHtmlSaveFolder(LookupOptions.HTMLDIRECTORY)
                .setType(LookupOptions.ARTICLEDOC);


        cartoonTemplate.addNext(linkTemplate, LookupOptions.BOOKLINK);
        linkTemplate.addNext(articleTemplate, LookupOptions.ARTICLELINK);
        WebFlow flow = new WebFlow(cartoonTemplate);
        return flow;
    }

    public static void main(String[] args) {

        ExecutorService service = Executors.newFixedThreadPool(1);
        WebFlow.batchSubmit(service, build());
        service.shutdown();

    }
}
