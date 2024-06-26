package data.crawler.web;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class WebButtonClickControl extends WebButtonClickCall {
    public WebButtonClickControl(Integer count, String cssSelector) {
        super(count, cssSelector);
    }

    @Override
    public String returnHTML(WebDriver existingDriver) {
        String mainHTML = null;
        try {

            WebElement element = existingDriver.findElement(By.cssSelector(getCssSelector()));
            System.out.println("Calling click for " + getCssSelector() + " with " + getCount() + " count");
            int myCount = getCount();
            isError  = false;

            String newHTML = existingDriver.getPageSource();
            WebPageLengthFilter lengthFilter = new WebPageLengthFilter(newHTML.length());
            mainHTML = newHTML;

            for (int i = 1; i <= myCount; i++) {
                try {
                    element.click();
                    waitFor();
                    newHTML = existingDriver.getPageSource();
                    if (lengthFilter.isOk(newHTML)) {
                        mainHTML = newHTML;
                        continue;
                    } else {
                        isError = true;
                        break;
                    }
                }
                catch(Exception ex){
                    isError = true;
                    System.out.println("Click error ... " + getCssSelector());
                }
            }
        } catch (Exception ex) {
            System.out.println("Click error for " + getCssSelector());
            isError = true;
        }


        return mainHTML;
    }
}
