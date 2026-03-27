package data.crawler.web;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.io.Serializable;

public abstract class WebFunctionCall implements Serializable {

    private static final String OS = System.getProperty("os.name").toLowerCase();
    protected ChromeDriver chromeDriver;
    protected FirefoxDriver firefoxDriver;

    protected JavascriptExecutor js;
    protected Boolean doDestroy = false;
    protected Boolean doFirefox = false;
    protected int waitTime;
    protected boolean isError;
    protected boolean doStopOnError;
    protected boolean doInform = true;

    public boolean isDoInform() {
        return doInform;
    }

    public WebFunctionCall setDoInform(boolean doInform) {
        this.doInform = doInform;
        return this;
    }

    public abstract String returnHTML(String url);

    public abstract String returnHTML(WebDriver driver);

    public int getWaitTime() {
        return waitTime;
    }

    public boolean isError() {
        return isError;
    }

    public void setError(boolean error) {
        isError = error;
    }

    public boolean isDoStopOnError() {
        return doStopOnError;
    }

    public WebFunctionCall setDoStopOnError(boolean doStopOnError) {
        this.doStopOnError = doStopOnError;
        return this;
    }

    public WebFunctionCall setWaitTime(int waitTime) {
        this.waitTime = waitTime;
        return this;
    }

    public Boolean getDoFirefox() {
        return doFirefox;
    }

    public WebFunctionCall setDoFirefox(Boolean doFirefox) {
        this.doFirefox = doFirefox;

        return this;
    }

    public WebFunctionCall doDestroy(){
        doDestroy = true;
        return this;
    }

    public WebFunctionCall waitFor(){
        try {
            Thread.sleep(waitTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return this;
    }

    public WebFunctionCall initialize() {

        if(doFirefox && isUnix()){
            System.setProperty("webdriver.gecko.driver", "/usr/bin/geckodriver");
            this.firefoxDriver = new FirefoxDriver();
            this.js = firefoxDriver;
        }
        else if(doFirefox){
            System.setProperty("webdriver.gecko.driver", new File("resources/selenium/geckodriver.exe").getPath());
            this.firefoxDriver = new FirefoxDriver();
            this.js = firefoxDriver;
        }
        else if (isUnix()) {
            System.setProperty("webdriver.chrome.driver", new File("resources/selenium/chromedriver").getPath());
            ChromeOptions options  = new ChromeOptions();
            //options.addArguments("incognito");
            options.addArguments("--disable-popup-blocking");
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setCapability(ChromeOptions.CAPABILITY, options);

            this.chromeDriver = new ChromeDriver(capabilities);
            this.js = chromeDriver;
        } else {
            System.setProperty("webdriver.chrome.driver", new File("resources/selenium/chromedriver.exe").getPath());
            ChromeOptions options  = new ChromeOptions();
            //options.addArguments("incognito");
            options.addArguments("--disable-popup-blocking");
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setCapability(ChromeOptions.CAPABILITY, options);
            this.chromeDriver = new ChromeDriver(capabilities);
            this.js = chromeDriver;
        }
        return this;
    }


    public WebFunctionCall destroy() {

        if(doFirefox && firefoxDriver!=null){
            firefoxDriver.close();
            firefoxDriver = null;
            js = null;
        }
        else if (chromeDriver != null) {
            chromeDriver.close();
            chromeDriver = null;
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
