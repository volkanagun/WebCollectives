package data.crawler.web;

import java.util.List;

public abstract class WebSuffixGenerator {
    public abstract List<String> apply(String url);
}

