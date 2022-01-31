package api;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class UserOrderList {


    public static Response userOrderList(String token) {
        Response response = given()
                .auth().oauth2(token)
                .and()
                .when()
                .get("/api/orders");

        return response;
    }

}
