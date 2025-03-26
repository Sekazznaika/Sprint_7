import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.qameta.allure.Step;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pojo.requests.ReqCreate;
import pojo.requests.ReqLogin;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class TestLoginWrongData {
    String login;
    String id;
    String password;

    @Before
    @Step("Подготовка тестовых данных: создание тестового курьера")
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
        login = "login" + Math.random();
        password = "Pass" + Math.random();

        createTestCourier(login, password);
    }

    @Step("Создание тестового курьера")
    private void createTestCourier(String login, String password) {
        ReqCreate reqCreate = new ReqCreate(login, password, "saske");
        given()
                .contentType(ContentType.JSON)
                .body(reqCreate)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(201);
    }

    @After
    @Step("Очистка: удаление тестового курьера")
    public void tearDown() {
        Response response = attemptLogin(login, password);
        if (loginSuccessful(response)) {
            id = extractCourierId(response);
            deleteCourier(id);
        }
    }

    @Step("Попытка авторизации курьера")
    private Response attemptLogin(String login, String password) {
        return given()
                .contentType(ContentType.JSON)
                .body(new ReqLogin(login, password))
                .when()
                .post("/api/v1/courier/login");
    }

    @Step("Проверка успешности авторизации")
    private boolean loginSuccessful(Response response) {
        return response.statusCode() == 200;
    }

    @Step("Извлечение ID курьера из ответа")
    private String extractCourierId(Response response) {
        return response.then().extract().path("id").toString();
    }

    @Step("Удаление курьера по ID")
    private void deleteCourier(String id) {
        given()
                .pathParam("id", id)
                .when()
                .delete("/api/v1/courier/{id}")
                .then()
                .statusCode(200);
    }

    @Test
    @Step("Тест авторизации с неверным логином")
    public void testWrongLogin() {
        ReqLogin reqLogin = prepareLoginRequestWithWrongLogin();
        verifyFailedLoginAttempt(reqLogin);
    }

    @Step("Подготовка запроса с неверным логином")
    private ReqLogin prepareLoginRequestWithWrongLogin() {
        return new ReqLogin("error", password);
    }

    @Test
    @Step("Тест авторизации с неверным паролем")
    public void testWrongPassword() {
        ReqLogin reqLogin = prepareLoginRequestWithWrongPassword();
        verifyFailedLoginAttempt(reqLogin);
    }

    @Step("Подготовка запроса с неверным паролем")
    private ReqLogin prepareLoginRequestWithWrongPassword() {
        return new ReqLogin(login, "password");
    }

    @Step("Проверка неудачной попытки авторизации")
    private void verifyFailedLoginAttempt(ReqLogin reqLogin) {
        given()
                .contentType(ContentType.JSON)
                .body(reqLogin)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }
}