package com.flypgs.pages;

import jquery.DatePicker;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.LoadableComponent;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Dmytro Novikov on 4/1/2015.
 */
public class OnlineTicketsTab extends LoadableComponent<OnlineTicketsTab>{

    private final String pageUrl = "http://www.flypgs.com";

    // Current language
    @FindBy(css = "span#lang-selected")
    private WebElement currentLanguage;

    // From
    private static final By openFrom = By.cssSelector("a#autodep-auto-show-all");
    private static final By valueFrom = By.cssSelector("input#autodep");

    // To
    private static final By openTo = By.cssSelector("a#autodest-auto-show-all");
    private static final By valueTo = By.cssSelector("input#autodest");

    private static final By citiesList = By.cssSelector("li.ui-menu-item a");

    // Round trip / One-way
    @FindBy(xpath = "//*[@id='module-online-actions']/div[2]/div[1]/table[1]/tbody/tr[3]/td/p[1]/span/a")
    private WebElement roundTripRadio;
    @FindBy(xpath = "//*[@id='module-online-actions']/div[2]/div[1]/table[1]/tbody/tr[3]/td/p[2]/span/a")
    private WebElement oneWayTripRadio;

    // Departure Date
    @FindBy(css = "input[id='linked-gidis']")
    private WebElement departureDate;
    private static final By returnDateValue = By.cssSelector("input[id='linked-donus']");

    // Adult
    @FindBy(xpath = "//*[@id='module-online-actions']/div[2]/div[1]/table[2]/tbody/tr[1]/td[1]/div/div/div/div/div")
    private WebElement adultSelector;
    private static final String xpathAdultsListItems = "//*[@id='pessengerddl-adult']/option[%number%]";
    // Children (2-12)
    @FindBy(xpath = "//*[@id='module-online-actions']/div[2]/div[1]/table[2]/tbody/tr[1]/td[2]/div/div/div/div/div")
    private WebElement childrenSelector;
    private static final String xpathChildrenListItems = "//*[@id='pessengerddl-child']/option[%number%]";
    // Infant (0-2)
    @FindBy(xpath = "//*[@id='module-online-actions']/div[2]/div[1]/table[2]/tbody/tr[1]/td[3]/div/div/div/div/div")
    private WebElement infantSelector;
    private static final String xpathInfantsListItems = "//*[@id='pessengerddl-baby']/option[%number%]";

    //  Are your travel dates flexible?
    @FindBy(xpath = "//*[@id='module-online-actions']/div[2]/div[2]/span/a")
    private WebElement flexibleCheckbox;

    // Continue Button
    @FindBy(css = "#OnlineTicket_btnOnlineTicketDevam")
    private WebElement continueButton;

    // Error window appeared after Continue Button pressed
    private final By errorCode = By.cssSelector("span.errorCode");
    private final By warning = By.cssSelector("td.warning");
    private final By afterError = By.cssSelector("a[href='Login.jsp']");

    private WebDriver driver;

