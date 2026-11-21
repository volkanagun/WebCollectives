package data.crawler.sites;

import data.crawler.web.*;

public class GZTBlog {

    public static WebFlow build() {

        int pageCount = 1;
        String domain = "https://www.gzt.com";

        WebFunctionCall scrollCall = new WebFunctionScrollHeight(1, 40)
                .setWaitTime(1000);

        WebFunctionCall sequenceCall = new WebFunctionSequence(pageCount, scrollCall).setDoFirefox(false)
                .setWaitBetweenCalls(1000L)
                .initialize();

        LookupPattern linkPattern = new LookupPattern(LookupOptions.SKIP, LookupOptions.ARTICLELINKCONTAINER, "<div class=\"feed-card-content\">", "</div>")
                .setStartEndMarker("<div", "</div>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a(.*?)href=\"", "\"").setRemoveTags(true));

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-links", domain)
                .addSeed("news", "https://www.gzt.com/gundem")
                .addSeed("news", "https://www.gzt.com/yazarlar")
                .addSeed("culture", "https://www.gzt.com/sinema")
                .addSeed("culture", "https://www.gzt.com/kultur")
                .addSeed("culture", "https://www.gzt.com/hayat")
                .addSeed("travel", "https://www.gzt.com/seyahat")
                .addSeed("history", "https://www.gzt.com/mecra")
                .addSeed("articles", "https://www.gzt.com/jurnalist")
                .addSeed("historical", "https://www.gzt.com/derin-tarih")
                .addSeed("history", "https://www.gzt.com/tarih")
                .addSeed("discussion", "https://www.gzt.com/gercek-hayat/dusunce")
                .addSeed("culture", "https://www.gzt.com/lokma")
                .addSeed("world", "https://www.gzt.com/gercek-hayat")
                .addSeed("world", "https://www.gzt.com/dunya")
                .addSeed("games", "https://www.gzt.com/oyun")
                .addSeed("politics", "https://www.gzt.com/jurnalist")
                .addSeed("culture", "https://www.gzt.com/cins")
                .addSeed("culture", "https://www.gzt.com/lokma/blog")
                .addSeed("economy", "https://www.gzt.com/ekonomi")
                .addSeed("auto", "https://www.gzt.com/otomobil")
                .addSeed("information", "https://www.gzt.com/infografik/lugat")
                .addSeed("sports", "https://www.gzt.com/spor")
                .addSeed("architecture", "https://www.gzt.com/mimari")
                .addSeed("technology", "https://www.gzt.com/teknoloji")
                .addSeed("culture", "https://www.gzt.com/eglence")
                .setDoFast(true)
                .setDoDeleteStart(true)
                .setSleepTime(1000L)
                .setFunctionCall(sequenceCall)
                .setThreadSize(1)
                .setDomain(domain)
                .setMainPattern(linkPattern);

        LookupPattern articleLookup = new LookupPattern(LookupOptions.CONTAINER, LookupOptions.ARTICLE, "<div class=\"detail-layout\" data-pr-detail-page>", "</div>")
                .setStartEndMarker("<div", "</div>")
                .setNth(0)
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1(.*?)>", "</h1>")
                        .setNth(0)
                        .setRequiredNotEmpty(true)
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.DATE, "<span class=\"detail-head-text\">", "</span>")
                        .setNth(0)
                        .setRequiredNotEmpty(false)
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.CONTAINER, LookupOptions.ARTICLETEXT, "<div class=\"node-renderer detail-node-renderer\">","</div>")
                        .setStartEndMarker("<div","</div>")
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLEPARAGRAPH, "<(p|h2)(.*?)>", "</(p|h2)>")
                                .setRemoveTags(true)));

        WebTemplate articleTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-text", domain)
                .setType(LookupOptions.ARTICLEDOC)
                .setLookComplete(false)
                .setThreadSize(1)
                .setDoFast(false)
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
        build().execute();
    }
}
