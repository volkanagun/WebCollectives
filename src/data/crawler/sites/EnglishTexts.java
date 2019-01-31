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

        webFlows.add(EngadetList.build());
        webFlows.add(Gizmodo.build());
        webFlows.add(HuffingtonPost.build());
        webFlows.add(MashableList.build());
        webFlows.add(BoingBlogList.build());
        for(WebFlow webFlow:webFlows){
            webFlow.setMainLookComplete(false);
            webFlow.setMainDirectory(LookupOptions.TEXTENGDIRECTORY);
        }

        return webFlows;
    }

    public static void main(String[] args){
        ExecutorService service = Executors.newFixedThreadPool(1);
        WebFlow.batchSubmit(service, build());
        service.shutdown();
    }
}
