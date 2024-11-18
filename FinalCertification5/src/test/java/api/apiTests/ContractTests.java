package api.apiTests;

import api.apiHelper.EmployeeHelper;
import api.apiModels.AuthResponse;
import ui.jpa.PUI;
import ui.jpa.entity.CompanyEntity;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.spi.PersistenceUnitInfo;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.junit.jupiter.api.*;

import java.io.IOException;

import static properties.GetProperties.getProperties;
import static properties.GetProperties.getProperty;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@DisplayName("ContractTests сервиса x-clients")
public class ContractTests {
    EmployeeHelper helper;
    AuthResponse auth = EmployeeHelper.auth(getProperty("app_user.login"), getProperty("app_user.password"));
    private static EntityManager entityManager;
    int companyId;

    @BeforeAll
    public static void setUp() throws IOException {
        RestAssured.baseURI = getProperty("ui.url");
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());

        PersistenceUnitInfo pui = new PUI(getProperties());

        HibernatePersistenceProvider hibernatePersistenceProvider = new HibernatePersistenceProvider();
        EntityManagerFactory emf = hibernatePersistenceProvider.createContainerEntityManagerFactory(pui, pui.getProperties());
        entityManager = emf.createEntityManager();
    }

    @BeforeEach
    public void setUpB() {
        helper = new EmployeeHelper();
        CompanyEntity company = new CompanyEntity();
        company.setName("Тестовая компания");
        company.setDescription("Тестовое описание322");
        company.setActive(true);

        entityManager.getTransaction().begin();
        entityManager.persist(company);
        entityManager.getTransaction().commit();
        companyId = company.getId();
    }

    @AfterEach
    public void tearDown() {
        entityManager.getTransaction().begin();
        entityManager.createQuery("DELETE FROM EmployeeEntity ee WHERE ee.companyId = :companyId").setParameter("companyId", companyId).executeUpdate();
        entityManager.createQuery("DELETE FROM CompanyEntity ce WHERE ce.id = :companyId").setParameter("companyId", companyId).executeUpdate();
        entityManager.getTransaction().commit();
    }

    @Test
    @DisplayName("При запросе списка сотрудников статус-код 200 и Content-Type JSON")
    public void status200OnGetEmployees() {
        step("Проверяем, что статус код и content-type валидны", () -> given()
                .log().all()
                .basePath("employee")
                .contentType(ContentType.JSON)
                .queryParam("company", companyId)
                .when()
                .get()
                .then()
                .statusCode(200)
                .contentType("application/json; charset=utf-8"));
    }

    @Test
    @DisplayName("Проверка авторизации через токен")
    public void iCanAuth() {
        step("Проверяем, что в ответе получили токен", () -> given()
                .basePath("auth/login")
                .body(getProperty("app_user.login"))
                .body("{\n\"username\": \"" + getProperty("app_user.login") + "\",\n\"password\": \"" + getProperty("app_user.password") + "\"\n}")
                .contentType(ContentType.JSON)
                .when()
                .post()
                .then()
                .statusCode(201)
                .contentType("application/json; charset=utf-8 ")
                .body("userToken", is(not(blankString()))));
    }

    @Test
    @DisplayName("При создании сотрудника статус-код 201 и Content-Type JSON")
    public void iCanCreateANewEmployee() {
        step("Проверяем, статус код и content-type валидны", () -> given()
                .basePath("employee")
                .header("x-client-token", auth.userToken())
                .body(helper.createFullEmployee(companyId))
                .contentType(ContentType.JSON)
                .when()
                .post()
                .then()
                .statusCode(201)
                .contentType("application/json; charset=utf-8 "));
    }

    @Test
    @DisplayName("При получении сотрудника по id статус-код 200 и Content-Type JSON")
    public void iCanGetEmployeeById() {
        step("Проверяем, статус код и content-type валидны", () -> given()
                .basePath("employee")
                .contentType(ContentType.JSON)
                .when()
                .get("{id}", helper.createNewEmployee(companyId, auth.userToken()))
                .then()
                .statusCode(200)
                .contentType("application/json; charset=utf-8 "));
    }

    @Test
    @DisplayName("При изменении информации о сотруднике статус-код 200 и Content-Type JSON")
    public void iCanChangeEmployeeInfo() {
        step("Проверяем, статус код и content-type валидны", () -> given()
                .basePath("employee")
                .header("x-client-token", auth.userToken())
                .contentType(ContentType.JSON)
                .body(helper.randomEmployee(companyId))
                .when()
                .patch("{id}", helper.createNewEmployee(companyId, auth.userToken()))
                .then()
                .statusCode(200)
                .contentType("application/json; charset=utf-8 "));
    }

    @Test
    @DisplayName("При получении сотрудника несуществующей компании статус-код 200 и Content-Type JSON")
    public void getListOfEmployeeNotExistedCompany() {
        step("Проверяем, статус код и content-type валидны", () -> given()
                .log().all()
                .basePath("employee")
                .contentType(ContentType.JSON)
                .queryParam("company", 1 - companyId)
                .when()
                .get()
                .then()
                .statusCode(200)
                .contentType("application/json; charset=utf-8"));
    }

    @Test
    @DisplayName("При создании сотрудника в несуществующей компании статус-код 500 и Content-Type JSON")
    public void createEmployeeNotExistedCompany() {
        step("Проверяем, статус код и content-type валидны", () -> given()
                .basePath("employee")
                .header("x-client-token", auth.userToken())
                .body(helper.createFullEmployee(1 - companyId))
                .contentType(ContentType.JSON)
                .when()
                .post()
                .then()
                .statusCode(500)
                .contentType("application/json; charset=utf-8 "));
    }

    @Test
    @DisplayName("При создании сотрудника без авторизационного токена статус-код 401 и Content-Type JSON")
    public void createEmployeeWithoutToken() {
        step("Проверяем, статус код и content-type валидны", () -> given()
                .basePath("employee")
                .body(helper.createFullEmployee(companyId))
                .contentType(ContentType.JSON)
                .when()
                .post()
                .then()
                .statusCode(401)
                .contentType("application/json; charset=utf-8 "));
    }

    @Test
    @DisplayName("При запросе несуществующего сотрудника статус-код 404 и Content-Type JSON")
    public void getEmployeeWithNotExistedId() {
        step("Проверяем, статус код и content-type валидны", () -> given()
                .basePath("employee")
                .contentType(ContentType.JSON)
                .when()
                .get("{id}", 1 - companyId)
                .then()
                .statusCode(404)
                .contentType("application/json; charset=utf-8 "));
    }

    @Test
    @DisplayName("При изменении информации о несуществующем сотруднике статус-код 404 и Content-Type JSON")
    public void changeEmployeeInfoNotExistedId() {
        step("Проверяем, статус код и content-type валидны", () -> given()
                .basePath("employee")
                .header("x-client-token", auth.userToken())
                .contentType(ContentType.JSON)
                .body(helper.randomEmployee(companyId))
                .when()
                .patch("{id}", 1 - companyId)
                .then()
                .statusCode(404)
                .contentType("application/json; charset=utf-8 "));
    }

    @Test
    @DisplayName("При изменении всей информации о сотруднике статус-код 200 и Content-Type JSON")
    public void changeFullEmployeeInfo() {
        step("Проверяем, статус код и content-type валидны", () -> given()
                .basePath("employee")
                .header("x-client-token", auth.userToken())
                .contentType(ContentType.JSON)
                .body(helper.createFullEmployee(companyId))
                .when()
                .patch("{id}", helper.createNewEmployee(companyId, auth.userToken()))
                .then()
                .statusCode(200)
                .contentType("application/json; charset=utf-8 "));
    }
}