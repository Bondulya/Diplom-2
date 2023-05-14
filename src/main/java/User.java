import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_OK;

public class User {
    private String login;
    private String password;
    private String firstName;
    private String accessToken;
    private String refreshToken;
    private boolean isLogin = false;

    public User(){
        int i = (int) (Math.random() * 9999);
        login = "Test_User_" + i + "@yandex.ru";
        password = "password" + i;
        firstName = "User" + i;
    }

    public User(String login, String password, String firstName){
        this.login = login;
        this.password = password;
        this.firstName = firstName;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public void setAccessToken(ValidatableResponse response) {
        this.accessToken = response.extract().path("accessToken");;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(ValidatableResponse response) {
        this.refreshToken = response.extract().path("refreshToken");
    }

    public boolean isLogin() {
        return this.isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }


    @Step("Создание пользователя")
    public ValidatableResponse apiUserRegister(boolean dropField){
        String strLogin;
        String strPassword;
        String strFirstName;

        if(login==null){
            strLogin = "";}
        else strLogin = login;
        if(password==null){
            strPassword ="";}
        else strPassword = password;

        if(firstName==null){
            strFirstName="";}
        else strFirstName = firstName;

        String json = "{";
        String separator = "";

        if (dropField){
            if(!strLogin.isBlank() && !strLogin.isEmpty()){
                json = json + "\"email\": \"" + strLogin + "\"";
                separator = ", ";
            }
            if (!strPassword.isBlank() && !strPassword.isEmpty()){
                json = json + separator + "\"password\": \""+strPassword+"\"";
                separator = ", ";
            }
            if (!strFirstName.isEmpty() && !strFirstName.isBlank()){
                json = json + separator + "\"name\": \""+strFirstName+"\"";
            }
            json = json + "}";
        } else {
            json = "{\"email\"" + strLogin +"\", \"password\": \"" + strPassword + "\", \"name\": \"" + strFirstName + "\"}";
        }
        ValidatableResponse response = given().header(ApiShop.apiPostHeaderType, ApiShop.apiPostHeaderValue).and()
                .body(json).when().post(ApiShop.apiUserRegister).then();

        if(response.extract().statusCode() == SC_OK){
            setAccessToken(response);
            setRefreshToken(response);
        }
        return response;
    }

    public ValidatableResponse apiUserRegister(){
        return apiUserRegister(false);
    }


    @Step("Авторизация")
    public ValidatableResponse apiLogin(String strLogin, String strPassword){
        if(strLogin == null){strLogin="";}
        if(strPassword==null){strPassword="";}
        String json="{\"email\": \"" + strLogin + "\", \"password\": \""+strPassword+"\"}";
        ValidatableResponse response = given()
                .header(ApiShop.apiPostHeaderType,ApiShop.apiPostHeaderValue)
                .and().body(json)
                .when().post(ApiShop.apiLogin).then();
        if(response.extract().statusCode() == SC_OK){
            setAccessToken(response);
            setRefreshToken(response);
        }
        return response;
    }
    public ValidatableResponse apiLogin(){
        return apiLogin(login,password);
    }

    @Step("Удалить пользователя")
    public ValidatableResponse apiDelete(){
        ValidatableResponse response = null;
        if(isLogin){
            response = given().auth().oauth2(accessToken).and().when().delete(ApiShop.apiDelete).then();
        }
        return response;
    }

    @Step("Обновить данные")
    public ValidatableResponse apiUpdate(String strEmail, String strName){
        String json = "{\n    \"email\": \"" + strEmail + "\",\n    \"name:\": \"" + strName + "\"\n}";
        ValidatableResponse response = given()
                .auth().oauth2(accessToken.substring(7))
                .header(ApiShop.apiPostHeaderType,ApiShop.apiPostHeaderValue)
                .and().body(json)
                .when().patch(ApiShop.apiUpdate)
                .then();
        return response;
    }

    @Step("Обновить без авторищации")
    public ValidatableResponse apiUpdateNoLogIn(String strEmail, String strName){
        String json = "{\n    \"email\": \"" + strEmail + "\",\n    \"name:\": \"" + strName + "\"\n}";
        ValidatableResponse response = given()
                .header(ApiShop.apiPostHeaderType,ApiShop.apiPostHeaderValue)
                .and().body(json)
                .when().patch(ApiShop.apiUpdate)
                .then();
        return response;
    }


}
