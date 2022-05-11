package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLines() {
        // given
        LineRequest lineRequest = new LineRequest("분당선", "bg-red-600", 1L, 2L, 10);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();

        assertThat(response.body().jsonPath().getString("name")).isEqualTo("분당선");
        assertThat(response.body().jsonPath().getString("color")).isEqualTo("bg-red-600");
    }

    @DisplayName("지하철 노선 전체를 조회한다.")
    @Test
    void getLines() {
        // given
        LineRequest firstLineRequest = new LineRequest("분당선", "bg-red-600", 1L, 2L, 10);
        LineRequest secondLineRequest = new LineRequest("분당4선", "bg-green-600", 1L, 3L, 10);

        // when
        ExtractableResponse<Response> firstCreateResponse = RestAssured.given().log().all()
                .body(firstLineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        ExtractableResponse<Response> secondCreateResponse = RestAssured.given().log().all()
                .body(secondLineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().jsonPath().getList(".", LineResponse.class)).hasSize(2);
    }

    @DisplayName("지하철 노선 조회")
    @Test
    void getLine() {
        // given
        LineRequest lineRequest = new LineRequest("분당선", "bg-red-600", 1L, 2L, 10);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log()
                .all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        long resultLineId = response.jsonPath().getLong("id");

        ExtractableResponse<Response> newResponse = RestAssured.given().log().all()
                .when()
                .get("/lines/" + resultLineId)
                .then().log().all()
                .extract();

        // then
        assertThat(newResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(resultLineId).isEqualTo(newResponse.jsonPath().getLong("id"));

        assertThat(newResponse.body().jsonPath().getString("name")).isEqualTo("분당선");
        assertThat(newResponse.body().jsonPath().getString("color")).isEqualTo("bg-red-600");
    }

    @DisplayName("지하철 노선 수정")
    @Test
    void modifyLine() {
        // given
        LineRequest lineRequest = new LineRequest("분당선", "bg-red-600", 1L, 2L, 10);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log()
                .all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        long resultLineId = response.jsonPath().getLong("id");
        LineRequest newLineRequest = new LineRequest("4호선", "bg-red-600", 1L, 2L, 10);

        ExtractableResponse<Response> newResponse = RestAssured.given().log()
                .all()
                .body(newLineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + resultLineId)
                .then().log().all()
                .extract();

        // then
        assertThat(newResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선 삭제")
    @Test
    void deleteLine() {
        // given
        LineRequest lineRequest = new LineRequest("분당선", "bg-red-600", 1L, 2L, 10);
        // when
        ExtractableResponse<Response> response = RestAssured.given().log()
                .all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        long resultLineId = response.jsonPath().getLong("id");

        ExtractableResponse<Response> newResponse = RestAssured.given().log()
                .all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/lines/" + resultLineId)
                .then().log().all()
                .extract();

        // then
        assertThat(newResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
