package data.crawler.web;

import org.openqa.selenium.WebDriver;

import java.io.Serializable;
import java.util.List;

public abstract class WebFunctionCall implements Serializable {


    public abstract WebFunctionCall initialize();
    public abstract String returnHTML(String url);
    public abstract String returnHTML(WebDriver driver);


}
