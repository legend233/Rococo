package guru.qa.rococo.page.modal;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.page.BasePage;
import guru.qa.rococo.page.MainPage;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byTagAndText;
import static com.codeborne.selenide.Selenide.$;

public class ProfileModal extends BasePage<ProfileModal> {

    private final SelenideElement pageTitle = $(byTagAndText("header", "Профиль"));
    private final SelenideElement logoutBtn = $(byTagAndText("button", "Выйти"));


    @Override
    public ProfileModal checkThatPageLoaded() {
        pageTitle.shouldBe(visible);
        return this;
    }

    @Step("Logout")
    public MainPage logout() {
        logoutBtn.click();
        return new MainPage();
    }
}
