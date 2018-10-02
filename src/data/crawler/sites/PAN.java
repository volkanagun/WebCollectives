package data.crawler.sites;

import data.crawler.web.LookupOptions;
import data.crawler.web.LookupPattern;
import data.crawler.web.WebFlow;
import data.crawler.web.WebTemplate;

import java.io.Serializable;

public class PAN implements Serializable {
    public static WebFlow buildForPAN2011Large() {
        WebTemplate mainTemplate = new WebTemplate(LookupOptions.PANLARGESOURCEDIRECTORY, "LargeTrain", LookupOptions.EMPTYDOMAIN);

        LookupPattern articlePattern = new LookupPattern(LookupOptions.CONTAINER, LookupOptions.ARTICLE, "<text.*?", "</text>")
                .setStartEndMarker("<text", "</text>")
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLEID, "file=\"", "\">").setNth(0))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.AUTHORNAME, "<author id=\"", "\"/>"))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETEXT, "<body>", "</body>")
                        .setRegex(new String[]{"&", "<NAME/>", "<.*?>", ">", "<"}).setReplaces(new String[]{"&amp;", "NamedEntity", "", "&gt;", "&lt;"}));

        mainTemplate.addFolder(LookupOptions.PANLARGEDIRECTORY);
        mainTemplate.setMainPattern(articlePattern);
        mainTemplate.setType(LookupOptions.PANDOC);
        WebFlow webFlow = new WebFlow(mainTemplate);
        return webFlow;
    }

    public static WebFlow buildForPAN2011LargeTest() {
        WebTemplate mainTemplate = new WebTemplate(LookupOptions.PANLARGETESTSOURCEDIRECTORY, "LargeTest", LookupOptions.EMPTYDOMAIN);

        LookupPattern articlePattern = new LookupPattern(LookupOptions.CONTAINER, LookupOptions.ARTICLE, "<text.*?", "</text>")
                .setStartEndMarker("<text", "</text>")
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLEID, "file=\"", "\">").setNth(0))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.AUTHORNAME, "<author id=\"", "\"/>"))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETEXT, "<body>", "</body>")
                        .setRegex(new String[]{"&", "<NAME/>", "<.*?>", ">", "<"}).setReplaces(new String[]{"&amp;", "NamedEntity", "", "&gt;", "&lt;"}));

        mainTemplate.addFolder(LookupOptions.PANLARGETESTDIRECTORY);
        mainTemplate.setMainPattern(articlePattern);
        mainTemplate.setType(LookupOptions.PANDOC);
        WebFlow webFlow = new WebFlow(mainTemplate);
        return webFlow;
    }

    public static WebFlow buildForPAN2011LargeTrain() {
        WebTemplate mainTemplate = new WebTemplate(LookupOptions.PANLARGETRAINSOURCEDIRECTORY, "LargeTest", LookupOptions.EMPTYDOMAIN);

        LookupPattern articlePattern = new LookupPattern(LookupOptions.CONTAINER, LookupOptions.ARTICLE, "<text.*?", "</text>")
                .setStartEndMarker("<text", "</text>")
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLEID, "file=\"", "\">").setNth(0))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.AUTHORNAME, "<author id=\"", "\"/>"))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETEXT, "<body>", "</body>")
                        .setRegex(new String[]{"&", "<NAME/>", "<.*?>", ">", "<"}).setReplaces(new String[]{"&amp;", "NamedEntity", "", "&gt;", "&lt;"}));

        mainTemplate.addFolder(LookupOptions.PANLARGETRAINDIRECTORY);
        mainTemplate.setMainPattern(articlePattern);
        mainTemplate.setType(LookupOptions.PANDOC);
        WebFlow webFlow = new WebFlow(mainTemplate);
        return webFlow;
    }

    public static WebFlow buildForPAN2011SmallTest() {
        WebTemplate mainTemplate = new WebTemplate(LookupOptions.PANSMALLTESTSOURCEDIRECTORY, "SmallTest", LookupOptions.EMPTYDOMAIN);

        LookupPattern articlePattern = new LookupPattern(LookupOptions.CONTAINER, LookupOptions.ARTICLE, "<text.*?", "</text>")
                .setStartEndMarker("<text", "</text>")
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLEID, "file=\"", "\">").setNth(0))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.AUTHORNAME, "<author id=\"", "\"/>"))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETEXT, "<body>", "</body>")
                        .setRegex(new String[]{"&", "<NAME/>", "<.*?>", ">", "<"}).setReplaces(new String[]{"&amp;", "NamedEntity", "", "&gt;", "&lt;"}));

        mainTemplate.addFolder(LookupOptions.PANSMALLTESTDIRECTORY);
        mainTemplate.setMainPattern(articlePattern);
        mainTemplate.setType(LookupOptions.PANDOC);
        WebFlow webFlow = new WebFlow(mainTemplate);
        return webFlow;
    }

    public static WebFlow buildForPAN2011SmallTrain() {

        WebTemplate mainTemplate = new WebTemplate(LookupOptions.PANSMALLTRAINSOURCEDIRECTORY, "SmallTrain", LookupOptions.EMPTYDOMAIN);

        LookupPattern articlePattern = new LookupPattern(LookupOptions.CONTAINER, LookupOptions.ARTICLE, "<text.*?", "</text>")
                .setStartEndMarker("<text", "</text>")
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLEID, "file=\"", "\">").setNth(0))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.AUTHORNAME, "<author id=\"", "\"/>"))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETEXT, "<body>", "</body>")
                        .setRegex(new String[]{"&", "<NAME/>", "<.*?>", ">", "<"}).setReplaces(new String[]{"&amp;", "NamedEntity", "", "&gt;", "&lt;"}));

        mainTemplate.addFolder(LookupOptions.PANSMALLTRAINDIRECTORY);
        mainTemplate.setMainPattern(articlePattern);
        mainTemplate.setType(LookupOptions.PANDOC);
        WebFlow webFlow = new WebFlow(mainTemplate);
        return webFlow;
    }

    public static WebFlow buildForPAN2011Small() {
        WebTemplate mainTemplate = new WebTemplate(LookupOptions.PANSMALLSOURCEDIRECTORY, "SmallTrain", LookupOptions.EMPTYDOMAIN);

        LookupPattern articlePattern = new LookupPattern(LookupOptions.CONTAINER, LookupOptions.ARTICLE, "<text.*?", "</text>")
                .setStartEndMarker("<text", "</text>")
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLEID, "file=\"", "\">").setNth(0))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.AUTHORNAME, "<author id=\"", "\"/>"))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETEXT, "<body>", "</body>")
                        .setRegex(new String[]{"&", "<NAME/>", "<.*?>", ">", "<"}).setReplaces(new String[]{"&amp;", "NamedEntity", "", "&gt;", "&lt;"}));

        mainTemplate.addFolder(LookupOptions.PANSMALLDIRECTORY);
        mainTemplate.setMainPattern(articlePattern);
        mainTemplate.setType(LookupOptions.PANDOC);
        WebFlow webFlow = new WebFlow(mainTemplate);
        return webFlow;
    }
}
