package data.crawler.web;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class WebButtonClickCall extends WebFunctionCall {

    private Integer count = 0;
    private final String cssSelector;



    public WebButtonClickCall(Integer count, String cssSelector) {
        this.count = count;
        this.cssSelector = cssSelector;
    }

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

            }
            else{
                firefoxDriver.getSessionId();
                firefoxDriver.get(url);

            }

            Thread.sleep(waitTime);

        } catch (InterruptedException ex) {

        }
        return returnHTML(chromeDriver);
    }

    @Override
    public String returnHTML(WebDriver existingDriver) {

        try {

            waitFor();
            isError = false;
            WebElement element = existingDriver.findElement(By.cssSelector(cssSelector));
            System.out.println("Calling click for " + cssSelector + " with "+count + " count");
            for (int i = 0; i < count; i++) {
                element.click();
            }
        } catch (Exception ex) {
            System.out.println("Click error for " + cssSelector);
            isError = true;
        }


        return existingDriver.getPageSource();
    }

    public Integer getCount() {
        return count;
    }

    public String getCssSelector() {
        return cssSelector;
    }
}
