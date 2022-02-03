package api;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class CreateUser {

    @Step("Создание пользователя")
    public static Response createUser(String body) {

        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(body)
                .when()
                .post("/api/auth/register");

        return response;
    }


}
