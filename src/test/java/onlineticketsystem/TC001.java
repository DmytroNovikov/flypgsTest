package onlineticketsystem;

import com.flypgs.pages.HomePage;
import com.flypgs.pages.OnlineTicketsTab;
import environment.TestEnvironment;
import org.junit.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.time.LocalDate;

/**
 * Created by Dmytro Novikov on 4/5/2015.
 */
public class TC001 {

    private WebDriver driver;

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
    public void testCase001(){
        OnlineTicketsTab onlineTicketsTab = new OnlineTicketsTab(driver);
        onlineTicketsTab.get();
        assertEquals("OK", onlineTicketsTab.buyOneWayTickets("Lviv", "Amsterdam",
                LocalDate.now(), 2, 1, 1, true));
    }

    @Test
    public void testCase002(){
        OnlineTicketsTab onlineTicketsTab = new OnlineTicketsTab(driver);
        onlineTicketsTab.get();
        assertNotEquals("OK", onlineTicketsTab.buyOneWayTickets("Lviv", "Amsterdam", LocalDate.now(), 1, 1, 2, true));
    }

    @Test
    public void testCase003(){
        OnlineTicketsTab onlineTicketsTab = new OnlineTicketsTab(driver);
        onlineTicketsTab.get();
        assertNotEquals("OK", onlineTicketsTab.buyOneWayTickets("Lviv", "Amsterdam", LocalDate.now(), 0, 0, 0, true));
    }
}
