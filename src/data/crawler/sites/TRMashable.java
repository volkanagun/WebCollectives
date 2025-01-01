package data.crawler.sites;

import data.crawler.web.*;

import java.io.Serializable;

public class TRMashable implements Serializable {

    public static WebFlow build() {

        String domain = "http://tr.mashable.com";
        int count = 10;

        WebFunctionCall clickCall = new WebButtonClickControl(1, "button#showmore")
                .setDoStopOnError(true)
                .setWaitTime(500);

        WebFunctionCall scrollCall = new WebFunctionScrollHeight(1)
                .setWaitTime(500)
                .setDoStopOnError(false);
        WebFunctionCall sequenceCall = new WebFunctionSequence(count,  scrollCall, clickCall)
                .setTryOnError(10)
                .setDoFirefox(true)
                .setWaitBetweenCalls(1000L)
                .initialize()
                .setWaitTime(500);

        LookupPattern linkPattern = new LookupPattern(LookupOptions.URL, LookupOptions.MAINPAGE, "<div id=\"content\">", "</div>")
                .setStartEndMarker("<div", "</div>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a(.*?)href=\"", "\""));

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.BLOGDIRECTORY, "blog-links", domain)
                .addSeed("technology", "https://tr.mashable.com/tech/")
                .addSeed("life", "https://tr.mashable.com/life/")
                .addSeed("entertainment", "https://tr.mashable.com/entertainment/")
                .addSeed("science", "https://tr.mashable.com/science/")
                .addSeed("trends", "https://tr.mashable.com/social-good/")
                .addSeed("shopping", "https://tr.mashable.com/deals/")
                .addSeed("shopping", "https://tr.mashable.com/roundups/")
                .addSeed("science", "https://tr.mashable.com/uzay/")
                .addSeed("entertainment", "https://tr.mashable.com/tv-shows/")
                .addSeed("transportation", "https://tr.mashable.com/transportation/")
                .setDoFast(false)
                .setDoDeleteStart(true)
                .setFunctionCall(sequenceCall)
                .setThreadSize(1)
                .setDomain(domain)
                .setMainPattern(linkPattern);

        LookupPattern articleLookup = new LookupPattern(LookupOptions.ARTICLE, LookupOptions.CONTAINER, "<article(.*?)>", "</article>")
                .setNth(0)
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.AUTHOR, "mashable turkiye")
                        .setNth(0)
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1(.*?)>", "</h1>")
                        .setNth(0)
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.DATE, "<time datetime=(.*?)>", "</time>")
                        .setNth(0)
                        .setRemoveTags(true))

                .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLETEXT, "<div class=\"main-container\">", "</div>")
                        .setStartEndMarker("<div", "</div>").setNth(0)
                        .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLEPARAGRAPH, "<p>", "</p>")));

        WebTemplate articleTemplate = new WebTemplate(LookupOptions.BLOGDIRECTORY, "blog-text", domain)
                .setType(LookupOptions.BLOGDOC)
                .setMainContent(true)
                .setLookComplete(true).setDoDeleteStart(false)
                .setThreadSize(1)
                .setDoFast(false)
                .setSleepTime(1000L)
                .setDomain(domain)
                .setDoRandomSeed(count)
                .setHtmlSaveFolder(LookupOptions.HTMLDIRECTORY)
                .setMainPattern(articleLookup)
                .setForceWrite(true);

        linkTemplate.addNext(articleTemplate, LookupOptions.ARTICLELINK);
        //articleTemplate.addExtraTemplate(linkTemplate, LookupOptions.ARTICLELINK);

        WebFlow flow = new WebFlow(linkTemplate);
        return flow;
    }

    public static void main(String[] args) {
        build().execute();
    }
}