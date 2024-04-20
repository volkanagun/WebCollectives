package data.crawler.sites;

import data.crawler.web.LookupOptions;
import data.crawler.web.LookupPattern;
import data.crawler.web.WebFlow;
import data.crawler.web.WebTemplate;

public class IndependentTurkce {

    public static WebFlow build() {
        String domain = "https://www.indyturk.com";

        LookupPattern linkPattern = new LookupPattern(LookupOptions.URL, LookupOptions.MAINPAGE, "<div class=\"article-item-title\">", "</div>")
                .setStartEndMarker("<div", "</div>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a href=\"", "\""));

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.INDEPENDENTDIRECTORY, "article-links", domain)
                .addSeed("sports", "https://www.indyturk.com/spor/")
                .addSeed("sports", "https://www.indyturk.com/spor/basketbol")
                .addSeed("sports", "https://www.indyturk.com/spor/futbol")
                .addSeed("sports", "https://www.indyturk.com/spor/voleybol")
                .addSeed("sports", "https://www.indyturk.com/spor/golf")

                .addSeed("culture", "https://www.indyturk.com/k%C3%BClt%C3%BCr/m%C3%BCzik")
                .addSeed("culture", "https://www.indyturk.com/k%C3%BClt%C3%BCr/tiyatro-0")
                .addSeed("culture", "https://www.indyturk.com/k%C3%BClt%C3%BCr/edebiyat")
                .addSeed("science", "https://www.indyturk.com/bilim")
                .addSeed("science", "https://www.indyturk.com/bilim/genetik")
                .addSeed("science", "https://www.indyturk.com/bilim/yapay-zeka")
                .addSeed("science", "https://www.indyturk.com/bilim/uzay")
                .addSeed("science", "https://www.indyturk.com/bilim/teknoloji")

                .addSeed("news", "https://www.indyturk.com/d%C3%BCnya/avrupa")
                .addSeed("news", "https://www.indyturk.com/d%C3%BCnya/amerika")
                .addSeed("news", "https://www.indyturk.com/d%C3%BCnya/asya")
                .addSeed("news", "https://www.indyturk.com/d%C3%BCnya/afrika")
                .addSeed("news", "https://www.indyturk.com/d%C3%BCnya/balkanlar")
                .addSeed("news", "https://www.indyturk.com/d%C3%BCnya/ortado%C4%9Fu")
                .addSeed("news", "https://www.indyturk.com/haber/tarih")
                .addSeed("news", "https://www.indyturk.com/haber/%C3%A7evre")
                .addSeed("news", "https://www.indyturk.com/haber/insan-haklar%C4%B1")
                .addSeed("news", "https://www.indyturk.com/haber/hayvan-haklar%C4%B1")
                .addSeed("life", "https://www.indyturk.com/ya%C5%9Fam")
                .addSeed("life", "https://www.indyturk.com/ya%C5%9Fam/magazin")

                .addSeed("economy", "https://www.indyturk.com/ekonomi%CC%87")
                .addSeed("economy", "https://www.indyturk.com/ekonomi%CC%87/emlak")
                .addSeed("economy", "https://www.indyturk.com/ekonomi%CC%87/emek")
                .addSeed("economy", "https://www.indyturk.com/ekonomi%CC%87/%C3%BCretim")
                .addSeed("voice-turkey", "https://www.indyturk.com/t%C3%BCrkiyeden-sesler")
                .addSeed("voice-world", "https://www.indyturk.com/d%C3%BCnyadan-sesler")

                .addSeed("health", "https://www.indyturk.com/sa%C4%9Flik")
                .addSeed("health", "https://www.indyturk.com/sa%C4%9Flik/psikoloji")
                .addSeed("health", "https://www.indyturk.com/sa%C4%9Flik/koronavir%C3%BCs")
                .addSeed("health", "https://www.indyturk.com/sa%C4%9Flik/alternatif-t%C4%B1p")
                .addSeed("health", "https://www.indyturk.com/sa%C4%9Flik/kalp-sa%C4%9Fl%C4%B1%C4%9F%C4%B1")
                .addSeed("health", "https://www.indyturk.com/sa%C4%9Flik/%C3%A7ocuk-sa%C4%9Fl%C4%B1%C4%9F%C4%B1")
                .addSeed("health", "https://www.indyturk.com/sa%C4%9Flik/kanser")
                .addSeed("health", "https://www.indyturk.com/sa%C4%9Flik/beslenme")
                .addSeed("health", "https://www.indyturk.com/sa%C4%9Flik/obezite")
                .addSeed("health", "https://www.indyturk.com/sa%C4%9Flik/kad%C4%B1n-sa%C4%9Fl%C4%B1%C4%9F%C4%B1")

                .addSeed("research", "https://www.indyturk.com/tags/dosya")

                .setDoFast(false)
                .setDoDeleteStart(true)
                .setSleepTime(1000L)
                .setThreadSize(1)
                .setDomain(domain)
                .setMainPattern(linkPattern);

        LookupPattern articleLookup = new LookupPattern(LookupOptions.ARTICLE, LookupOptions.CONTAINER, "<main>", "</main>")
                .setNth(0)
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.DATE, "<time>", "</time>")
                        .setNth(0)
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1(.*?)>", "</h1>")
                        .setNth(0)
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLETEXT, "<div class=\"content(.*?)>", "</div>")
                        .setStartEndMarker("<div", "</div>").setNth(0)
                        .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLEPARAGRAPH, "<(p|h2(.*?))>", "</(p|h2)>")))
                .addPattern(new LookupPattern(LookupOptions.TAG, LookupOptions.CONTAINER, "<div class=\"entry-tags\">", "</div>").setNth(0)
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.TOPIC, "<a(.*?)>", "</a>")
                                .setNth(0)
                                .setRemoveTags(true)));

        WebTemplate articleTemplate = new WebTemplate(LookupOptions.INDEPENDENTDIRECTORY, "article-text", domain)
                .setType(LookupOptions.ARTICLEDOC)
                .setMainContent(true)
                .setLookComplete(true).setDoDeleteStart(false)
                .setThreadSize(1)
                .setDoFast(false)
                .setSleepTime(1000L)
                .setDomain(domain)
                .setHtmlSaveFolder(LookupOptions.HTMLDIRECTORY)
                .setMainPattern(articleLookup)
                .setForceWrite(true);

        linkTemplate.addNext(articleTemplate, LookupOptions.ARTICLELINK);
        WebFlow flow = new WebFlow(linkTemplate);
        return flow;
    }

    public static void main(String[] args) {
        build().execute();
    }
}
