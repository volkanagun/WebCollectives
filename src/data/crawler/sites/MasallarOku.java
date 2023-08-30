package data.crawler.sites;

import data.crawler.web.LookupOptions;
import data.crawler.web.LookupPattern;
import data.crawler.web.WebFlow;
import data.crawler.web.WebTemplate;

public class MasallarOku {

    public static WebFlow build(){

        String domain = "https://www.masallaroku.com/";

        LookupPattern linkPattern = new LookupPattern(LookupOptions.URL, LookupOptions.MAINPAGE, "<div class=\"container-wrapper\">", "</div>")
                .setStartEndMarker("<div","</div>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a class=\"more-link button\" href=\"", "\">"));

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.TURKISHSTORYDIRECTORY, "story-links", domain)
                .addSeed("hikaye","https://www.masallaroku.com/kisa-hikayeler/")
                .addSeed("hikaye","https://www.masallaroku.com/efsaneler/")
                .addSeed("masal","https://www.masallaroku.com/kisa-masallar/")
                .addSeed("hikaye","https://www.masallaroku.com/dede-korkut-hikayeleri/")
                .addSeed("hikaye","https://www.masallaroku.com/dini-hikayeler/")
                .addSeed("hikaye","https://www.masallaroku.com/ataturkun-cocukluk-anilari/")
                .addSeed("hikaye","https://www.masallaroku.com/ilginc-hikayeler/")
                .addSeed("hikaye","https://www.masallaroku.com/hikaye-oku/")
                .addSeed("masal","https://www.masallaroku.com/anadolu-masallari/")
                .addSeed("masal","https://www.masallaroku.com/cocuk-masallari/")
                .addSeed("masal","https://www.masallaroku.com/bebek-masallari/")
                .addSeed("masal","https://www.masallaroku.com/uyku-masallari/")
                .addSeed("masal","https://www.masallaroku.com/la-fontaine-masallari/")
                .setDoFast(false)
                .setSleepTime(1000L)
                .setDoDeleteStart(false)
                .setNextPageSize(1)
                .setNextPageStart(1)
                .setNextPageJump(1)
                .setThreadSize(1).setNextPageSuffix("page/")
                .setDomain(domain).setDomainSame(true)
                .setMainPattern(linkPattern);

        LookupPattern articleLookup = new LookupPattern(LookupOptions.ARTICLE, LookupOptions.CONTAINER, "<article.*?>", "</article>")
                .setTagLowercase(true).setNth(0)
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1.*?>","</h1>")
                        .setRemoveTags(true)).setRequiredNotEmpty(true)
                .addPattern(new LookupPattern(LookupOptions.CONTAINER, LookupOptions.ARTICLE, LookupOptions.EMPTY)
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLEPARAGRAPH,"<(b|p)>","</(p|b)>")
                                .setRemoveTags(true)));

        WebTemplate articleTemplate = new WebTemplate(LookupOptions.TURKISHSTORYDIRECTORY, "story-text", domain)
                .setType(LookupOptions.ARTICLEDOC)
                .setLookComplete(true)
                .setThreadSize(1).setSleepTime(2500L)
                .setDoFast(false)
                .setDomain(domain)
                .setHtmlSaveFolder(LookupOptions.HTMLDIRECTORY)
                .setMainPattern(articleLookup)
                .setForceWrite(false);

        linkTemplate.addNext(articleTemplate, LookupOptions.ARTICLELINK);
        WebFlow flow = new WebFlow(linkTemplate);
        return flow;
    }

    public static void main(String[] args) {
        WebFlow flow = (new MasallarOku()).build();
        flow.execute();
    }
}
