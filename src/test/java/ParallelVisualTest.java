import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.StdoutLogHandler;
import com.applitools.eyes.appium.Eyes;
import io.appium.java_client.MobileBy;
import io.appium.java_client.android.AndroidDriver;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.zeroturnaround.exec.ProcessExecutor;

class ParallelVisualTest {

    private AndroidDriver driver;
    private Eyes eyes;
    private final static By LOGIN_SCREEN = MobileBy.AccessibilityId("Login Screen");
    private final static By USERNAME_FIELD = MobileBy.AccessibilityId("username");
    private final static String CHECK_HOME = "home_screen";
    private final static String CHECK_LOGIN = "login_screen";
    private static BatchInfo info;

    ParallelVisualTest() {
        info = new BatchInfo("webinar batch");
    }

    private void setUp(String udid, int systemPort) throws Exception {
        URL server = new URL("http://localhost:4723/wd/hub");
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("platformName", "Android");
        caps.setCapability("deviceName", "Android Emulator");
        caps.setCapability("automationName", "UiAutomator2");
        caps.setCapability("udid", udid);
        caps.setCapability("adbExecTimeout", 30000);
        caps.setCapability("systemPort", systemPort);
        caps.setCapability("appWaitActivity", "com.reactnativenavigation.controllers.NavigationActivity");
        caps.setCapability("app", getResource("apps/TheApp-v1.apk").toString());
        // make sure we uninstall the app before each test regardless of version
        caps.setCapability("uninstallOtherPackages", "io.cloudgrey.the_app");
        driver = new AndroidDriver(server, caps);
        eyes = new Eyes();
        eyes.setLogHandler(new StdoutLogHandler());
        eyes.setBatch(info);
        eyes.setApiKey(System.getenv("APPLITOOLS_API_KEY"));
    }

    @ParameterizedTest
    @MethodSource("getUdidsAndPorts")
    void testAppDesign(String udid, int systemPort) throws Exception {
        setUp(udid, systemPort);
        try {
            eyes.open(driver, "TheApp", "[applitools+genymotion webinar] basic design test");

            WebDriverWait wait = new WebDriverWait(driver, 5);

            // wait for an element that's on the home screen
            WebElement loginScreen = waitForElement(wait, LOGIN_SCREEN);

            // now we know the home screen is loaded, so do a visual check
            eyes.checkWindow(CHECK_HOME);

            // nav to the login screen, and wait for an element that's on the login screen
            loginScreen.click();
            waitForElement(wait, USERNAME_FIELD);

            // perform our second visual check, this time of the login screen
            eyes.checkWindow(CHECK_LOGIN);
        } finally {
            if (driver != null) {
                driver.quit();
            }
            eyes.close();
        }
    }

    private static ArrayList<Arguments> getUdidsAndPorts() throws Exception {
        String output = new ProcessExecutor()
            .command("adb", "devices")
            .readOutput(true).execute()
            .outputUTF8();
        Pattern devicePattern = Pattern.compile("^([^ ]+?)\tdevice$", Pattern.DOTALL | Pattern.MULTILINE);
        Matcher matcher = devicePattern.matcher(output);
        ArrayList<Arguments> args = new ArrayList<>();
        int port = 8200;
        while (matcher.find()) {
            args.add(Arguments.of(matcher.group(1), port));
            port += 1;
        }
        if (args.size() < 1) {
            throw new Exception("Did not find any connected devices!");
        }
        return args;
    }

    private WebElement waitForElement(WebDriverWait wait, By selector) {
        WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(selector));
        try { Thread.sleep(750); } catch (InterruptedException ign) {}
        return el;
    }

    private synchronized Path getResource(String fileName) throws URISyntaxException {
        URL refImgUrl = getClass().getClassLoader().getResource(fileName);
        return Paths.get(refImgUrl.toURI()).toFile().toPath();
    }
}
