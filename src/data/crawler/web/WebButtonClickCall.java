package data.crawler.web;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class WebButtonClickCall extends WebFunctionCall {

    private Integer count = 0;
    private String cssSelector;
    private WebDriver driver;
    private JavascriptExecutor js;
    public WebButtonClickCall(Integer count, String cssSelector) {
        this.count = count;
        this.cssSelector = cssSelector;
    }

    @Override
    public WebButtonClickCall initialize() {
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
            WebElement element = existingDriver.findElement(By.cssSelector(cssSelector));
            for (int i = 0; i < count; i++) {

                element.click();
            }
        }
        catch(Exception ex){
            System.out.println();
        }


        return existingDriver.getPageSource();
    }
}
