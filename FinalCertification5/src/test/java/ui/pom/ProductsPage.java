package ui.pom;

import ui.pom.elements.Header;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProductsPage {

    public final Header header;

    public ProductsPage() {
        this.header = new Header();
    }

    private final SelenideElement pageNameLocator = $(".title");
    private final SelenideElement backpackLocator = $("#add-to-cart-sauce-labs-backpack");
    private final SelenideElement boltTshirtLocator = $("#add-to-cart-sauce-labs-bolt-t-shirt");
    private final SelenideElement onesieLocator = $("#add-to-cart-sauce-labs-onesie");

    @Step("Добавляем товары в корзину")
    public void addToCart() {
        backpackLocator.click();
        boltTshirtLocator.click();
        onesieLocator.click();
    }

    @Step("Подтверждаем, что текущая страница - продуктовая")
    public void actuallyProductsPage() {
        assertEquals("Products", pageNameLocator.text());
    }
}
