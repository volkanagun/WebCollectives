package data.crawler.web;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class WebActionClickCall extends WebButtonClickCall{
    public WebActionClickCall(Integer count, String cssSelector) {
        super(count, cssSelector);
    }

    @Override
    public String returnHTML(WebDriver existingDriver) {

        try {

            waitFor();
            isError = false;

            WebElement element = existingDriver.findElement(By.cssSelector(cssSelector));
            WebDriverWait wait = new WebDriverWait(existingDriver, 15);
            wait.ignoring(StaleElementReferenceException.class);

            for (int i = 0; i < count; i++) {
                JavascriptExecutor executor = (JavascriptExecutor) existingDriver;
                WebElement link = wait.until(
                        ExpectedConditions.elementToBeClickable(element));

                executor.executeScript(
                        "arguments[0].scrollIntoView({block: 'center', behavior: 'instant'});",
                        link);

                link.click();

            }
        } catch (Exception ex) {
            System.out.println("Click error for " + cssSelector);
            isError = true;
        }


        return existingDriver.getPageSource();
    }
}
