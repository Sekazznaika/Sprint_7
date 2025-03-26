import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.qameta.allure.Step;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pojo.requests.ReqCreate;
import pojo.requests.ReqLogin;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class TestDoubleCreate {

    String login;
    String id;
    String password;

    @Before
    @Step("Подготовка тестовых данных: генерация случайных логина и пароля")
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
        login = "login" + Math.random();
        password = "Pass" + Math.random();
    }

    @After
    @Step("Удаление тестового курьера, если он был создан")
    public void tearDown() {
        id = deleteId(login, password);
        if (id != null) {
            deleteCourier(id);
        }
    }

    @Test
    @Step("Тест попытки создания дублирующего курьера")
    public void testDouble() {
        ReqCreate reqCreate = new ReqCreate(login, password, "12");
        createCourier(reqCreate);
        attemptDuplicateCreation(reqCreate);
    }

    @Step("Создание курьера")
    private static void createCourier(ReqCreate reqCreate) {
        given()
                .contentType(ContentType.JSON)
                .body(reqCreate)
                .when()
                .post("/api/v1/courier");
    }

    @Step("Попытка создания дублирующего курьера")
    private static void attemptDuplicateCreation(ReqCreate reqCreate) {
        given()
                .contentType(ContentType.JSON)
                .body(reqCreate)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(409)
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
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

    @Step("Получение ID курьера по логину и паролю")
    private String deleteId(String login, String password) {
        ReqLogin reqLogin = new ReqLogin(login, password);
        return given()
                .contentType(ContentType.JSON)
                .body(reqLogin)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(200)
                .extract()
                .path("id").toString();
    }
}