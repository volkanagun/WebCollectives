package data.crawler.web;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.io.File;

public class WebButtonClickCall extends WebFunctionCall {

    private Integer count = 0;
    private String cssSelector;
    private WebDriver driver;
    private JavascriptExecutor js;
    private static String OS = System.getProperty("os.name").toLowerCase();
    public WebButtonClickCall(Integer count, String cssSelector) {
        this.count = count;
        this.cssSelector = cssSelector;
    }

    private static boolean isWindows() {

        return (OS.indexOf("win") >= 0);

    }

    private static boolean isMac() {

        return (OS.indexOf("mac") >= 0);

    }

    private static boolean isUnix() {

        return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 );

    }

    private static boolean isSolaris() {

        return (OS.indexOf("sunos") >= 0);

    }

    @Override
    public WebButtonClickCall initialize() {

        if(isUnix()) System.setProperty("webdriver.chrome.driver","/usr/bin/chromedriver");
        else System.setProperty("webdriver.chrome.driver", new File(".").getPath()+"resources/selenium/chromedirever.exe");
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
