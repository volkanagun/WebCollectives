package data.crawler.sites;

import data.crawler.web.LookupOptions;
import data.crawler.web.LookupPattern;
import data.crawler.web.WebFlow;
import data.crawler.web.WebTemplate;

import java.io.Serializable;

/**
 * @author Volkan Agun
 * NOT RUNNING
 * Do it by json for at least 100 web-sites...
 */
public class HalkTVWeb implements Serializable {

    public static WebFlow build() {
        String domain = "http://halktv.com.tr";
        int pageCount = 50;

        LookupPattern linkPattern = new LookupPattern(LookupOptions.URL, LookupOptions.MAINPAGE, "<div class=\"row\"(.*?)>", "</div>")
                .setStartEndMarker("<div", "</div>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a(.*?)href=\"", "\""));

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.CUMHURIYETDIRECTORY, "article-links", domain)
                .addSeed("sports", "https://halktv.com.tr/spor")
                .addSeed("world", "https://halktv.com.tr/dunya")
               .addSeed("culture", "https://halktv.com.tr/kultur-sanat")
                .addSeed("culture", "https://halktv.com.tr/tiyatro")
                .addSeed("culture", "https://halktv.com.tr/kitap")
                .addSeed("culture", "https://halktv.com.tr/sinema")
                .addSeed("health", "https://halktv.com.tr/yasam")
                .addSeed("health", "https://halktv.com.tr/saglik")
                .addSeed("health", "https://halktv.com.tr/guzellik")
                .addSeed("economy", "https://halktv.com.tr/finans")
                .addSeed("economy", "https://halktv.com.tr/borsa")
                .addSeed("economy", "https://halktv.com.tr/kripto")
                .addSeed("breaking", "https://halktv.com.tr/gundem")
                .addSeed("trends", "https://halktv.com.tr/moda")
                .addSeed("education", "https://halktv.com.tr/egitim")
                .addSeed("music", "https://halktv.com.tr/muzik")
                .addSeed("sports", "https://halktv.com.tr/futbol")
                .setDoFast(false).setNextPageSuffix("?page=").setNextPageSize(pageCount)
                .setDoDeleteStart(true)
                .setSleepTime(1000L)
                .setThreadSize(1)
                .setDomain(domain)
                .setMainPattern(linkPattern);

        LookupPattern articleLookup = new LookupPattern(LookupOptions.ARTICLE, LookupOptions.CONTAINER, "<div class=\"col-12 col-lg-8 article-detail news-detail\">", "</div>")
                .setStartEndMarker("<div", "</div>")
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.DATE, "<time datetime=\"", "\">")
                        .setNth(0)
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1(.*?)>", "</h1>")
                        .setNth(0)
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLETEXT, "<div class=\"article-content\"(.*?)>", "</div>")
                        .setStartEndMarker("<div","</div")
                        .setNth(0)
                        .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLEPARAGRAPH, "<p>", "</p>")
                                .setRemoveTags(true)));

        WebTemplate articleTemplate = new WebTemplate(LookupOptions.HALKTVDIRECTORY, "article-text", domain)
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
