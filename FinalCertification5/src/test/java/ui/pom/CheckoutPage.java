package ui.pom;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.$;

public class CheckoutPage {

    private final SelenideElement firstNameLocator = $("#first-name");
    private final SelenideElement lastNameLocator = $("#last-name");
    private final SelenideElement postalCodeLocator = $("#postal-code");
    private final SelenideElement continueButtonLocator = $("#continue");


    @Step("Заполняем данные об оплате")
    public void insertDataForm(String firstName, String lastName, String postalCode) {
        firstNameLocator.setValue(firstName);
        lastNameLocator.setValue(lastName);
        postalCodeLocator.setValue(postalCode);
    }

    @Step("переходим на страницу просмотра заказа")
    public void clickContinue() {
        continueButtonLocator.click();
    }
}
