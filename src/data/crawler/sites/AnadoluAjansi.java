package data.crawler.sites;

import data.crawler.web.*;

import java.io.Serializable;

public class AnadoluAjansi implements Serializable {

    public static WebFlow build() {

        String domain = "https://www.aa.com.tr/tr";
        Integer pageCount = 10;

        WebFunctionCall clickCallAccept = new WebButtonClickControl(1, "button.px-4.py-3.text-sm.caustenMedium.transition-opacity.cursor-pointer.whitespace-nowrap")
                .setDoStopOnError(false)
                .setWaitTime(1000);
        WebFunctionCall clickCall1 = new WebButtonClickControl(1, "a.button-daha.text-center")
                .setDoStopOnError(false)
                .setWaitTime(1000);
        WebFunctionCall clickCall2 = new WebButtonClickControl(1, "button[aria-label='Daha fazla']")
                .setDoStopOnError(false)
                .setWaitTime(1000);

        WebFunctionCall scrollCall = new WebFunctionScrollHeight(1).setWaitTime(500);
        WebFunctionCall sequenceCall = new WebFunctionSequence(pageCount,  scrollCall, clickCallAccept, clickCall1, clickCall2)
                .setDoFirefox(false)
                .setWaitBetweenCalls(500L)
                .setDoStopOnError(false)
                .initialize()
                .setWaitTime(500);

        LookupPattern linkPattern = new LookupPattern(LookupOptions.ARTICLELINKCONTAINER, LookupOptions.ARTICLELINKCONTAINER, "<section>","</section>")
                .setStartEndMarker("<section","</section>")
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLELINK, "<a(.*?)href=\"", "\""));

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-links", domain)
                .addSeed("economy", "https://www.aa.com.tr/tr/ekonomi")
                .addSeed("world", "https://www.aa.com.tr/tr/dunya")
                .addSeed("biography", "https://www.aa.com.tr/tr/portre")
                .addSeed("analysis", "https://www.aa.com.tr/tr/analiz")
                .addSeed("culture", "https://www.aa.com.tr/tr/yasam")
                .addSeed("health", "https://www.aa.com.tr/tr/saglik")
                .addSeed("culture", "https://www.aa.com.tr/tr/kultur")
                .addSeed("sports", "https://www.aa.com.tr/tr/spor")
                .addSeed("sports", "https://www.aa.com.tr/tr/dunyadan-spor")
                .addSeed("sports", "https://www.aa.com.tr/tr/basketbol")
                .addSeed("breaking", "https://www.aa.com.tr/tr/gundem")
                .addSeed("politics", "https://www.aa.com.tr/tr/politika")
                .addSeed("covid", "https://www.aa.com.tr/tr/koronavirus")
                .addSeed("news", "https://www.aa.com.tr/tr/kurumsal-haberler")
                .addSeed("education", "https://www.aa.com.tr/tr/egitim")
                .addSeed("technology", "https://www.aa.com.tr/tr/bilim-teknoloji")
                .addSeed("economy", "https://www.aa.com.tr/tr/sirkethaberleri")
                .addSeed("terror", "https://www.aa.com.tr/tr/feto-ve-inkar-stratejisi")
                .addSeed("environment", "https://www.aa.com.tr/tr/yesilhat/yesil-ekonomi")
                .addSeed("environment", "https://www.aa.com.tr/tr/yesilhat/iklim-degisikligi")
                .addSeed("environment", "https://www.aa.com.tr/tr/yesilhat/sifir-atik")
                .addSeed("environment", "https://www.aa.com.tr/tr/yesilhat/cevre-hikayeleri")
                .addSeed("environment", "https://www.aa.com.tr/tr/yesilhat/yesil-sozluk")
                .addSeed("rights", "https://www.aa.com.tr/tr/ayrimcilikhatti/ayrimcilik")
                .addSeed("rights", "https://www.aa.com.tr/tr/ayrimcilikhatti/musluman-karsitligi")
                .addSeed("rights", "https://www.aa.com.tr/tr/ayrimcilikhatti/kadin")
                .addSeed("rights", "https://www.aa.com.tr/tr/ayrimcilikhatti/insan-hikayeleri")
                .setDoFast(false)
                .setSleepTime(2500L)
                .setDoDeleteStart(true)
                .setThreadSize(1)
                .setDomain(domain)
                .setFunctionCall(sequenceCall)
                .setMainPattern(linkPattern);

        LookupPattern articleLookup = new LookupPattern(LookupOptions.ARTICLE, LookupOptions.CONTAINER, "<main(.*?)>", "</main>")
                .setStartEndMarker("<main","</main>")
                .setNth(0)
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1(.*?)>", "</h1>")
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.SKIP, LookupOptions.CONTAINER, "<div class=\"flex flex-col items-start text-descriptionColor text-sm mt-2\">", "</div>")
                        .setNth(0).setStartEndMarker("<div", "</div>")
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.AUTHOR, "<span class=\"caustenMedium text-base\">", "</span>")
                                .setNth(0)
                                .setRemoveTags(true))
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.DATE, "<span class=\"caustenMedium text-dateColor! text-sm\">", "</span>")
                                .setNth(0)
                                .setRemoveTags(true)))
                .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLETEXT, "<div dir=\"ltr\"(.*?)>", "</div>")
                        .setStartEndMarker("<div", "</div>").setNth(0)
                        .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLEPARAGRAPH, "<p>", "</p>")
                                .setRemoveTags(true)));

        WebTemplate articleTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-text", domain)
                .setType(LookupOptions.ARTICLEDOC)
                .setFunctionCall(new WebEmptyFunctionCall().setWaitTime(1000).initialize())
                .setLookComplete(false)
                .setThreadSize(1)
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
        build().execute();
    }
}

