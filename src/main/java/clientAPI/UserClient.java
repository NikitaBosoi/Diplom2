package clientAPI;

import dataModels.User;
import dataModels.UserCreds;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class UserClient {

    private static final String BASE_URI = "https://stellarburgers.nomoreparties.site";
    private static final String ADD_USER = "/api/auth/register";
    private static final String AUTH_USER = "/api/auth/user";
    private static final String LOGIN_USER = "/api/auth/login";

    private RequestSpecification getHeader() {
        return given()
                .baseUri(BASE_URI)
                .header("Content-type", "application/json");
    }

    // метод для "Отправить запрос на добавление user":
    @Step("Create user")
    public Response sendRequestAddUser(User user) {
        return sendPostRequest(user, ADD_USER);
    }

    // метод для "Отправить POST запрос":
    private Response sendPostRequest(Object body, String post) {
        return getHeader() // заполни header
                        .body(body) // заполни body
                        .when()
                        .post(post); // отправь запрос на ручку
    }

    // метод для "Удалить user по accessToken":
    @Step("Delete user")
    public Response deleteUser(User user) {
        String accessToken = sendRequestAddUser(user)
                .body()
                .path("accessToken");
        return getHeader()
                .when()
                .delete(String.format(AUTH_USER, accessToken)); // отправь запрос на ручку
    }

    // метод для "Авторизация":
    @Step("Authorization user")
    public Response returnUserAuthResponse(UserCreds creds) {
        return sendPostRequest(creds, LOGIN_USER);
    }

    // метод для "Change user по accessToken":
    @Step("Change user")
    public Response changeUser(String accessToken, User newUser) {
        return getHeader()
                .auth()
                .oauth2(accessToken)
                .body(newUser)
                .when()
                .patch(AUTH_USER);
    }

}
