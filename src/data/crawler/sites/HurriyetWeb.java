package data.crawler.sites;

import data.crawler.web.LookupOptions;
import data.crawler.web.LookupPattern;
import data.crawler.web.WebFlow;
import data.crawler.web.WebTemplate;

import javax.jws.WebParam;
import java.io.Serializable;

/**
 * @author Volkan Agun
 * Do it by json for at least 100 web sites...
 */
public class HurriyetWeb implements Serializable {

    public static WebFlow build() {

        String domain = "http://www.hurriyet.com.tr";

        LookupPattern linkPattern = new LookupPattern(LookupOptions.URL, LookupOptions.MAINPAGE, "<div class(.*?)>", "</div>")
                .setStartEndMarker("<div", "</div>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a href=\"", "\""));

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-links", domain)
                .addSeed("economy", "http://www.hurriyet.com.tr/ekonomi/")
                .addSeed("world", "http://www.hurriyet.com.tr/dunya/")
                .addSeed("trending", "http://www.hurriyet.com.tr/gundem/")
                .addSeed("sports", "http://www.hurriyet.com.tr/sporarena/")
                .addSeed("travel", "http://www.hurriyet.com.tr/seyahat/")
                .addSeed("magazine", "http://www.hurriyet.com.tr/kelebek-magazin/")
                .addSeed("arts", "http://www.hurriyet.com.tr/hayat/")
                .addSeed("news", "http://www.hurriyet.com.tr/son-dakika-haberleri/")
                .addSeed("technology", "http://www.hurriyet.com.tr/teknoloji/")
                .addSeed("local-news", "http://www.hurriyet.com.tr/yerel-haberler/")
                .addSeed("health", "http://www.hurriyet.com.tr/saglik/")
                .addSeed("accident-news", "http://www.hurriyet.com.tr/yoldurumu/")
                .addSeed("arts", "http://www.hurriyet.com.tr/kitap-sanat/")
                .setDoFast(false)
                .setDoDeleteStart(true)
                .setSleepTime(500L)
                .setThreadSize(4)
                .setDomain(domain)
                .setMainPattern(linkPattern);

        LookupPattern articleLookup = new LookupPattern(LookupOptions.ARTICLE, LookupOptions.CONTAINER, "<div class=\"container\">", "</div>")
                .setStartEndMarker("<div", "</div>")
                .setNth(0)
                .addPattern(new LookupPattern(LookupOptions.SKIP, LookupOptions.CONTAINER, "<header class(.*?)>", "</header>").setNth(0)
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.GENRE, "<div class=\"rhd-category-box(.*?)\">", "</div>")
                                .setNth(0)
                                .setRemoveTags(true)))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.AUTHOR, "((<div class=\"rhd-editor-title\">)|(<h6 class=\"rhd-author-name\">))", "</div>")
                        .setNth(0)
                        .setValue("hurriyet")
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.DATE, "<div class=\"rhd-time-box\">", "</div>")
                        .setNth(0)
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1 class=\"rhd-article-title\">", "</h1>")
                        .setNth(0)
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLETEXT, "<div class=\"rhd-sticky-limit\">", "</div>")
                        .setStartEndMarker("<div", "</div>").setNth(0)
                        .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLEPARAGRAPH, "<(p|h2(.*?))>", "</(p|h2)>")));

        WebTemplate articleTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-text", domain)
                .setType(LookupOptions.ARTICLEDOC)
                .setLookComplete(true)
                .setThreadSize(1)
                .setDoFast(true)
                .setSleepTime(1000L)
                .setDomain(domain)
                .setHtmlSaveFolder(LookupOptions.HTMLDIRECTORY)
                .setMainPattern(articleLookup)
                .setForceWrite(false);

        linkTemplate.addNext(articleTemplate, LookupOptions.ARTICLELINK);
        WebFlow flow = new WebFlow(linkTemplate);
        return flow;

    }

    public static void main(String[] args) {
        for(int i=0; i<2; i++) {
            build().execute();
        }
    }
}
