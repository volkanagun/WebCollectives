package data.crawler.web;

import java.io.Serializable;
import java.util.List;

public abstract class WebSink implements Serializable {

    public abstract void addDocuments(List<WebDocument> documents);
    public abstract List<String> getText();
    public abstract WebSink initialize();
}
