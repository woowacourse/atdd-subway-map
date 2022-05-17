package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationRequest;

@DisplayName("노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @BeforeEach
    void setUpStation() {
        RestAssured.given().log().all()
            .body(new StationRequest("ㄱ역"))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then().log().all()
            .extract();

        RestAssured.given().log().all()
            .body(new StationRequest("ㄴ역"))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then().log().all()
            .extract();
    }

    @DisplayName("노선을 등록한다.")
    @Test
    void createLine() {
        // given
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 노선 이름을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);
        RestAssured.given().log().all()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then()
            .log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선들을 조회한다.")
    @Test
    void getLines() {
        /// given
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);
        ExtractableResponse<Response> createResponse1 = RestAssured.given().log().all()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        LineRequest lineRequest2 = new LineRequest("다른분당선", "bg-red-600", 1L, 2L, 12);
        ExtractableResponse<Response> createResponse2 = RestAssured.given().log().all()
            .body(lineRequest2)
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

        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
            .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
            .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
            .map(LineResponse::getId)
            .collect(Collectors.toList());

        assertThat(resultLineIds).containsAll(expectedLineIds);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("특정 노선을 조회한다.")
    @Test
    void getLine() {
        /// given
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);
        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .get(uri)
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("특정 노선을 업데이트한다")
    @Test
    public void updateLine() {
        // given
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);
        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        // when
        String uri = createResponse.header("Location");
        LineRequest lineRequest2 = new LineRequest("인간분당선", "bg-blue-600", 1L, 2L, 10);
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(lineRequest2)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put(uri)
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("노선을 삭제한다.")
    @Test
    void deleteLine() {
        // given
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);
        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        // when
        String uri = createResponse.header("Location");
        Long id = Long.parseLong(createResponse.header("Location").split("/")[2]);
        RestAssured.given().log().all()
            .when()
            .delete(uri)
            .then().log().all()
            .extract();

        // then
        ExtractableResponse<Response> linesResponse = RestAssured.given().log().all()
            .when()
            .get("/lines")
            .then().log().all()
            .extract();

        List<Long> resultLineIds = linesResponse.jsonPath().getList(".", LineResponse.class).stream()
            .map(LineResponse::getId)
            .collect(Collectors.toList());

        assertThat(resultLineIds.contains(id)).isFalse();
    }
}
