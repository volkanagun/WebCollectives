package data.crawler.sites;

import data.crawler.web.LookupOptions;
import data.crawler.web.LookupPattern;
import data.crawler.web.WebFlow;
import data.crawler.web.WebTemplate;

import java.io.Serializable;

public class Fotomac implements Serializable {



    public static WebFlow build() {
        WebTemplate mainTemplate = new WebTemplate(LookupOptions.ARTICLEDIRECTORY, "fotomac", "http://www.fotomac.com.tr");
        String mainSeed = "http://www.fotomac.com.tr/yazarlar/tumyazarlar";
        mainTemplate.addSeed(mainSeed);
        mainTemplate.setNextPageSuffix("?tc=107&page=");

        //Download Author Archive
        LookupPattern yazarPattern = new LookupPattern(LookupOptions.URL, LookupOptions.CONTAINER, "<ul\\sclass=\"writerList\">", "</ul>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.AUTHOR, "<li>", "</li>")
                        .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.AUTHORLINK, "<a href=\"", "\"\\sclass=\"article\">")))
                .setStartEndMarker("<ul", "</ul>");

        mainTemplate.setMainPattern(yazarPattern);

        //Download Writing Links for each author archive
        WebTemplate linkTemplate = new WebTemplate(LookupOptions.ARTICLEDIRECTORY, "article-links", "http://www.fotomac.com.tr/");
        LookupPattern linkPattern = new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLE, "<ul\\sclass=\"writerArchive\">", "</ul>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a href=\"", "\"\\s"));


        linkTemplate.setMainPattern(linkPattern);

        WebTemplate articleTemplate = new WebTemplate(LookupOptions.ARTICLEDIRECTORY, "article-text", "http://www.fotomac.com.tr")
                .setCharset("Windows-1254")
                .setType(LookupOptions.ARTICLEDOC);
        LookupPattern articlePattern = new LookupPattern(LookupOptions.CONTAINER, LookupOptions.ARTICLE, "<div class=\"detail news\">", "</div>")
                .setStartMarker("<div")
                .setEndMarker("</div>")
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.AUTHORNAME, "<span class=\"title\">", "</span>"))
                .addPattern(new LookupPattern(LookupOptions.LOOKUP, LookupOptions.GENRE, LookupOptions.GENRESPORTS))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1\\sid=\"NewsTitle\">", "</h1>"))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETEXT, "<p\\sid=\"NewsDescription\">", "</p>")
                        .setStartMarker("<p").setEndMarker("</p>"));

        articleTemplate.setMainPattern(articlePattern);
        articleTemplate.setType(LookupOptions.ARTICLEDOC);

        linkTemplate.addNext(articleTemplate, LookupOptions.ARTICLELINK);
        mainTemplate.addNext(linkTemplate, LookupOptions.AUTHORLINK);


        WebFlow flow = new WebFlow(mainTemplate);

        return flow;

    }
}
