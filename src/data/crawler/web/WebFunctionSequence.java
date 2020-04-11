package data.crawler.web;

import org.openqa.selenium.WebDriver;

import java.util.Arrays;
import java.util.List;

public class WebFunctionSequence extends WebFunctionCall {
    private List<WebFunctionCall> webFunctionCallList;
    private Long waitBetweenCalls = 500L;

    private Integer count = 1;

    public WebFunctionSequence(Integer count, WebFunctionCall... webFunctionCalls) {
        this(webFunctionCalls);
        this.count = count;
    }

    public WebFunctionSequence(WebFunctionCall... webFunctionCalls) {
        webFunctionCallList = Arrays.asList(webFunctionCalls);
    }


    public Long getWaitBetweenCalls() {
        return waitBetweenCalls;
    }

    public WebFunctionSequence setWaitBetweenCalls(Long waitBetweenCalls) {
        this.waitBetweenCalls = waitBetweenCalls;
        return this;
    }

    @Override
    public String returnHTML(WebDriver driver) {
        String htmlSource = null;
        for (int i = 0; i < count; i++) {
            for (WebFunctionCall functionCall : webFunctionCallList) {
                sleep();
                htmlSource = functionCall.returnHTML(driver);
            }
        }


        if(doDestroy) destroy();


        return htmlSource;
    }

    public void sleep(){
        try {
            Thread.sleep(waitBetweenCalls);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public String returnHTML(String url) {

        chromeDriver.get(url);

        return returnHTML(chromeDriver);
    }
}
