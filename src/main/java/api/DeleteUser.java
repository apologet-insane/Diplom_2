package api;

import static io.restassured.RestAssured.given;

public class DeleteUser {

    public static void deleteUser(String token) {

        given()
                .auth().oauth2(token)
                .when()
                .delete("/api/auth/user");
    }
}
