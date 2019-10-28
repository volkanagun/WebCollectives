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
        System.setProperty("webdriver.chrome.driver","/usr/bin/chromedriver");
        this.driver = new ChromeDriver();
        this.count = count;
        this.cssSelector = cssSelector;
        this.js = (JavascriptExecutor) driver;
    }

    @Override
    public String returnHTML(String url) {
        driver.get(url);

        WebElement element = driver.findElement(By.cssSelector(cssSelector));
        try {
            for(int i=0; i< 50; i++){
                js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
            }


            for (int i = 0; i < count; i++) {
                js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
                element.click();
            }
        }
        catch(Exception ex){
            System.out.println();
        }


        return driver.getPageSource();
    }
}
