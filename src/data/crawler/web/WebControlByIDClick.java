package data.crawler.web;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class WebControlByIDClick extends WebButtonClickControl{
    public WebControlByIDClick(Integer count, String idSelector) {
        super(count, idSelector);
    }

    public String returnHTML(WebDriver existingDriver) {
        String mainHTML = null;
        try {

            WebElement element = existingDriver.findElement(By.id(getCssSelector()));
            System.out.println("Calling click for " + getCssSelector() + " with " + getCount() + " count");
            int myCount = getCount();

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
