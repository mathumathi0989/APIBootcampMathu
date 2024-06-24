package UsingExcelandDataProvider;


import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class RequestUsingExcel {

	 protected String baseURI;
     protected String username;
     protected String password;
     public int userId;
     public int userCount;
     public int AfterPostuserCount;

     private ExcelUtils excelUtils;
     private List<Map<String, Object>> usersData;

     @BeforeTest
     public void setup() {
         baseURI = "https://userserviceapi-a54ceee3346a.herokuapp.com/uap/";
         RestAssured.baseURI = baseURI;
         username = "Numpy@gmail.com";
         password = "userapi123";
         excelUtils = new ExcelUtils("src/test/java/Utilities/APIDummyData.xlsx"); 
         usersData = excelUtils.getUserData("Sheet1");
     }

@Test
     public void testCRUDUsers() {
    	 ObjectMapper objectMapper = new ObjectMapper(); 
    	 for (Map<String, Object> userData : usersData) {
    		 //Get all users
    		 Response response = given().auth().basic(username, password).when().get("users").then().statusCode(200).extract().response();
             userCount = response.jsonPath().getList("users").size();
             System.out.println("Total Number of users: " + userCount);
             System.out.println("-----------------------------------------------------------------------------------------------------------");
       
             // Create user
             
             try {
            
                 String jsonBody = objectMapper.writeValueAsString(userData);

                 System.out.println("++++++++++++++++++++++++++++++++++++++++++++");
                 System.out.println(jsonBody);
                 System.out.println("++++++++++++++++++++++++++++++++++++++++++++");

                 Response createresponse = given()
                         .auth().basic(username, password)
                         .contentType(ContentType.JSON)
                         .body(jsonBody)
                         .when().post("createusers")
                         .then().log().all().statusCode(201).extract().response();

                 userId = createresponse.jsonPath().getInt("user_id");
                 System.out.println("Created user ID: " + userId);
                 System.out.println("-----------------------------------------------------------------------------------------------------------");
             } catch (Exception e) {
                 e.printStackTrace();
             }
       
    //Get by id
        Response getresponse = given().auth().basic(username, password).when().get("users").then().statusCode(200).extract().response();
         AfterPostuserCount = getresponse.jsonPath().getList("users").size();
         System.out.println("Total Number of users: " + AfterPostuserCount);
         int count = AfterPostuserCount - userCount;
         Assert.assertEquals(count, 1); 
         System.out.println("-----------------------------------------------------------------------------------------------------------");
    
//update user


             try {
                 // Convert Map to JSON string
                 String jsonBody = objectMapper.writeValueAsString(userData);

                 Response updateresponse = given()
                         .auth().basic(username, password)
                         .contentType(ContentType.JSON)
                         .body(jsonBody)
                         .when().put("updateuser/" + userId)
                         .then().log().all()                     
                         .statusCode(200).extract().response();

                 String body = updateresponse.getBody().asString();
                 System.out.println("Response Body is " + body);
                 
                 System.out.println("-----------------------------------------------------------------------------------------------------------");
             } catch (Exception e) {
                 e.printStackTrace();
             }
         
     

  //delete user
     
             Response deleteresponse = given().auth().basic(username, password).when().delete("deleteuser/" + userId).then().log().all().statusCode(200).extract().response();
             System.out.println("delete response "+deleteresponse);
             System.out.println("-----------------------------------------------------------------------------------------------------------");
         }
     
     }
}
