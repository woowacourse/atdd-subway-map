package wooteco.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.AcceptanceTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("노선을 생성한다.")
    @Test
    void createLine() {

        // given
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-red-600");
        params.put("name", "신분당선");

        // when
        ExtractableResponse<Response> response = RestAssured.given()
                                                            .log()
                                                            .all()
                                                            .body(params)
                                                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                                                            .when()
                                                            .post("/lines")
                                                            .then()
                                                            .log()
                                                            .all()
                                                            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();

    }

    @DisplayName("기존에 존재하는 노선 이름으로 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-red-600");
        params.put("name", "신분당선");
        RestAssured.given()
                   .log()
                   .all()
                   .body(params)
                   .contentType(MediaType.APPLICATION_JSON_VALUE)
                   .when()
                   .post("/lines")
                   .then()
                   .log()
                   .all()
                   .extract();

        // when
        ExtractableResponse<Response> response = RestAssured.given()
                                                            .log()
                                                            .all()
                                                            .body(params)
                                                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                                                            .when()
                                                            .post("/lines")
                                                            .then()
                                                            .log()
                                                            .all()
                                                            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("모든 노선을 조회한다.")
    @Test
    void getLines() {
        /// given
        Map<String, String> params1 = new HashMap<>();
        params1.put("color", "bg-red-600");
        params1.put("name", "신분당선");
        ExtractableResponse<Response> createResponse1 = RestAssured.given()
                                                                   .log()
                                                                   .all()
                                                                   .body(params1)
                                                                   .contentType(MediaType.APPLICATION_JSON_VALUE)
                                                                   .when()
                                                                   .post("/lines")
                                                                   .then()
                                                                   .log()
                                                                   .all()
                                                                   .extract();

        Map<String, String> params2 = new HashMap<>();
        params2.put("color", "bg-green-600");
        params2.put("name", "2호선");
        ExtractableResponse<Response> createResponse2 = RestAssured.given()
                                                                   .log()
                                                                   .all()
                                                                   .body(params2)
                                                                   .contentType(MediaType.APPLICATION_JSON_VALUE)
                                                                   .when()
                                                                   .post("/lines")
                                                                   .then()
                                                                   .log()
                                                                   .all()
                                                                   .extract();

        // when
        ExtractableResponse<Response> response = RestAssured.given()
                                                            .log()
                                                            .all()
                                                            .when()
                                                            .get("/lines")
                                                            .then()
                                                            .log()
                                                            .all()
                                                            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
                                           .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                                           .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath()
                                           .getList(".", LineResponse.class)
                                           .stream()
                                           .map(LineResponse::getId)
                                           .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("단일 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-red-600");
        params.put("name", "신분당선");
        ExtractableResponse<Response> createResponse = RestAssured.given()
                                                                  .log()
                                                                  .all()
                                                                  .body(params)
                                                                  .contentType(MediaType.APPLICATION_JSON_VALUE)
                                                                  .when()
                                                                  .post("/lines")
                                                                  .then()
                                                                  .log()
                                                                  .all()
                                                                  .extract();

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssured.given()
                                                            .log()
                                                            .all()
                                                            .when()
                                                            .get(uri)
                                                            .then()
                                                            .log()
                                                            .all()
                                                            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        final LineResponse lineResponse = response.body().as(LineResponse.class);
        assertThat(lineResponse.getColor()).isEqualTo("bg-red-600");
        assertThat(lineResponse.getName()).isEqualTo("신분당선");
    }

    @DisplayName("노선을 수정한다.")
    @Test
    void editLine() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-red-600");
        params.put("name", "신분당선");
        ExtractableResponse<Response> createResponse = RestAssured.given()
                                                                  .log()
                                                                  .all()
                                                                  .body(params)
                                                                  .contentType(MediaType.APPLICATION_JSON_VALUE)
                                                                  .when()
                                                                  .post("/lines")
                                                                  .then()
                                                                  .log()
                                                                  .all()
                                                                  .extract();

        // when
        Map<String, String> params2 = new HashMap<>();
        params2.put("color", "bg-green-600");
        params2.put("name", "2호선");
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssured.given()
                                                            .log()
                                                            .all()
                                                            .body(params2)
                                                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                                                            .when()
                                                            .put(uri)
                                                            .then()
                                                            .log()
                                                            .all()
                                                            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        final LineResponse lineResponse = RestAssured.get(uri).as(LineResponse.class);
        assertThat(lineResponse.getColor()).isEqualTo("bg-green-600");
        assertThat(lineResponse.getName()).isEqualTo("2호선");
    }

    @DisplayName("노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-red-600");
        params.put("name", "신분당선");
        ExtractableResponse<Response> createResponse = RestAssured.given()
                                                                  .log()
                                                                  .all()
                                                                  .body(params)
                                                                  .contentType(MediaType.APPLICATION_JSON_VALUE)
                                                                  .when()
                                                                  .post("/lines")
                                                                  .then()
                                                                  .log()
                                                                  .all()
                                                                  .extract();

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssured.given()
                                                            .log()
                                                            .all()
                                                            .when()
                                                            .delete(uri)
                                                            .then()
                                                            .log()
                                                            .all()
                                                            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
