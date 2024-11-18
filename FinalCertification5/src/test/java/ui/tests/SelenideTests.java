package ui.tests;

import ui.pom.*;
import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.*;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static properties.GetProperties.getProperty;

@DisplayName("Тесты сайта saucedemo.com")
public class SelenideTests {

    LoginPage loginPage;
    ProductsPage productsPage;
    CartPage cartPage;
    CheckoutPage checkoutPage;
    CompletePage completePage;
    OverviewPage overviewPage;


    @BeforeAll
    public static void setUpGlobal() throws IOException {
        Configuration.baseUrl = getProperty("URL");
        Configuration.headless = true;
    }

    @BeforeEach
    public void setUp() {
        loginPage = new LoginPage();
        productsPage = new ProductsPage();
        cartPage = new CartPage();
        checkoutPage = new CheckoutPage();
        completePage = new CompletePage();
        overviewPage = new OverviewPage();
    }

    @Test
    @DisplayName("Авторизация валидного юзера")
    @Tag("positive")
    public void validUserLogin() {
        loginPage.login(getProperty("STANDARD_USERNAME"), getProperty("PASSWORD"));
        loginPage.clickLogin();
        productsPage.actuallyProductsPage();
    }

    @Test
    @DisplayName("Авторизация заблокированного юзера")
    @Tag("negative")
    public void blockedUserLogin() {
        String errorTextToBe = "Epic sadface: Sorry, this user has been locked out.";
        loginPage.login(getProperty("LOCKED_USERNAME"), getProperty("PASSWORD"));
        loginPage.clickLogin();
        assertEquals(errorTextToBe, loginPage.getError());
    }

    @Test
    @DisplayName("Сквозной сценарий пользователя standart_user")
    @Tag("positive")
    public void e2eStandardUser() {
        String totalPriceText = "Total: $58.29";
        String finalMessage = "Thank you for your order!";

        loginPage.login(getProperty("STANDARD_USERNAME"), getProperty("PASSWORD"));
        loginPage.clickLogin();
        productsPage.addToCart();
        productsPage.header.clickToCart();
        cartPage.itemsInCartShouldBe(3);
        cartPage.checkout();
        checkoutPage.insertDataForm(getProperty("FIRST_NAME"), getProperty("LAST_NAME"), getProperty("POSTAL_CODE"));
        checkoutPage.clickContinue();
        overviewPage.checkItemsInOverview();
        assertEquals(totalPriceText, overviewPage.getTotalPrice());
        overviewPage.finishOrder();
        assertEquals(finalMessage, completePage.getCompleteMessage());
    }

    @Test
    @DisplayName("Сквозной сценарий пользователя performance_glitch_user")
    @Tag("positive")
    public void e2ePerformanceGlitchUser() {
        String totalPriceText = "Total: $58.29";
        String finalMessage = "Thank you for your order!";

        loginPage.login(getProperty("PERFORMANCE_GLITCH_USERNAME"), getProperty("PASSWORD"));
        loginPage.clickLogin();
        productsPage.addToCart();
        productsPage.header.clickToCart();
        cartPage.itemsInCartShouldBe(3);
        cartPage.checkout();
        checkoutPage.insertDataForm(getProperty("FIRST_NAME"), getProperty("LAST_NAME"), getProperty("POSTAL_CODE"));
        checkoutPage.clickContinue();
        overviewPage.checkItemsInOverview();
        assertEquals(totalPriceText, overviewPage.getTotalPrice());
        overviewPage.finishOrder();
        assertEquals(finalMessage, completePage.getCompleteMessage());
    }
}
