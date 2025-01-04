package data.crawler.sites;


import data.crawler.web.*;

public class BBCTurkish {

    public static WebFlow build() {

        String domain = "https://www.bbc.com/turkce/";
        int pageCount = 1;

        LookupPattern linkPattern = new LookupPattern(LookupOptions.SKIP, LookupOptions.TEXT, "<main(.*?)>", "</main>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a(.*?)href=\"", "\""));

        LookupPattern shortArticle = new LookupPattern(LookupOptions.SKIP, LookupOptions.TEXT, "<main(.*?)>", "</main>")
                .setNth(0)
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.DATE, "<time(.*?)>", "</time>")
                        .setNth(0))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1(.*?)>", "</h1>")
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.CONTAINER, LookupOptions.ARTICLE, LookupOptions.EMPTY)
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETEXT, "<p(.*?)>", "</p>")
                                .setRemoveTags(true)));

        WebTemplate articleTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-text", domain)
                .setMainPattern(shortArticle);

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-links", domain)
                .addSeed("sports", "https://www.bbc.com/turkce/topics/cnq68n6wgzdt")
                .addSeed("technology", "https://www.bbc.com/turkce/topics/c2dwqnwkvnqt")
                .addSeed("science", "https://www.bbc.com/turkce/topics/c404v74nk56t")
                .addSeed("economy", "https://www.bbc.com/turkce/topics/cg726y2k82dt")
                .addSeed("health", "https://www.bbc.com/turkce/topics/cnq68n6wgzdt")
                .addSeed("world", "https://www.bbc.com/turkce/topics/c95y3wy7842t")
                .addSeed("world", "https://www.bbc.com/turkce/topics/cy0ryl4pvx6t")
                .addSeed("world", "https://www.bbc.com/turkce/topics/cg726yxwgygt")
                .addSeed("war", "https://www.bbc.com/turkce/topics/cy0ryl4pvx6t")
                .addSeed("health", "https://www.bbc.com/turkce/topics/ck0r47pk362t")
                .setSuffixGenerator(new WebCountGenerator(1, pageCount, "?page="))
                //.setLinkPattern("^https\\://(.*?)$","^https://www.bbc.com/\\?page=\\d+$")
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
