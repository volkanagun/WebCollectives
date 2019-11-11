package data.crawler.sites;

import data.crawler.web.LookupOptions;
import data.crawler.web.LookupPattern;
import data.crawler.web.WebFlow;
import data.crawler.web.WebTemplate;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InternetHaber implements Serializable {

    public static WebFlow build(int i){

        int startIndice = 0;
        int maxSize = 20;
        WebTemplate linkTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-links", LookupOptions.EMPTY)
                .addSeed("magazine","http://www.internethaber.com/haber")
                .addSeed("politics","http://www.internethaber.com/politika")
                .addSeed("world","http://www.internethaber.com/dunya")
                .addSeed("economics","http://www.internethaber.com/ekonomi")
                .addSeed("sports","http://www.internethaber.com/spor")
                .addSeed("flash","http://www.internethaber.com/guncel-haberler")
                .addSeed("education","http://www.internethaber.com/egitim")
                .addSeed("auto","http://www.internethaber.com/otomobil")
                .addSeed("housing","http://www.internethaber.com/emlak")
                .addSeed("local","http://www.internethaber.com/yerel")
                .addSeed("media","http://www.internethaber.com/medya")
                .addSeed("health","http://www.internethaber.com/saglik")
                .addSeed("jobs","http://www.internethaber.com/calisma-hayati")
                .addSeed("technology","http://www.internethaber.com/bilim-teknoloji")
                .addSeed("magazine","http://www.internethaber.com/magazin")
                .addSeed("culture","http://www.internethaber.com/kultur-ve-sanat")
                .setNextPageStart(startIndice+i*maxSize)
                .setNextPageSize(maxSize)
                .setWaitTimeAfter(1000000L)
                .setWaitTime(50L)
                .setDoFast(false)
                .setDoDeleteStart(true)
                .setNextPageSuffix("?page=")
                .setThreadSize(12);

        /*LookupPattern linkPattern = new LookupPattern(LookupOptions.URL, LookupOptions.MAINPAGE, "<ul class=\"list\">", "</ul>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINKCONTAINER, "<li>", "</li>")
                        .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a href=\"", "\"")
                                .setNth(0)));*/

        LookupPattern linkPattern = new LookupPattern(LookupOptions.URL, LookupOptions.MAINPAGE, "<div class=\"container\">", "</div>")
                .setStartEndMarker("<div","</div>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINKCONTAINER, "<div class=\"row\">", "</div>")
                        .setStartEndMarker("<div","</div>")
                        .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a href=\"", "\"")));

        linkTemplate.setMainPattern(linkPattern);
        linkTemplate.setDomainSame(true);
        linkTemplate.setLinkPattern("(.*?)\\-\\d+\\p{L}\\.htm","(.*?)(foto|video)-galerisi\\-\\d+\\.htm");



        //Article Download
        WebTemplate articleTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-text", LookupOptions.EMPTY)
                .setType(LookupOptions.ARTICLEDOC)
                .setLookComplete(true)
                .setThreadSize(1)
                .setDoFast(true)
                .setHtmlSaveFolder(LookupOptions.HTMLDIRECTORY)
                .setForceWrite(false);

        LookupPattern articleLookup = new LookupPattern(LookupOptions.ARTICLE, LookupOptions.CONTAINER, "<div class=\"news-detail(.*?)\">", "</div>")
                .setStartEndMarker("<div","</div>")
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1(.*?)>","</h1>").setNth(0))
                .addPattern(new LookupPattern(LookupOptions.TEXT,LookupOptions.DATE, "<time datetime=\"","\">"))
                .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLETEXT,"<div class=\"content-text(.*?)\">","</div>")
                        .setStartEndMarker("<div", "</div>")
                        .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLEPARAGRAPH,"<p>","</p>")));


        articleTemplate.setMainPattern(articleLookup);


        linkTemplate.addNext(articleTemplate, LookupOptions.ARTICLELINK);

        WebFlow flow = new WebFlow(linkTemplate);
        return flow;
    }

    public static void main(String[] args) {
        for(int i=0; i< 5; i++) {
            build(i).execute();
        }
    }
}
