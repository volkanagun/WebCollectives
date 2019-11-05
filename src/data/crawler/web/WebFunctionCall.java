package data.crawler.web;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.io.Serializable;
import java.util.List;

public abstract class WebFunctionCall implements Serializable {

    private static String OS = System.getProperty("os.name").toLowerCase();
    protected ChromeDriver driver;
    protected JavascriptExecutor js;


    public abstract String returnHTML(String url);

    public abstract String returnHTML(WebDriver driver);

    public WebFunctionCall initialize() {

        if (isUnix()) {
            System.setProperty("webdriver.chrome.driver", "/usr/bin/chromedriver");
            this.driver = new ChromeDriver();
            this.js = driver;

        } else {
            System.setProperty("webdriver.chrome.driver", new File("resources/selenium/chromedriver.exe").getPath());
            /*ChromeOptions options = new ChromeOptions();
            options.setBinary(new File("resources/selenium/chromedriver.exe").getPath());*/
            this.driver = new ChromeDriver();
            this.js = driver;

        }
        return this;
    }


    public WebFunctionCall destroy() {
        if (driver != null) {
            driver.close();
            driver = null;
            js = null;
        }
        return this;
    }

    public static boolean isWindows() {

        return (OS.indexOf("win") >= 0);

    }

    public static boolean isMac() {

        return (OS.indexOf("mac") >= 0);

    }

    public static boolean isUnix() {

        return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0);

    }

    public static boolean isSolaris() {

        return (OS.indexOf("sunos") >= 0);

    }

}
