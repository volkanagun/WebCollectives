package data.crawler.sites;

import data.crawler.web.*;

public class GZTBlog {

    public static WebFlow build() {

        int pageCount = 5;
        String domain = "https://www.gzt.com/";

        WebFunctionCall scrollCall = new WebFunctionScrollHeight(1, 40)
                .setWaitTime(1000);

        WebFunctionCall sequenceCall = new WebFunctionSequence(pageCount, scrollCall).setDoFirefox(true)
                .setWaitBetweenCalls(1000L)
                .initialize();

        LookupPattern linkPattern = new LookupPattern(LookupOptions.SKIP, LookupOptions.ARTICLELINKCONTAINER, "<div class=\"lazy\"(.*?)>", "</div>")
                .setStartEndMarker("<div", "</div>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a href=\"", "\"").setRemoveTags(true));

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-links", domain)
                .addSeed("general", "https://www.gzt.com/")
                .addSeed("history", "https://www.gzt.com/mecra")
                .addSeed("articles", "https://www.gzt.com/jurnalist")
                .addSeed("historical", "https://www.gzt.com/derin-tarih")
                .addSeed("discussion", "https://www.gzt.com/gercek-hayat/dusunce")
                .addSeed("culture", "https://www.gzt.com/lokma")
                .addSeed("world", "https://www.gzt.com/gercek-hayat")
                .addSeed("politics", "https://www.gzt.com/jurnalist")
                .addSeed("culture", "https://www.gzt.com/cins")
                .addSeed("culture", "https://www.gzt.com/lokma/blog")
                .addSeed("economy", "https://www.gzt.com/ekonomi")
                .addSeed("auto", "https://www.gzt.com/otomobil")
                .addSeed("information", "https://www.gzt.com/infografik/lugat")
                .addSeed("sports", "https://www.gzt.com/zpor")
                .setDoFast(false)
                .setDoDeleteStart(true)
                .setSleepTime(1000L)
                .setFunctionCall(sequenceCall)
                .setThreadSize(1)
                .setDomain(domain)
                .setMainPattern(linkPattern);

        LookupPattern articleLookup = new LookupPattern(LookupOptions.ARTICLE, LookupOptions.CONTAINER, "<div property=\"articleBody\"(.*?)>", "</div>")
                .setStartEndMarker("<div", "</div")
                .setNth(0)
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1(.*?)>", "</h1>")
                        .setNth(0)
                        .setRequiredNotEmpty(true)
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.DATE, "<div class=\"news-date-content\">", "</div>")
                        .setNth(0)
                        .setStartEndMarker("<div","</div")
                        .setRequiredNotEmpty(false)
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.AUTHOR, "<span class=\"news-detail-cover-post-info-author(.*?)>", "</div>")
                        .setNth(0)
                        .setRequiredNotEmpty(false)
                        .setStartEndMarker("<div","</div")
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLEPARAGRAPH, "<div id=\"dynamic-context\"(.*?)>", "</div>")
                        .setStartEndMarker("<div", "</div>")
                        .setRequiredNotEmpty(true)
                        .setNth(0))
                .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLEPARAGRAPH, "<(p|h2(.*?))>", "</(p|h2)>")
                        .setRemoveTags(true));

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
