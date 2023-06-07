package data.crawler.sites;

import data.crawler.web.LookupOptions;
import data.crawler.web.LookupPattern;
import data.crawler.web.WebFlow;
import data.crawler.web.WebTemplate;

import java.io.Serializable;

/**
 * @author Volkan Agun
 */
public class EdirneHaber implements Serializable {

    public static WebFlow build(){

        String domain = "http://www.edirnehaber.org";

        LookupPattern linkPattern = new LookupPattern(LookupOptions.URL, LookupOptions.MAINPAGE, "<TD valign=center(.*?)>", "</TD>")
                .setStartEndMarker("<TD","</TD>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a class=mansetspot2 href=", ">"));

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-links", domain)
                .addSeed("economy","http://www.edirnehaber.org/haberler/ekonomi/")
                .addSeed("sports","http://www.edirnehaber.org/haberler/spor/")
                .addSeed("magazine","http://www.edirnehaber.org/haberler/yasam/")
                .addSeed("news","http://www.edirnehaber.org/haberler/guncel/")
                .addSeed("arts","http://www.edirnehaber.org/haberler/kultur-sanat/")
                .addSeed("local-news","http://www.edirnehaber.org/haberler/edirne-gundemi/")
                .addSeed("local-news","http://www.edirnehaber.org/haberler/kirklareli/")
                .addSeed("local-news","http://www.edirnehaber.org/haberler/canakkale/")
                .addSeed("local-news","http://www.edirnehaber.org/haberler/tekirdag/")
                .addSeed("local-news","http://www.edirnehaber.org/haberler/edirne/")
                .addSeed("health","http://www.edirnehaber.org/haberler/saglik/")
                .addSeed("education","http://www.edirnehaber.org/haberler/egitim/")
                .addSeed("politics","http://www.edirnehaber.org/haberler/politika/")
                .setDoFast(false)
                .setSleepTime(1000L)
                .setDoDeleteStart(true)
                .setNextPageSuffix("")
                .setThreadSize(1)
                .setDomain(domain).setDomainSame(true)
                .setMainPattern(linkPattern);

        LookupPattern articleLookup = new LookupPattern(LookupOptions.ARTICLE, LookupOptions.CONTAINER, "<table cellspacing=0 cellpadding=0 width=660>", "</table>")
                .setStartEndMarker("<table","</table>")
                .setTagLowercase(true)
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.DATE, "<td(.*?)class=date>","</td>")
                        .setNth(0)
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.LOOKUP, LookupOptions.GENRE, LookupOptions.GENRE))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<td valign=top width=660 class=mansetspot>","</td>")
                        .setNth(0)
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLETEXT, "<td valign=top width=660 class=text(.*?)>","</td>")
                        .setRequiredNotEmpty(true)
                        .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLEPARAGRAPH,"<(b|p)>","</(p|b)>")
                                .setRemoveTags(true)));

        WebTemplate articleTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-text", domain)
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
        build().execute();
    }
}
