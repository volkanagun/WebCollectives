package data.crawler.comparison;

import data.crawler.web.*;

import java.util.ArrayList;
import java.util.List;

public class JournalExample {

    public WebFunctionCall function1(){
        return null;
    }
    public WebSuffixGenerator generator1(){
        return null;
    }


    public void lookupPattern(){
        //Pattern definition is used in extracting text. The parent pattern will not be produced in the output.
        LookupPattern mainPattern = new LookupPattern(LookupOptions.SKIP, LookupOptions.CONTAINER,
                "<div id=\"mainbar\"(.*?)>","</div>")
                //Search the HTML by using child div patterns
                .setStartEndMarker("<div","</div>")
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE,
                        "<h3(.*?)>","</h3>")
                        //Clean the h3 content by removing all tags
                        .setRemoveTags(true));
    }

    public void webTemplate(){
        //Previously defined function call
        WebFunctionCall functionCall = function1();
        //Previously defined seed generator
        WebSuffixGenerator generator = generator1();
        WebTemplate linkTemplate = new WebTemplate(LookupOptions.ARTICLEDIRECTORY, "article-links", "news.sky.com");
        //Main seed to be used as input to seed generator
        linkTemplate.addSeed("news.sky.com/world");
        linkTemplate.setFunctionCall(functionCall);
        linkTemplate.setSuffixGenerator(generator);

    }

    public void suffixGenerators(){
        List<WebSuffixGenerator> suffixGenerators = new ArrayList<>();
        suffixGenerators.add(new WebCountGenerator(1, 354, "&KategoriNo="));
        suffixGenerators.add(new WebCountGenerator(1, 5, "&Page="));
    }

    public static void main(String[] args) {

        //Javascript function call
        WebFunctionCall functionCall = new WebExecuteJS("loadMoreNews()")
                .setDoStopOnError(true)
                .setWaitTime(2000);
                                                   //call scroll down 1 time
        WebFunctionCall scrollCall = new WebFunctionScrollHeight(1)
                .setWaitTime(1500);

        //Repeat the sequence call 5 times
        int pageCount = 5;                                            // scroll and function JS calls
        WebFunctionCall sequenceCall = new WebFunctionSequence(pageCount, scrollCall, functionCall)
                .setDoFirefox(true)
                .setWaitBetweenCalls(1000L)
                .initialize()
                .setWaitTime(1000);


    }

}
