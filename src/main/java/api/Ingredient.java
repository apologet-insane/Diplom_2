package api;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class Ingredient {


    public static Response ingredient (String token){
       Response response = given()
                .header("Content-type", "application/json")
                .auth().oauth2(token)
                .and()
                .when()
                .get("/api/ingredients");

        return  response;
    }
}
