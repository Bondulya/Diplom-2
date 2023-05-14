
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.assertEquals;


public class LoginTest {
    private User user;
    private ValidatableResponse response;

    @Before
    public void setUp() {
        RestAssured.baseURI = ApiShop.apiURL;
    }

    @After
    public void cleanUp() {
        response = user.apiDelete();
    }

    @Test
    @DisplayName("Авторизация пользователя")
    public void userLoginSuccessTest() {
        user = new User();
        user.apiUserRegister();

        response = user.apiLogin();

        Assert.assertEquals("User login is not successful. Status code is incorrect.", SC_OK, response.extract().statusCode());

        Assert.assertTrue("Ошибка входа пользователя. Код должен быть правильный", response.extract().path("success"));
    }

    @Test
    @DisplayName("Авторизая пользователя, который уже зарегистрирован")
    public void userWrongLoginTest() {

        user = new User();
        user.apiUserRegister();

        response = user.apiLogin("Bondulya2226", user.getPassword());

        assertEquals("Неудачный вход пользователя.", SC_UNAUTHORIZED, response.extract().statusCode());

        String strExpected = "email or password are incorrect";
        String strResponse = response.extract().path("message");
        assertEquals("Неудачный вход в систему. Содержимое тела неверно", strExpected, strResponse);
    }

    @Test
    @DisplayName("Авторизация пользователя с неправильным паролем")
    public void userWrongPasswordTest() {

        user = new User();
        user.apiUserRegister();

        response = user.apiLogin(user.getLogin(), "6bond6");

        assertEquals("Неудачный вход пользователя.", SC_UNAUTHORIZED, response.extract().statusCode());

        String strExpected = "email or password are incorrect";
        String strResponse = response.extract().path("message");
        assertEquals("Неудачный вход в систему. Содержимое тела неверно", strExpected, strResponse);
    }
}


