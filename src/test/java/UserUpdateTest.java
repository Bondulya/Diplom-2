
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;


public class UserUpdateTest {
    private User user;
    private ValidatableResponse response;

    @Before
    public void setUp() {
        RestAssured.baseURI = ApiShop.API_URL;
    }

    @After
    public void cleanUp() {
        response = user.apiDelete();
    }

    @Test
    @DisplayName("Обновление данных пользователя")
    public void userUpdateSuccessTest() {
        user = new User();
        user.apiUserRegister();
        user.apiLogin();

        response = user.apiUpdate(user.getLogin(), "Bondulya222");

        Assert.assertEquals("User update is not successful. Status code is incorrect.", SC_OK, response.extract().statusCode());

        Assert.assertTrue("User update failed. Success code should be true", response.extract().path("success"));

    }
    @Test
    @DisplayName("Обновление данных без авторизации")
    public void userUpdateNoAuthTest() {
        user = new User();
        user.apiUserRegister();

        response = user.apiUpdateNoLogIn(user.getLogin(), "TestNameUpdate");

        Assert.assertEquals("User update expected to be failed. Status code is incorrect.", SC_UNAUTHORIZED, response.extract().statusCode());

        Assert.assertFalse("User update expected to be failed. Success code should be false", response.extract().path("success"));

        String strExpected = "You should be authorised";
        Assert.assertEquals("User update expected to be failed. Response message is incorrect.", strExpected, response.extract().path("message"));
    }
}

