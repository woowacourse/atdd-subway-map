package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private static LineRequest 신분당선 = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(신분당선)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        RestAssured.given().log().all()
                .body(신분당선)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // when
        LineRequest 초록신분당선 = new LineRequest("신분당선", "bg-green-600", 1L, 2L, 10);
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(초록신분당선)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then()
                .log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("기존에 존재하는 지하철 노선 색상으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicateColor() {
        // given
        RestAssured.given().log().all()
                .body(신분당선)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // when
        LineRequest 다른신분당선 = new LineRequest("다른신분당선", "bg-red-600", 1L, 2L, 10);
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(다른신분당선)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then()
                .log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("전체 지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        /// given
        ExtractableResponse<Response> createResponse1 = RestAssured.given().log().all()
                .body(신분당선)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        LineRequest 분당선 = new LineRequest("분당선", "bg-green-600", 1L, 2L, 10);
        ExtractableResponse<Response> createResponse2 = RestAssured.given().log().all()
                .body(분당선)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        /// given
        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(신분당선)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        int expectedLineId = Integer.parseInt(createResponse.header("Location").split("/")[2]);

        // when & then
        RestAssured.given().log().all()
                .when()
                .get("/lines/" + expectedLineId)
                .then().log().all()
                .body("id", equalTo(expectedLineId));
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(신분당선)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
        int expectedLineId = Integer.parseInt(createResponse.header("Location").split("/")[2]);

        // when
        LineRequest 초록다른분당선 = new LineRequest("다른분당선", "bg-green-600", 3L, 4L, 20);
        ExtractableResponse<Response> updateResponse = RestAssured.given().log().all()
                .body(초록다른분당선)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + expectedLineId)
                .then().log().all()
                .extract();

        // then
        RestAssured.given().log().all()
                .when()
                .get("/lines/" + expectedLineId)
                .then().log().all()
                .body("id", equalTo(expectedLineId))
                .body("name", equalTo("다른분당선"))
                .body("color", equalTo("bg-green-600"));
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(신분당선)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
