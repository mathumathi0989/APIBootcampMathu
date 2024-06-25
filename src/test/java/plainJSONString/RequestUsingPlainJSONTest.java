package plainJSONString;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static io.restassured.RestAssured.given;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Set;

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
		String requestBody = "{" + "\"user_first_name\": \"gurdev\"," + "\"user_last_name\": \"dheena\","
				+ "\"user_contact_number\": \"2000012005\"," + "\"user_email_id\": \"gurdev@gmail.com\","
				+ "\"userAddress\": {" + "\"plotNumber\": \"pl-0233\"," + "\"street\": \"avenue\","
				+ "\"state\": \"NJ\"," + "\"country\": \"usa\"," + "\"zipCode\": \"1234\"" + "}" + "}";

		Response response = given().auth().basic(username, password).contentType(ContentType.JSON).body(requestBody)
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

	}

	@Test(priority = 3)
	public void testGetUserID() {
		Response response = given().auth().basic(username, password).when().get("user/"+ userId).then().statusCode(200)
				.extract().response();
	
		System.out.println(
				"-----------------------------------------------------------------------------------------------------------");

	}

	@Test(priority = 4)
	public void testUpdateUser() {

		String requestBody = "{\r\n" + "  \"user_first_name\": \"thanya\",\r\n" + "  \"user_last_name\": \"dheena\",\r\n"
				+ "  \"user_contact_number\": \"3051040545\",\r\n" + "  \"user_email_id\": \"thanya@gml.com\",\r\n"
				+ "  \"userAddress\": {\r\n" + "    \"plotNumber\": \"ab-0233\",\r\n"
				+ "    \"street\": \"avenuee\",\r\n" + "    \"state\": \"TNZSd\",\r\n" + "    \"country\": \"USA\",\r\n"
				+ "    \"zipCode\": \"1234\"\r\n" + "  }\r\n" + "}";

		System.out.println(requestBody);
		Response response = given().auth().basic(username, password).contentType(ContentType.JSON).body(requestBody)
				.when().put("updateuser/" + userId).then().log().all().statusCode(200).extract().response();

		String body = response.getBody().asString();
		System.out.println("Response Body is " + body);
		assertThat(body, containsString("\"user_first_name\":\"thanya\""));


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
