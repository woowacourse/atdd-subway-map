package wooteco.subway.acceptance;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.response.ResponseBodyExtractionOptions;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.LineResponse;

@DisplayName("노선 관련 기능")
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

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        String name = "2호선";
        String color = "bg-green-600";
        generateLine(name, color);

        ExtractableResponse<Response> response = generateLine(name, color);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선을 조회한다.")
    @Test
    void getLines() {
        ExtractableResponse<Response> createdResponse1 = generateLine("2호선", "bg-green-600");
        ExtractableResponse<Response> createdResponse2 = generateLine("신분당선", "bg-red-600");

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
    }

    @DisplayName("단일 노선을 조회한다.")
    @Test
    void getLine() {
        ExtractableResponse<Response> createdResponse = generateLine("1호선", "bg-blue-600");
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
    }

    @DisplayName("노선을 수정한다.")
    @Test
    void updateLine() {
        ExtractableResponse<Response> createdResponse = generateLine("2호선", "bg-green-600");
        Long id = createdResponse.body().jsonPath().getLong("id");

        String updateName = "2호선";
        String updateColor = "bg-blue-600";
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(Map.of("name", updateName, "color", updateColor))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + id)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("존재하지 않는 노선을 수정할 경우 해당 id와 body 를 통해 노선을 생성한다.")
    @Test
    void updateLineWithNoneId() {
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

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("중복된 이름을 가진 노선으로 수정할 경우 예외를 던진다.")
    @Test
    void updateLineWithDuplicateName() {
        generateLine("2호선", "bg-green-600");
        ExtractableResponse<Response> createdResponse = generateLine("3호선", "bg-orange-600");
        Long id = createdResponse.body().jsonPath().getLong("id");

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(Map.of("name", "2호선", "color", "bg-green-600"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + id)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
    
    @DisplayName("노선을 삭제한다.")
    @Test
    void deleteLine() {
        ExtractableResponse<Response> createdResponse = generateLine("2호선", "bg-green-600");
        Long id = createdResponse.body().jsonPath().getLong("id");

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete("/lines/" + id)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("존재하지 않는 노선의 id를 삭제할 경우 동일하게 204를 반환한다.")
    @Test
    void deleteLineWithNoneId() {
        Long id = 10L;

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete("/lines/" + id)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
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
