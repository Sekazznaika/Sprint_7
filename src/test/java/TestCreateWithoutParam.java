import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.qameta.allure.Step;
import org.junit.Before;
import org.junit.Test;
import pojo.requests.ReqCreate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class TestCreateWithoutParam {

    @Before
    @Step("Настройка базового URI")
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @Test
    @Step("Тест создания курьера без указания логина")
    public void testCreateWithoutLogin() {
        ReqCreate reqCreate = createRequestWithoutLogin();
        sendCreateRequestAndVerifyResponse(reqCreate);
    }

    @Test
    @Step("Тест создания курьера без указания пароля")
    public void testCreateWithoutPassword() {
        ReqCreate reqCreate = createRequestWithoutPassword();
        sendCreateRequestAndVerifyResponse(reqCreate);
    }

    @Step("Создание запроса без логина (только пароль и имя)")
    private ReqCreate createRequestWithoutLogin() {
        ReqCreate reqCreate = new ReqCreate();
        reqCreate.setPassword("1");
        reqCreate.setFirstName("saske");
        return reqCreate;
    }

    @Step("Создание запроса без пароля (только логин и имя)")
    private ReqCreate createRequestWithoutPassword() {
        ReqCreate reqCreate = new ReqCreate();
        reqCreate.setLogin("1");
        reqCreate.setFirstName("saske");
        return reqCreate;
    }

    @Step("Отправка запроса на создание курьера и проверка ответа")
    private void sendCreateRequestAndVerifyResponse(ReqCreate reqCreate) {
        given()
                .contentType(ContentType.JSON)
                .body(reqCreate)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }
}