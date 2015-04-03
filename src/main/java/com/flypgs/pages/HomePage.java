package com.flypgs.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.LoadableComponent;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 *
 * Created by Dmytro Novikov on 3/25/2015.
 *
 */
public class HomePage extends LoadableComponent<HomePage>{

    private static final String pageUrl = "http://www.flypgs.com";
    private static final String pageTitle = "Pegasus Havayolları - Ucuz Uçak Bileti Demek Özgürlük Demek!";

    private static final String cssCurrentLanguage = "span#lang-selected";
    private static final String cssLanguages = "ul#select-lang a";
    private static final String xpathTargetLanguage="//li/a[text()='%language%']";

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

    public String getCurrentLanguage(){
        return driver.findElement(By.cssSelector(cssCurrentLanguage)).getText();
    }

    public List<String> getLanguages(){
        List<String> languages = new ArrayList<String>();
        for(WebElement e : driver.findElements(By.cssSelector(cssLanguages)))
            languages.add(e.getAttribute("innerHTML"));
        return languages;
    }

    public void setCurrentLanguage(String language){
        if(!language.equals(getCurrentLanguage())){
            int n = getLanguages().indexOf(language) + 1;
            if(n > 0){
                driver.findElement(By.cssSelector(cssCurrentLanguage)).click();
                driver.findElement(By.xpath(xpathTargetLanguage.replace("%language%", language))).click();
            }
        }
    }
}