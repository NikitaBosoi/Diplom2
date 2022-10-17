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

public class ChangeUserTest {
    private User user;
    UserClient userClient = new UserClient();

    @Before
    public void setUp() {
        this.user = User.getRandomUser();
    }

    // CHANGE USER WITH AUTH
    @Test
    @DisplayName("Check change user email and name") // имя теста
    @Description("Checking change user email and name") // описание теста
    public void changeUserEmailAndName() {
        User newUser = User.getRandomUser();
        Response responsePatch = changeUser(newUser);
        responsePatch.then().assertThat().statusCode(SC_OK)
                .and()
                .body("success", equalTo(true))
                .body("user.email", equalTo(newUser.getEmail().toLowerCase()))
                .body("user.name", equalTo(newUser.getName()));
        this.user = newUser;
    }

    public Response changeUser(User newUser) {
        userClient.sendRequestAddUser(user);
        Response response = userClient.returnUserAuthResponse(new UserCreds(user.getEmail(), user.getPassword()));
        String accessToken = response.body().path("accessToken");
        return userClient.changeUser(accessToken.substring(7), newUser);
    }

    @Test
    @DisplayName("Check change user password") // имя теста
    @Description("Checking change user password") // описание теста
    public void changeUserPassword() {
        User newUser = User.getRandomUser();
        changeUser(newUser);
        Response response = userClient.returnUserAuthResponse(new UserCreds(newUser.getEmail(), newUser.getPassword()));
        response.then().assertThat().statusCode(SC_OK)
                .and()
                .body("success", equalTo(true))
                .body("user", notNullValue());
        this.user = newUser;
    }

    // CHANGE USER WITHOUT AUTH
    @Test
    @DisplayName("Check change user without token") // имя теста
    @Description("Checking change user without token") // описание теста
    public void changeUserWithoutToken() {
        User newUser = User.getRandomUser();
        userClient.sendRequestAddUser(user);
        Response responsePatch = userClient.changeUser("", newUser);
        responsePatch.then().assertThat().statusCode(SC_UNAUTHORIZED)
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
