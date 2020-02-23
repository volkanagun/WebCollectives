package data.crawler.sites;

import data.crawler.web.*;

import java.io.Serializable;

/**
 * @author Volkan Agun
 */
public class EskSonHaber implements Serializable {
    public static WebFlow build(){

        String domain = "https://www.eskisehirsonhaber.com";

        WebLuceneSink webLuceneSink = new WebLuceneSink(LookupOptions.LUCENEINDEXDIR).openWriter();

        LookupPattern linkPattern = new LookupPattern(LookupOptions.URL, LookupOptions.MAINPAGE, "<div id=\"main\"(.*?)>", "</div>")
                .setStartEndMarker("<div","</div>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a href=\"", "\"\\s(title|target)"));

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-links", domain)
                .addSeed("economy","https://www.eskisehirsonhaber.com/ekonomi/")
                .addSeed("politics","https://www.eskisehirsonhaber.com/siyaset/")
                .addSeed("world","https://www.eskisehirsonhaber.com/dunya/")
                .addSeed("technology","https://www.eskisehirsonhaber.com/teknoloji/")
                .addSeed("sports","https://www.eskisehirsonhaber.com/spor/")
                .addSeed("magazine","https://www.eskisehirsonhaber.com/yasam/")
                .addSeed("magazine","https://www.eskisehirsonhaber.com/magazin/")
                .addSeed("news","https://www.eskisehirsonhaber.com/gundem/")
                .addSeed("arts","https://www.eskisehirsonhaber.com/kultur-sanat/")
         /*       .addSeed("local-news","https://www.eskisehirsonhaber.com/eskisehir-haber/")
                .addSeed("health","https://www.eskisehirsonhaber.com/saglik/")
                .addSeed("education","https://www.eskisehirsonhaber.com/egitim/")*/
                .setDoFast(false)
                .setDoDeleteStart(true)
                .setNextPageSize(1000)
                .setNextPageStart(1)
                .setNextPageSuffix("")
                .setThreadSize(2)
                .setNextPageSuffixAddition("#horizontal_news")
                .setDomain(domain).setDomainSame(true)
                .setMainPattern(linkPattern);

        LookupPattern articleLookup = new LookupPattern(LookupOptions.ARTICLE, LookupOptions.CONTAINER, "<div class=\"row\">", "</div>")
                .setStartEndMarker("<div","</div>")
                .setTagLowercase(true)
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.DATE, "<span class=(.*?)data-date=\"","\">")
                        .setNth(0)
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.LOOKUP, LookupOptions.GENRE, LookupOptions.GENRE))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h3 class=(.*?)>","</h3>")
                        .setNth(0)
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLETEXT, "<div id=\"newsbody\">","</div>")
                        .setStartEndMarker("<div","</div>")
                        .setRequiredNotEmpty(true)
                        .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLEPARAGRAPH,"<p>","</p>")
                                .setRemoveTags(true)));

        WebTemplate articleTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-text", domain)
                .setType(LookupOptions.ARTICLEDOC)
                .setLookComplete(true)
                .setWebSink(webLuceneSink)
                .setThreadSize(1)
                .setDoFast(true)
                .setDomain(domain)
                .setHtmlSaveFolder(LookupOptions.HTMLDIRECTORY)
                .setMainPattern(articleLookup)
                .setForceWrite(false);

        linkTemplate.addNext(articleTemplate, LookupOptions.ARTICLELINK);
        WebFlow flow = new WebFlow(linkTemplate);
        return flow;

    }

    public static void main(String[] args) {
        build().execute();
    }


}
