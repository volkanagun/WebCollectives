package data.crawler.sites;

import data.crawler.web.*;

public class Euronews {
    //https://tr.euronews.com/tag/bilim-kurgu?p=2

    public static WebFlow build() {

        String domain = "https://tr.euronews.com/";
        int pageCount = 20;

        WebFunctionCall functionCall = new WebButtonClickCall(1, "button.c-link-chevron--load")
                .setDoStopOnError(true)
                .setWaitTime(100);

        WebFunctionCall scrollCall = new WebFunctionScroll(1,"button.c-link-chevron--load").setWaitTime(500);
        WebFunctionCall sequenceCall = new WebFunctionSequence(pageCount,  scrollCall, functionCall)
                .setDoFirefox(true)
                .setWaitBetweenCalls(500L)
                .setTryOnError(7)
                .initialize()
                .setWaitTime(500);

        LookupPattern linkPattern = new LookupPattern(LookupOptions.URL, LookupOptions.MAINPAGE, "<main class=\"o-site-main\" id=\"enw-main-content\">", "</main>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a rel=\"bookmark\"(.*?)href=\"", "\""));

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-links", domain)
                .addSeed("smart-regions", "https://tr.euronews.com/programlar/smart-regions")
                .addSeed("european-news", "https://tr.euronews.com/european-affairs/avrupa-haber")
                .addSeed("european-news", "https://tr.euronews.com/programlar/state-of-the-union")
                .addSeed("european-news", "https://tr.euronews.com/programlar/bruksel-burosu")
                .addSeed("world", "https://tr.euronews.com/tag/filistin")
                .addSeed("world", "https://tr.euronews.com/special/destination-dubai")
                .addSeed("world", "https://tr.euronews.com/haber/avrupa/rusya")
                .addSeed("world", "https://tr.euronews.com/tag/fransa")
                .addSeed("world", "https://tr.euronews.com/tag/turkiye")
                .addSeed("world", "https://tr.euronews.com/haber/avrupa/isvicre")
                .addSeed("world", "https://tr.euronews.com/haber/uluslararasi")
                .addSeed("world", "https://tr.euronews.com/programlar/globalconversation")
                .addSeed("world", "https://tr.euronews.com/programlar/view")
                .addSeed("world", "https://tr.euronews.com/programlar/unreported-europe")
                .addSeed("world", "https://tr.euronews.com/programlar/dunya")
                .addSeed("world", "https://tr.euronews.com/programlar/spotlight")
                .addSeed("world", "https://tr.euronews.com/special/dogu-akdeniz-krizi")
                .addSeed("world", "https://tr.euronews.com/haber/amerika/brezilya")
                .addSeed("world", "https://tr.euronews.com/haber/amerika/kolombiya")
                .addSeed("world", "https://tr.euronews.com/haber/asya/japonya")
                .addSeed("world", "https://tr.euronews.com/haber/amerika/abd")
                .addSeed("world", "https://tr.euronews.com/tag/gazze")
                .addSeed("world", "https://tr.euronews.com/tag/islam-kars-tl-g-")
                .addSeed("world", "https://tr.euronews.com/my-europe/avrupa-haberleri")
                .addSeed("politics", "https://tr.euronews.com/tag/turk-siyaseti")
                .addSeed("economy", "https://tr.euronews.com/haber/ekonomi")
                .addSeed("economy", "https://tr.euronews.com/programlar/realeconomy")
                .addSeed("economy", "https://tr.euronews.com/programlar/target")
                .addSeed("economy", "https://tr.euronews.com/programlar/business-planet")
                .addSeed("economy", "https://tr.euronews.com/programlar/business-line")
                .addSeed("rop", "https://tr.euronews.com/programlar/rop")
                .addSeed("science", "https://tr.euronews.com/knowledge/sci-tech")
                .addSeed("science", "https://tr.euronews.com/programlar/sci-tech")
                .addSeed("travel", "https://tr.euronews.com/gezi")
                .addSeed("travel", "https://tr.euronews.com/gezi/stays")
                .addSeed("travel", "https://tr.euronews.com/gezi/destinations")
                .addSeed("travel", "https://tr.euronews.com/gezi/people")
                .addSeed("travel", "https://tr.euronews.com/programlar/postcards")
                .addSeed("travel", "https://tr.euronews.com/programlar/european-lens")
                .addSeed("travel", "https://tr.euronews.com/programlar/adventures")
                .addSeed("travel", "https://tr.euronews.com/programlar/explore")
                .addSeed("global-japan", "https://tr.euronews.com/programlar/global-japan")
                .addSeed("culture", "https://tr.euronews.com/programlar/taste")
                .addSeed("culture", "https://tr.euronews.com/programlar/cult")
                .addSeed("culture", "https://tr.euronews.com/programlar/musica")
                .addSeed("culture", "https://tr.euronews.com/kultur/art")
                .addSeed("culture", "https://tr.euronews.com/kultur/culture-news")
                .addSeed("culture", "https://tr.euronews.com/kultur/yiyecek-ve-i%C3%A7ecek")
                .addSeed("environment", "https://tr.euronews.com/programlar/focus")
                .addSeed("environment", "https://tr.euronews.com/programlar/climate-now")
                .addSeed("environment", "https://tr.euronews.com/special/climate")
                .addSeed("environment", "https://tr.euronews.com/tag/olum")
                .addSeed("sports", "https://tr.euronews.com/tag/diger-sporlar")
                //.setSuffixGenerator(new WebCountGenerator(1, 1,"?p="))
                .setFunctionCall(sequenceCall)
                .setDoFast(false)
                .setDoDeleteStart(true)
                .setSleepTime(2500L)
                .setThreadSize(4)
                .setDomain(domain)
                .setMainPattern(linkPattern);

