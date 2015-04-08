package environment;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.service.DriverService;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by Dmytro Novikov on 4/5/2015.
 */
public class TestEnvironment {
    public static enum Browser {FIREFOX, CHROME, IE, HTMLUNIT}

    private static final String pathToChromeWebdriver =
            "C:\\Users\\Dmytro\\Documents\\QA\\Tools\\DriversForSelenium\\chromedriver.exe";
    private static final String pathToIEWebdriver =
            "C:\\Users\\Dmytro\\Documents\\QA\\Tools\\DriversForSelenium\\IEDriverServer.exe";

    private static Map<Browser, DriverService> services = new HashMap<Browser, DriverService>();

    public static WebDriver getDriver(Browser browser) throws IOException {
        WebDriver driver = null;
        if(!services.containsKey(browser)){
            // Starting a Driver Service
            switch (browser){
                case CHROME:
                    services.put(Browser.CHROME, new ChromeDriverService.Builder()
                            .usingDriverExecutable(new File(pathToChromeWebdriver))
                            .usingAnyFreePort()
                            .build());
                    services.get(browser).start();
                    break;
                case IE:
                    services.put(Browser.IE, new InternetExplorerDriverService.Builder()
                            .usingDriverExecutable(new File(pathToIEWebdriver))
                            .usingAnyFreePort()
                            .build());
                    services.get(browser).start();
                    break;
            }
        }
        switch(browser){
            case CHROME:
                driver = new RemoteWebDriver(services.get(browser).getUrl(), DesiredCapabilities.chrome());
                break;
            case IE:
                driver = new RemoteWebDriver(services.get(browser).getUrl(), DesiredCapabilities.internetExplorer());
                break;
            case FIREFOX:
                driver = new FirefoxDriver();
                break;
            case HTMLUNIT:
                driver = new HtmlUnitDriver(BrowserVersion.CHROME);
                ((HtmlUnitDriver)driver).setJavascriptEnabled(true);
                break;
        }
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        return driver;
    }

    public static void stopServices(){
        Set<Browser> serviceKeys = services.keySet();
        for(Browser b : serviceKeys)
            services.get(b).stop();
    }
}
