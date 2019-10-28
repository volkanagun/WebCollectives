package data.crawler.web;

import java.io.Serializable;
import java.util.List;

public abstract class WebFunctionCall implements Serializable {


    public abstract String returnHTML(String url);

}
