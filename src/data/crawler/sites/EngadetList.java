package data.crawler.sites;

import data.crawler.web.LookupOptions;
import data.crawler.web.LookupPattern;
import data.crawler.web.WebFlow;
import data.crawler.web.WebTemplate;

import java.io.Serializable;

public class EngadetList implements Serializable {
    public static WebFlow build() {
        WebTemplate authorTemplate = new WebTemplate(LookupOptions.BLOGENGDIRECTORY, "author-links", LookupOptions.EMPTYDOMAIN);
        LookupPattern authorPattern = new LookupPattern(LookupOptions.URL, LookupOptions.CONTAINER, "<span\\sclass\\=\"block@m-\">", "</span>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.AUTHORLINK, "<a\\shref\\=\"", "\"(.*?)>"));

        authorTemplate.setDomain("https://www.engadget.com/")
                .addSeed("https://www.engadget.com")
                .setType("BLOG-DOC")
                .setNextPageSuffix("/all/page/")
                .setNextPageSize(100)
                .setNextPageStart(1)
                .setThreadSize(1)
                .setMainPattern(authorPattern);

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.BLOGENGDIRECTORY, "blog-links", LookupOptions.EMPTYDOMAIN);
        LookupPattern linkPattern = new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLELINK, null, null)
                .setSingleGroup(1).setSingleRegex("<a\\shref\\=\"(.*?)\"\\sclass\\=\"o-hit\\_\\_link\">");
        linkTemplate.setDomain("https://www.engadget.com/")
                .setNextPageSuffix("/page/")
                .setThreadSize(1)
                .setNextPageStart(1)
                .setNextPageSize(80)
                .setMainPattern(linkPattern);

        WebTemplate mainTemplate = new WebTemplate(LookupOptions.BLOGENGDIRECTORY, "blog-text", LookupOptions.EMPTYDOMAIN);
        LookupPattern topPattern = new LookupPattern(LookupOptions.CONTAINER, LookupOptions.ARTICLE, LookupOptions.EMPTY)
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.DATE, "<meta\\sclass\\=\"swiftype\"\\sname\\=\"published_at\"(.*?)content=\"", "\">"));

        LookupPattern mainPattern = new LookupPattern(LookupOptions.CONTAINER, LookupOptions.ARTICLE, "<article\\sclass\\=\"c-gray(.*?)\">", "</article>")
                .setStartEndMarker("<article", "</article>")
                .setType(LookupOptions.SKIP)
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1 class=\"t-h(.*?)\">", "</h1>"))
                .addPattern(new LookupPattern(LookupOptions.CONTAINER, LookupOptions.ARTICLE, "<div\\sclass\\=\"t-meta-small(.*?)>", "</div>")
                        .setStartEndMarker("<div", "</div>")
                        .setType(LookupOptions.SKIP)
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.AUTHOR, "<a\\shref\\=\"/about/(.*?)>", "</a>")
                                .setNth(0))
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.GENRE, "<a\\shref\\=\"/tags/(.*?)>", "</a>")))
                .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLETEXT, "<div\\sclass=\"article-text\\sc-gray-1\\sno-review\">", "</div>")
                        .setStartEndMarker("<div", "</div>")
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLEPARAGRAPH, "<p>", "</p>")));

        topPattern.addPattern(mainPattern);
        mainTemplate.setMainPattern(topPattern).setDomain("https://www.engadget.com")
                .setForceWrite(true)
                .setType("BLOG-DOC");

        authorTemplate.addNext(linkTemplate, LookupOptions.AUTHORLINK);
        linkTemplate.addNext(mainTemplate, LookupOptions.ARTICLELINK);

        WebFlow flow = new WebFlow(authorTemplate);
        return flow;

    }
}
