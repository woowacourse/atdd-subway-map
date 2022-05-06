package wooteco.subway.acceptance;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;

public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLines() {
        // given
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600");

        // then
        RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .body("name", is("신분당선"))
                .body("color", is("bg-red-600"))
                .header("Location", "/lines/1");
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLines() {
        // given
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600");
        LineRequest newLineRequest = new LineRequest("분당선", "bg-green-600");

        // when
        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .body("name", is("신분당선"))
                .body("color", is("bg-red-600"))
                .extract();

        ExtractableResponse<Response> newCreateResponse = RestAssured.given().log().all()
                .body(newLineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .body("name", is("분당선"))
                .body("color", is("bg-green-600"))
                .extract();

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();

        // then
        List<Long> expectedLineIds = Stream.of(createResponse, newCreateResponse)
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선 조회")
    @Test
    void getLine() {
        // given
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log()
                .all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .body("name", is("신분당선"))
                .body("color", is("bg-red-600"))
                .extract();

        long resultLineId = response.jsonPath().getLong("id");

        ExtractableResponse<Response> newResponse = RestAssured.given().log().all()
                .when()
                .get("/lines/" + resultLineId)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();

        // then
        assertThat(resultLineId).isEqualTo(newResponse.jsonPath().getLong("id"));
    }

    @DisplayName("지하철 노선 수정")
    @Test
    void modifyLine() {
        // given
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log()
                .all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .body("name", is("신분당선"))
                .body("color", is("bg-red-600"))
                .extract();

        long resultLineId = response.jsonPath().getLong("id");

        RestAssured.given().log()
                .all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + resultLineId)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }

    @DisplayName("지하철 노선 삭제")
    @Test
    void deleteLine() {
        // given
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log()
                .all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .body("name", is("신분당선"))
                .body("color", is("bg-red-600"))
                .extract();

        long resultLineId = response.jsonPath().getLong("id");

        //then
        RestAssured.given().log()
                .all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/lines/" + resultLineId)
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
    }
}
