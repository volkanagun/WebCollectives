package data.crawler.web;

import com.google.common.collect.Maps;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

public class WebFunctionScrollHeight extends WebFunctionCall {

    private Integer count = 1;
    private Integer minusHeight = 60;

    public WebFunctionScrollHeight(Integer count) {
        this.count = count;
    }

    public WebFunctionScrollHeight(Integer count, Integer minusHeight) {
        this(count);
        this.minusHeight = minusHeight;
    }

    @Override
    public String returnHTML(String url) {
        try {

            log();
            chromeDriver.get(url);
            chromeDriver.wait(waitTime);
        }
        catch(InterruptedException ex){}

        return returnHTML(chromeDriver);
    }

    public WebFunctionScrollHeight log(){
        if(doInform) System.out.println("Calling Scroll Height");
        return this;
    }


    @Override
    public String returnHTML(WebDriver existingDriver) {
        String htmlText = null;
        try {
            for (int i = 0; i < count; i++) {
                waitFor();
                log();
                ((JavascriptExecutor) existingDriver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
                ((JavascriptExecutor) existingDriver).executeScript("window.scrollBy(0, -"+minusHeight.toString()+")");
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
