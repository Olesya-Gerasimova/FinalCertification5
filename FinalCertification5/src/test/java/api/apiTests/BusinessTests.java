package api.apiTests;

import api.apiHelper.EmployeeHelper;
import api.apiModels.AuthResponse;
import api.apiModels.FullEmployee;
import api.apiModels.RandomEmployee;
import ui.jpa.PUI;
import ui.jpa.entity.CompanyEntity;
import ui.jpa.entity.EmployeeEntity;
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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@DisplayName("Бизнес тесты x-clients")
public class BusinessTests {
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
        company.setDescription("Тестовое описание");
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
    @DisplayName("Получение списка сотрудников компании")
    public void iCanGetListOfEmployee() {
        EmployeeEntity testEmployee = entityManager.find(EmployeeEntity.class, helper.createNewEmployee(companyId, auth.userToken()));
        EmployeeEntity secondEmployee = entityManager.find(EmployeeEntity.class, helper.createNewEmployee(companyId, auth.userToken()));


        step("Проверка того, что оба проверяемых сотрудника присутствуют в компании", () -> given()
                .basePath("employee")
                .contentType(ContentType.JSON)
                .queryParam("company", companyId)
                .when()
                .get()
                .then()
                .body("firstName", hasItem(testEmployee.getFirstName()))
                .body("firstName", hasItem(secondEmployee.getFirstName()))
                .body("lastName", hasItem(testEmployee.getLastName()))
                .body("lastName", hasItem(secondEmployee.getLastName()))
                .body("middleName", hasItem(testEmployee.getMiddleName()))
                .body("middleName", hasItem(secondEmployee.getMiddleName()))
                .body("birthdate", hasItem(testEmployee.getBirthdate()))
                .body("birthdate", hasItem(secondEmployee.getBirthdate()))
                .body("phone", hasItem(testEmployee.getPhone()))
                .body("phone", hasItem(secondEmployee.getPhone()))
                .body("isActive", hasItem(testEmployee.isActive()))
                .body("isActive", hasItem(secondEmployee.isActive()))
                .body("avatar_url", hasItem(testEmployee.getUrl()))
                .body("avatar_url", hasItem(secondEmployee.getUrl()))
                .body("email", hasItem(testEmployee.getEmail()))
                .body("email", hasItem(secondEmployee.getEmail()))
                .body("id", hasItem(testEmployee.getId()))
                .body("id", hasItem(secondEmployee.getId())));
    }

    @Test
    @DisplayName("Создание сотрудника")
    public void iCanCreateEmployee() {
        FullEmployee testEmployee = helper.createFullEmployee(companyId);

        int testId = given()
                .basePath("employee")
                .header("x-client-token", auth.userToken())
                .body(testEmployee)
                .contentType(ContentType.JSON)
                .when()
                .post()
                .as(FullEmployee.class).id();

        EmployeeEntity assertEmployee = entityManager.find(EmployeeEntity.class, testId);

        step("Проверка, что у созданного сотрудника валидное имя", () -> assertEquals(testEmployee.firstName(), assertEmployee.getFirstName()));
        step("Проверка, что у созданного сотрудника валидная фамилия", () -> assertEquals(testEmployee.lastName(), assertEmployee.getLastName()));
        step("Проверка, что у созданного сотрудника валидное отчество", () -> assertEquals(testEmployee.middleName(), assertEmployee.getMiddleName()));
        step("Проверка, что у созданного сотрудника валидное id компании", () -> assertEquals(testEmployee.companyId(), assertEmployee.getCompanyId()));
        step("Проверка, что у созданного сотрудника валидный email", () -> assertEquals(testEmployee.email(), assertEmployee.getEmail()));
        step("Проверка, что у созданного сотрудника валидный сайт", () -> assertEquals(testEmployee.url(), assertEmployee.getUrl()));
        step("Проверка, что у созданного сотрудника валидный телефон", () -> assertEquals(testEmployee.phone(), assertEmployee.getPhone()));
        step("Проверка, что у созданного сотрудника валидный статус", () -> assertEquals(testEmployee.isActive(), assertEmployee.isActive()));
    }

