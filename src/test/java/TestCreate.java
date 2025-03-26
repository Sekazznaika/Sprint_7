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
import static org.hamcrest.Matchers.equalTo;

public class TestCreate {

    String login;
    String id;
    String password;

    @Before
    @Step("Настройка тестового окружения: установка базового URI и генерация тестовых данных")
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
        login = "login" + Math.random();
        password = "Pass" + Math.random();
    }

    @After
    @Step("Удаление тестовых данных: получение и удаление курьера, если он был создан")
    public void tearDown() {
        id = deleteId(login, password);
        if (id != null) {
            given()
                    .pathParam("id", id)
                    .when()
                    .delete("/api/v1/courier/{id}")
                    .then()
                    .statusCode(200);
        }
    }

    @Test
    @Step("Тест создания нового курьера")
    public void testCreate() {
        ReqCreate reqCreate = new ReqCreate(login, password, "12");
        Response response = given()
                .contentType(ContentType.JSON)
                .body(reqCreate)
                .when()
                .post("/api/v1/courier");
        response.then()
                .statusCode(201)
                .assertThat()
                .body("ok", equalTo(true));
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