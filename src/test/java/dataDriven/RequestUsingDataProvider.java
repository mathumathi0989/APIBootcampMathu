package dataDriven;


import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.*;

import org.json.simple.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

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
		 user.setUser_first_name("vvhu");
		 user.setUser_last_name("ril");
		 user.setUser_contact_number("2993000005");
		 user.setUser_email_id("mzil@gmail.com");
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
		System.out.println(
				"-----------------------------------------------------------------------------------------------------------");
		return userId;

	}


	public void testGetUserID(int userId) {
		
		Response response = given().auth().basic(username, password).when().get("users").then().statusCode(200)
				.extract().response();
		AfterPostuserCount = response.jsonPath().getList("users").size();
		System.out.println("Total Number of users: " + AfterPostuserCount);
		int count = AfterPostuserCount - userCount;
		//System.out.println("One user is added : " + count);
		Assert.assertEquals(count, 1);
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
		 user.setUser_first_name("dummy");
		 user.setUser_last_name("uedfr");
		 user.setUser_contact_number("3051044545");
		 user.setUser_email_id("dummy21@gml.com");
		   user.setUserAddress(userAddress);
		
		Response response = given().auth().basic(username, password).contentType(ContentType.JSON).body(user)
				.when().put("updateuser/" + userId).then().log().all()
				.body("user_last_name", equalTo("uedfr"))
				.statusCode(200).extract().response();

		String body = response.getBody().asString();
		System.out.println("Response Body is " + body);
		assertThat(body, containsString("\"user_first_name\":\"dummy\""));


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

}