    @Test
    @DisplayName("Получение сотрудника по id")
    public void iCanGetEmployeeById() {
        EmployeeEntity testEmployee = entityManager.find(EmployeeEntity.class, helper.createNewEmployee(companyId, auth.userToken()));

        step("Проверка, что данные сотрудника совпадают с созданным и найденным сотрудником", () -> given()
                .basePath("employee")
                .contentType(ContentType.JSON)
                .when()
                .get("{id}", testEmployee.getId())
                .then()
                .body("firstName", equalTo(testEmployee.getFirstName()))
                .body("lastName", equalTo(testEmployee.getLastName()))
                .body("middleName", equalTo(testEmployee.getMiddleName()))
                .body("birthdate", equalTo(testEmployee.getBirthdate()))
                .body("phone", equalTo(testEmployee.getPhone()))
                .body("isActive", equalTo(testEmployee.isActive()))
                .body("avatar_url", equalTo(testEmployee.getUrl()))
                .body("email", equalTo(testEmployee.getEmail()))
                .body("id", equalTo(testEmployee.getId())));
    }

    @Test
    @DisplayName("Изменение информации о сотруднике")
    public void iCanChangeEmployeeInfo() {
        RandomEmployee testEmployee = helper.randomEmployee(companyId);

        int testId = given()
                .basePath("employee")
                .header("x-client-token", auth.userToken())
                .contentType(ContentType.JSON)
                .body(testEmployee)
                .when()
                .patch("{id}", helper.createNewEmployee(companyId, auth.userToken()))
                .as(FullEmployee.class).id();

        EmployeeEntity assertEmployee = entityManager.find(EmployeeEntity.class, testId);

        step("Проверка, что новая фамилия равна фамилии, на которую произведена замена", () -> assertEquals(testEmployee.lastName(), assertEmployee.getLastName()));
        step("Проверка, что новый email равен email, на который произведена замена", () -> assertEquals(testEmployee.email(), assertEmployee.getEmail()));
        step("Проверка, что новый сайт сотрудника равен сайту, на который произведена замена", () -> assertEquals(testEmployee.url(), assertEmployee.getUrl()));
        step("Проверка, что новый телефон равен телефону, на который произведена замена", () -> assertEquals(testEmployee.phone(), assertEmployee.getPhone()));
        step("Проверка, что статус равен статусу, на который произведена замена", () -> assertEquals(testEmployee.isActive(), assertEmployee.isActive()));
    }

    @Test
    @DisplayName("Изменение всей информации о сотруднике")
    public void changeFullEmployeeInfo() {
        FullEmployee testEmployee = helper.createFullEmployee(companyId);

        int testId = given()
                .basePath("employee")
                .header("x-client-token", auth.userToken())
                .contentType(ContentType.JSON)
                .body(testEmployee)
                .when()
                .patch("{id}", helper.createNewEmployee(companyId, auth.userToken()))
                .as(FullEmployee.class).id();

        EmployeeEntity assertEmployee = entityManager.find(EmployeeEntity.class, testId);

        step("Проверка, что новое имя не равно измененному имени", () -> assertNotEquals(testEmployee.firstName(), assertEmployee.getFirstName()));
        step("Проверка, что новая фамилия равна измененной фамилии", () -> assertEquals(testEmployee.lastName(), assertEmployee.getLastName()));
        step("Проверка, что новое отчество не равно измененному отчеству", () -> assertNotEquals(testEmployee.middleName(), assertEmployee.getMiddleName()));
        step("Проверка, что новое id компании не равно измененному id компании", () -> assertEquals(testEmployee.companyId(), assertEmployee.getCompanyId()));
        step("Проверка, что новый email не равен измененному email", () -> assertEquals(testEmployee.email(), assertEmployee.getEmail()));
        step("Проверка, что новый сайт сотрудника не равен измененному сайту", () -> assertEquals(testEmployee.url(), assertEmployee.getUrl()));
        step("Проверка, что новый телефон не равен измененному телефону", () -> assertEquals(testEmployee.phone(), assertEmployee.getPhone()));
        step("Проверка, что статус не равен измененному статусу", () -> assertEquals(testEmployee.isActive(), assertEmployee.isActive()));
    }

    @Test
    @DisplayName("Получение пустого списка сотрудников")
    public void getEmptyEmployeeList() {
        step("Проверка, что список сотрудников пуст", () -> given()
                .queryParam("company", companyId)
                .basePath("employee")
                .contentType(ContentType.JSON)
                .when()
                .get()
                .then()
                .header("Content-Length", (String) null));
    }

    @Test
    @DisplayName("Получение сотрудника по несуществующему id")
    public void getNotExistedEmployee() {
        step("Проверяем, что статус код 404, т.к сотрудник не найден", () -> given()
                .basePath("employee")
                .contentType(ContentType.JSON)
                .when()
                .get("{id}", 1 - companyId)
                .then()
                .statusCode(404));
    }
}
