package data.crawler.web;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

public class WebFunctionScrollHeight extends WebFunctionCall {

    private Integer count = 1;

    public WebFunctionScrollHeight() {
    }

    public WebFunctionScrollHeight(Integer count) {
        this.count = count;
    }
    @Override
    public String returnHTML(String url) {
        chromeDriver.get(url);
        return returnHTML(chromeDriver);
    }



    @Override
    public String returnHTML(WebDriver existingDriver) {
        try {
            for (int i = 0; i < count; i++) {
                ((JavascriptExecutor) existingDriver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
            }
        }
        catch (Exception ex){
            System.out.println("Scroll height error...");
        }


        return existingDriver.getPageSource();
    }
}
