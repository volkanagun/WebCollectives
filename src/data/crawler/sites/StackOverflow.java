package data.crawler.sites;

import data.crawler.web.*;

import java.awt.image.LookupOp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StackOverflow {
    public static WebFlow build() {
        String url = "https://stackoverflow.com/questions";

        List<WebSuffixGenerator> suffixGenerators = new ArrayList<>();
        suffixGenerators.add(new WebCountGenerator(1, 3000, "?tab=newest&page="));
        WebMultiSuffixGenerator suffixGenerator = new WebMultiSuffixGenerator(suffixGenerators);

        LookupPattern mainPattern = new LookupPattern(LookupOptions.SKIP, LookupOptions.CONTAINER, "<div id=\"questions\"(.*?)>", "</div>")
                .setStartEndMarker("<div","</div>")
                .addPattern(new LookupPattern(LookupOptions.CONTAINER, LookupOptions.ARTICLE, "<div class=\"summary\">", "</div>")
                        .setStartEndMarker("<div","</div>")
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h3>", "</h3>")
                                .setRemoveTags(true))
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETEXT, "<div class=\"excerpt\">", "</div>")
                                .setStartEndMarker("<div","</div>"))
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.GENRE, "rel=\"tag\">","</a>")
                                .setRemoveTags(true)));

        WebTemplate mainTemplate = new WebTemplate(LookupOptions.FORUMENDIRECTORY, "forum-text", LookupOptions.EMPTYDOMAIN);
        mainTemplate.setMainPattern(mainPattern)
                .setDoDeleteStart(true).setMultipleDocuments(true)
                .setMultipleIdentifier(LookupOptions.ARTICLETITLE)
                .setHtmlSaveFolder(LookupOptions.HTMLDIRECTORY)
                .setDomain(url)
                .setSuffixGenerator(suffixGenerator)
                .addSeed("QUESTIONS",url)
                .setThreadSize(1);

        WebFlow flow = new WebFlow(mainTemplate);
        return flow;
    }

    public static void main(String[] args) {
       build().execute();
    }
}
