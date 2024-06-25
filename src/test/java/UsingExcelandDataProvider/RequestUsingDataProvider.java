package UsingExcelandDataProvider;


import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import static org.hamcrest.Matchers.*;

import org.json.simple.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import testCasesUsingPOJO.UserAddress;
import testCasesUsingPOJO.user;

public class RequestUsingDataProvider {

	protected String baseURI;
	protected String username;
	protected String password;
	public int userId;
	public int userCount;
	public int AfterPostuserCount;

	@BeforeTest
	public void setup() {

		baseURI = "https://userserviceapi-a54ceee3346a.herokuapp.com/uap/";
		RestAssured.baseURI = baseURI;
		username = "Numpy@gmail.com";
		password = "userapi123";

	}


	@DataProvider(name="userDataProvider")
	public Object[][] userDataProvider(){
		 UserAddress userAddress = new UserAddress();
		 userAddress.setPlotNumber("pl-0233");
		 userAddress.setStreet("avenue");
		 userAddress.setState("NJ");
		 userAddress.setCountry("usa");
		 userAddress.setZipCode("1234");
		 user user= new user();
		 user.setUser_first_name("sharanya");
		 user.setUser_last_name("jaash");
		 user.setUser_contact_number("2903000005");
		 user.setUser_email_id("jaashvi@gmail.com");
		   user.setUserAddress(userAddress);
		
		   UserAddress userAddress1 = new UserAddress();
			 userAddress1.setPlotNumber("nj-0233");
			 userAddress1.setStreet("ajfk");
			 userAddress1.setState("MI");
			 userAddress1.setCountry("usa");
			 userAddress1.setZipCode("1234");
			 user user1= new user();
			 user1.setUser_first_name("nhtham");
			 user1.setUser_last_name("alab");
			 user1.setUser_contact_number("4125712145");
			 user1.setUser_email_id("uham@gmail.com");
			   user1.setUserAddress(userAddress1);
		
		return new Object[][] {{user},{user1}};
		
	}
	

	@Test(priority = 1, dataProvider="userDataProvider")
	 public void runTestSequence(user user) {
		testGetUser();

     testCreateUser(user);
     testGetUserID(userId);
     testUpdateUser(userId);
    
     
  testDeleteUser(userId);
		
	 }
	

	public void testGetUser() {

		Response response = given().auth().basic(username, password).when().get("users").then().statusCode(200).extract().response();
		userCount = response.jsonPath().getList("users").size();
		System.out.println("Total Number of users: " + userCount);
		System.out.println(
				"-----------------------------------------------------------------------------------------------------------");
	}

	

	public int testCreateUser(user user) {
		


		Response response = given().auth().basic(username, password).contentType(ContentType.JSON).body(user)
				.when().post("createusers").then().log().all().statusCode(201).extract().response();

		userId = response.jsonPath().getInt("user_id");
		System.out.println("Created user ID: " + userId);
		// Validate the response JSON schema
        validateJsonSchema(response.asString(), "src/test/java/Utilities/user-schema.json");
        //Validate after creating user
    	AfterPostuserCount = given().auth().basic(username, password).when().get("users").then().extract().response().jsonPath().getList("users").size();
		System.out.println("Total Number of users after post: " + AfterPostuserCount);
		int count = AfterPostuserCount - userCount;
		
		Assert.assertEquals(count, 1);
		
		System.out.println(
				"-----------------------------------------------------------------------------------------------------------");
		return userId;

	}


	public void testGetUserID(int userId) {
		
		Response response = given().auth().basic(username, password).when().get("user/" + userId).then().statusCode(200)
				.extract().response();

		System.out.println(
				"-----------------------------------------------------------------------------------------------------------");

	}

	
	public void testUpdateUser(int userId) {
		 
		 UserAddress userAddress = new UserAddress();
		 userAddress.setPlotNumber("ab-0233");
		 userAddress.setStreet("avenuee");
		 userAddress.setState("TNZSd");
		 userAddress.setCountry("USA");
		 userAddress.setZipCode("1234");
		 user user= new user();
		 user.setUser_first_name("jaashvi");
		 user.setUser_last_name("shara");
		 user.setUser_contact_number("3051004545");
		 user.setUser_email_id("sharan@gml.com");
		   user.setUserAddress(userAddress);
		
		Response response = given().auth().basic(username, password).contentType(ContentType.JSON).body(user)
				.when().put("updateuser/" + userId).then().log().all()
				.body("user_last_name", equalTo("shara"))
				.statusCode(200).extract().response();

		String body = response.getBody().asString();
		System.out.println("Response Body is " + body);
		assertThat(body, containsString("\"user_first_name\":\"jaashvi\""));


		System.out.println(
				"-----------------------------------------------------------------------------------------------------------");

	}

	
	public void testDeleteUser(int userId) {

		Response response = given().auth().basic(username, password).when().delete("deleteuser/" + userId).then().log()
				.all().statusCode(200).extract().response();
		System.out.println(response);
		System.out.println(
				"-----------------------------------------------------------------------------------------------------------");

	}
	
	private void validateJsonSchema(String jsonResponse, String schemaPath) {
        try {
        	 Path path = Paths.get(schemaPath);
             System.out.println("Schema file absolute path: " + path.toAbsolutePath());
             
             // Check if the file exists
             if (!Files.exists(path)) {
                 throw new RuntimeException("Schema file does not exist at path: " + path.toAbsolutePath());
             }
             
            JsonSchemaFactory schemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
            JsonSchema schema = schemaFactory.getSchema(Files.newInputStream(Paths.get(schemaPath)));

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonResponse);

            Set<ValidationMessage> validationMessages = schema.validate(jsonNode);

            if (!validationMessages.isEmpty()) {
                for (ValidationMessage message : validationMessages) {
                    System.out.println("Validation Message: " + message.getMessage());
                }
                Assert.fail("JSON validation against the schema failed.");
            } else {
                System.out.println("JSON is valid against the schema.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("JSON validation against the schema failed: " + e.getMessage());
        }
        
	}
	

}
