import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;


public class ApiTests {

    private ClassLoader cl = ApiTests.class.getClassLoader();

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "https://reqres.in";
    }


    @Test
    void test1(){
        Response response = (Response) given()
                .when()
                .get("/api/users/2")
                .then()
                .statusCode(200)
                .body("data.first_name", is("Janet"))
                .body("data.last_name", is("Weaver"))
                .extract().response();

        JsonPath jsonPath = response.jsonPath();
        String first_name = jsonPath.get("data.first_name");
        System.out.println(first_name);
    }

    @Test
    void test2(){
        Response response = (Response) given()
                .when()
                .get("/api/users?page=2")
                .then()
                .statusCode(200)
                .extract().response();

        JsonPath jsonPath = response.jsonPath();
        List<String> names = jsonPath.get("data.first_name");
        System.out.println(names.get(2));

        assertThat(names.get(2))
                .isEqualTo("Tobias");
    }

    @Test
    void test3(){
        Map<String, String> user = new HashMap<>();
        user.put("name", "Geralt");
        user.put("job", "Witcher");

        given()
                .contentType(JSON)
                .body(user)
                .when()
                .post("/api/users")
                .then()
                .statusCode(201)
                .body("id", notNullValue());
    }

    @DisplayName("Скачиваем аватар пользователя и сравниваем с локальным изображением")
    @Test
    void test4() throws Exception{
        byte[] image = RestAssured.given()
                .when()
                .get("https://reqres.in/img/faces/2-image.jpg")
                .then().extract().asByteArray();

        try {
            FileOutputStream os = new FileOutputStream(new File("src/test/resources/downloaded.jpg"));
            os.write(image);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        File localFile = new File("src/test/resources/avatar.jpg");
        File downloaded = new File("src/test/resources/downloaded.jpg");

        try(InputStream is1 = new FileInputStream(downloaded); InputStream is2 = new FileInputStream(localFile)) {
            assertThat(new String(is1.readAllBytes()))
                    .contains(new String(is2.readAllBytes()));
        }

    }

    @Test
    void test5(){
        Map<String, String> user = new HashMap<>();
        Map<String, String> error = new HashMap<>();
        user.put("email", "peter@klaven");
        error.put("error", "Missing password");


        given()
                .contentType(JSON)
                .body(user)
                .when()
                .post("/api/login")
                .then()
                .statusCode(400)
                .body("error", is(error.get("error")));
    }

}
