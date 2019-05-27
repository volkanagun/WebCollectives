package data.crawler.web;

import java.io.Serializable;

public abstract class LookupFilter implements Serializable {
    public abstract String accept(String text);
}
