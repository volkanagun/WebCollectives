package data.crawler.sites;

import data.crawler.web.*;

public class AnkaHaber {
    public static WebFlow build() {
        String domain = "https://ankahaber.net";
        int pageCount = 200;

        WebFunctionCall functionCall = new WebButtonClickCall(1,"button.px-4.py-2.font-sans.text-black.border")
                .setDoStopOnError(false)
                .setWaitTime(1500);
       
        WebFunctionCall sequenceCall = new WebFunctionSequence(pageCount, functionCall)
                .setDoFirefox(false)
                .setWaitBetweenCalls(1000L).initialize()
                .setWaitTime(100);

        WebFunctionCall scrollJS = new WebFunctionScroll(1,"a.btn").setWaitTime(150);

        /*WebFunctionCall sequenceScrollCall = new WebFunctionSequence(1, scrollJS)
                .setDoFirefox(true)
                .setTryOnError(3)
                .setWaitBetweenCalls(100L)
                .initialize()
                .setWaitTime(100);*/

        LookupPattern linkPattern = new LookupPattern(LookupOptions.URL, LookupOptions.MAINPAGE, "<div class=\"space-y-4 sm:space-y-5 w-full\">", "</div>")
                .setStartEndMarker("<div", "</div>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a(.*?)href=\"", "\""));

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-links", domain)
                .addSeed("economy", "https://ankahaber.net/kategori/ekonomi/")
                .addSeed("culture", "https://ankahaber.net/kategori/bulten/")
                .addSeed("turkey", "https://ankahaber.net/kategori/yurt/")
                .addSeed("world", "https://ankahaber.net/kategori/dunya/")
                .addSeed("breaking", "https://ankahaber.net/kategori/gundem/")
                .setLinkPattern("(.*?\\d+)", null)
                .setDoFast(false)
                .setDoDeleteStart(true)
                .setSleepTime(1000L)
                .setWaitTime(2000L)
                .setFunctionCall(sequenceCall)
                .setThreadSize(1)
                .setDomain(domain)
                .setMainPattern(linkPattern);

        LookupPattern articleLookup = new LookupPattern(LookupOptions.ARTICLE, LookupOptions.CONTAINER, "<div data-news-entry-article=\"true\"(.*?)>", "</div>")
                .setStartEndMarker("<div", "</div>")
                .setNth(0)
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.DATE, "<div class=\"text-\\[12px\\] font-bold text-white\">","\\<"))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1(.*?)>", "</h1>")
                        .setNth(0)
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLETEXT, "div class=\"leading-relaxed text-article-body whitespace-normal\">", "</div>")
                        .setStartEndMarker("<div", "</div>").setNth(0)
                        .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLEPARAGRAPH, "<(p(.*?))>", "</(p)>")
                                .setRemoveTags(true)));

        WebTemplate articleTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-text", domain)
                .setType(LookupOptions.ARTICLEDOC)
                .setMainContent(true)
                .setLookComplete(true).setDoDeleteStart(false)
                .setThreadSize(1)
                .setFunctionCall(new WebEmptyFunctionCall().setDoFirefox(false).initialize())
                //.setFunctionCall(sequenceScrollCall)
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
