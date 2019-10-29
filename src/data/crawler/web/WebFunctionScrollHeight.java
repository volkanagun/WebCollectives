package data.crawler.web;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class WebFunctionScrollHeight extends WebFunctionCall {
    private WebDriver driver;
    private JavascriptExecutor js;
    private Integer count = 1;

    public WebFunctionScrollHeight() {
    }

    public WebFunctionScrollHeight(Integer count) {
        this.count = count;
    }

    @Override
    public WebFunctionCall initialize() {
        System.setProperty("webdriver.chrome.driver","/usr/bin/chromedriver");
        this.driver = new ChromeDriver();
        this.js = (JavascriptExecutor) driver;
        return this;
    }

    @Override
    public String returnHTML(String url) {
        driver.get(url);
        return returnHTML(driver);
    }



    @Override
    public String returnHTML(WebDriver existingDriver) {
        try {
            for (int i = 0; i < count; i++) {
                ((JavascriptExecutor) existingDriver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
            }
        }
        catch (Exception ex){}


        return existingDriver.getPageSource();
    }
}
