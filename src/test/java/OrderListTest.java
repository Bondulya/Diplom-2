
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
public class OrderListTest {
        private User user;
        private Order order;
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
        @DisplayName("Получение заказов конкретного пользователя")
        public void userOrderListPositiveTest() {
            int numIngredients = 3;
            ValidatableResponse response;

            user = new User();
            user.apiUserRegister();
            order = new Order(user.getAccessToken());

            Order.Ingredient[] ingredientMenu = order.getIngredientsMenu();
            Assert.assertTrue("Test failed. Ingredients are not available for the order", ingredientMenu.length > 0);

            if(numIngredients > ingredientMenu.length ){
                numIngredients = ingredientMenu.length;
            }

            response = order.orderCreate(numIngredients);
            Assert.assertTrue("Order is not created. Test can`t be completed.", response.extract().path("success"));
            response = order.orderCreate(numIngredients);
            Assert.assertTrue("Order is not created. Test can`t be completed.", response.extract().path("success"));

            response = order.burgerUserOrderList(user.getAccessToken());

            Assert.assertEquals("Order list is not returned. Invalid status code.", SC_OK, response.extract().statusCode());

            Assert.assertTrue("Order list is not returned. Success field value should be true.", response.extract().path("success"));

            Assert.assertNotNull("Order list is not returned. 'orders' node is not provided.", response.extract().path("orders"));
        }
        @Test
        @DisplayName("Получение заказов конкретного пользователя неавторизованный пользователь.")
        public void userOrderListNoAuthTest() {
            int numIngredients = 3;
            ValidatableResponse response;

            user = new User();
            user.apiUserRegister();
            order = new Order(user.getAccessToken());

            Order.Ingredient[] ingredientMenu = order.getIngredientsMenu();
            Assert.assertTrue("Test failed. Ingredients are not available for the order", ingredientMenu.length > 0);

            if(numIngredients > ingredientMenu.length ){
                numIngredients = ingredientMenu.length;
            }

            response = order.orderCreate(numIngredients);
            Assert.assertTrue("Order is not created. Test can`t be completed.", response.extract().path("success"));
            response = order.orderCreate(numIngredients);
            Assert.assertTrue("Order is not created. Test can`t be completed.", response.extract().path("success"));

            response = order.burgerUserOrderList();

            Assert.assertEquals("Order list is not returned. Invalid status code.", SC_UNAUTHORIZED, response.extract().statusCode());

            Assert.assertFalse("Order list should not be returned. Success field value should be false.", response.extract().path("success"));
        }

    }

