import clientAPI.OrderClient;
import clientAPI.UserClient;
import dataModels.Order;
import dataModels.User;
import dataModels.UserCreds;
import dataModels.responseDataOfingredients;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class OrderTest {

    OrderClient orderClient = new OrderClient();
    List<String> ingredients = new ArrayList<>();
    private User user;
    UserClient userClient = new UserClient();

    @Before
    public void setUp() {
        this.user = User.getRandomUser();
    }

    // Создание заказа без авторизации с ингредиентами
    @Test
    @DisplayName("Create order without auth with ingredients") // имя теста
    @Description("Create order without auth with ingredients") // описание теста
    public void orderWithoutAuth() {
        Response response = orderClient.sendRequestGetIngredients();
        responseDataOfingredients responseDataOfingredients = response.body().as(responseDataOfingredients.class);
        ingredients.add(responseDataOfingredients.getDatas().get(0).get_id());
        int size = responseDataOfingredients.getDatas().size() - 1;
        ingredients.add(responseDataOfingredients.getDatas().get(size).get_id());
        Order order = new Order(ingredients);
        Response responseRequestCreateOrder = orderClient.sendRequestCreateOrder(order);
        responseRequestCreateOrder.then().assertThat().statusCode(SC_OK)
                .and()
                .body("success", equalTo(true))
                .body("order.number", notNullValue());
    }

    // Создание заказа без ингредиента
    @Test
    @DisplayName("Create order without ingredients") // имя теста
    @Description("Create order without ingredients") // описание теста
    public void orderWithoutIngredients() {
        Order order = new Order();
        Response response = orderClient.sendRequestCreateOrder(order);
        response.then().assertThat().statusCode(SC_BAD_REQUEST)
                .and()
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    // Создание заказа с невалидным хэшем ингредиента
    @Test
    @DisplayName("Create order with invalid hash") // имя теста
    @Description("Create order with invalid hash") // описание теста
    public void orderWithInvalidHash() {
        Response response = orderClient.sendRequestGetIngredients();
        responseDataOfingredients responseDataOfingredients = response.body().as(responseDataOfingredients.class);
        ingredients.add(responseDataOfingredients.getDatas().get(0).get_id() + "12345");
        Order order = new Order(ingredients);
        Response responseRequestCreateOrder = orderClient.sendRequestCreateOrder(order);
        responseRequestCreateOrder.then().assertThat().statusCode(SC_INTERNAL_SERVER_ERROR);
    }

    // Создание заказа с авторизацией с ингредиентами
    @Test
    @DisplayName("Create order with auth with ingredients") // имя теста
    @Description("Create order with auth with ingredients") // описание теста
    public void orderWithAuth() {
        userClient.sendRequestAddUser(user);
        Response responseUserToken = userClient.returnUserAuthResponse(new UserCreds(user.getEmail(), user.getPassword()));
        String accessToken = responseUserToken.body().path("accessToken");
        Response response = orderClient.sendRequestGetIngredients();
        responseDataOfingredients responseDataOfingredients = response.body().as(responseDataOfingredients.class);
        ingredients.add(responseDataOfingredients.getDatas().get(0).get_id());
        int size = responseDataOfingredients.getDatas().size() - 1;
        ingredients.add(responseDataOfingredients.getDatas().get(size).get_id());
        Order order = new Order(ingredients);
        Response responseRequestCreateOrder = orderClient.sendRequestCreateOrder(order, accessToken.substring(7));
        responseRequestCreateOrder.then().assertThat().statusCode(SC_OK)
                .and()
                .body("success", equalTo(true))
                .body("order.number", notNullValue());
    }

    // Получение заказа авторизованного пользователя
    @Test
    @DisplayName("Get orders auth user") // имя теста
    @Description("Get orders auth user") // описание теста
    public void getOrderWithAuth() {
        userClient.sendRequestAddUser(user);
        Response responseUserToken = userClient.returnUserAuthResponse(new UserCreds(user.getEmail(), user.getPassword()));
        String accessToken = responseUserToken.body().path("accessToken");
        Response response = orderClient.sendRequestGetIngredients();
        responseDataOfingredients responseDataOfingredients = response.body().as(responseDataOfingredients.class);
        ingredients.add(responseDataOfingredients.getDatas().get(0).get_id());
        int size = responseDataOfingredients.getDatas().size() - 1;
        ingredients.add(responseDataOfingredients.getDatas().get(size).get_id());
        Order order = new Order(ingredients);
        Response responseOrder = orderClient.sendRequestCreateOrder(order, accessToken.substring(7));
        int orderNumber = responseOrder.body().path("order.number");
        Response responseGetOrders = orderClient.sendRequestGetOrders(accessToken.substring(7));
        responseGetOrders.then().assertThat().statusCode(SC_OK)
                .and()
                .body("success", equalTo(true))
                .body("orders.number", equalTo(Arrays.asList(orderNumber)));
    }

    // Получение заказа неавторизованного пользователя
    @Test
    @DisplayName("Get orders unauth user") // имя теста
    @Description("Get orders unauth user") // описание теста
    public void getOrderWithoutAuth() {
        userClient.sendRequestAddUser(user);
        Response responseUserToken = userClient.returnUserAuthResponse(new UserCreds(user.getEmail(), user.getPassword()));
        String accessToken = responseUserToken.body().path("accessToken");
        Response response = orderClient.sendRequestGetIngredients();
        responseDataOfingredients responseDataOfingredients = response.body().as(responseDataOfingredients.class);
        ingredients.add(responseDataOfingredients.getDatas().get(0).get_id());
        int size = responseDataOfingredients.getDatas().size() - 1;
        ingredients.add(responseDataOfingredients.getDatas().get(size).get_id());
        Order order = new Order(ingredients);
        Response responseOrder = orderClient.sendRequestCreateOrder(order);
        int orderNumber = responseOrder.body().path("order.number");
        Response responseGetOrders = orderClient.sendRequestGetOrders("");
        responseGetOrders.then().assertThat().statusCode(SC_UNAUTHORIZED)
                .and()
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @After
    public void teardown() {
        try {
            userClient.deleteUser(user);
        } catch (NullPointerException err) {
            // skip NullPointerException if user not exist
        }
    }

}
