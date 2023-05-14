import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Objects;

import static org.apache.http.HttpStatus.*;



public class OrderTest {
    private User user;
    private Order order;
    private ValidatableResponse response;

    @Before
    public void setUp(){
        RestAssured.baseURI = ApiShop.apiURL;
    }

    @After
    public void cleanUp(){
        response = user.apiDelete();
    }

    @Test
    @DisplayName("Создание заказа")
    public void orderCreatePositiveTest(){
        int numIngredients = 3;

        user = new User();
        user.apiUserRegister();
        order = new Order(user.getAccessToken());

        Order.Ingredient[] ingredients = order.getIngredientsMenu();
        Assert.assertTrue("Test failed. Ingredients are not available for the order", ingredients.length > 0);

        if(numIngredients > ingredients.length ){
            numIngredients = ingredients.length;
        }

        ValidatableResponse response = order.orderCreate(numIngredients);

        Assert.assertEquals("Order creation failed. Invalid status code.", SC_OK, response.extract().statusCode());

        Assert.assertTrue("Order creation failed. Invalis response success field value.", response.extract().path("success"));

        Assert.assertEquals("", "done", response.extract().path("order.status"));

        Assert.assertTrue("Order creation failed. Order number is not returned", order.getBurgerOrderNumber() > 0);
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    public void orderCreateNoAuthTest(){
        int numIngredients = 3;

        user = new User();
        user.apiUserRegister();
        order = new Order(user.getAccessToken());
        Order.Ingredient[] ingredientMenu = order.getIngredientsMenu();
        Assert.assertTrue("Test failed. Ingredients are not available for the order", ingredientMenu.length > 0);

        if(numIngredients > ingredientMenu.length ){
            numIngredients = ingredientMenu.length;
        }

        ValidatableResponse response = order.burgerOrderCreateNoAuth(numIngredients);

        Assert.assertEquals("Order creation failed. Invalid status code.", SC_OK, response.extract().statusCode());

        Assert.assertTrue("Order creation failed. Invalid response success field value.", response.extract().path("success"));

        Assert.assertTrue("Expected no order status returned. But order status exists.", Objects.isNull(response.extract().path("order.status")));
    }

    @Test
    @DisplayName("Создание заказа без состовляющих")
    public void orderCreateNoIngredientsTest(){
        int numIngredients = 0;

        user = new User();
        user.apiUserRegister();
        order = new Order(user.getAccessToken());

        //Create order
        ValidatableResponse response = order.orderCreate(numIngredients);

        Assert.assertEquals("Order creation expected to be failed. Invalid status code.", SC_BAD_REQUEST, response.extract().statusCode());

        Assert.assertFalse("Order creation expected to be failed. Invalid response success field value.", response.extract().path("success"));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов.")
    public void orderCreateInvalidHashTest(){

        user = new User();
        user.apiUserRegister();
        order = new Order(user.getAccessToken());

        ValidatableResponse response = order.burgerOrderCreateInvalidHash("zzzzzz");

        Assert.assertEquals("Order creation expected to be failed. Invalid status code.", SC_INTERNAL_SERVER_ERROR, response.extract().statusCode());
    }

}
