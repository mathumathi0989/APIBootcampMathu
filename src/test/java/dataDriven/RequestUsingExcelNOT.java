package dataDriven;


import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class RequestUsingExcelNOT {

        protected String baseURI;
        protected String username;
        protected String password;
        public int userId;
        public int userCount;
        public int AfterPostuserCount;

        private ExcelUtils excelUtils;
        private List<Map<String, String>> usersData;

        @BeforeTest
        public void setup() {
            baseURI = "https://userserviceapi-a54ceee3346a.herokuapp.com/uap/";
            RestAssured.baseURI = baseURI;
            username = "Numpy@gmail.com";
            password = "userapi123";
            excelUtils = new ExcelUtils("C:/Users/mathu/Downloads/APIDummyData.xlsx"); // Path to your Excel file
            usersData = excelUtils.getUserData("Sheet1"); // Read all user data from the sheet
        }

        @Test(priority = 1)
        public void testGetUser() {
            Response response = given().auth().basic(username, password).when().get("users").then().statusCode(200).extract().response();
            userCount = response.jsonPath().getList("users").size();
            System.out.println("Total Number of users: " + userCount);
            System.out.println("-----------------------------------------------------------------------------------------------------------");
        }

        @Test(priority = 2)
        public void testCreateUsers() {
            for (int i = 0; i < 5 && i < usersData.size(); i++) { // Only use 5 sets of data
                Map<String, String> req = usersData.get(i);
                System.out.println("++++++++++++++++++++++++++++++++++++++++++++");
System.out.println(req);
System.out.println("++++++++++++++++++++++++++++++++++++++++++++");
                Response response = given().auth().basic(username, password).contentType(ContentType.JSON).body(req)
                        .when().post("createusers").then().log().all().statusCode(201).extract().response();

                userId = response.jsonPath().getInt("user_id");
                System.out.println("Created user ID: " + userId);
                System.out.println("-----------------------------------------------------------------------------------------------------------");
            }
        }

        @Test(priority = 3)
        public void testGetUserID() {
            Response response = given().auth().basic(username, password).when().get("users").then().statusCode(200).extract().response();
            AfterPostuserCount = response.jsonPath().getList("users").size();
            System.out.println("Total Number of users: " + AfterPostuserCount);
            int count = AfterPostuserCount - userCount;
            Assert.assertEquals(count, 5); // Assert 5 users are added
            System.out.println("-----------------------------------------------------------------------------------------------------------");
        }

        @Test(priority = 4)
        public void testUpdateUsers() {
            for (int i = 0; i < 5 && i < usersData.size(); i++) { // Only use 5 sets of data
                Map<String, String> req = usersData.get(i);

                Response response = given().auth().basic(username, password).contentType(ContentType.JSON).body(req)
                        .when().put("updateuser/" + userId).then().log().all()
                        .body("user_last_name", equalTo(req.get("user_last_name")))
                        .statusCode(200).extract().response();

                String body = response.getBody().asString();
                System.out.println("Response Body is " + body);
                assertThat(body, containsString("\"user_first_name\":\"" + req.get("user_first_name") + "\""));
                System.out.println("-----------------------------------------------------------------------------------------------------------");
            }
        }

        @Test(priority = 5)
        public void testDeleteUsers() {
            for (int i = 0; i < 5 && i < usersData.size(); i++) { // Only use 5 sets of data
                Response response = given().auth().basic(username, password).when().delete("deleteuser/" + userId).then().log().all().statusCode(200).extract().response();
                System.out.println(response);
                System.out.println("-----------------------------------------------------------------------------------------------------------");
            }
        }
}
