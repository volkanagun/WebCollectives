package data.crawler.sites;

import data.crawler.web.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StackExchange implements Serializable {
    public static WebFlow build() {
        String domain = "https://rpg.stackexchange.com";
        String url = "https://rpg.stackexchange.com/questions";

        List<WebSuffixGenerator> suffixGenerators = new ArrayList<>();
        suffixGenerators.add(new WebCountGenerator(1, 3000, "?tab=newest&page="));
        WebMultiSuffixGenerator suffixGenerator = new WebMultiSuffixGenerator(suffixGenerators);

        LookupPattern linkPattern = new LookupPattern(LookupOptions.SKIP, LookupOptions.CONTAINER, "<h3>", "</h3>")
                .setStartEndMarker("<h3", "</h3>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a href=\"", "\""));

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.FORUMENDIRECTORY, "forum-links", LookupOptions.EMPTYDOMAIN)
                .setMainPattern(linkPattern)
                .setDomain(domain)
                .addSeed(url);

        LookupPattern mainPattern = new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLE, "<div class=\"inner-content(.*?)>", "</div>")
                .setStartEndMarker("<div", "</div>")
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1(.*?)>", "</h1>")
                        .setNth(0)
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETEXT, "<div class=\"post-text\"(.*?)>", "</div>")
                        .setStartEndMarker("<div", "</div>")
                        .setRemoveTags(false)
                        .setNth(0)
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLEPARAGRAPH, "<p>","</p>")))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.DATE, "<span title=\"", "Z\"")
                        .setNth(0)
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.SKIP, LookupOptions.CONTAINER, "<div class=\"user-details\">", "</div>")
                        .setStartEndMarker("</div","</div>").setNth(0)
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.AUTHOR, "<a href=\"/users(.*?)>","</a>")));

        WebTemplate mainTemplate = new WebTemplate(LookupOptions.FORUMENDIRECTORY, "forum-text", LookupOptions.EMPTYDOMAIN);
        mainTemplate.setMainPattern(mainPattern)
                .setDoDeleteStart(true).setMultipleDocuments(true)
                .setMultipleIdentifier(LookupOptions.ARTICLETITLE)
                .setHtmlSaveFolder(LookupOptions.HTMLDIRECTORY)
                .setDomain(domain)
                .setSuffixGenerator(suffixGenerator)
                .setThreadSize(1);

        linkTemplate.addNext(mainTemplate, LookupOptions.ARTICLELINK);

        WebFlow flow = new WebFlow(linkTemplate);
        return flow;
    }

    public static void main(String[] args) {
        build().execute();
    }
}
