package guru.qa.rococo.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.page.MainPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@WebTest
@Tag("web")
@DisplayName("WEB | Navigation tests")
public class NavigateWebTest {

    @Test
    @DisplayName("General navigation test")
    public void generalNavigationTest() {
        Selenide.open(MainPage.URL, MainPage.class)
                .getHeader()
                .toPaintingPage()
                .checkThatPageLoaded()
                .getHeader()
                .toArtistPage()
                .checkThatPageLoaded()
                .getHeader()
                .toMuseumPage()
                .checkThatPageLoaded()
                .getHeader()
                .toMainPage()
                .checkThatPageLoaded();
    }

    @Test
    @DisplayName("Toggle dark mode test")
    public void toggleDarkModeTest() {
        Selenide.open(MainPage.URL, MainPage.class)
                .checkThatPageInLightMode()
                .getHeader()
                .checkLogoOnLightMode()
                .getHeader()
                .switchLight()
                .checkLogoOnDarkMode()
                .checkThatPageInDarkMode();
    }
}
