package data.crawler.sites;

import data.crawler.web.LookupOptions;
import data.crawler.web.LookupPattern;
import data.crawler.web.WebFlow;
import data.crawler.web.WebTemplate;

import java.io.Serializable;

public class DWNews implements Serializable {

    public static WebFlow build() {

        String domain = "https://www.dw.com/";
        int pageCount = 2;
        int randomCount = 2;

        LookupPattern linkPattern = new LookupPattern(LookupOptions.URL, LookupOptions.MAINPAGE, "<div class=\"content-block\">", "</div>")
                .setStartEndMarker("<div","</div>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a class(.*?)href=\"", "\""));

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-links", domain)
                .addSeed( "https://www.dw.com/tr/g%C3%BCndem/s-10201")
                .addSeed( "https://www.dw.com/tr/ekonomi/t-18993647")
                .addSeed( "https://www.dw.com/tr/t%C3%BCrkiye/t-18753375")
                .addSeed( "https://www.dw.com/tr/ankara/t-18986975")
                .addSeed( "https://www.dw.com/tr/ilo/t-18998507")
                .addSeed( "https://www.dw.com/tr/birle%C5%9Fmi%C5%9F-milletler-bm/t-18583106")
                .addSeed( "https://www.dw.com/tr/avrupa/t-19053726")
                .addSeed( "https://www.dw.com/tr/nato-t%C3%BCrkiye-krizi/t-61917410")
                .addSeed( "https://www.dw.com/tr/ukrayna-sava%C5%9F%C4%B1/t-60933976")
                .addSeed( "https://www.dw.com/tr/anayasa-mahkemesi/t-38539650")
                .addSeed( "https://www.dw.com/tr/amerika-birle%C5%9Fik-devletleri-abd/t-18583118")
                .addSeed( "https://www.dw.com/tr/euro/t-18989135")
                .addSeed( "https://www.dw.com/tr/euro-b%C3%B6lgesi/t-18989113")
                .addSeed( "https://www.dw.com/tr/avrupa-birli%C4%9Fi-ab/t-18582993")
                .addSeed( "https://www.dw.com/tr/ab-komisyonu/t-19060982")
                .setDoFast(false)
                .setDoRandomSeed(randomCount)
                .setDoDeleteStart(true)
                .setSleepTime(2500L)
                .setThreadSize(4)
                .setMainPattern(linkPattern)
                .setDomain(domain);

        LookupPattern articleLookup = new LookupPattern(LookupOptions.TEXT, LookupOptions.CONTAINER, "<article class=\"(.*?)\">", "</article>")
                .setStartEndMarker("<div", "</div>")
                .setNth(0)
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1 class=\"(.*?)\">","</h1>"))
                .addPattern(new LookupPattern(LookupOptions.SKIP, LookupOptions.AUTHORLINK,"<div class=\"c-article-contributors\">","</div>")
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.AUTHOR, "<a(.*?)>","</a>")))
                .addPattern(new LookupPattern(LookupOptions.ARTICLEDOC, LookupOptions.CONTAINER, "<div class=\"c-article-content(.*?)\">", "</div>")
                        .setStartEndMarker("<div", "</div>")
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLEPARAGRAPH, "<(p|h2(.*?))>", "</(p|h2)>")
                                .setRemoveTags(true)));


        WebTemplate articleTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-text", domain)
                .setType(LookupOptions.ARTICLEDOC)
                .setLookComplete(false)
                .setThreadSize(1)
                .setDoFast(false)
                .setSleepTime(2000L)
                .setDomain(domain)
                .setHtmlSaveFolder(LookupOptions.HTMLDIRECTORY)
                .setMainPattern(articleLookup)
                .setForceWrite(false);

        linkTemplate.addNext(articleTemplate, LookupOptions.ARTICLELINK);
        WebFlow flow = new WebFlow(linkTemplate);
        return flow;
    }

    public static void main(String[] args){

        build().execute();
    }
}

