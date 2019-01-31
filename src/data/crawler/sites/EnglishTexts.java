package data.crawler.sites;

import data.crawler.web.LookupOptions;
import data.crawler.web.WebFlow;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EnglishTexts {
    public static List<WebFlow> build(){
        List<WebFlow> webFlows = new ArrayList<>();

        webFlows.add(EngadetList.build(LookupOptions.TEXTENGDIRECTORY));
        webFlows.add(Gizmodo.build(LookupOptions.TEXTENGDIRECTORY));
        webFlows.add(HuffingtonPost.build(LookupOptions.TEXTENGDIRECTORY));
        webFlows.add(MashableList.build(LookupOptions.TEXTENGDIRECTORY));
        webFlows.add(BoingBlogList.build(LookupOptions.TEXTENGDIRECTORY));
        for(WebFlow webFlow:webFlows){
            webFlow.setMainLookComplete(false);
        }

        return webFlows;
    }

    public static void main(String[] args){
        ExecutorService service = Executors.newFixedThreadPool(1);
        WebFlow.batchSubmit(service, build());
        service.shutdown();
    }
}