        /*LookupPattern articleLookup = new LookupPattern(LookupOptions.TEXT, LookupOptions.CONTAINER, "<div class=\"o-section o-article", "</div>")
                .setStartEndMarker("<div", "</div>")
                .setNth(0)
                .addPattern(new LookupPattern(LookupOptions.SKIP, LookupOptions.ARTICLERATING, "<div class=\"c-article-meta\">","</div>")
                        .setStartEndMarker("<div","</div>")
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.DATE, "<time class=\"c-article-date\"(.*?)datetime=\"","\""))
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.AUTHOR,"<b>","</b>")))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1 class=\"c-article-title", "</h1>")
                        .setNth(0)
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.ARTICLEDOC, LookupOptions.CONTAINER, "<div class=\"c-article-content(.*>)", "</div>")
                        .setStartEndMarker("<div", "</div>")
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLEPARAGRAPH, "<(p|h2(.*?))>", "</(p|h2)>")
                                .setRemoveTags(true)));*/

        LookupPattern articleLookup = new LookupPattern(LookupOptions.TEXT, LookupOptions.CONTAINER, "<article class=\"o-article(.*?)\">", "</article>")
                .setStartEndMarker("<article", "</article>")
                .setNth(0)
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<(h1|h3) class=\"(.*?)\">","</(h1|h3)>")
                        .setNth(0))
                .addPattern(new LookupPattern(LookupOptions.SKIP, LookupOptions.AUTHORLINK,"<div class=\"c-article-contributors(.*?)\">","</div>")
                        .setStartEndMarker("<div", "</div")
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.AUTHOR, "<(a|b)(.*?)>","</(a|b)>").setRemoveTags(true)))
                .addPattern(new LookupPattern(LookupOptions.ARTICLEDOC, LookupOptions.CONTAINER, "<div class=\"c-article-content(.*?)\">", "</div>")
                        .setStartEndMarker("<div", "</div>")
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLEPARAGRAPH, "<(p|h2)(.*?)>", "</(p|h2)>")
                                .setRemoveTags(true)));


        WebFunctionCall emptyCall = new WebEmptyFunctionCall()
                .setDoFirefox(true)
                .initialize()
                .setWaitTime(500);

        WebTemplate articleTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-text", domain)
                .setType(LookupOptions.ARTICLEDOC)
                .setLookComplete(false)
                .setThreadSize(1)
                .setDoFast(false)
                .setSleepTime(2000L)
                .setDomain(domain)
                .setFunctionCall(emptyCall)
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
