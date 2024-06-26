package data.crawler.sites;

import data.crawler.web.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MilliyetBlogList implements Serializable {

    public static WebFlow build() {

              List<WebSuffixGenerator> suffixGenerators = new ArrayList<>();

        suffixGenerators.add(new WebCountGenerator(1,354,"&KategoriNo="));
        suffixGenerators.add(new WebCountGenerator(1,2,"&Page="));

        WebTemplate yazarTemplate = new WebTemplate(LookupOptions.BLOGDIRECTORY, "yazar-links", LookupOptions.EMPTYDOMAIN);
        LookupPattern authorPattern = new LookupPattern(LookupOptions.URL, LookupOptions.AUTHORLINK, "<a(.*?)href\\=\"/(.*?)/\\?UyeNo\\=", "\"\\s?(target=\"_blank\"|class=\"flt_left\"?)>");

        yazarTemplate.setMainPattern(authorPattern);
        yazarTemplate.setDomain("http://blog.milliyet.com.tr/BloggerBloglar/?UyeNo=")
                .setForceWrite(false)
                .setThreadSize(1).setDoFast(false)
                .setSleepTime(500L).setWaitTimeAfter(50000L).setWaitTime(5000L)
                .setDoDeleteStart(true)
                .setSuffixGenerator(new WebMultiSuffixGenerator(suffixGenerators))
                .addSeed("http://blog.milliyet.com.tr/BlogListeKategori/?Status=&Sort=");

        LookupPattern linkPattern = new LookupPattern(LookupOptions.SKIP, LookupOptions.CONTAINER, "<div\\sclass=\"details\">", "</div>")
                .setStartEndMarker("<div","</div>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.CONTAINER, "<h6>", "</h6>")
                        .setStartEndMarker("<h6", "</h6")
                        .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a\\shref\\=\"", "\"")));

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.BLOGDIRECTORY, "blog-links", LookupOptions.EMPTYDOMAIN);
        linkTemplate.setMainPattern(linkPattern)
                .setDoDeleteStart(true)
                .setDomain("http://blog.milliyet.com.tr/BloggerBloglar/?UyeNo=")
                .setNextPageSuffix("&Page=")
                .setNextPageStart(1)
                .setDoFast(false).setDoDeleteStart(true)
                .setSleepTime(1000L)
                .setWaitTime(1500L)
                .setNextPageSize(2)
                .setThreadSize(1);

        LookupPattern articlePattern = new LookupPattern(LookupOptions.CONTAINER, LookupOptions.ARTICLE, "<div\\sid\\=\"_middle_content_bottom_child2\"\\sclass\\=\"colA\">", "</div>")
                .setStartEndMarker("<div", "</div")
                .addPattern(new LookupPattern(LookupOptions.CONTAINER, LookupOptions.AUTHOR, "<div\\sclass\\=\"blogerBaslik\\scurvedNoBottom\">", "</div>")
                        .setType(LookupOptions.SKIP)
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.AUTHORNAME, "<h4>", "</h4>")))
                .addPattern(new LookupPattern(LookupOptions.CONTAINER, LookupOptions.GENRETEXT, "<dd>", "</dd>")
                        .setType(LookupOptions.SKIP)
                        .setStartEndMarker("<dd", "</dd>")
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.GENRE, "<a\\shref\\=\"", "\\/Kategori\\/\\?")))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1>", "</h1>"))
                .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLETEXT, "<div\\sid\\=\"BlogDetail\"(.*?)>", "</div>")
                        .setStartEndMarker("<div", "</div>")
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLEPARAGRAPH, "<p(.*?)>", "</p>")));

        WebTemplate articleTemplate = new WebTemplate(LookupOptions.BLOGDIRECTORY, "blog-text", LookupOptions.EMPTYDOMAIN)
                .setDomain("http://blog.milliyet.com.tr");
        
        articleTemplate.setMainPattern(articlePattern).setForceWrite(true).setLookComplete(false)
                .setType(LookupOptions.BLOGDOC)
                .setHtmlSaveFolder(LookupOptions.HTMLDIRECTORY);

        yazarTemplate.addNext(linkTemplate, LookupOptions.AUTHORLINK);
        linkTemplate.addNext(articleTemplate, LookupOptions.ARTICLELINK);
        WebFlow flow = new WebFlow(yazarTemplate);
        return flow;
    }

    public static void main(String[] args) {
        ExecutorService service = Executors.newFixedThreadPool(1);
        WebFlow.submit(service, build());
        service.shutdown();
    }
}
