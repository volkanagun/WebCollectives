package data.crawler.sites;

import data.crawler.web.*;

public class IhlasAjansi {


    public static WebFlow build() {

        String domain = "https://www.iha.com.tr";
        int start = 1, size = 5;
        long seedSize = 100000;
        WebFunctionCall click = new WebActionClickCall(1, "a.inline-flex").setWaitTime(1000);
        //WebFunctionCall scrollCall = new WebFunctionScrollHeight(1).setMinusHeight(0).setWaitTime(500);
        WebFunctionCall sequence = new WebFunctionSequence(size, click).setDoRepetitiveInit(true).setDoFirefox(false).setWaitTime(300).initialize();

        LookupPattern linkPattern = new LookupPattern(LookupOptions.URL, LookupOptions.MAINPAGE, "<div class=\"container mx-auto\">", "</div>")
                .setStartEndMarker("<div", "</div>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a(.*?)href=\"", "\""));

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-links", domain)
                .addSeed("economy", "https://www.iha.com.tr/ekonomi")
                .addSeed("news", "https://www.iha.com.tr/dunya")
                .addSeed("breaking", "https://www.iha.com.tr/gundem")
                .addSeed("politics", "https://www.iha.com.tr/politika")
                .addSeed("criminal", "https://www.iha.com.tr/asayis")
                .addSeed("sports", "https://www.iha.com.tr/spor")
                .addSeed("local", "https://www.iha.com.tr/yerel-haberler")
                .addSeed("education", "https://www.iha.com.tr/egitim")
                .addSeed("culture", "https://www.iha.com.tr/kultur-sanat")
                .addSeed("technology", "https://www.iha.com.tr/teknoloji")
                .addSeed("environment", "https://www.iha.com.tr/cevre")
                .addSeed("tv-shows", "https://www.iha.com.tr/magazin")
                .setLinkPattern("(.*?)\\d+",null)
                .setDoFast(false)
                .setDoDeleteStart(true)
                .setFunctionCall(sequence)

                .setSleepTime(1L)
                .setThreadSize(1)
                .setDomain(domain)
                .setMainPattern(linkPattern);

        LookupPattern articleLookup = new LookupPattern(LookupOptions.ARTICLE, LookupOptions.CONTAINER, "<div class=\"column__left\">", "</div>")
                .setStartEndMarker("<div","</div>")
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.DATE, "<span class=\"text-sm text-gray-1 opacity-100\">", "</span>")
                        .setNth(0)
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1(.*?)>", "</h1>")
                        .setNth(0)
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLETEXT, "<div id=\"content\"(.*?)>", "</div>")
                        .setStartEndMarker("<div", "</div>").setNth(0)
                        .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLEPARAGRAPH, "<(p|h2(.*?))>", "</(p|h2)>")));


        WebTemplate articleTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-text", domain)
                .setType(LookupOptions.ARTICLEDOC)
                .setMainContent(true)
                .setLookComplete(true).setDoDeleteStart(false)
                .setThreadSize(1)
                .setDoFast(false)
                .setSleepTime(1000L)
                .setDomain(domain).setFunctionCall(new WebEmptyFunctionCall(true).setDoFirefox(false).initialize())
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
