import io.restassured.RestAssured;
import io.qameta.allure.Step;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;

public class TestOrderList {

    @Before
    @Step("Подготовка тестового окружения: установка базового URL")
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @Test
    @Step("Проверка получения списка заказов")
    public void testGetOrderList() {
        sendGetOrderListRequest();
        verifyOrderListResponse();
    }

    @Step("Отправка GET-запроса для получения списка заказов")
    private void sendGetOrderListRequest() {
        given()
                .when()
                .get("/api/v1/orders");
    }

    @Step("Проверка ответа: статус код 200")
    private void verifyOrderListResponse() {
        given()
                .when()
                .get("/api/v1/orders")
                .then()
                .statusCode(200);
    }
}