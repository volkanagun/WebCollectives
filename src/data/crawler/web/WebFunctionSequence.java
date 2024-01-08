package data.crawler.web;

import org.openqa.selenium.WebDriver;

import java.util.Arrays;
import java.util.List;

public class WebFunctionSequence extends WebFunctionCall {
    private final List<WebFunctionCall> webFunctionCallList;
    private Long waitBetweenCalls = 1500L;

    private Integer count = 1;

    public WebFunctionSequence(Integer count, WebFunctionCall... webFunctionCalls) {
        this(webFunctionCalls);
        this.count = count;
    }

    @Override
    public WebFunctionSequence setDoFirefox(Boolean doFirefox) {
        this.doFirefox = doFirefox;
        for(WebFunctionCall call:webFunctionCallList){
            call.setDoFirefox(doFirefox);
        }
        return this;
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
        int tryCount = 0;
        for (int i = 0; i < count; i++) {

            isError = false;
            for (WebFunctionCall functionCall : webFunctionCallList) {
                waitForNext();
                String newSource = functionCall.returnHTML(driver);
                if (functionCall.isError() && functionCall.isDoStopOnError()) {
                    isError = true;
                    break;
                }
                else{
                    htmlSource = newSource;
                }
            }

            if (isError) {
                System.out.println("Sequence error occurred...");
                tryCount++;
            }

            if(tryCount > 3){
                break;
            }
        }

        if (doDestroy) destroy();
        return htmlSource;
    }

    public void waitForNext() {
        try {
            Thread.sleep(waitBetweenCalls);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
}
