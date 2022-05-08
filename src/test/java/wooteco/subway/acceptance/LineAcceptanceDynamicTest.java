package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.LineResponse;

public class LineAcceptanceDynamicTest extends AcceptanceTest {

    @DisplayName("노선을 관리한다.")
    @TestFactory
    Stream<DynamicTest> dynamicTestFromStream() {
        String name1 = "2호선";
        String color1 = "bg-green-600";

        String name2 = "신분당선";
        String color2 = "bg-red-600";

        String name3 = "1호선";
        String color3 = "bg-blue-600";

        return Stream.of(
                dynamicTest("노선을 생성한다.", () -> {
                    ExtractableResponse<Response> response = generateLine(name1, color1);

                    assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
                    assertThat(response.header("Location")).isNotBlank();
                }),
                dynamicTest("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.", () -> {
                    ExtractableResponse<Response> response = generateLine(name1, color1);

                    assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                }),
                dynamicTest("노선을 조회한다.", () -> {
                    generateLine(name2, color2);

                    ExtractableResponse<Response> response = RestAssured.given().log().all()
                            .when()
                            .get("/lines")
                            .then().log().all()
                            .extract();

                    assertAll(
                            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                            () -> {
                                int size = response.jsonPath().getList(".", LineResponse.class).size();
                                assertThat(size).isEqualTo(2);
                            }
                    );
                }),
                dynamicTest("단일 노선을 조회한다.", () -> {
                    ExtractableResponse<Response> createdResponse = generateLine(name3, color3);
                    JsonPath createdResponseJsonPath = createdResponse.body().jsonPath();
                    Long id = createdResponseJsonPath.getLong("id");
                    String name = createdResponseJsonPath.getString("name");
                    String color = createdResponseJsonPath.getString("color");

                    ExtractableResponse<Response> response = RestAssured.given().log().all()
                            .when()
                            .get("/lines/" + id)
                            .then().log().all()
                            .extract();

                    JsonPath responseJsonPath = response.body().jsonPath();
                    assertAll(
                            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                            () -> assertThat(responseJsonPath.getLong("id")).isEqualTo(id),
                            () -> assertThat(responseJsonPath.getString("name")).isEqualTo(name),
                            () -> assertThat(responseJsonPath.getString("color")).isEqualTo(color)
                    );
                }),
                dynamicTest("노선을 수정한다.", () -> {
                    ExtractableResponse<Response> createdResponse = generateLine("3호선", "bg-orange-600");
                    Long id = createdResponse.body().jsonPath().getLong("id");

                    String updateName = "3호선";
                    String updateColor = "bg-blue-600";
                    ExtractableResponse<Response> response = RestAssured.given().log().all()
                            .body(Map.of("name", updateName, "color", updateColor))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .when()
                            .put("/lines/" + id)
                            .then().log().all()
                            .extract();

                    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                }),
                dynamicTest("존재하지 않는 노선을 수정할 경우 해당 id와 body 를 통해 노선을 생성한다.", () -> {
                    Long id = 10L;
                    String name = "4호선";
                    String color = "bg-skyblue-600";

                    ExtractableResponse<Response> response = RestAssured.given().log().all()
                            .body(Map.of("name", name, "color", color))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .when()
                            .put("/lines/" + id)
                            .then().log().all()
                            .extract();

                    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                }),
                dynamicTest("중복된 이름을 가진 노선으로 수정할 경우 예외를 던진다.", () -> {
                    ExtractableResponse<Response> createdResponse = generateLine("5호선", "bg-purple-600");
                    Long id = createdResponse.body().jsonPath().getLong("id");

                    ExtractableResponse<Response> response = RestAssured.given().log().all()
                            .body(Map.of("name", "2호선", "color", "bg-green-600"))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .when()
                            .put("/lines/" + id)
                            .then().log().all()
                            .extract();

                    assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                }),
                dynamicTest("노선을 삭제한다.", () -> {
                    ExtractableResponse<Response> response = RestAssured.given().log().all()
                            .when()
                            .delete("/lines/" + 10L)
                            .then().log().all()
                            .extract();

                    assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
                }),
                dynamicTest("존재하지 않는 노선의 id를 삭제할 경우 동일하게 204를 반환한다.", () -> {
                    Long id = 10L;

                    ExtractableResponse<Response> response = RestAssured.given().log().all()
                            .when()
                            .delete("/lines/" + id)
                            .then().log().all()
                            .extract();

                    assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
                })
        );
    }

    private ExtractableResponse<Response> generateLine(String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }
}
