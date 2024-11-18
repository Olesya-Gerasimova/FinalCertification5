package ui.pom;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoginPage {
    private final SelenideElement usernameLocator = $("#user-name");
    private final SelenideElement passwordLocator = $("#password");
    private final SelenideElement loginButtonLocator = $("#login-button");
    private final SelenideElement errorLocator = $("[data-test='error']");

    @Step("Логинимся под пользователем {username}")
    public void login(String username, String password) {
        Selenide.open("/");
        usernameLocator.setValue(username);
        passwordLocator.setValue(password);
    }

    @Step("переходим на страницу с выбором вещей")
    public void clickLogin() {
        loginButtonLocator.click();
    }

    @Step("Получаем сообщение об ошибке логина")
    public String getError() {
        return errorLocator.text();
    }
}
