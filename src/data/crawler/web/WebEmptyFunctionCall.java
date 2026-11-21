package data.crawler.web;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class WebEmptyFunctionCall extends WebFunctionCall{
    private int tryCount = 2;
    private int crrCount = 1;
    private boolean doRepetitiveInit = false;

    public WebEmptyFunctionCall() {
    }

    public WebEmptyFunctionCall(boolean doRepetitiveInit) {
        this.doRepetitiveInit = doRepetitiveInit;
    }

    @Override
    public WebFunctionCall destroy() {
        if (chromeDriver != null) {
            chromeDriver.close();
            chromeDriver = null;
            js = null;
        }
        else if(firefoxDriver != null){
            firefoxDriver.close();
            firefoxDriver = null;
            js = null;
        }
        return this;
    }


    @Override
    public String returnHTML(String url) {
        try {

            if (doRepetitiveInit) {
                destroy();
                initialize();
            }
            
            if(chromeDriver != null) {
                
               
                
                chromeDriver.getSessionId();
                chromeDriver.get(url);
                Thread.sleep(waitTime);
                return returnHTML(chromeDriver);

            }
            else if(firefoxDriver != null){
                
                
                firefoxDriver.getSessionId();
                firefoxDriver.get(url);
                Thread.sleep(waitTime);
                return returnHTML(firefoxDriver);
            }
            else{                
                return null;
            }

        } catch (Exception ex) {
                
                //destroy();
                //initialize();
                if (crrCount <= tryCount){
                    String text = returnHTML(url);
                    if (text != null){
                        crrCount = 0;
                    }
                    return text;
                }
                else{
                    crrCount = 0;
                    return null;
                }
        }

    }

    @Override
    public String returnHTML(WebDriver existingDriver) {


        return existingDriver.getPageSource();
    }
}
