package data.crawler.sites;

import data.crawler.web.LookupOptions;
import data.crawler.web.LookupPattern;
import data.crawler.web.WebFlow;
import data.crawler.web.WebTemplate;

public class SavunmaSanayist {
    public static WebFlow build(int start, int pageCount) {

        String domain = "https://www.savunmasanayist.com/";


        LookupPattern linkPattern = new LookupPattern(LookupOptions.SKIP, LookupOptions.TEXT, "<h2 class=\"post-title\">", "</h2>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a href=\"", "\""));

        LookupPattern shortArticle = new LookupPattern(LookupOptions.SKIP, LookupOptions.TEXT, "<div id=\"tie-wrapper\">", "</div>")
                .setStartEndMarker("<div","</div")
                .setNth(0)
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.AUTHOR, "<span class=\"meta-author\">", "</span>")
                        .setRemoveTags(true)
                        .setNth(0))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.DATE, "<span class=\"date meta-item tie-icon\">", "</span>")
                        .setNth(0))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1(.*?)>", "</h1>")
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.CONTAINER, LookupOptions.ARTICLE, "<div class=\"entry-content entry clearfix\">","</div")
                        .setStartEndMarker("<div","</div>")
                        .setNth(0)
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETEXT, "<(p|h4)(.*?)>", "</(p|h4)>")
                                .setRemoveTags(true)));

        WebTemplate articleTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-text", domain)
                .setMainPattern(shortArticle);

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-links", domain)
                .addSeed("defence", "https://www.savunmasanayist.com/category/haberler/")
                .addSeed("defence", "https://www.savunmasanayist.com/category/haberler/fms-haberleri/")
                .addSeed("defence", "https://www.savunmasanayist.com/category/haberler/ihracat-haberleri/")
                .addSeed("defence", "https://www.savunmasanayist.com/category/turk-savunma-sanayi/")
                .addSeed("defence", "https://www.savunmasanayist.com/category/yazilar/")
                .addSeed("defence", "https://www.savunmasanayist.com/category/soylesiler/")
                .setNextPageStart(start)
                .setNextPageSize(pageCount)
                .setNextPageSuffix("page/")
                .setDoFast(Boolean.TRUE)
                .setDoDeleteStart(Boolean.TRUE)
                .setSleepTime(100L)
                .setThreadSize(12)
                .setDomain(domain)
                .setMainPattern(linkPattern);


        linkTemplate.addNext(articleTemplate, LookupOptions.ARTICLELINK);
        WebFlow flow = new WebFlow(linkTemplate);
        return flow;

    }

    public static void main(String[] args) {
        for(int start=0; start<50; start+=10) {
            build(start, 10).execute();
        }
    }
}
