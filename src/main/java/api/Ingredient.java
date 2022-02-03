package api;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class Ingredient {

    @Step("Получение ингредиента")
    public static Response ingredient(String token) {
        Response response = given()
                .header("Content-type", "application/json")
                .auth().oauth2(token)
                .and()
                .when()
                .get("/api/ingredients");

        return response;
    }
}