    public OnlineTicketsTab(WebDriver driver){
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @Override
    protected void load() {
        if(!driver.getCurrentUrl().equals(pageUrl))
            driver.get(pageUrl);
    }

    @Override
    protected void isLoaded() throws Error {
        assertTrue((driver.findElements(openFrom).size() == 1));
    }

    public String buyRoundTripTickets(String from, String to,
                                      LocalDate departureDate, LocalDate returnDate,
                                      int adults, int children, int infants,
                                      boolean flexible){
        setTheRoute(from, to);
        setRoundTrip(true);
        assertEquals(true, getRoundTrip());
        setDepartureDate(departureDate);
        assertEquals(departureDate, getDepartureDate());
        setReturnDate(returnDate);
        assertEquals(returnDate, getReturnDate());
        setAdultsCount(adults);
        assertEquals(adults, getAdultsCount());
        setChildrenCount(children);
        assertEquals(children, getChildrenCount());
        setInfantsCount(infants);
        assertEquals(infants, getInfantsCount());
        setFlexible(flexible);
        assertEquals(flexible, getFlexible());
        return pushContinueButton();
    }

    public String buyOneWayTickets(String from, String to,
                                   LocalDate departureDate,
                                   int adults, int children, int infants,
                                   boolean flexible){
        setTheRoute(from, to);
        setRoundTrip(false);
        assertEquals(false, getRoundTrip());
        setDepartureDate(departureDate);
        assertEquals(departureDate, getDepartureDate());
        setAdultsCount(adults);
        assertEquals(adults, getAdultsCount());
        setChildrenCount(children);
        assertEquals(children, getChildrenCount());
        setInfantsCount(infants);
        assertEquals(infants, getInfantsCount());
        setFlexible(flexible);
        assertEquals(flexible, getFlexible());
        return pushContinueButton();
    }

    public void setTheRoute(String from, String to){
        setCityFrom(from);
        assertEquals(from, getFromCity());
        setToCity(to);
        assertEquals(to, getToCity());
    }

    public void setCityFrom(String city){
        //selectCityByScrolling(cssOpenFrom, city);
        setCity(true, city);
    }

    public String getFromCity(){
        //return driver.findElement(valueFrom).getAttribute("value");
        return driver.findElement(valueFrom).getAttribute("value");
    }

    public void setToCity(String city){
        setCity(false, city);
    }

    public String getToCity(){
        return driver.findElement(valueTo).getAttribute("value");
    }

    private String xpathToItemToWait = "/html/body/ul[%data%]/li[1]/a";
    //private String xpathToItemToWait = "//*[@id='ui-active-menuitem']";
    private String cityToWait;

    /**
     * Emulation of city selection by typing it's name
     *
     * Idea: If a correct city name is enetered to the 'From' text area there will be only one item
     *       in the dropdown list. Choose the item by pressing ArrowDown and Enter keys.
     *
     * @param departure
     * @param city
     */
    private void setCity(boolean departure, String city){
        By selector = departure?valueFrom:valueTo;
        WebElement inputElement = driver.findElement(selector);
        if (!departure)
            waitForDestCityEnabled(driver);
        new Actions(driver)
                .click(inputElement)
                .sendKeys(inputElement, city)
                .perform();
        // Wait for single item in dropdown list
        cityToWait = city;
        String storedTemplate = xpathToItemToWait;
        xpathToItemToWait = storedTemplate.replace("%data%", Integer.toString(departure?1:2));
        waitForCityInList(driver);
        xpathToItemToWait = storedTemplate;
        new Actions(driver)
                .sendKeys(Keys.ARROW_DOWN)
                .sendKeys(Keys.ENTER)
                .perform();
    }

    public void waitForDestCityEnabled(WebDriver driver) {
        (new WebDriverWait(driver, 15, 500)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return (d.findElement(valueTo).getAttribute("disabled") == null);
            }
        });
    }

    public void waitForCityInList(WebDriver driver) {
        (new WebDriverWait(driver, 15, 500)).until(new ExpectedCondition<Boolean>() {
            List<WebElement> e;
            String item;
            public Boolean apply(WebDriver d){
                e = d.findElements(By.xpath(xpathToItemToWait));
                return (e.size() > 0 && e.get(0).getText().equals(cityToWait));
            }
        });
    }

    public void setRoundTrip(boolean roundTrip){
        if(roundTrip)
            roundTripRadio.click();
        else
            oneWayTripRadio.click();
    }

    public boolean getRoundTrip(){
        return roundTripRadio.getAttribute("class").equals("input-custom-class-radio-active");
    }

    public void setDepartureDate(LocalDate date){
        if(date.compareTo(LocalDate.now()) >= 0) {
            departureDate.click();
            DatePicker.setDate(driver, date, currentLanguage.getText());
        }
    }

    public LocalDate getDepartureDate(){
        return LocalDate.parse(departureDate.getAttribute("value"), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public void setReturnDate(LocalDate date){
        if(getRoundTrip()){
            if(date.compareTo(getDepartureDate()) >= 0){
                driver.findElement(returnDateValue).click();
                DatePicker.setDate(driver, date, currentLanguage.getText());
            }
        }
    }

    public LocalDate getReturnDate(){
        return LocalDate.parse(driver.findElement(returnDateValue).getAttribute("value"),
                DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public void setAdultsCount(int n){
        adultSelector.click();
        List<WebElement> e = driver.findElements(By.xpath(
                xpathAdultsListItems.replace("%number%", Integer.toString(n + 1))));
        if(e.size() == 1)
            e.get(0).click();
    }

    public int getAdultsCount(){
        return Integer.parseInt(adultSelector.getText());
    }

    public void setChildrenCount(int n){
        childrenSelector.click();
        List<WebElement> e = driver.findElements(By.xpath(
                xpathChildrenListItems.replace("%number%", Integer.toString(n + 1))));
        if(e.size() == 1)
            e.get(0).click();
    }

    public int getChildrenCount(){
        return Integer.parseInt(childrenSelector.getText());
    }

    public void setInfantsCount(int n){
        infantSelector.click();
        List<WebElement> e = driver.findElements(By.xpath(
                xpathInfantsListItems.replace("%number%", Integer.toString(n + 1))));
        if(e.size() == 1)
            e.get(0).click();
    }

    public int getInfantsCount(){
        return Integer.parseInt(infantSelector.getText());
    }

    public void setFlexible(boolean flexible){
        String tagClass = flexibleCheckbox.getAttribute("class");
        if(flexible && tagClass.equals("input-custom-class-checkbox"))
            flexibleCheckbox.click();
        if(!flexible && tagClass.equals("input-custom-class-checkbox-active"))
            flexibleCheckbox.click();
    }

    public boolean getFlexible(){
        return flexibleCheckbox.getAttribute("class").equals("input-custom-class-checkbox-active");
    }

    public String pushContinueButton(){
        String ok = "OK";
        continueButton.click();
        try {
            Alert alert = driver.switchTo().alert();
            ok = alert.getText();
            alert.dismiss();
        } catch (Exception e){
            // Nothing to do. It's not a good idea, but...
        }
        if(ok.equals("OK") && driver.getCurrentUrl().contains("Error")){
            List<WebElement> e = driver.findElements(errorCode);
            ok="";
            if(e.size() > 0) {
                ok = ok.concat(e.get(0).getText());
                ok = ok.concat("\n");
            }
            e = driver.findElements(warning);
            if(e.size() > 0)
                ok = ok.concat(e.get(0).getText());
            driver.findElement(afterError).click();
        }
        return ok;
    }

    public Map<String, String> getDepCities(){
        Map<String, String> cities = new HashMap<String, String>();
        ArrayList<Object> citiesArray =
                (ArrayList<Object>)((JavascriptExecutor)driver).executeScript("return depData.Rows;");
        String city, airport, data;
        for(Object o : citiesArray){
            data = o.toString();    // data string looks like
            // "{Origin_Name=Adana, Origin_Code=ADA}"
            city = data.split(", ")[0]; // part of the string containing city name
            city = city.substring(13, city.length());   // 13 - city name starting position
            airport = null;
            Matcher m = Pattern.compile("(=\\w+\\})").matcher(data);    // searching for airport abbreviation -
            // symbols between '=' and '}'
            if(m.find())
                airport = data.substring(m.start() + 1, m.end() - 1);   // abbreviation starts after '='
            // and ends before '}'
            cities.put(city, airport);
        }
        return cities;
    }

    public Map<String, String> getDestCities(){
        if(driver.findElement(valueTo).getAttribute("disabled")==null){
            Map<String, String> cities = new HashMap<String, String>();
            ArrayList<Object> citiesArray =
                    (ArrayList<Object>)((JavascriptExecutor)driver).executeScript("return destData.Rows;");
            String city, airport, data;
            for(Object o : citiesArray){
                data = o.toString();    // data string looks like
                // {Destination_Name=Adana, Destination_CarrierCode=PC,
                //  Destination_Code=ADA, Destination_Sse=F}
                city = data.split(", ")[0]; // part of the string containing city name
                city = city.substring(18, city.length());   // 18 - city name starting position
                airport = null;
                Matcher m = Pattern.compile("(Destination_Code=\\w+,)").matcher(data);  // searching for airport
                // abbreviation - symbols
                // between 'Destination_Code='
                // and ','
                if(m.find())
                    airport = data.substring(m.start() + 17, m.end() - 1);  // abbreviation starts after
                // 'Destination_Code=' (len()=17) and ends
                // before ','
                cities.put(city, airport);
            }
            return cities;
        }
        else
            return null;
    }
}