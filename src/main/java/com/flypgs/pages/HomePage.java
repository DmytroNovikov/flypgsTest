package com.flypgs.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.LoadableComponent;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 *
 * Created by Dmytro Novikov on 3/25/2015.
 *
 */
public class HomePage extends LoadableComponent<HomePage>{

    private static final String pageUrl = "http://www.flypgs.com/en/";
    private static final String pageTitle = "Pegasus Airlines";

    @FindBy(css = "input#autodep")
    WebElement flightFrom;

    @FindBy(css = "input#autodest")
    WebElement flightTo;


    @FindBy(how= How.XPATH, using="id(\"lang-selected\")")
    private WebElement languageSelector;


    @FindBy(how=How.XPATH, using="id(\"autodep-auto-show-all\")")
    private WebElement fromSelector;


    @FindBy(how=How.XPATH, using="id(\"ui-active-menuitem\")")
    private WebElement amsterdamItem;


    @FindBy(how=How.XPATH, using="id(\"module-online-actions\")/div[2]/div[1]/table[2]/tbody[1]/tr[1]/td[1]/div[1]/div[1]/div[1]/div[1]/div[1]")
    private WebElement adultSelector;


    private WebDriver driver;

    public HomePage(WebDriver driver){
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @Override
    protected void load() {
        driver.get(pageUrl);
    }

    @Override
    protected void isLoaded() throws Error {
        assertTrue(driver.getTitle().equals(pageTitle));
    }

    public void prn(){
        System.out.println(flightFrom.getText());
        System.out.println(flightTo.getText());
        flightFrom.sendKeys("Almaty");
        //driver.findElement(By.cssSelector("a#autodep-auto-show-all")).click();
        //flightFrom.click();
        List<WebElement> li = driver.findElements(By.cssSelector("a.ui-corner-all[tabindex='-1']"));
        //System.out.println(li.size());
        for(WebElement e : li)
            System.out.println(e);
    }
}
