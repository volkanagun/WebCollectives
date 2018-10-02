package data.crawler.sites;

import data.crawler.web.LookupOptions;
import data.crawler.web.LookupPattern;
import data.crawler.web.WebFlow;
import data.crawler.web.WebTemplate;

import java.io.Serializable;

public class Reuters implements Serializable {
    public static WebFlow build() {
        WebTemplate mainTemplate = new WebTemplate(LookupOptions.REUTERSDIRECTORY, "reuters-article", LookupOptions.EMPTYDOMAIN);

        LookupPattern articlePattern = new LookupPattern(LookupOptions.CONTAINER, LookupOptions.ARTICLE, "<REUTERS TOPICS=\"YES\".*?>", "</REUTERS>")
                .setStartEndMarker("<REUTERS", "</REUTERS>")
                .addPattern(new LookupPattern(LookupOptions.SKIP, LookupOptions.CONTAINER, "<TOPICS>", "</TOPICS>")
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.TOPIC, "<D>", "</D>")))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETEXT, "<BODY>", "</BODY"));


        mainTemplate.addFolder(LookupOptions.REUTERSSOURCEDIRECTORY);

        mainTemplate.setMainPattern(articlePattern);
        mainTemplate.setType(LookupOptions.REUTERSDOC);
        WebFlow webFlow = new WebFlow(mainTemplate);
        return webFlow;
    }
}
