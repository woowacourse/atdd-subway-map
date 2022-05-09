package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.request.LineRequest;
import wooteco.subway.dto.response.LineResponse;

@DisplayName("지하철 노선 관련 기능")
class LineAcceptanceTest extends AcceptanceTest {

    private static final LineRequest LINE_REQUEST_신분당선 = new LineRequest("신분당선", "bg-red-600");
    private static final LineRequest LINE_REQUEST_분당선 = new LineRequest("분당선", "bg-red-601");
    private static final LineRequest LINE_REQUEST_1호선 = new LineRequest("1호선", "bg-red-602");

    @Test
    @DisplayName("노선을 추가한다.")
    void createLine() {
        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(LINE_REQUEST_신분당선)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @Test
    @DisplayName("전체 노선 목록을 조회한다.")
    void findAllLine() {
        //given
        ExtractableResponse<Response> createResponse1 = RestAssured.given().log().all()
            .body(LINE_REQUEST_신분당선)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        ExtractableResponse<Response> createResponse2 = RestAssured.given().log().all()
            .body(LINE_REQUEST_분당선)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .get("/lines")
            .then().log().all()
            .extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
            .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
            .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
            .map(LineResponse::getId)
            .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @Test
    @DisplayName("단일 노선을 조회한다.")
    void findLineById() {
        //given
        RestAssured.given().log().all()
            .body(LINE_REQUEST_신분당선)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/lines/1")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("노선을 수정한다.")
    void update() {
        //given
        RestAssured.given().log().all()
            .body(LINE_REQUEST_신분당선)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(LINE_REQUEST_1호선)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put("/lines/1")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("노선을 삭제한다.")
    void delete() {
        //given
        RestAssured.given().log().all()
            .body(LINE_REQUEST_신분당선)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .delete("/lines/1")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
