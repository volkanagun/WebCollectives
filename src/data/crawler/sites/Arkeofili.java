package data.crawler.sites;

import data.crawler.web.*;

public class Arkeofili {

    public static WebFlow build() {

        String domain = "https://arkeofili.com/";
        int pageCount = 5;

        LookupPattern linkPattern = new LookupPattern(LookupOptions.SKIP, LookupOptions.TEXT, "<h2>", "</h2>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a(.*?)href=\"", "\""));

        LookupPattern shortArticle = new LookupPattern(LookupOptions.SKIP, LookupOptions.TEXT, "<div id=\"main\">", "</div>")
                .setStartEndMarker("<div","</div>")
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.AUTHOR, "<a(.*?)rel=\"author\">", "</a>")
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1(.*?)", "</h1>"))
                .addPattern(new LookupPattern(LookupOptions.ARTICLEDOC, LookupOptions.CONTAINER, "<div id=\"content-area\">","</div>")
                        .setStartEndMarker("<div","</div")
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETEXT, "<p(.*?)", "</p>")));

        WebTemplate articleTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-text", domain)
                .setMainPattern(shortArticle);

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-links", domain)
                .addSeed("arkeoloji", "https://arkeofili.com/category/arkeoloji/")
                .addSeed("antropoloji", "https://arkeofili.com/category/antropoloji/")
                .addSeed("paleontoloji", "https://arkeofili.com/category/paleontoloji/")
                .addSeed("evolution", "https://arkeofili.com/category/evrim/")
                .addSeed("science", "https://arkeofili.com/category/ozel-dosya/")
                .addSeed("science", "https://arkeofili.com/category/liste/")
                .addSeed("herritage", "https://arkeofili.com/category/kulturel-miras/")
                .setSuffixGenerator(new WebCountGenerator(1, pageCount, "page/"))
                .setDoFast(Boolean.FALSE)
                .setDoDeleteStart(Boolean.TRUE)
                .setSleepTime(2500L)
                .setThreadSize(1)
                .setDomain(domain)
                .setMainPattern(linkPattern)
                .addExtraTemplate(articleTemplate, articleTemplate.getName());

        linkTemplate.addNext(articleTemplate, LookupOptions.ARTICLELINK);
        WebFlow flow = new WebFlow(linkTemplate);
        return flow;

    }

    public static void main(String[] args) {
        build().execute();
    }
}

