import clientAPI.UserClient;
import dataModels.User;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;

public class CreateUserTest {

    private User user;
    UserClient userClient = new UserClient();

    @Before
    public void setUp() {
        this.user = User.getRandomUser();
    }

    // Добавление нового user по всем полям
    @Test
    @DisplayName("Check add user with all data") // имя теста
    @Description("Checking add user with all data") // описание теста
    public void createNewUser() {
        Response response = userClient.sendRequestAddUser(user);
        response.then().assertThat().statusCode(SC_OK)
                .and()
                .body("success", equalTo(true));
    }

    // Повторное добавление user, проверка на существование в системе
    @Test
    @DisplayName("Check add duplicate user") // имя теста
    @Description("Checking add duplicate user") // описание теста
    public void createDuplicateUser() {
        userClient.sendRequestAddUser(user);
        Response response = userClient.sendRequestAddUser(user);
        response.then().assertThat().statusCode(SC_FORBIDDEN)
                .and()
                .body("message", equalTo("User already exists"));
    }

    // ОБЯЗАТЕЛЬНОСТЬ ПОЛЕЙ
    // Обязательность поля email
    @Test
    @DisplayName("Check add user without email") // имя теста
    @Description("Checking add user without email") // описание теста
    public void createWithoutEmail() {
        user = User.getRandomUserWithoutEmail();
        Response response = userClient.sendRequestAddUser(user);
        response.then().assertThat().statusCode(SC_FORBIDDEN)
                .and()
                .body("message", equalTo("Email, password and name are required fields"));
    }

    // Обязательность поля пароль
    @Test
    @DisplayName("Check add user without password") // имя теста
    @Description("Checking add user without password") // описание теста
    public void createWithoutPassword() {
        user = User.getRandomUserWithoutPassword();
        Response response = userClient.sendRequestAddUser(user);
        response.then().assertThat().statusCode(SC_FORBIDDEN)
                .and()
                .body("message", equalTo("Email, password and name are required fields"));
    }

    // Обязательность поля имя
    @Test
    @DisplayName("Check add user without name") // имя теста
    @Description("Checking add user without name") // описание теста
    public void createWithoutName() {
        user = User.getRandomUserWithoutName();
        Response response = userClient.sendRequestAddUser(user);
        response.then().assertThat().statusCode(SC_FORBIDDEN)
                .and()
                .body("message", equalTo("Email, password and name are required fields"));
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
