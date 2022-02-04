package api;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class Login {


    @Step("Вход пользователем в аккаунт")
    public static Response login(String body) {

        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(body)
                .when()
                .post("/api/auth/login");

        return response;
    }


}
