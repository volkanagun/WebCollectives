package data.crawler.web;

import org.openqa.selenium.WebDriver;

public class WebFunctionAggregate extends WebFunctionCall {
    private WebFunctionCall functionCall;
    private Long waitBetweenCalls = 1500L;

    private Integer count = 1;

    public WebFunctionAggregate(WebFunctionCall functionSequence, int count) {
        this.functionCall = functionSequence;
        this.count = count;
    }

    public WebFunctionAggregate setDoFirefox(Boolean doFirefox) {
        this.doFirefox = doFirefox;
        this.functionCall.setDoFirefox(doFirefox);
        return this;
    }

    public WebFunctionAggregate setWaitBetweenCalls(Long waitBetweenCalls) {
        this.waitBetweenCalls = waitBetweenCalls;
        return this;
    }

    public WebFunctionAggregate setCount(Integer count) {
        this.count = count;
        return this;
    }

    @Override
    public String returnHTML(String url) {
        try {
            if(chromeDriver != null) {
                chromeDriver.getSessionId();
                chromeDriver.get(url);
            }
            else{
                firefoxDriver.getSessionId();
                firefoxDriver.get(url);
            }
            Thread.sleep(waitTime);
            //chromeDriver.wait(waitTime);
        } catch (InterruptedException ex) {

        }
        if(chromeDriver != null) return returnHTML(chromeDriver);
        else return returnHTML(firefoxDriver);
    }

    @Override
    public String returnHTML(WebDriver driver) {
        String htmlSource = "";
        int i = 0;
        isError = false;
        while (i < count) {
            String newSource = functionCall.returnHTML(driver);
            if (functionCall.isError() && functionCall.isDoStopOnError()) {
                isError = true;
                break;
            } else if(!functionCall.isError()) {
                htmlSource += newSource;

            }

            i += 1;
        }

        if (doDestroy) {
            destroy();
        }
        return htmlSource;
    }
}
