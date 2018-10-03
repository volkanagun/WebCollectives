package data.crawler.sites;

import data.crawler.web.*;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SikayetVar implements Serializable {

    public static WebFlow build(){
        String folder = "resources/sikayetvar/";
        String domain = "https://www.sikayetvar.com/";
        String seed = "https://www.sikayetvar.com/sikayetler";
        Integer start = 10000;
        Integer end = 10005;

        LookupPattern urlPattern = new LookupPattern(LookupOptions.CONTAINER, LookupOptions.ARTICLELINKCONTAINER, "<div class=\"media-body\">","</div>")
                .setStartEndMarker("<div","</div>")
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.URL,"<a href=\"","\"\\s(title||class)"));

        WebTemplate linkTemplate = new WebTemplate(folder, "links", LookupOptions.EMPTYDOMAIN);

        linkTemplate = linkTemplate.setMainPattern(urlPattern)
                .setThreadSize(2)
                .setNextPageStart(start)
                .setNextPageSize(end)
                .setNextPageSuffix("?page=")
                .addSeed(seed)
                .setDomain(domain);

        LookupPattern mainPattern = new LookupPattern(LookupOptions.CONTAINER, LookupOptions.URL, "<div class=\"quickPreviewContainer\"","</div>")
                .setStartEndMarker("<div","</div>")
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE,"<h[12]\\sclass\\=\"title\"","</h[12]>"))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.DATE,"<span title=\"","\"")
                        .setNth(0)
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.CONTAINER, LookupOptions.ARTICLETEXT,"<div class=\"description\">","</div>")
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETEXT, "<p>","</p>")))
                .addPattern(new LookupPattern(LookupOptions.CONTAINER, LookupOptions.TAG, "<div class=\"hashtags\">","</div>")
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.TAG, "<a(.*?)>","</a>")));


        WebTemplate mainTemplate = new WebTemplate(folder, "docs", LookupOptions.EMPTYDOMAIN);
        mainTemplate.setMainPattern(mainPattern)
                .setDomain(domain)
                .setLookComplete(true);


        linkTemplate.addNext(mainTemplate,LookupOptions.URL);

        WebFlow mainFlow = new WebFlow(linkTemplate);
        return mainFlow;
    }

    public static void main(String[] args) {

        ExecutorService service = Executors.newFixedThreadPool(5);
        WebFlow.submit(service, build());
        service.shutdown();
    }
}
