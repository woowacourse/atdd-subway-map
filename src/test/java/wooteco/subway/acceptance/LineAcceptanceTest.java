package wooteco.subway.acceptance;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.response.ResponseBodyExtractionOptions;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.LineResponse;

@DisplayName("노선 관련 기능")
@Disabled
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("노선을 생성한다.")
    @Test
    void createLine() {
        String name = "2호선";
        String color = "bg-green-600";

        ExtractableResponse<Response> response = generateLine(name, color);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("노선을 관리한다.")
    @TestFactory
    Stream<DynamicTest> dynamicTestStream() {
        ExtractableResponse<Response> createdResponse1 = generateLine("1호선", "bg-blue-600");
        ExtractableResponse<Response> createdResponse2 = generateLine("2호선", "bg-green-600");

        return Stream.of(
                dynamicTest("노선을 조회한다.", () -> {
                    ExtractableResponse<Response> response = RestAssured.given().log().all()
                            .when()
                            .get("/lines")
                            .then().log().all()
                            .extract();

                    List<Long> expectedLineIds = List.of(createdResponse1, createdResponse2)
                            .stream()
                            .map(ExtractableResponse::response)
                            .map(ResponseBodyExtractionOptions::jsonPath)
                            .map(it -> it.getLong("id"))
                            .collect(toList());
                    List<Long> resultLineIds = response.jsonPath()
                            .getList(".", LineResponse.class)
                            .stream()
                            .map(it -> it.getId())
                            .collect(toList());
                    assertAll(
                            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                            () -> assertThat(resultLineIds).containsAll(expectedLineIds)
                    );
                }),

                dynamicTest("단일 노선을 조회한다.", () -> {
                    JsonPath createdResponseJsonPath = createdResponse1.body().jsonPath();
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
                    Long id = createdResponse1.body().jsonPath().getLong("id");

                    String updateName = "1호선";
                    String updateColor = "bg-green-600";
                    ExtractableResponse<Response> response = RestAssured.given().log().all()
                            .body(Map.of("name", updateName, "color", updateColor))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .when()
                            .put("/lines/" + id)
                            .then().log().all()
                            .extract();

                    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                }),

                dynamicTest("존재하지 않는 노선을 수정할 경우 404를 반환한다.", () -> {
                    Long id = 10L;
                    String name = "2호선";
                    String color = "bg-blue-600";

                    ExtractableResponse<Response> response = RestAssured.given().log().all()
                            .body(Map.of("name", name, "color", color))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .when()
                            .put("/lines/" + id)
                            .then().log().all()
                            .extract();

                    assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
                }),

                dynamicTest("중복된 이름을 가진 노선으로 수정할 경우 예외를 던진다.", () -> {
                    Long id = createdResponse1.body().jsonPath().getLong("id");

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
                    Long id = createdResponse1.body().jsonPath().getLong("id");

                    ExtractableResponse<Response> response = RestAssured.given().log().all()
                            .when()
                            .delete("/lines/" + id)
                            .then().log().all()
                            .extract();

                    assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
                }),

                dynamicTest("존재하지 않는 노선의 id를 삭제할 경우 잘못된 요청이므로 404를 반환한다.", () -> {
                    Long id = createdResponse1.body().jsonPath().getLong("id");

                    ExtractableResponse<Response> response = RestAssured.given().log().all()
                            .when()
                            .delete("/lines/" + id)
                            .then().log().all()
                            .extract();

                    assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
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
