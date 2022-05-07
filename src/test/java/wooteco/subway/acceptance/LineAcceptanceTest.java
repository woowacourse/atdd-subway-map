package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

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
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("노선을 생성한다.")
    @Test
    void createLine() {
        // given
        final LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", null, null, 0);

        // when
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.header("Location")).isNotBlank()
        );
    }

    @DisplayName("노션을 조회한다.")
    @Test
    void getLines() {
        /// given
        final LineRequest lineRequest1 = new LineRequest("신분당선", "bg-red-600", null, null, 0);

        final ExtractableResponse<Response> createResponse1 = RestAssured.given().log().all()
                .body(lineRequest1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        final LineRequest lineRequest2 = new LineRequest("분당선", "bg-green-600", null, null, 0);

        final ExtractableResponse<Response> createResponse2 = RestAssured.given().log().all()
                .body(lineRequest2)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // when
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();

        // then
        final List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        final List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(resultLineIds).containsAll(expectedLineIds)
        );
    }

    @DisplayName("개별 노선을 ID 값으로 조회한다.")
    @Test
    void getLineById() {
        /// given
        final LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", null, null, 0);

        final ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
        final Long createId = createResponse.jsonPath().getLong("id");

        // when
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines/" + createId)
                .then().log().all()
                .extract();

        // then
        final Long id = response.jsonPath().getLong("id");
        final String name = response.jsonPath().getString("name");
        final String color = response.jsonPath().getString("color");

        assertAll(
                () -> assertThat(id).isEqualTo(createId),
                () -> assertThat(name).isEqualTo("신분당선"),
                () -> assertThat(color).isEqualTo("bg-red-600")
        );
    }

    @DisplayName("노선 정보를 수정한다.")
    @Test
    void updateLineById() {
        // given
        final LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", null, null, 0);

        final ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
        final String uri = createResponse.header("Location");

        // when
        final String name = "다른분당선";
        final String color = "bg-red-600";
        final LineRequest updateRequest = new LineRequest(name, color, null, null, 0);

        RestAssured.given().log().all()
                .body(updateRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(uri)
                .then().log().all()
                .extract();

        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get(uri)
                .then().log().all()
                .extract();

        // then
        final String responseName = response.jsonPath().getString("name");
        final String responseColor = response.jsonPath().getString("color");

        assertAll(
                () -> assertThat(responseName).isEqualTo(name),
                () -> assertThat(responseColor).isEqualTo(color)
        );
    }

    @DisplayName("노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        final LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", null, null, 0);

        final ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // when
        final String uri = createResponse.header("Location");
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철역을 생성할 경우 예외를 발생한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        final LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", null, null, 0);

        RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // when

        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
