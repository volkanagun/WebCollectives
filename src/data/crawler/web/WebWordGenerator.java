package data.crawler.web;

import data.util.TextFile;

import java.util.ArrayList;
import java.util.List;

public class WebWordGenerator extends WebSuffixGenerator {
    private String filename;
    private String wordSeparator = "%20";
    private List<String> wordList;
    public WebWordGenerator(String filename) {
        this.filename = filename;
        this.wordList = new TextFile(filename).readLines();
    }

    @Override
    public List<String> apply(String url) {
        List<String> urlList = new ArrayList<>();
        for(String c1:wordList){
            for(String c2:wordList){
                if(!c1.equals(c2)){
                     urlList.add(url + c1 + wordSeparator + c2);
                }
            }
        }
        return urlList;
    }
}
