package plainJSONString;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static io.restassured.RestAssured.given;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class RequestUsingPlainJSONTest {

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

	@Test(priority = 1)
	public void testGetUser() {

		Response response = given().auth().basic(username, password).when().get("users").then().statusCode(200).extract().response();
		userCount = response.jsonPath().getList("users").size();
		System.out.println("Total Number of users: " + userCount);
		System.out.println(
				"-----------------------------------------------------------------------------------------------------------");
	}

	@Test(priority = 2)
	public void testCreateUser() {
		String requestBody = "{" + "\"user_first_name\": \"vvhu\"," + "\"user_last_name\": \"ril\","
				+ "\"user_contact_number\": \"2003000005\"," + "\"user_email_id\": \"mazil@gmail.com\","
				+ "\"userAddress\": {" + "\"plotNumber\": \"pl-0233\"," + "\"Street\": \"avenue\","
				+ "\"state\": \"NJ\"," + "\"Country\": \"usa\"," + "\"zipCode\": \"1234\"" + "}" + "}";

		Response response = given().auth().basic(username, password).contentType(ContentType.JSON).body(requestBody)
				.when().post("createusers").then().log().all().statusCode(201).extract().response();

		userId = response.jsonPath().getInt("user_id");
		System.out.println("Created user ID: " + userId);
		System.out.println(
				"-----------------------------------------------------------------------------------------------------------");

	}

	@Test(priority = 3)
	public void testGetUserID() {
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

	@Test(priority = 4)
	public void testUpdateUser() {

		String requestBody = "{\r\n" + "  \"user_first_name\": \"dummy\",\r\n" + "  \"user_last_name\": \"uedfr\",\r\n"
				+ "  \"user_contact_number\": \"3051044545\",\r\n" + "  \"user_email_id\": \"dummy21@gml.com\",\r\n"
				+ "  \"userAddress\": {\r\n" + "    \"plotNumber\": \"ab-0233\",\r\n"
				+ "    \"street\": \"avenuee\",\r\n" + "    \"state\": \"TNZSd\",\r\n" + "    \"country\": \"USA\",\r\n"
				+ "    \"zipCode\": \"1234\"\r\n" + "  }\r\n" + "}";

		System.out.println(requestBody);
		Response response = given().auth().basic(username, password).contentType(ContentType.JSON).body(requestBody)
				.when().put("updateuser/" + userId).then().log().all().statusCode(200).extract().response();

		String body = response.getBody().asString();
		System.out.println("Response Body is " + body);
		assertThat(body, containsString("\"user_first_name\":\"dummy\""));


		System.out.println(
				"-----------------------------------------------------------------------------------------------------------");

	}

	@Test(priority = 5)
	public void testDeleteUser() {

		Response response = given().auth().basic(username, password).when().delete("deleteuser/" + userId).then().log()
				.all().statusCode(200).extract().response();
		System.out.println(response);
		System.out.println(
				"-----------------------------------------------------------------------------------------------------------");

	}

}
