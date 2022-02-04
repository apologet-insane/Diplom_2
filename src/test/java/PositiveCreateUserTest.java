import api.Base;
import api.CreateUser;
import api.DeleteUser;
import api.Login;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PositiveCreateUserTest {

    String userName = RandomStringUtils.randomAlphabetic(10);
    String userPassword = RandomStringUtils.randomAlphabetic(10);
    String userMail = RandomStringUtils.randomAlphabetic(5) + "@" + RandomStringUtils.randomAlphabetic(5) + ".ru";

    @Before
    public void setUp() {
        RestAssured.baseURI = Base.BASE_URL;
    }

    @Test
    @DisplayName("Проверка создания пользователя: код ответа")
    public void createUserPosistive() {
        Response response = CreateUser.createUser(registerRequestBody());
        response
                .then()
                .assertThat()
                .statusCode(200);

    }

    @Step("Получить JSON для тела реквеста")
    public String registerRequestBody() {

        String registerRequestBody = "{\"name\":\"" + userName + "\","
                + "\"password\":\"" + userPassword + "\","
                + "\"email\":\"" + userMail + "\"}";

        return registerRequestBody;
    }

    @After
    public void deleteTestUser2() {
        String token = Login.login(registerRequestBody())
                .then()
                .extract()
                .path("accessToken");

        DeleteUser.deleteUser(token.substring(7));

    }

}
