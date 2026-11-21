package data.crawler.sites;

import data.crawler.web.*;

public class WannartBlog {

    public static WebFlow build() {

        int pageCount = 100;
        String domain = "https://wannart.com/";

        WebFunctionCall clickCall = new WebButtonClickControl(1, "button.load-more-btn")
                .setDoStopOnError(false)
                .setWaitTime(50);

        WebFunctionCall scrollCall = new WebFunctionScrollHeight(1, 40)
                .setWaitTime(100);

        WebFunctionCall sequenceCall = new WebFunctionSequence(pageCount, clickCall, scrollCall).setDoFirefox(false)
                .setWaitBetweenCalls(500L)
                .initialize();

        //<a data-v-38474bd4="" href="/icerik/49994-spotify-2025-yilin-ozetini-duyurdu-iste-yilin-en-cok-dinlenenleri" class="title-link">
        LookupPattern linkPattern = new LookupPattern(LookupOptions.SKIP, LookupOptions.ARTICLELINKCONTAINER, "<article(.*?)class=\"post-card view-(landscape|grid)\"(.*?)>", "</article>")
                .setStartEndMarker("<article","</article>")
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLELINK, LookupOptions.NULL)
                        .setSingleRegex("(href=\"(\\/icerik(.*?))\" class=\"title-link\")")
                        .setSingleGroup(2));

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-links", domain)
                .addSeed("culture", "https://wannart.com/kategori/kultur-sanat")
                .addSeed("music", "https://wannart.com/kategori/muzik")
                .addSeed("cinema", "https://wannart.com/kategori/sinema")
                .addSeed("geek", "https://wannart.com/kategori/geek")
                .addSeed("trends", "https://wannart.com/kategori/moda")
                .addSeed("culture", "https://wannart.com/kategori/moda")
                .addSeed("culture", "https://wannart.com/")
                
                //.addSeed("blog", "https://wannart.com/en")
                .setDoFast(true)
                .setDoDeleteStart(true)
                .setSleepTime(1000L)
                .setFunctionCall(sequenceCall)
                .setThreadSize(1)
                .setDomain(domain)
                .setMainPattern(linkPattern);

        LookupPattern articleLookup = new LookupPattern(LookupOptions.CONTAINER, LookupOptions.ARTICLE, "<article(.*?)>", "</article>")
                .setNth(0)
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1(.*?)>", "</h1>")
                        .setNth(0)
                        .setRequiredNotEmpty(true)
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.AUTHOR, LookupOptions.NULL)
                        .setSingleRegex("<a href=\"/(.*?)\" class=\"meta-author\"")
                        .setSingleGroup(1))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.DATE, "<img src=\"/_nuxt/img/calendar.2c645a5.svg\" class=\"meta-icon\"(.*?)>", "</span>")
                        .setNth(0)
                        .setRequiredNotEmpty(false)
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.CONTAINER, LookupOptions.ARTICLETEXT, "<div class=\"post-body\"(.*?)>","</div>")
                        .setStartEndMarker("<div","</div>")
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLEPARAGRAPH, "<(p|h2|div)(.*?)>", "</(p|h2|div)>")
                                .setStartEndMarker("<(p|h2|div)","</(p|h2|div)>")
                                .setRemoveTags(true)));

        WebTemplate articleTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-text", domain)
                .setType(LookupOptions.BLOGDOC)
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
