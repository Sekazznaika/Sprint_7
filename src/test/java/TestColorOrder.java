import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.qameta.allure.Step;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import pojo.requests.ReqOrder;

import java.util.Arrays;
import java.util.Collection;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class TestColorOrder {
    public TestColorOrder(String[] colors) {
        this.colors = colors;
    }

    private final String[] colors;
    private Integer trackId;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {new String[]{"BLACK"}},
                {new String[]{"GREY"}},
                {new String[]{"BLACK", "GREY"}},
                {new String[]{}}
        });
    }

    @Before
    @Step("Настройка базового URI")
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @After
    @Step("Отмена созданного заказа, если он существует")
    public void tearDown() {
        if (trackId != null) {
            cancelOrder(trackId);
        }
    }

    @Test
    @Step("Тест создания заказа с комбинацией цветов: {colors}")
    public void testOrderCreationWithDifferentColors() {
        ReqOrder reqOrder = createRequest(colors);
        trackId = createOrder(reqOrder);
    }

    @Step("Создание запроса на заказ с цветами: {colors}")
    private ReqOrder createRequest(String[] colors) {
        return new ReqOrder(
                "Джонни",
                "Ибилли",
                colors,
                "комментарий",
                "2025-06-01",
                2,
                "8999999",
                "Папаево",
                "ул.Пушкина,д. Колотушкина");
    }

    @Step("Создание заказа и получение трек-номера")
    private Integer createOrder(ReqOrder reqOrder) {
        return given()
                .contentType(ContentType.JSON)
                .body(reqOrder)
                .when()
                .post("/api/v1/orders")
                .then()
                .statusCode(201)
                .body("track", notNullValue())
                .extract()
                .path("track");
    }

    @Step("Отмена заказа с трек-номером: {trackId}")
    private void cancelOrder(Integer trackId) {
        given()
                .contentType(ContentType.JSON)
                .queryParam("track", trackId)
                .when()
                .put("/api/v1/orders/cancel")
                .then()
                .statusCode(200);
    }
}