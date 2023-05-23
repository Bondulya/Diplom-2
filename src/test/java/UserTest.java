import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.assertEquals;


public class UserTest {
    private User user;
    private ValidatableResponse response;

    @Before
    public void setUp(){
        RestAssured.baseURI = ApiShop.API_URL;
    }

    @After
    public void cleanUp(){
        response = user.apiDelete();
    }
    @Test
    @DisplayName("Авторизация")
    public void userLoginSuccessTest() {

        user = new User();
        user.apiUserRegister();
        response = user.apiLogin();

        Assert.assertEquals("User login is not successful. Status code is incorrect.", SC_OK, response.extract().statusCode());

        Assert.assertTrue("User login failed. Success code should be true", response.extract().path("success"));
    }
    @Test
    @DisplayName("Неправильный логин")
    public void userWrongLoginTest() {
        user = new User();
        user.apiUserRegister();

        response = user.apiLogin("Bond777", user.getPassword());

        assertEquals("User login rejection failed.", SC_UNAUTHORIZED, response.extract().statusCode());

        String strExpected = "email or password are incorrect";
        String strResponse = response.extract().path("message");
        assertEquals("Courier login rejection failed. Body content is incorrect", strExpected, strResponse );
    }
    @Test
    @DisplayName("неправильный пароль")
    public void userWrongPasswordTest() {

        user = new User();
        user.apiUserRegister();

        response = user.apiLogin(user.getLogin(), "7bond7");

        assertEquals("User login rejection failed.", SC_UNAUTHORIZED, response.extract().statusCode());

        String strExpected = "email or password are incorrect";
        String strResponse = response.extract().path("message");
        assertEquals("Courier login rejection failed. Body content is incorrect", strExpected, strResponse );
    }

}

