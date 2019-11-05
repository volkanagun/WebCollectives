package data.crawler.web;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.Arrays;
import java.util.List;

public class WebFunctionSequence extends WebFunctionCall {
    private List<WebFunctionCall> webFunctionCallList;

    private Integer count = 1;

    public WebFunctionSequence(Integer count, WebFunctionCall... webFunctionCalls) {
        this(webFunctionCalls);
        this.count = count;
    }

    public WebFunctionSequence(WebFunctionCall... webFunctionCalls) {
        webFunctionCallList = Arrays.asList(webFunctionCalls);
    }



    @Override
    public String returnHTML(WebDriver driver) {
        String htmlSource = null;
        for (int i = 0; i < count; i++) {
            for (WebFunctionCall functionCall : webFunctionCallList) {
                htmlSource = functionCall.returnHTML(driver);
            }
        }

        return htmlSource;
    }


    @Override
    public String returnHTML(String url) {
        driver.get(url);
        return returnHTML(driver);
    }
}
