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

public class TestLogin {
    String login;
    String id;
    String password;

    @Before
    @Step("Подготовка тестовых данных и создание тестового курьера")
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
    @Step("Удаление тестового курьера")
    public void tearDown() {
        Response response = loginCourier(login, password);
        if (response.statusCode() == 200) {
            id = extractCourierId(response);
            deleteCourier(id);
        }
    }

    @Step("Авторизация курьера")
    private Response loginCourier(String login, String password) {
        return given()
                .contentType(ContentType.JSON)
                .body(new ReqLogin(login, password))
                .when()
                .post("/api/v1/courier/login");
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
    @Step("Тест авторизации курьера с валидными данными")
    public void testLogin() {
        ReqLogin reqLogin = new ReqLogin(login, password);
        given()
                .contentType(ContentType.JSON)
                .body(reqLogin)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(200)
                .body("id", notNullValue());
    }
}