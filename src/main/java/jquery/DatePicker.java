package jquery;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.LocalDate;
import java.util.*;

/**
 * Created by Dmytro Novikov on 4/2/2015.
 *
 * Based on solution by Aravind G
 * http://software-testing-tutorials-automation.blogspot.com/2014/10/how-to-select-date-in-selenium.html
 */
public class DatePicker{

    private static Map<String, List<String>> monthListMap;
    static{
        monthListMap = new HashMap<String, List<String>>();
        monthListMap.put("Türkçe",
                Arrays.asList("Oca", "?ub", "Mar", "Nis", "May", "Haz", "Tem", "A?u", "Eyl", "Eki", "Kas"));
        monthListMap.put("English",
                Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov"));
        monthListMap.put("Deutsch",
                Arrays.asList("Jan", "Feb", "Mrz", "Apr", "Mai", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov"));
        monthListMap.put("Français",
                Arrays.asList("janv.", "f?vr.", "mars", "avr.", "mai", "juin", "juil.", "ao?t", "sept.", "oct.", "nov."));
        monthListMap.put("Русский",
                Arrays.asList("янв", "фев", "мар", "апр", "май", "июн", "июл", "авг", "сен", "окт", "ноя"));
        monthListMap.put("Nederlands",
                Arrays.asList("jan", "feb", "mrt", "apr", "mei", "jun", "jul", "aug", "sep", "okt", "nov"));
        monthListMap.put("Dansk",
                Arrays.asList("jan", "feb", "mar", "apr", "maj", "jun", "jul", "aug", "sep", "okt", "nov"));
        monthListMap.put("Italiano",
                Arrays.asList("gen", "feb", "mar", "apr", "mag", "giu", "lug", "ago", "set", "ott", "nov"));
        monthListMap.put("Svenska",
                Arrays.asList("jan", "feb", "mar", "apr", "maj", "jun", "jul", "aug", "sep", "okt", "nov"));
        monthListMap = Collections.unmodifiableMap(monthListMap);
    }

    public static void setDate(WebDriver driver, LocalDate date, String language){
        boolean dateNotFound = true;
        String calMonth, calYear, calDay;
        //This loop will be executed continuously till dateNotFound Is true.
        while (dateNotFound){
            //Retrieve current selected month name from date picker popup.
            calMonth = driver.findElement(By.className("ui-datepicker-month")).getText();
            //Retrieve current selected year name from date picker popup.
            calYear = driver.findElement(By.className("ui-datepicker-year")).getText();
            //If current selected month and year are same as expected month and year then go Inside this condition.
            if (getMonthIndex(calMonth, language) == date.getMonthValue()
                    && date.getYear() == Integer.parseInt(calYear)){
                //Call selectDate function with date to select and set dateNotFound flag to false.
                WebElement datePicker = driver.findElement(By.id("ui-datepicker-div"));
                List<WebElement> noOfColumns = datePicker.findElements(By.tagName("td"));
                String day = String.valueOf(date.getDayOfMonth());
                //Loop will rotate till expected date not found.
                for (WebElement cell : noOfColumns){
                    //Select the date from date picker when condition match.
                    if (cell.getText().equals(day)){
                        cell.findElement(By.linkText(day)).click();
                        break;
                    }
                }
                dateNotFound = false;
            }
            //If current selected month and year are less than expected month and year then go Inside this condition.
            else if (getMonthIndex(calMonth, language) < date.getMonthValue()
                    && (date.getYear() >= Integer.parseInt(calYear))){
                //Click on next button of date picker.
                driver.findElement(By.xpath(".//*[@id='ui-datepicker-div']/div/a[2]/span")).click();
            }
            //If current selected month and year are greater than expected month and year then go Inside this condition.
            else if (getMonthIndex(calMonth, language) > date.getMonthValue()
                    && (date.getYear() <= Integer.parseInt(calYear))){
                //Click on previous button of date picker.
                driver.findElement(By.xpath(".//*[@id='ui-datepicker-div']/div/a[1]/span")).click();
            }
        }
    }

    private static int getMonthIndex(String month, String language){
        return (monthListMap.get(language).indexOf(month) + 1);
    }
}