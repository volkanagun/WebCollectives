package data.crawler.sites;

import data.crawler.web.LookupOptions;
import data.crawler.web.LookupPattern;
import data.crawler.web.WebFlow;
import data.crawler.web.WebTemplate;

public class MasalOkuOrg {

    public static WebFlow build() {

        String domain = "https://www.masaloku.org/";

        LookupPattern linkPattern = new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINKCONTAINER, "<main class=\"site-main\" id=\"main\">", "</main>")
                .setStartEndMarker("<main","</main>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.CONTAINER, "<h2 class=\"title\">", "</h2>")
                        .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a href=\"", "\"")));

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.TURKISHSTORYDIRECTORY, "story-links", domain)
                .addSeed("masal", "https://www.masaloku.org/")
                .addSeed("masal", "https://www.masaloku.org/category/sesli-masal-diyari/")
                .addSeed("masal", "https://www.masaloku.org/category/cocuk-masallari/")
                .addSeed("masal", "https://www.masaloku.org/category/dunya-masallari/")
                .addSeed("masal", "https://www.masaloku.org/category/keloglan-masallari/")
                .addSeed("masal", "https://www.masaloku.org/category/nasrettin-hoca-masallari/")
                .addSeed("masal", "https://www.masaloku.org/category/la-fontaine-masallari/")
                .addSeed("masal", "https://www.masaloku.org/category/grimm-masallari/")
                .addSeed("masal", "https://www.masaloku.org/category/cerkes-masallari")
                .addSeed("masal", "https://www.masaloku.org/category/andersen-masallari")
                .addSeed("masal", "https://www.masaloku.org/category/masallar-diyari")
                .addSeed("masal", "https://www.masaloku.org/category/ezop-masallari")
                .addSeed("masal", "https://www.masaloku.org/category/cocuk-hikayeleri")
                .addSeed("masal", "https://www.masaloku.org/category/cocuk-masallari")
                .addSeed("masal", "https://www.masaloku.org/category/mevlana-masallari")
                .addSeed("masal", "https://www.masaloku.org/category/turk-masallari")
                .setDoFast(false)
                .setSleepTime(1000L)
                .setDoDeleteStart(false)
                .setNextPageSize(5)
                .setNextPageStart(1)
                .setNextPageJump(1)
                .setThreadSize(1).setNextPageSuffix("page/")
                .setDomain(domain).setDomainSame(true)
                .setMainPattern(linkPattern);

        LookupPattern articleLookup = new LookupPattern(LookupOptions.ARTICLE, LookupOptions.CONTAINER, "<article.*?>", "</article>")
                .setTagLowercase(true)
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1.*?>", "</h1>")
                        .setRemoveTags(true)).setRequiredNotEmpty(true)
                .addPattern(new LookupPattern(LookupOptions.CONTAINER, LookupOptions.ARTICLE, LookupOptions.EMPTY)
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLEPARAGRAPH, "<(b|p)>", "</(p|b)>")
                                .setRemoveTags(true)));

        WebTemplate articleTemplate = new WebTemplate(LookupOptions.TURKISHSTORYDIRECTORY, "story-text", domain)
                .setType(LookupOptions.ARTICLEDOC)
                .setLookComplete(true)
                .setThreadSize(1).setSleepTime(2500L)
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
        WebFlow flow = (new MasalOkuOrg()).build();
        flow.execute();
    }
}
