package clientAPI;

import dataModels.Order;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class OrderClient {

    private static final String BASE_URI = "https://stellarburgers.nomoreparties.site";
    private static final String INGREDIENTS = "/api/ingredients";
    private static final String ORDER = "/api/orders";

    private RequestSpecification getHeader() {
        return given().baseUri(BASE_URI).header("Content-type", "application/json");
    }

    // метод для "Отправить GET запрос":
    private Response sendGetRequest(String get, String accessToken) {
        Response response = getHeader() // заполни header
                .auth().oauth2(accessToken).get(get); // отправь запрос на ручку
        return response;
    }

    @Step("Get ingredients")
    public Response sendRequestGetIngredients() {
        return sendGetRequest(INGREDIENTS, "");
    }

    @Step("Create order")
    public Response sendRequestCreateOrder(Order order) {
        return sendPostRequest(order, ORDER, "");
    }

    // метод для "Отправить POST запрос":
    private Response sendPostRequest(Object body, String post, String accessToken) {

        Response response = getHeader() // заполни header
                .auth().oauth2(accessToken).body(body) // заполни body
                .when().post(post); // отправь запрос на ручку
        return response;
    }

    @Step("Create order with token")
    public Response sendRequestCreateOrder(Order order, String accessToken) {
        return sendPostRequest(order, ORDER, accessToken);
    }

    @Step("Get orders")
    public Response sendRequestGetOrders(String accessToken) {
        return sendGetRequest(ORDER, accessToken);
    }
}
