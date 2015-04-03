import com.flypgs.pages.HomePage;
import com.flypgs.pages.OnlineTicketsTab;
import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.service.DriverService;

import java.io.*;
import java.time.LocalDate;
import java.time.Month;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 * Created by Dmytro Novikov on 3/25/2015.
 *
 */
public class TestSuit {

    private enum Browser {FIREFOX, CHROME, IE}

    private static Browser currentBrowser = Browser.CHROME;

    private static final String pathToChromeWebdriver =
            "C:\\Users\\Dmytro\\Documents\\QA\\Tools\\DriversForSelenium\\chromedriver.exe";
    private static final String pathToIEWebdriver =
            "C:\\Users\\Dmytro\\Documents\\QA\\Tools\\DriversForSelenium\\IEDriverServer.exe";

    private static DriverService service;
    private WebDriver driver;

    @BeforeClass
    public static void createAndStartService() throws IOException {
        switch (currentBrowser){
            case CHROME:
                service = new ChromeDriverService.Builder()
                        .usingDriverExecutable(new File(pathToChromeWebdriver))
                        .usingAnyFreePort()
                        .build();
                service.start();
                break;
            case IE:
                service = new InternetExplorerDriverService.Builder()
                        .usingDriverExecutable(new File(pathToIEWebdriver))
                        .usingAnyFreePort()
                        .build();
                service.start();
                break;
            case FIREFOX:
                break;
            default:

        }
    }

    @AfterClass
    public static void stopService(){
        switch (currentBrowser){
            case CHROME:
            case IE:
                service.stop();
        }
    }

    @Before
    public void createDriver(){
        switch(currentBrowser){
            case CHROME:
                driver = new RemoteWebDriver(service.getUrl(), DesiredCapabilities.chrome());
                break;
            case FIREFOX:
                driver = new FirefoxDriver();
                break;
            case IE:
                driver = new RemoteWebDriver(service.getUrl(), DesiredCapabilities.internetExplorer());
                break;
            default:
                driver = new FirefoxDriver();
        }
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }

    @After
    public void quitDriver(){
        driver.quit();
    }

    @Test
    public void testCase001(){
        HomePage hp = new HomePage(driver);
        hp.get();
        // Türkçe English Deutsch Français Русский Nederlands Dansk Italiano Svenska
        String language = "English";
        if(!hp.getCurrentLanguage().equals(language)){
            assertTrue(language + " is not in the list of the site's languages.",
                    hp.getLanguages().contains(language));
            hp.setCurrentLanguage(language);
            assertTrue("Can't set " + language + " as a site's language.",
                    hp.getCurrentLanguage().contains(language));
        }
        OnlineTicketsTab ott = new OnlineTicketsTab(driver);
        ott.setCityFrom("Lviv");
        assertEquals("Lviv", ott.getCityFrom());
        ott.setCityTo("Amsterdam");
        assertEquals("Amsterdam", ott.getCityTo());
        ott.setRoundTrip(true);
        assertEquals(true, ott.getRoundTrip());
        LocalDate departureDate = LocalDate.of(2015, Month.APRIL, 15);
        ott.setDepartureDate(departureDate);
        assertEquals(departureDate, ott.getDepartureDate());
        LocalDate returnDate = LocalDate.of(2015, Month.APRIL, 19);
        ott.setReturnDate(returnDate);
        assertEquals(returnDate, ott.getReturnDate());
        ott.setAdultsCount(3);
        assertEquals(3, ott.getAdultsCount());
        ott.setChildrenCount(2);
        assertEquals(2, ott.getChildrenCount());
        ott.setInfantsCount(1);
        assertEquals(1, ott.getInfantsCount());
        ott.setFlexible(true);
        assertEquals(true, ott.getFlexible());
        assertEquals("OK", ott.pushContinueButton());
/*
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
*/
    }
}