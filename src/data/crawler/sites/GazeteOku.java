package data.crawler.sites;

import data.crawler.web.LookupOptions;
import data.crawler.web.LookupPattern;
import data.crawler.web.WebFlow;
import data.crawler.web.WebTemplate;

import java.io.Serializable;

public class GazeteOku implements Serializable {
    public static WebFlow build() {

        //Main Download
        WebTemplate mainTemplate = new WebTemplate(LookupOptions.ARTICLEDIRECTORY, "article-links", "http://www.gazeteoku.com");
        String mainSeed = "http://www.gazeteoku.com/tum-yazarlar.html";
        mainTemplate.addSeed(mainSeed);

        LookupPattern yazarPattern = new LookupPattern(LookupOptions.URL, LookupOptions.MAINPAGE, "<li class=\"clearfix\">", "</li>")
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.AUTHORLINK, "<a\\shref\\=\"", "\" title").setNth(1));

        mainTemplate.setMainPattern(yazarPattern);

        //Link Download
        WebTemplate linkTemplate = new WebTemplate(LookupOptions.ARTICLEDIRECTORY, "author-links", "http://www.gazeteoku.com", "?page=")
                .setNextPageStart(1)
                .setNextPageSize(10);
        LookupPattern patternLinkArticle = new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLE, "<div\\sclass=\"syList\">", "</div>");
        LookupPattern patternLink = new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLELINK, "<a href=\"", "\"\\s");

        patternLinkArticle.addPattern(patternLink);
        linkTemplate.setMainPattern(patternLinkArticle);

        //Article Download
        WebTemplate articleTemplate = new WebTemplate(LookupOptions.ARTICLEDIRECTORY, "article-text", "http://www.gazeteoku.com")
                .setType(LookupOptions.ARTICLEDOC);
        LookupPattern nameLookup = new LookupPattern(LookupOptions.ARTICLE, LookupOptions.AUTHORNAME, "<span\\sclass=\"yiyName\">", "</span>")
                .setStartMarker("<span")
                .setEndMarker("</span>");

        LookupPattern titleLookup = new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLETITLE, "<div\\sclass=\"quotesEnd\">", "</div>");
        LookupPattern genreeLookup = new LookupPattern(LookupOptions.TEXT, LookupOptions.GENRE, LookupOptions.GENREPOLITICS);
        LookupPattern paragraphLookup = new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLEPARAGRAPH, "<p>", "</p>");
        LookupPattern contentLookup = new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLETEXT, "<div\\sclass=\"articleBlock\\sclearfix\">", "</div>")
                .setStartMarker("<div")
                .setEndMarker("</div>")
                .addPattern(paragraphLookup);

        LookupPattern articleLookup = new LookupPattern(LookupOptions.ARTICLE, LookupOptions.CONTAINER, "<div\\sclass=\"ydbLert\\s\">", "</div>")
                .setStartMarker("<div")
                .setEndMarker("</div")
                .addPattern(nameLookup)
                .addPattern(titleLookup)
                .addPattern(genreeLookup)
                .addPattern(contentLookup);

        articleTemplate.setMainPattern(articleLookup);
        articleTemplate.setType(LookupOptions.ARTICLEDOC);

        mainTemplate.addNext(linkTemplate, LookupOptions.AUTHORLINK);
        linkTemplate.addNext(articleTemplate, LookupOptions.ARTICLELINK);


        WebFlow flow = new WebFlow(mainTemplate);
        return flow;
    }
}
