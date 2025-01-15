package data.crawler.sites;

import data.crawler.web.LookupOptions;
import data.crawler.web.LookupPattern;
import data.crawler.web.WebFlow;
import data.crawler.web.WebTemplate;

public class TRTHaber {
    public static WebFlow build() {
        String domain = "http://www.trthaber.com";
        int pageCount = 20;
        int pageStart = 1;

        LookupPattern linkPattern = new LookupPattern(LookupOptions.URL, LookupOptions.MAINPAGE, "<div class=\"left\"(.*?)>", "</div>")
                .setStartEndMarker("<div", "</div>")
                .addPattern(new LookupPattern(LookupOptions.SKIP, LookupOptions.CONTAINER,"<div class=\"title\">","</div>")
                        .setStartEndMarker("<div","</div")
                        .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a(.*?)href=\"", "\"")));

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-links", domain)
                .addSeed("millitary", "https://www.trthaber.com/haber/savunma")
                .addSeed("science", "https://www.trthaber.com/haber/dunya-disi")
                .addSeed("culture", "https://www.trthaber.com/haber/kultur-sanat")
                .addSeed("life", "https://www.trthaber.com/haber/yasam")
                .addSeed("travel", "https://www.trthaber.com/gezi")
                .addSeed("environment", "https://www.trthaber.com/haber/cevre")
                .addSeed("news", "https://www.trthaber.com/haber/guncel")
                .addSeed("breaking", "https://www.trthaber.com/haber/gundem")
                .addSeed("breaking", "https://www.trthaber.com/tum-mansetler")
                .addSeed("tv-shows", "https://www.trthaber.com/programlar")
                .addSeed("archieve", "https://www.trthaber.com/haber/trt-arsiv")
                .addSeed("health", "https://www.trthaber.com/haber/saglik")
                .addSeed("technology", "https://www.trthaber.com/haber/bilim-teknoloji")
                .addSeed("education", "https://www.trthaber.com/haber/egitim")
                .addSeed("world", "https://www.trthaber.com/haber/dunya")
                .addSeed("turkey", "https://www.trthaber.com/haber/turkiye")
                .addSeed("economy", "https://www.trthaber.com/haber/ekonomi")
                .addSeed("sports", "https://www.trthaber.com/spor")
                .addSeed("news", "https://www.trthaber.com/ozel-haberler")
                .addSeed("news", "https://www.trthaber.com/dosya-haberler")
                .addSeed("kids", "https://www.trthaber.com/haber/cocuk")
                .setDoFast(false)
                .setDoDeleteStart(true)
                .setSleepTime(1000L).setNextPageSuffixAddition(".sayfa.html")
                .setNextPageSuffix("/")
                .setNextPageSize(pageCount)
                .setNextPageStart(pageStart)
                .setThreadSize(1)
                .setDomain(domain)
                .setMainPattern(linkPattern);

        LookupPattern articleLookup = new LookupPattern(LookupOptions.ARTICLE, LookupOptions.CONTAINER, "<div class=\"news-content-container\"(.*?)>", "</div>")
                .setStartEndMarker("<div", "</div>")
                .setNth(0)
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.AUTHOR, "<div class=\"author(.*?)\">", "</div>")
                        .setStartEndMarker("<div", "/div")
                        .setNth(0)
                        .setRemoveTags(true)
                        .setRequiredNotEmpty(false))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.DATE, "<time(.*?)datetime=\"", "\">")
                        .setNth(0)
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<(h1|h2)(.*?)>", "</(h1|h2)>")
                        .setNth(0)
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLETEXT, "<div class=\"news-content\">", "</div>")
                        .setStartEndMarker("<div", "</div>").setNth(0)
                        .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLEPARAGRAPH, "<(p|h2(.*?))>", "</(p|h2)>")));

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
        //articleTemplate.addExtraTemplate(linkTemplate, LookupOptions.ARTICLELINK);

        WebFlow flow = new WebFlow(linkTemplate);
        return flow;
    }

    public static void main(String[] args) {

        build().execute();

    }
}
