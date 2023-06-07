package data.crawler.web;

import org.openqa.selenium.WebDriver;

public class WebEmptyFunctionCall extends WebFunctionCall{
    @Override
    public WebFunctionCall destroy() {
        if (chromeDriver != null) {
            chromeDriver.close();
            chromeDriver = null;
            js = null;
        }
        else{
            firefoxDriver.close();
            firefoxDriver = null;
            js = null;
        }
        return this;
    }


    @Override
    public String returnHTML(String url) {
        try {
            if(chromeDriver != null) {
                chromeDriver.getSessionId();
                chromeDriver.get(url);
                Thread.sleep(waitTime);
                return returnHTML(chromeDriver);

            }
            else{
                firefoxDriver.getSessionId();
                firefoxDriver.get(url);
                Thread.sleep(waitTime);
                return returnHTML(firefoxDriver);
            }



        } catch (InterruptedException ex) {
                return null;
        }

    }

    @Override
    public String returnHTML(WebDriver existingDriver) {


        return existingDriver.getPageSource();
    }
}
