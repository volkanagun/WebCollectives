package data.crawler.sites;

import data.crawler.web.*;

public class TurkHaberTv {
    public static WebFlow build() {

        String domain = "https://www.turkhabertv.com/";
        int count = 100;

        /*WebFunctionCall clickCall = new WebButtonClickControl(1, "a.categoryLoadMore")
                .setDoStopOnError(false)
                .setWaitTime(500);*/

        WebFunctionCall scrollCall = new WebFunctionScrollHeight(1)
                .setWaitTime(1500)
                .setDoStopOnError(false);
        WebFunctionCall sequenceCall = new WebFunctionSequence(count,  scrollCall)
                .setTryOnError(10)
                .setDoFirefox(false)
                .setWaitBetweenCalls(1000L)
                .initialize()
                .setWaitTime(500);

        LookupPattern linkPattern = new LookupPattern(LookupOptions.URL, LookupOptions.MAINPAGE, "<div class=\"grid-list-container post-detail-page sb-kat-kartlar\" id=\"post-list\">", "</div>")
                .setStartEndMarker("<div", "</div>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a(.*?)href=\"", "\""));

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-links", domain)
                .addSeed("economy", "https://www.turkhabertv.com/category/ekonomi/")
                .addSeed("sports", "https://www.turkhabertv.com/category/spor/")
                .addSeed("world", "https://www.turkhabertv.com/category/dunya/")
                .addSeed("news", "https://www.turkhabertv.com/category/gundem/")
                .setDoFast(false)
                .setDoDeleteStart(true)
                .setFunctionCall(sequenceCall)
                .setThreadSize(1)
                .setDomain(domain)
                .setMainPattern(linkPattern);

        LookupPattern articleLookup = new LookupPattern(LookupOptions.ARTICLE, LookupOptions.CONTAINER, "<div class=\"news-detail-inner\">", "</div>")
                .setStartEndMarker("<div","</div>")
                .setNth(0)
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1(.*?)>", "</h1>")
                        .setNth(0)
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLETEXT, "<div class=\"news-detail-content(.*?)\">", "</div>")
                        .setStartEndMarker("<div", "</div>").setNth(0)
                        .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLEPARAGRAPH, "<p(.*?)>", "</p>")));

        WebTemplate articleTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-text", domain)
                .setType(LookupOptions.ARTICLEDOC)
                .setLookComplete(true)
                .setDoDeleteStart(false)
                .setThreadSize(1)
                .setDoFast(false)
                .setSleepTime(1000L)
                .setDomain(domain)
                .setHtmlSaveFolder(LookupOptions.HTMLDIRECTORY)
                .setMainPattern(articleLookup);

        linkTemplate.addNext(articleTemplate, LookupOptions.ARTICLELINK);
        //articleTemplate.addExtraTemplate(linkTemplate, LookupOptions.ARTICLELINK);

        WebFlow flow = new WebFlow(linkTemplate);
        return flow;
    }

    public static void main(String[] args) {
        build().execute();
    }
}
