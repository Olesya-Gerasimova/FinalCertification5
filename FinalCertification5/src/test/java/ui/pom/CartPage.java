package ui.pom;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CartPage {

    private final ElementsCollection itemsLocator = $$(".inventory_item_name");
    private final SelenideElement checkoutLocator = $("#checkout");

    @Step("Проверяем, что в корзине лежит {i} товара")
    public void itemsInCartShouldBe(int i) {
        itemsLocator.shouldHave(size(i));
        assertEquals(i, itemsLocator.size());
    }

    @Step("Переходим к оплате")
    public void checkout() {
        checkoutLocator.click();
    }
}
