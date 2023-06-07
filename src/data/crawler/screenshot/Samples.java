package data.crawler.screenshot;

import data.crawler.web.*;

import java.util.ArrayList;
import java.util.List;

public class Samples {


    public LookupPattern json1(){
        LookupPattern mainPattern = new LookupJSON(LookupOptions.CONTAINER, LookupOptions.ARTICLE, "new")
                .addPattern(new LookupJSON(LookupOptions.TEXT, LookupOptions.DATE, "post_date"))
                .addPattern(new LookupJSON(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "title"))
                .addPattern(new LookupJSON(LookupOptions.TEXT, LookupOptions.GENRE, "channel"))
                .addPattern(new LookupJSON(LookupOptions.TEXT, LookupOptions.AUTHOR, "author"))
                .addPattern(new LookupJSON(LookupOptions.SKIP, LookupOptions.ARTICLE, "content")
                        .addPattern(new LookupJSON(LookupOptions.ARTICLE, LookupOptions.ARTICLETEXT, "plain")
                                .addPattern(new LookupSplit(LookupOptions.TEXT, LookupOptions.ARTICLEPARAGRAPH, "\n\n"))));

        return mainPattern;
    }

    public WebSuffixGenerator generator1(){
        List<WebSuffixGenerator> suffixGenerators = new ArrayList<>();
        suffixGenerators.add(new WebCountGenerator(1,354,"&KategoriNo="));
        suffixGenerators.add(new WebCountGenerator(1,5,"&Page="));
        return new WebMultiSuffixGenerator(suffixGenerators);
    }

    public WebTemplate template1(){
        WebFunctionCall functionCall = function1();
        WebSuffixGenerator generator = generator1();
        WebTemplate linkTemplate = new WebTemplate(LookupOptions.ARTICLEDIRECTORY, "article-links", "news.sky.com");
        linkTemplate.addSeed("news.sky.com/world");
        linkTemplate.setFunctionCall(functionCall);
        linkTemplate.setSuffixGenerator(generator);
        return linkTemplate;
    }
    public WebFunctionCall function1(){
        int pageCount = 1;

        WebFunctionCall functionCall = new WebExecuteJS("loadMoreNews()")
                .setDoStopOnError(true)
                .setWaitTime(2000);

        WebFunctionCall scrollCall = new WebFunctionScrollHeight(1).setWaitTime(1500);
        WebFunctionCall sequenceCall = new WebFunctionSequence(pageCount,  scrollCall, functionCall)
                .setDoFirefox(true)
                .setWaitBetweenCalls(1000L)
                .initialize()
                .setWaitTime(1000);

        return sequenceCall;
    }

    public void sample1(){

        WebFunctionCall function = function1();
        WebSuffixGenerator suffixGenerator = generator1();

        LookupPattern linkPattern = new LookupPattern(LookupOptions.URL, LookupOptions.MAINPAGE, "<main class=\"o-site-main\" id=\"enw-main-content\">", "</main>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a rel=\"bookmark\"(.*?)href=\"", "\""));

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.TURKISHARTICLEDIRECTORY, "article-links", "https://tr.euronews.com/")
                .addSeed("smart-regions", "https://tr.euronews.com/programlar/smart-regions")
                .setSuffixGenerator(generator1())
                //Skip waiting between calls if true
                .setDoFast(false)
                //Sample a number of links from a large geenrated link set
                .setDoRandomSeed(10)
                //Delete the links if they are saved to folder
                .setDoDeleteStart(true)
                //Set the time to wait bween each link download
                .setSleepTime(2500L)
                //Set the number of threads for link download
                .setThreadSize(4)
                //Set the domain
                .setDomain("https://tr.euronews.com/")
                //Set function call
                .setFunctionCall(function)
                //Set suffix generator
                .setMainPattern(linkPattern);
    }

}
