package onlineticketsystem;

import com.flypgs.pages.HomePage;
import com.flypgs.pages.OnlineTicketsTab;
import environment.TestEnvironment;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.WebDriver;

import java.io.*;
import java.util.*;

import static org.junit.Assert.assertTrue;

/**
 * Created by Dmytro Novikov on 4/6/2015.
 */
@RunWith(value = Parameterized.class)
public class TC0DDT {

    private WebDriver driver;
    private static final float coverage = 0.001f;
    private String from, to;


    @Parameterized.Parameters
    public static Collection routsData() throws IOException {
        File csvFile = new File("./data/connected_cities_en.csv");
        BufferedReader reader = new BufferedReader(new FileReader(csvFile));
        String line;
        List<Object> data = new ArrayList<Object>();
        while((line=reader.readLine()) != null) {
            String[] lines = line.split(",");
            data.add(new Object[]{lines[0].trim(), lines[1].trim()});
        }
        if(coverage != 1.0f){
            // If the test coverage is not equal to 100% we randomly select the number of input parameters
            // according to the coverage percentage defined in 'coverage' varible.
            List<Object> temp = new ArrayList<Object>();
            int size = data.size();
            int newSize = (int) (size * coverage);
            List<Integer> indexes = new ArrayList<Integer>();
            Random rnd = new Random();
            while(indexes.size() < newSize) {
                int i = rnd.nextInt(size);
                if (!indexes.contains(i)) {
                    indexes.add(i);
                    temp.add(data.get(i));
                }
            }
            data = temp;
        }
        return data;
    }

    public TC0DDT(String from, String to){
        this.from = from;
        this.to = to;
    }

    @Before
    public void getDriver() throws IOException {
        driver = TestEnvironment.getDriver(TestEnvironment.Browser.CHROME);
        HomePage homePage = new HomePage(driver);
        homePage.get();
        // T?rk?e English Deutsch Fran?ais Русский Nederlands Dansk Italiano Svenska
        String language = "English";
        if(!homePage.getCurrentLanguage().equals(language)){
            assertTrue(language + " is not in the list of the site's languages.",
                    homePage.getLanguages().contains(language));
            homePage.setCurrentLanguage(language);
            assertTrue("Can't set " + language + " as a site's language.",
                    homePage.getCurrentLanguage().contains(language));
        }
    }

    @After
    public void quitDriver(){
        driver.quit();
    }

    @AfterClass
    public static void cleanEnvironment(){
        //TestEnvironment.stopServices();
    }

    @Test
    public void testCase(){
        OnlineTicketsTab onlineTicketsTab = new OnlineTicketsTab(driver);
        onlineTicketsTab.get();
        onlineTicketsTab.setTheRoute(from, to);
    }
}
