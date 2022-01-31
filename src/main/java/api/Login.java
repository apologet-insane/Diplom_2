package api;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class Login {

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
