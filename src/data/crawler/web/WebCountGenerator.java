package data.crawler.web;

import java.util.ArrayList;
import java.util.List;

public class WebCountGenerator extends WebSuffixGenerator{
    private final int start;
    private final int end;
    private String suffix = "";

    public WebCountGenerator(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public WebCountGenerator(int start, int end, String suffix) {
        this.start = start;
        this.end = end;
        this.suffix = suffix;
    }

    @Override
    public List<String> apply(String url) {
        List<String> urlList = new ArrayList<>();
        for(int i=start; i<end; i++){
            urlList.add(url+suffix+i);
        }

        return urlList;
    }
}
