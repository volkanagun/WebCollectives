package data.crawler.sites;

import data.crawler.web.LookupOptions;
import data.crawler.web.LookupPattern;
import data.crawler.web.WebFlow;
import data.crawler.web.WebTemplate;


import java.io.Serializable;

/**
 * @author Volkan Agun
 * NOT RUNNING
 * Do it by json for at least 100 web sites...
 */
public class HurriyetWeb implements Serializable {

    public static WebFlow build() {
        String domain = "http://www.hurriyet.com.tr";
        int randomCount = 250;

        LookupPattern linkPattern = new LookupPattern(LookupOptions.URL, LookupOptions.MAINPAGE, "<div class=\"row\"(.*?)>", "</div>")
                .setStartEndMarker("<div", "</div>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a href=\"", "\""));

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.HURRIYETDIRECTORY, "article-links", domain)
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
                .setLinkPattern("(.*)","(.*?);")
                .setDoFast(false)
                .setDoDeleteStart(true)
                .setSleepTime(1000L)
                .setDoRandomSeed(randomCount)
                .setThreadSize(1)
                .setDomain(domain)
                .setMainPattern(linkPattern);

        LookupPattern articleLookup = new LookupPattern(LookupOptions.ARTICLE, LookupOptions.CONTAINER, "<div class=\"container\">", "</div>")
                .setStartEndMarker("<div", "</div>")
                .setNth(0)
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.AUTHOR, "<div class=\"news-profile\">", "</div>")
                        .setStartEndMarker("<div","/div")
                        .setNth(0)
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.SKIP, LookupOptions.CONTAINER, "<span class=\"news-date\">", "</span>")
                        .setNth(0)
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1(.*?)>", "</h1>")
                        .setNth(0)
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLETEXT, "<div class=\"news-content(.*?)>", "</div>")
                        .setStartEndMarker("<div", "</div>").setNth(0)
                        .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLEPARAGRAPH, "<(p|h2(.*?))>", "</(p|h2)>")))
                .addPattern(new LookupPattern(LookupOptions.SKIP, LookupOptions.CONTAINER, "<div class=\"news-tags\">", "</div>").setNth(0)
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.GENRE, "<a(.*?)>", "</a>")
                                        .setNth(0)
                                        .setRemoveTags(true)));

        WebTemplate articleTemplate = new WebTemplate(LookupOptions.HURRIYETDIRECTORY, "article-text", domain)
                .setType(LookupOptions.ARTICLEDOC)
                .setMainContent(true)
                .setLookComplete(true).setDoDeleteStart(false)
                .setThreadSize(1)
                .setDoFast(false)
                .setSleepTime(1000L)
                .setDomain(domain)
                .setDoRandomSeed(randomCount)
                .setHtmlSaveFolder(LookupOptions.HTMLDIRECTORY)
                .setMainPattern(articleLookup)
                .setForceWrite(true);

        linkTemplate.addNext(articleTemplate, LookupOptions.ARTICLELINK);
        articleTemplate.addExtraTemplate(linkTemplate, LookupOptions.ARTICLELINK);

        WebFlow flow = new WebFlow(linkTemplate);
        return flow;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 1; i++) {
            build().execute();
        }
    }
}
