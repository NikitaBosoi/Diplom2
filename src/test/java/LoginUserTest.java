import clientAPI.UserClient;
import dataModels.User;
import dataModels.UserCreds;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;

public class LoginUserTest {

    private User user;
    UserClient userClient = new UserClient();

    @Before
    public void setUp() {
        this.user = User.getRandomUser();
    }

    // AUTHORIZATION SUCCESS
    @Test
    @DisplayName("Check authorization user with all data") // имя теста
    @Description("Checking authorization user with all data") // описание теста
    public void authUser() {
        userClient.sendRequestAddUser(user);
        Response response = userClient.returnUserAuthResponse(new UserCreds(user.getEmail(), user.getPassword()));
        response.then().assertThat().statusCode(SC_OK)
                .and()
                .body("user", notNullValue());
    }

    // AUTHORIZATION FAIL
    @Test
    @DisplayName("Check fail authorization user without password") // имя теста
    @Description("Checking fail authorization user without password") // описание теста
    public void authFailUserWithoutPassword() {
        userClient.sendRequestAddUser(user);
        Response response = userClient.returnUserAuthResponse(new UserCreds(user.getEmail(), ""));
        response.then().assertThat().statusCode(SC_UNAUTHORIZED)
                .and()
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Check fail authorization user without login") // имя теста
    @Description("Checking fail authorization user without login") // описание теста
    public void authFailUserWithoutLogin() {
        userClient.sendRequestAddUser(user);
        Response response = userClient.returnUserAuthResponse(new UserCreds("", user.getPassword()));
        response.then().assertThat().statusCode(SC_UNAUTHORIZED)
                .and()
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Check fail authorization user with invalid login") // имя теста
    @Description("Checking fail authorization user with invalid login") // описание теста
    public void authFailUserWithInvalidLogin() {
        userClient.sendRequestAddUser(user);
        Response response = userClient.returnUserAuthResponse(new UserCreds(user.getEmail() + "/*", user.getPassword()));
        response.then().assertThat().statusCode(SC_UNAUTHORIZED)
                .and()
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Check fail authorization user with invalid password") // имя теста
    @Description("Checking fail authorization user with invalid password") // описание теста
    public void authFailUserWithInvalidPassword() {
        userClient.sendRequestAddUser(user);
        Response response = userClient.returnUserAuthResponse(new UserCreds(user.getEmail(), user.getPassword() + "/*"));
        response.then().assertThat().statusCode(SC_UNAUTHORIZED)
                .and()
                .body("message", equalTo("email or password are incorrect"));
    }

    @After
    public void teardown() {
        try {
            userClient.deleteUser(user);
        } catch (NullPointerException err) {
        }
    }

}
