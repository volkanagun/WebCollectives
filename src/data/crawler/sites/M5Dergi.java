package data.crawler.sites;

import data.crawler.web.*;

public class M5Dergi {

    public static WebFlow build(int start, int pageCount) {

        String domain = "https://m5dergi.com/";

        LookupPattern linkPattern = new LookupPattern(LookupOptions.SKIP, LookupOptions.TEXT, "<div class=\"post-details\">", "</div>")
                .setStartEndMarker("<div","</div>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a class=\"more-link button\" href=\"", "\""));

        LookupPattern shortArticle = new LookupPattern(LookupOptions.SKIP, LookupOptions.TEXT, "<article id=\"the-post\"(.*?)>", "</article>")
                .setNth(0)
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.DATE, "<span class=\"date meta-item fa-before\">", "</span>")
                        .setNth(0))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1(.*?)>", "</h1>")
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.CONTAINER, LookupOptions.ARTICLE, "<div class=\"entry-content(.*?)\">","</div")
                        .setNth(0)
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETEXT, "<(p|h4)(.*?)>", "</(p|h4)>")
                                .setRemoveTags(true)));

        WebTemplate articleTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-text", domain)
                .setMainPattern(shortArticle);

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-links", domain)
                .addSeed("defence", "https://m5dergi.com/kategori/son-sayi/makaleler/")
                .addSeed("defence", "https://m5dergi.com/kategori/infografik/")
                .addSeed("defence", "https://m5dergi.com/kategori/strateji-analiz/")
                .addSeed("defence", "https://m5dergi.com/kategori/dunya/")
                .addSeed("news", "https://m5dergi.com/kategori/savunma-haberleri/")
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
        for(int start=0; start<10; start+=50) {
            build(start, 50).execute();
        }
    }
}
