package api;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class PatchUser {

    @Step("Изменение данных пользователя")
    public static Response patchUser(String token, String body) {
        Response response = given()
                .header("Content-type", "application/json")
                .auth().oauth2(token)
                .and()
                .body(body)
                .when()
                .patch("/api/auth/user");

        return response;
    }

}
