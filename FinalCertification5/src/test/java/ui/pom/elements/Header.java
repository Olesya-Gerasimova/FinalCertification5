package ui.pom.elements;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.$;

public class Header {

    private final SelenideElement cartButtonLocator = $(".shopping_cart_link");

    @Step("Кликаем и переходим в корзину")
    public void clickToCart() {
        cartButtonLocator.click();
    }
}
