package data.crawler.web;

import java.util.ArrayList;
import java.util.List;

public class WebMultiSuffixGenerator extends WebSuffixGenerator{

    private final List<WebSuffixGenerator> generators;

    public WebMultiSuffixGenerator(List<WebSuffixGenerator> generators) {
        this.generators = generators;
    }

    public List<String> apply(String url){
        List<String> currentList = new ArrayList<>();
        currentList.add(url);

        for(int i=0; i< generators.size(); i++){
            WebSuffixGenerator generator = generators.get(i);
            List<String> tempList = new ArrayList<>();
            for(String newUrl:currentList){
                List<String> newList = generator.apply(newUrl);
                tempList.addAll(newList);
            }

            currentList = tempList;
        }

        return currentList;
    }

}
