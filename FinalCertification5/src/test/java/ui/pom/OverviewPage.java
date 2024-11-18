package ui.pom;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class OverviewPage {

    private final SelenideElement firstItemLocator = $("#item_4_title_link");
    private final SelenideElement secondItemLocator = $("#item_1_title_link");
    private final SelenideElement thirdItemLocator = $("#item_2_title_link");
    private final SelenideElement totalPriceLocator = $(".summary_total_label");
    private final SelenideElement finishButtonLocator = $("#finish");

    @Step("Проверяем, что на странице оплаты лежат корректные вещи")
    public void checkItemsInOverview() {
        firstItemLocator.shouldHave(text("Sauce Labs Backpack"));
        secondItemLocator.shouldHave(text("Sauce Labs Bolt T-Shirt"));
        thirdItemLocator.shouldHave(text("Sauce Labs Onesie"));
    }

    @Step("Проверяем что итоговая сумма равна $58.29")
    public String getTotalPrice() {
        return totalPriceLocator.text();
    }

    @Step("Нажимаем на кнопку завершения оплаты")
    public void finishOrder() {
        finishButtonLocator.click();
    }
}
