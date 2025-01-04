package data.crawler.sites;

import data.crawler.web.*;

public class TurkiyeHaberAjansi {

    public static WebFlow build() {

        String domain = "https://www.turkiyehaberajansi.com/";
        int pageCount = 10;

        WebFunctionCall functionCall = new WebButtonClickCall(1, "a.get_ajax_data")
                .setDoStopOnError(false)
                .setWaitTime(500);

        WebFunctionCall scrollCall = new WebFunctionScrollHeight(1).setWaitTime(500);
        WebFunctionCall sequenceCall = new WebFunctionSequence(pageCount, scrollCall, functionCall)
                .setDoFirefox(true)
                .setWaitBetweenCalls(1000L)
                .initialize()
                .setWaitTime(1000);

        LookupPattern linkPattern = new LookupPattern(LookupOptions.URL, LookupOptions.MAINPAGE, "<div class=\"col-md-6\">", "</div>")
                .setStartEndMarker("<div", "</div>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a(.*?)href=\"", "\""));

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-links", domain)
                .addSeed("health", "https://www.turkiyehaberajansi.com/saglik/")
                .addSeed("politics", "https://www.turkiyehaberajansi.com/siyaset/")
                .addSeed("breaking", "https://www.turkiyehaberajansi.com/guncel/")
                .addSeed("economy", "https://www.turkiyehaberajansi.com/ekonomi/")
                .addSeed("culture", "https://www.turkiyehaberajansi.com/kultur-sanat-sinema/")
                .addSeed("education", "https://www.turkiyehaberajansi.com/egitim/")
                .addSeed("world", "https://www.turkiyehaberajansi.com/dunya/")
                .addSeed("sports", "https://www.turkiyehaberajansi.com/spor/")
                .addSeed("media", "https://www.turkiyehaberajansi.com/medya/")
                .addSeed("istanbul", "https://www.turkiyehaberajansi.com/istanbul/")

                .setLinkPattern("(.*?\\d+/)", null)
                .setDoFast(false)
                .setDoDeleteStart(true)
                .setSleepTime(1000L)
                .setFunctionCall(sequenceCall)
                .setThreadSize(1)
                .setDomain(domain)
                .setMainPattern(linkPattern);

        LookupPattern articleLookup = new LookupPattern(LookupOptions.ARTICLE, LookupOptions.CONTAINER, "<article(.*?)\">", "</article>")
                .setStartEndMarker("<article", "</article>")
                .setNth(1)
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.DATE, "<div class=\"(.*?)text-12 text-fade\">", "</div>")
                        .setStartEndMarker("<div", "</div>")
                        .setNth(0)
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1(.*?)>", "</h1>")
                        .setNth(0)
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLETEXT, "<div class=\"post-body(.*?)>", "</div>")
                        .setStartEndMarker("<div", "</div")
                        .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLEPARAGRAPH, "<div class=\"detay\" property=\"articleBody\">", "</div>")
                                .setStartEndMarker("<div", "</div>")
                                .setRemoveTags(true)));

        WebTemplate articleTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-text", domain)
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
        build().execute();
    }
}
