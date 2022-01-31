package api;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class CreateUser {

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
