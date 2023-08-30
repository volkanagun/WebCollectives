package data.crawler.web;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

public class WebExecuteJS extends WebFunctionCall{
    private String executeJS;

    public WebExecuteJS(String executeJS) {
        this.executeJS = executeJS;
    }

    public String getExecuteJS() {
        return executeJS;
    }

    public void setExecuteJS(String executeJS) {
        this.executeJS = executeJS;
    }

    @Override
    public String returnHTML(String url) {
        try {

            if(chromeDriver !=null) {
                chromeDriver.get(url);
                chromeDriver.wait(waitTime);
            }
            else{
                firefoxDriver.get(url);
                firefoxDriver.wait(waitTime);
            }
        }
        catch(InterruptedException ex){}

        return returnHTML(chromeDriver);
    }

    @Override
    public String returnHTML(WebDriver driver) {
        ((JavascriptExecutor) driver).executeScript(executeJS);
        waitFor();
        return driver.getPageSource();
    }
}
