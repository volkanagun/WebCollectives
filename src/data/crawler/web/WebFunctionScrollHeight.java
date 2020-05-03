package data.crawler.web;

import com.google.common.collect.Maps;
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
        try {
            chromeDriver.get(url);
            chromeDriver.wait(waitTime);
        }
        catch(InterruptedException ex){}

        return returnHTML(chromeDriver);
    }



    @Override
    public String returnHTML(WebDriver existingDriver) {
        String htmlText = null;
        try {
            for (int i = 0; i < count; i++) {
                waitFor();
                ((JavascriptExecutor) existingDriver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
                ((JavascriptExecutor) existingDriver).executeScript("window.scrollBy(0, -70)");
            }

            waitFor();
            htmlText = existingDriver.getPageSource();

        }
        catch (ClassCastException ex){
            ex.printStackTrace();
        }
        catch (Exception ex){
            System.out.println("Scroll height error...");
        }



        return htmlText;
    }
}
