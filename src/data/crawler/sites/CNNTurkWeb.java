package data.crawler.sites;

import data.crawler.web.*;

import java.io.Serializable;

/**
 * @author Volkan Agun
 */
public class CNNTurkWeb implements Serializable {
    public static WebFlow build() {
        String domain = "https://www.cnnturk.com";
        int pageCount = 1000;

        WebButtonClickCall clickCall = new WebButtonClickCall(1, "button.btn.btn-load-more");

        WebFunctionScrollHeight scrollCall = new WebFunctionScrollHeight(2);
        WebFunctionCall sequenceCall = new WebFunctionSequence(pageCount, scrollCall/*, clickCall*/).initialize();

        LookupPattern linkPattern = new LookupPattern(LookupOptions.URL, LookupOptions.MAINPAGE, "<div class(.*?)>", "</div>")
                .setStartEndMarker("<div", "</div>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a(.*?)href=\"", "\""));

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-links", domain)
                .addSeed("economy", "https://www.cnnturk.com/ekonomi-haberleri")
                .addSeed("world", "https://www.cnnturk.com/dunya-haberleri")
                .addSeed("trending", "http://www.hurriyet.com.tr/gundem/")
                .addSeed("sports", "https://www.cnnturk.com/spor-haberleri")
                .addSeed("travel", "https://www.cnnturk.com/seyahat-haberleri")
                .addSeed("magazine", "https://www.cnnturk.com/magazin-haberleri")
                .addSeed("technology", "https://www.cnnturk.com/teknoloji-haberleri")
                .addSeed("news", "https://www.cnnturk.com/turkiye")
                .addSeed("general", "https://www.cnnturk.com/yasam-haberleri")
                .addSeed("health", "https://www.cnnturk.com/iyiliksaglik")
                .addSeed("arts", "https://www.cnnturk.com/kultur-sanat-haberleri")
                .addSeed("local-news", "https://www.cnnturk.com/yerel-haberler")
                .addSeed("auto", "https://www.cnnturk.com/otomobil-haberleri")
                .addSeed("realestate", "https://www.cnnturk.com/emlak")
                .setDoFast(false)
                .setDoDeleteStart(true)
                .setSleepTime(500L)
                .setFunctionCall(sequenceCall)
                .setThreadSize(1)
                .setDomain(domain)
                .setMainPattern(linkPattern);

        LookupPattern articleLookup = new LookupPattern(LookupOptions.ARTICLE, LookupOptions.CONTAINER, "<div class=\"container section-container\">", "</div>")
                .setStartEndMarker("<div", "</div>")
                .setNth(0)
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1 class=\"detail-title\">", "</h1>")
                        .setNth(0)
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLETEXT, "<div class=\"row(.*?)\">", "</div>")
                        .setStartEndMarker("<div", "</div>").setNth(0)
                        .addPattern(new LookupPattern(LookupOptions.SKIP, LookupOptions.CONTAINER, "<div class=\"detail-meta(.*?)\">", "</div>")
                                .setNth(0)
                                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.DATE, "<time(.*?)>", "</time>")
                                        .setNth(0)
                                        .setRemoveTags(true))
                                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.AUTHOR, "<b>", "</b>")
                                        .setNth(0)
                                        .setRemoveTags(true)))
                        .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLEPARAGRAPH, "<(p|h2(.*?))>", "</(p|h2)>")
                                .setRemoveTags(true)));

        WebTemplate articleTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-text", domain)
                .setType(LookupOptions.ARTICLEDOC)
                .setLookComplete(false)
                .setThreadSize(1)
                .setDoFast(true)
                .setDomain(domain)
                .setHtmlSaveFolder(LookupOptions.HTMLDIRECTORY)
                .setMainPattern(articleLookup)
                .setForceWrite(false);

        linkTemplate.addNext(articleTemplate, LookupOptions.ARTICLELINK);
        WebFlow flow = new WebFlow(linkTemplate);
        return flow;
    }

    public static void main(String[] args) {
        build().execute();
    }
}
