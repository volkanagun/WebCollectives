package data.crawler.sites;

import data.crawler.web.LookupOptions;
import data.crawler.web.LookupPattern;
import data.crawler.web.WebFlow;
import data.crawler.web.WebTemplate;

public class SecmeHikayeler {

    public static WebFlow build(){
       String domain = "https://secmehikayeler.com/";

        LookupPattern linkPattern = new LookupPattern(LookupOptions.URL, LookupOptions.MAINPAGE, "<div class=\"post-details\">", "</div>")
                .setStartEndMarker("<div","</div>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a class=\"more-link button\" href=\"", "\">"));

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.TURKISHSTORYDIRECTORY, "story-links", domain)
                .addSeed("story","https://secmehikayeler.com/konular/anton-cehov/")
                .addSeed("story","https://secmehikayeler.com/konular/halk-hikayeleri/")
                .addSeed("science-fiction","https://secmehikayeler.com/konular/bilim-kurgu-hikayeleri/")
                .addSeed("action","https://secmehikayeler.com/konular/macera/")
                .addSeed("world classics","https://secmehikayeler.com/konular/dunya-klasikleri/")
                .addSeed("romance","https://secmehikayeler.com/konular/ask-hikayeleri/")
                .addSeed("horror","https://secmehikayeler.com/konular/korku-hikayeleri/")
                .addSeed("war","https://secmehikayeler.com/konular/kahramanlik-hikayeleri/")
                .addSeed("dram","https://secmehikayeler.com/konular/aglatan-hikayeler/")
                .addSeed("crime","https://secmehikayeler.com/konular/polisiye-hikayeler/")
                .addSeed("kids","https://secmehikayeler.com/konular/cocuk-hikayeleri/masallar/")
                .addSeed("fantastic","https://secmehikayeler.com/konular/fantastik-hikayeler/")
                .addSeed("spiritual","https://secmehikayeler.com/konular/mesneviden-hikayeler-2/")
                .addSeed("spiritual","https://secmehikayeler.com/konular/dini-hikayeler/")
                .setDoFast(false)
                .setSleepTime(1000L)
                .setDoDeleteStart(false)
                .setNextPageSize(40)
                .setNextPageStart(1)
                .setNextPageJump(1)
                .setThreadSize(1).setNextPageSuffix("page/")
                .setDomain(domain).setDomainSame(true)
                .setMainPattern(linkPattern);

        LookupPattern articleLookup = new LookupPattern(LookupOptions.ARTICLE, LookupOptions.CONTAINER, "<article.*?>", "</article>")
                .setTagLowercase(true).setNth(0)
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<(h3|h1).*?>","</(h3|h1)>")
                        .setRemoveTags(true)).setRequiredNotEmpty(true)
                .addPattern(new LookupPattern(LookupOptions.LOOKUP, LookupOptions.GENRE, LookupOptions.GENRE)
                        .setRemoveTags(true)).setRequiredNotEmpty(true)
                .addPattern(new LookupPattern(LookupOptions.CONTAINER, LookupOptions.ARTICLE, LookupOptions.EMPTY)
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLEPARAGRAPH,"<p(.*?)>","</p>")
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
        WebFlow flow = (new SecmeHikayeler()).build();
        flow.execute();
    }
}
