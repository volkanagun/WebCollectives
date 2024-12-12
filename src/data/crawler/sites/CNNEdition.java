package data.crawler.sites;

import data.crawler.web.LookupOptions;
import data.crawler.web.LookupPattern;
import data.crawler.web.WebFlow;
import data.crawler.web.WebTemplate;

import java.io.Serializable;

public class CNNEdition implements Serializable {

    public static WebFlow build(){
        String domain = "edition.cnn.com";
        LookupPattern urlLookup = new LookupPattern(LookupOptions.ARTICLE, LookupOptions.CONTAINER, "<div class=\"container section-container\">", "</div>")
                .setStartEndMarker("<div", "</div>")
                .setNth(0);

        LookupPattern articleLookup = new LookupPattern(LookupOptions.ARTICLE, LookupOptions.CONTAINER, "<div class=\"container section-container\">", "</div>");

        return null;
    }
}
