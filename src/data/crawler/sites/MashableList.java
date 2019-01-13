package data.crawler.sites;

import data.crawler.web.*;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MashableList implements Serializable {
    public static WebFlow build() {
        WebTemplate articleTemplate = new WebTemplate(LookupOptions.BLOGENGDIRECTORY, "blog-text", LookupOptions.EMPTYDOMAIN);
        LookupPattern mainPattern = new LookupJSON(LookupOptions.CONTAINER, LookupOptions.ARTICLE, "new")
                .addPattern(new LookupJSON(LookupOptions.TEXT, LookupOptions.DATE, "post_date"))
                .addPattern(new LookupJSON(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "title"))
                .addPattern(new LookupJSON(LookupOptions.TEXT, LookupOptions.GENRE, "channel"))
                .addPattern(new LookupJSON(LookupOptions.TEXT, LookupOptions.AUTHOR, "author"))
                .addPattern(new LookupJSON(LookupOptions.ARTICLE, LookupOptions.ARTICLE, "content")
                        .setType(LookupOptions.SKIP)
                        .addPattern(new LookupJSON(LookupOptions.ARTICLE, LookupOptions.ARTICLETEXT, "plain")
                                .addPattern(new LookupSplit(LookupOptions.TEXT, LookupOptions.ARTICLEPARAGRAPH, "\n\n"))));

        articleTemplate.addSeed("https://mashable.com/stories.json?").setNextPageSuffix("page=")
                .setType("BLOG-DOC").setMultipleIdentifier(LookupOptions.ARTICLETITLE)
                .setNextPageSize(5000)
                .setNextPageStart(1)
                .setThreadSize(1).setMainPattern(mainPattern);

        WebFlow flow = new WebFlow(articleTemplate);
        return flow;

    }

    public static void main(String[] args){
        ExecutorService service = Executors.newFixedThreadPool(5);
        WebFlow.submit(service, build());
        service.shutdown();
    }
}
