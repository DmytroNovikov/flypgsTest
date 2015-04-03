package com.flypgs.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.LoadableComponent;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertTrue;

/**
 * Created by Dmytro Novikov on 4/1/2015.
 */
public class OnlineTicketsTab extends LoadableComponent<OnlineTicketsTab>{

    private final String pageUrl = "http://www.flypgs.com";

    // From
    private static final String cssOpenFrom = "a#autodep-auto-show-all";
    private static final String cssValueFrom = "input#autodep";

    // To
    private static final String cssOpenTo = "a#autodest-auto-show-all";
    private static final String cssValueTo = "input#autodest";

    private static final String cssCitiesList = "li.ui-menu-item a";

    // Round trip / One-way
    @FindBy(xpath = "//*[@id='module-online-actions']/div[2]/div[1]/table[1]/tbody/tr[3]/td/p[1]/span/a")
    WebElement roundTripRadio;
    @FindBy(xpath = "//*[@id='module-online-actions']/div[2]/div[1]/table[1]/tbody/tr[3]/td/p[2]/span/a")
    WebElement oneWayTripRadio;

    // Departure Date
    @FindBy(css = "input[id='linked-gidis']")
    WebElement departureDate;
    private static final String cssReturnDateValue = "input[id='linked-donus']";

    // Adult
    @FindBy(xpath = "//*[@id='module-online-actions']/div[2]/div[1]/table[2]/tbody/tr[1]/td[1]/div/div/div/div/div")
    WebElement adultSelector;
    private static final String xpathAdultsListItems = "//*[@id='pessengerddl-adult']/option[%number%]";
    // Children (2-12)
    @FindBy(xpath = "//*[@id='module-online-actions']/div[2]/div[1]/table[2]/tbody/tr[1]/td[2]/div/div/div/div/div")
    WebElement childrenSelector;
    private static final String xpathChildrenListItems = "//*[@id='pessengerddl-child']/option[%number%]";
    // Infant (0-2)
    @FindBy(xpath = "//*[@id='module-online-actions']/div[2]/div[1]/table[2]/tbody/tr[1]/td[3]/div/div/div/div/div")
    WebElement infantSelector;
    private static final String xpathInfantsListItems = "//*[@id='pessengerddl-baby']/option[%number%]";

    //  Are your travel dates flexible?
    @FindBy(xpath = "//*[@id='module-online-actions']/div[2]/div[2]/span/a")
    WebElement flexibleCheckbox;

    // Continue Button
    @FindBy(css = "#OnlineTicket_btnOnlineTicketDevam")
    WebElement continueButton;

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
        assertTrue((driver.findElements(By.cssSelector(cssOpenFrom)).size() == 1));
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
        if(driver.findElement(By.cssSelector(cssValueTo)).getAttribute("disabled")==null){
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

    public void setCityFrom(String city){
        //selectCityByScrolling(cssOpenFrom, city);
        selectCityByTyping(true, city);
    }

    public String getCityFrom(){
        //return driver.findElement(By.cssSelector(cssValueFrom)).getAttribute("value");
        return driver.findElement(By.cssSelector(cssValueFrom)).getAttribute("value");
    }

    public void setCityTo(String city){
        selectCityByTyping(false, city);
    }

    public String getCityTo(){
        return driver.findElement(By.cssSelector(cssValueTo)).getAttribute("value");
    }

    /**
     * Emulation of city selection by scrolling the list
     * @param cssSelector
     * @param city
     */
    private void selectCityByScrolling(String cssSelector, String city){
        driver.findElement(By.cssSelector(cssSelector)).click();
        List<String> cities = new ArrayList<String>();
        for(WebElement e : driver.findElements(By.cssSelector(cssCitiesList)))
            cities.add(e.getAttribute("innerHTML"));
        int cityPosition = cities.indexOf(city) + 1;
        if(cityPosition > 0 ){
            Actions builder = new Actions(driver);
            for(int i = 0; i < cityPosition; i++)
                builder.sendKeys(Keys.ARROW_DOWN);
            builder.sendKeys(Keys.ENTER).perform();
        }
    }

    private String xpathToItemToWait = "/html/body/ul[%data%]/li[1]/a";
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
    private void selectCityByTyping(boolean departure, String city){
        String cssSelector = departure?cssValueFrom:cssValueTo;
        WebElement inputElement = driver.findElement(By.cssSelector(cssSelector));
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
        (new WebDriverWait(driver, 5, 500)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return (d.findElement(By.cssSelector(cssValueTo)).getAttribute("disabled") == null);
            }
        });
    }

    public void waitForCityInList(WebDriver driver) {
        (new WebDriverWait(driver, 5, 500)).until(new ExpectedCondition<Boolean>() {
            List<WebElement> e;
            String item;
            public Boolean apply(WebDriver d) {
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
            jquery.Calendar.setDate(driver, date);
        }
    }

    public LocalDate getDepartureDate(){
        return LocalDate.parse(departureDate.getAttribute("value"), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public void setReturnDate(LocalDate date){
        if(getRoundTrip()){
            if(date.compareTo(getDepartureDate()) >= 0){
                driver.findElement(By.cssSelector(cssReturnDateValue)).click();
                jquery.Calendar.setDate(driver, date);
            }
        }
    }

    public LocalDate getReturnDate(){
        return LocalDate.parse(driver.findElement(By.cssSelector(cssReturnDateValue)).getAttribute("value"),
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

    public void pushContinueButton(){
        continueButton.click();
    }
}