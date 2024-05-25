package data.crawler.sites;

import data.crawler.web.*;


import java.io.Serializable;

/**
 * @author Volkan Agun
 * NOT RUNNING
 * Do it by json for at least 100 web sites...
 */
public class SozcuWeb implements Serializable {

    public static WebFlow build() {
        String domain = "http://www.sozcu.com.tr";
        int pageCount = 5;

        WebFunctionCall jsCall = new WebControlByIDClick(1, "javascript::").setWaitTime(10000);

        WebFunctionCall clickCall = new WebButtonClickControl(1, "button.loadmore")
                .setDoStopOnError(false)
                .setWaitTime(1000);

        WebFunctionCall scrollCall = new WebFunctionScroll(1,"button.loadmore").setWaitTime(1500);
        WebFunctionCall sequenceCall = new WebFunctionSequence(pageCount,jsCall, scrollCall,  clickCall)
                .setDoFirefox(true)
                .setWaitBetweenCalls(1000L)
                .setDoStopOnError(false)
                .initialize()
                .setWaitTime(1000);

        LookupPattern linkPattern = new LookupPattern(LookupOptions.URL, LookupOptions.MAINPAGE, "<div class=\"row\">", "</div>")
                .setStartEndMarker("<div", "</div>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "href=\"", "\""));

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.SOZCUDIRECTORY, "article-links", domain)
                .addSeed("breaking", "https://www.sozcu.com.tr/kategori/gundem/")
                .addSeed("breaking", "https://www.sozcu.com.tr/gunun-icinden/")
                .addSeed("health", "https://www.sozcu.com.tr/hayatim/")
                .addSeed("world", "https://www.sozcu.com.tr/kategori/dunya/")
                .addSeed("economy", "https://www.sozcu.com.tr/kategori/ekonomi/")
                .addSeed("economy", "https://www.sozcu.com.tr/finans/")
                .addSeed("auto", "https://www.sozcu.com.tr/kategori/otomotiv/")
                .addSeed("education", "https://www.sozcu.com.tr/kategori/egitim/")
                .addSeed("astrology", "https://www.sozcu.com.tr/astroloji/")
                .addSeed("health", "https://www.sozcu.com.tr/kategori/saglik/")
                .addSeed("technology", "https://www.sozcu.com.tr/bilim-teknoloji/")
                .setDoFast(false)
                .setDoDeleteStart(true)
                .setSleepTime(1000L)
                .setDomain(domain)
                .setFunctionCall(sequenceCall)
                .setMainPattern(linkPattern);

        LookupPattern articleLookup = new LookupPattern(LookupOptions.ARTICLE, LookupOptions.CONTAINER, "<article(.*?)>", "</article>")
                .setStartEndMarker("<article", "</article>")
                .setNth(0)
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.AUTHOR, "<div class=\"content-meta-name\">", "</div>")
                        .setStartEndMarker("<div","/div")
                        .setNth(0)
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.DATE, "<time (.*?)>", "</time>")
                        .setNth(0)
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1(.*?)>", "</h1>")
                        .setNth(0)
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.CONTAINER, LookupOptions.ARTICLE,LookupOptions.EMPTY)
                        .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLEPARAGRAPH, "<(p|h2(.*?))>", "</(p|h2)>")));


        WebTemplate articleTemplate = new WebTemplate(LookupOptions.SOZCUDIRECTORY, "article-text", domain)
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
        //articleTemplate.addExtraTemplate(linkTemplate, LookupOptions.ARTICLELINK);

        WebFlow flow = new WebFlow(linkTemplate);
        return flow;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 1; i++) {
            build().execute();
        }
    }
}
