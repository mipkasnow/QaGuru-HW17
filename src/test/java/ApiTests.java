import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;


public class ApiTests {


    @Test
    void test1(){
        Response response = (Response) given()
                .when()
                .get("https://reqres.in/api/users/2")
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
                .get("https://reqres.in/api/users?page=2")
                .then()
                .statusCode(200)
                .extract().response();

        JsonPath jsonPath = response.jsonPath();
        List<String> names = jsonPath.get("data.first_name");
        System.out.println(names.get(2));

        assertThat(names.get(2))
                .isEqualTo("Tobias");
    }

}
