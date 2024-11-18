package api.apiHelper;

import api.apiModels.*;
import com.github.javafaker.Faker;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;

import java.util.Locale;

import static io.restassured.RestAssured.given;

public class EmployeeHelper {
    static Locale locale = new Locale("ru");
    static Faker faker = new Faker(locale);

    @Step("Логинимся в сервис")
    public static AuthResponse auth(String username, String password) {
        AuthRequest authRequest = new AuthRequest(username, password);

        return given()
                .basePath("auth/login")
                .body(authRequest)
                .contentType(ContentType.JSON)
                .when()
                .post()
                .as(AuthResponse.class);
    }

    @Step("Создание нового сотрудника")
    public int createNewEmployee(int companyId, String token) {
        Employee testEmployee = new Employee(faker.name().firstName(), faker.name().lastName(), faker.name().lastName(), companyId, faker.internet().emailAddress(), faker.phoneNumber().cellPhone(), faker.bool().bool());

        return given()
                .basePath("employee")
                .body(testEmployee)
                .header("x-client-token", token)
                .contentType(ContentType.JSON)
                .when()
                .post()
                .as(EmployeeResponse.class).id();
    }

    @Step("Создание сотрудника")
    public RandomEmployee randomEmployee(int companyId) {
        return new RandomEmployee(faker.name().lastName(), faker.internet().emailAddress(), faker.company().url(), faker.phoneNumber().cellPhone(), faker.bool().bool());
    }

    @Step("Создание сотрудника с заполненными данными")
    public FullEmployee createFullEmployee(int companyId) {
        return new FullEmployee(faker.number().numberBetween(0,15), faker.name().firstName(), faker.name().lastName(), faker.name().lastName(), companyId, faker.internet().emailAddress(), faker.company().url(), faker.phoneNumber().cellPhone(), faker.bool().bool());
    }
}