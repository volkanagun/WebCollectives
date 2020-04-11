package data.crawler.web;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class WebButtonClickCall extends WebFunctionCall {

    private Integer count = 0;
    private String cssSelector;


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
        return this;
    }


    @Override
    public String returnHTML(String url) {
        chromeDriver.get(url);
        return returnHTML(chromeDriver);
    }

    @Override
    public String returnHTML(WebDriver existingDriver) {


        try {
            WebElement element = existingDriver.findElement(By.cssSelector(cssSelector));
            for (int i = 0; i < count; i++) {

                element.click();
            }
        }
        catch(Exception ex){
            System.out.println("Click error for "+cssSelector);
        }


        return existingDriver.getPageSource();
    }
}
