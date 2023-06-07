package data.crawler.web;


import java.io.Serializable;

public abstract class WebPageFilter implements Serializable {

    public abstract boolean isOk(String html);
}

