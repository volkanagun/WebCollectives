package data.crawler.web;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;

public class WebFunctionScroll extends WebFunctionScrollHeight{
    private String selector;
    public WebFunctionScroll(Integer count, String cssSelector) {
        super(count);
        this.selector = cssSelector;
    }

    public WebFunctionScroll(Integer count, Integer minusHeight, String cssSelector) {
        super(count, minusHeight);
        this.selector = cssSelector;
    }

    @Override
    public String returnHTML(WebDriver existingDriver) {
        String htmlText = null;
        try {
            isError = false;
            for (int i = 0; i < getCount(); i++) {
                waitFor();
                log();
                existingDriver.findElement(By.cssSelector(selector)).sendKeys(Keys.PAGE_DOWN);
            }

            waitFor();
            htmlText = existingDriver.getPageSource();

        }
        catch (ClassCastException ex){
            isError = true;
            ex.printStackTrace();
        }
        catch (Exception ex){
            isError = true;
            System.out.println("Scroll height error...");
        }



        return htmlText;
    }
}
