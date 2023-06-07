package data.crawler.sites;

import data.crawler.web.*;

import java.io.Serializable;

/**
 * @author Volkan Agun
 * NOT RUNNING
 * Do it by json for at least 100
 */
public class CumhuriyetAuto implements Serializable {

    public static WebFlow build() {
        String domain = "http://www.cumhuriyet.com.tr";
        int pageCount = 100;

        WebFunctionCall functionCall = new WebExecuteJS("loadMoreNews()")
                .setDoStopOnError(true)
                .setWaitTime(2000);

        WebFunctionCall scrollCall = new WebFunctionScrollHeight(1).setWaitTime(1500);
        WebFunctionCall sequenceCall = new WebFunctionSequence(pageCount,  scrollCall, functionCall)
                .setDoFirefox(true)
                .setWaitBetweenCalls(1000L)
                .initialize()
                .setWaitTime(1000);

        LookupPattern linkPattern = new LookupPattern(LookupOptions.URL, LookupOptions.MAINPAGE, "<div class=\"content\"(.*?)>", "</div>")
                .setStartEndMarker("<div", "</div>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a(.*?)href=\"", "\""));

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.CUMHURIYETDIRECTORY, "article-links", domain)
                .addSeed("breaking", "http://www.cumhuriyet.com.tr/gundem/")
                .addSeed("world", "http://www.cumhuriyet.com.tr/dunya/")
                .addSeed("tukey", "http://www.cumhuriyet.com.tr/turkiye/")
                .addSeed("sports", "http://www.cumhuriyet.com.tr/spor/")
                .addSeed("economy", "http://www.cumhuriyet.com.tr/ekonomi/")
                .addSeed("politics", "http://www.cumhuriyet.com.tr/siyaset/")
                .addSeed("health", "http://www.cumhuriyet.com.tr/yasam/")
                .addSeed("science", "https://www.cumhuriyet.com.tr/bilim-teknoloji/")
                .addSeed("culture", "https://www.cumhuriyet.com.tr/kultur-sanat/")
                .setDoFast(false)
                .setDoDeleteStart(true)
                .setSleepTime(1000L)
                .setFunctionCall(sequenceCall)
                .setThreadSize(1)
                .setDomain(domain)
                .setMainPattern(linkPattern);

        LookupPattern articleLookup = new LookupPattern(LookupOptions.ARTICLE, LookupOptions.CONTAINER, "<div class=\"row main-row\">", "</div>")
                .setStartEndMarker("<div", "</div>")
                .setNth(0)
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.AUTHOR, "<div class=\"haberKaynagi\">", "</div>")
                        .setStartEndMarker("<div","/div")
                        .setNth(0)
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.DATE, "<div class=\"yayin-tarihi\">", "</div>")
                        .setStartEndMarker("<div","</div>")
                        .setNth(0)
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<(h1|h2)(.*?)>", "</(h1|h2)>")
                        .setNth(0)
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLETEXT, "<div class=\"haberMetni(.*?)>", "</div>")
                        .setStartEndMarker("<div", "</div>").setNth(0)
                        .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLEPARAGRAPH, "<(p|h2(.*?))>", "</(p|h2)>")));

        WebTemplate articleTemplate = new WebTemplate(LookupOptions.CUMHURIYETDIRECTORY, "article-text", domain)
                .setType(LookupOptions.ARTICLEDOC)
                .setMainContent(true)
                .setLookComplete(true).setDoDeleteStart(false)
                .setThreadSize(1)
                .setDoFast(false)
                .setSleepTime(1000L)
                .setDomain(domain)
                .setHtmlSaveFolder(LookupOptions.HTMLDIRECTORY)
                .setMainPattern(articleLookup)
                .setForceWrite(true);

        linkTemplate.addNext(articleTemplate, LookupOptions.ARTICLELINK);

        WebFlow flow = new WebFlow(linkTemplate);
        return flow;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 1; i++) {
            build().execute();
        }
    }
}
