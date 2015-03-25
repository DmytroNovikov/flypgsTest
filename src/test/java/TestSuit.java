import com.flypgs.pages.HomePage;
import com.sun.jna.platform.FileUtils;
import org.junit.*;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.io.IOException;

/**
 *
 * Created by Dmytro Novikov on 3/25/2015.
 *
 */
public class TestSuit {

    private static final String pathToChromeWebdriver =
            "C:\\Users\\Dmytro\\Documents\\QA\\Tools\\DriversForSelenium\\chromedriver.exe";

    private static ChromeDriverService service;
    private WebDriver driver;

    @BeforeClass
    public static void createAndStartService() throws IOException {
        service = new ChromeDriverService.Builder()
                .usingDriverExecutable(new File(pathToChromeWebdriver))
                .usingAnyFreePort()
                .build();
        service.start();
    }

    @AfterClass
    public static void stopService(){
        service.stop();
    }

    @Before
    public void createDriver(){
        driver = new RemoteWebDriver(service.getUrl(), DesiredCapabilities.chrome());
        driver.manage().window().maximize();
    }

    @After
    public void quitDriver(){
        driver.quit();
    }

    @Test
    public void testCase001(){
        HomePage hp = new HomePage(driver);
        hp.get();
        hp.prn();

        File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        try {
            org.apache.commons.io.FileUtils.copyFile(scrFile, new File("C:\\Users\\Dmytro\\Desktop\\scr.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
