package data.crawler.sites;

import data.crawler.web.LookupOptions;
import data.crawler.web.LookupPattern;
import data.crawler.web.WebFlow;
import data.crawler.web.WebTemplate;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Should be updated!!!
 */
public class EngadetList implements Serializable {
    public static WebFlow build() {
        WebTemplate authorTemplate = new WebTemplate(LookupOptions.BLOGENGDIRECTORY, "author-links", LookupOptions.EMPTYDOMAIN);
        /*LookupPattern authorPattern = new LookupPattern(LookupOptions.URL, LookupOptions.CONTAINER, "<span\\sclass\\=\"block@m-\">", "</span>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.AUTHORLINK, "<a\\shref\\=\"", "\"(.*?)>"));
*/
        LookupPattern authorPattern = new LookupPattern(LookupOptions.TEXT, LookupOptions.AUTHORLINK, null, null)
                .setSingleRegex("(href\\=\"(/about/editors/(.*?)/))\"")
                .setSingleGroup(2);
        //.addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.AUTHORLINK, "<a\\shref\\=\"", "\"(.*?)>"));

        authorTemplate.setDomain("https://www.engadget.com")
                .addSeed("https://www.engadget.com")
                .setType("BLOG-DOC")
                .setNextPageSuffix("/all/page/")
                .setNextPageSize(10)
                .setNextPageStart(1)
                .setThreadSize(4)
                .setMainPattern(authorPattern);

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.BLOGENGDIRECTORY, "blog-links", LookupOptions.EMPTYDOMAIN);
        LookupPattern linkPattern = new LookupPattern(LookupOptions.CONTAINER, LookupOptions.ARTICLELINKCONTAINER, "<article class=\"o-hit(.*?)\">", "</article>")
                .setStartEndMarker("<article", "</article>")
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLELINK, "<a data-ylk=\"pos(.*?)href=\"", "\""));
        linkTemplate.setDomain("https://www.engadget.com/")
                .setNextPageSuffix("page/")
                .setThreadSize(48)
                .setNextPageStart(1)
                .setNextPageSize(20)
                .setForceWrite(false)
                .setMainPattern(linkPattern);

        WebTemplate mainTemplate = new WebTemplate(LookupOptions.BLOGENGDIRECTORY, "blog-text", LookupOptions.EMPTYDOMAIN);
        LookupPattern topPattern = new LookupPattern(LookupOptions.CONTAINER, LookupOptions.ARTICLE, LookupOptions.EMPTY)
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.DATE, "<meta\\sclass\\=\"swiftype\"\\sname\\=\"published_at\"(.*?)content=\"", "\">"));

        LookupPattern mainPattern = new LookupPattern(LookupOptions.CONTAINER, LookupOptions.ARTICLE, "(<main role=\"main\"(.*?)>)", "(</main>)")
                .setStartEndMarker("(<main)", "(</main>)")
                .setType(LookupOptions.SKIP)
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1 class=\"(.*?)\">", "</h1>").setNth(0))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.AUTHOR, null, null).setSingleRegex("(href\\=\"/about/editors/(.*?)\")")
                        .setSingleGroup(2).setNth(0))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.GENRE, null, null).setSingleRegex("(href\\=\"/tags/(.*?)\")")
                        .setNth(0)
                        .setSingleGroup(2).setNth(0))
                .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLETEXT, "<div id=\"page\\_body\"(.*?)>", "</div>")
                        .setStartEndMarker("<div", "</div>")
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLEPARAGRAPH, "<p>", "</p>")));

        topPattern.addPattern(mainPattern);
        mainTemplate.setMainPattern(topPattern).setDomain("https://www.engadget.com")
                .setThreadSize(1)
                .setForceWrite(true)
                .setLookComplete(true)
                .setType("BLOG-DOC");

        authorTemplate.addNext(linkTemplate, LookupOptions.AUTHORLINK);
        linkTemplate.addNext(mainTemplate, LookupOptions.ARTICLELINK);

        WebFlow flow = new WebFlow(authorTemplate);
        return flow;

    }

    public static void main(String[] args) {
        ExecutorService service = Executors.newFixedThreadPool(5);
        WebFlow.submit(service, build());
        service.shutdown();
    }
}
