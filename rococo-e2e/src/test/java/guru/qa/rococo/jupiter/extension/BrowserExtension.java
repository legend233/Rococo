package guru.qa.rococo.jupiter.extension;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.Allure;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.extension.*;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;

import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;

public class BrowserExtension implements
        BeforeEachCallback,
        AfterEachCallback,
        TestExecutionExceptionHandler,
        LifecycleMethodExecutionExceptionHandler {

    static {
        String browser = System.getenv("BROWSER");
        Configuration.browser = "firefox".equals(browser) ? "firefox" : "chrome";
        Configuration.timeout = 8000;
        Configuration.pageLoadStrategy = "eager";
        if ("docker".equals(System.getProperty("test.env"))) {
            Configuration.remote = "http://selenoid:4444/wd/hub";
            if (browser.equals("firefox")) {
                Configuration.browserVersion = "125.0";
                Configuration.browserCapabilities = new FirefoxOptions().addPreference("intl.accept_languages", "ru");
            } else {
                Configuration.browserVersion = "127.0";
                Configuration.browserCapabilities = new ChromeOptions().addArguments("--no-sandbox");
            }
        }
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        if (WebDriverRunner.hasWebDriverStarted()) {
            Selenide.closeWebDriver();
        }
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        SelenideLogger.addListener("Allure-selenide", new AllureSelenide()
                .savePageSource(false)
                .screenshots(false)
        );
        Allure.addAttachment("Browser", "text/plain", Configuration.browser + " " + Configuration.browserVersion);
    }

    @Override
    public void handleTestExecutionException(@Nonnull ExtensionContext context,
                                             @Nonnull Throwable throwable) throws Throwable {
        doScreenshot();
        throw throwable;
    }

    @Override
    public void handleBeforeEachMethodExecutionException(@Nonnull ExtensionContext context,
                                                         @Nonnull Throwable throwable) throws Throwable {
        doScreenshot();
        throw throwable;
    }

    @Override
    public void handleAfterEachMethodExecutionException(@Nonnull ExtensionContext context,
                                                        @Nonnull Throwable throwable) throws Throwable {
        doScreenshot();
        throw throwable;
    }

    private static void doScreenshot() {
        if (WebDriverRunner.hasWebDriverStarted()) {
            Allure.addAttachment(
                    "Screen on fail",
                    new ByteArrayInputStream(
                            ((TakesScreenshot) WebDriverRunner.getWebDriver()).getScreenshotAs(OutputType.BYTES)
                    )
            );
        }
    }
}
