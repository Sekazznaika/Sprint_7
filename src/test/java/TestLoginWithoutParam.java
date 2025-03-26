import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.qameta.allure.Step;
import org.junit.Before;
import org.junit.Test;
import pojo.requests.ReqLogin;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class TestLoginWithoutParam {

    @Before
    @Step("Настройка базового URI")
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @Test
    @Step("Тест авторизации без указания логина")
    public void testWithoutLogin() {
        ReqLogin reqLogin = createLoginRequestWithoutLogin();
        sendLoginRequestAndVerifyResponse(reqLogin);
    }

    @Step("Создание запроса авторизации без логина")
    private ReqLogin createLoginRequestWithoutLogin() {
        ReqLogin reqLogin = new ReqLogin();
        reqLogin.setPassword("12");
        return reqLogin;
    }

    @Step("Отправка запроса авторизации и проверка ответа")
    private void sendLoginRequestAndVerifyResponse(ReqLogin reqLogin) {
        given()
                .contentType(ContentType.JSON)
                .body(reqLogin)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }
}