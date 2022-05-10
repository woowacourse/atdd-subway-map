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
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

@DisplayName("지하철 노선 관련 기능")
class LineAcceptanceTest extends AcceptanceTest {
    LineRequest lineRequest1 = new LineRequest("1호선", "blue");
    LineRequest lineRequest2 = new LineRequest("2호선", "green");

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given

        // when
        ExtractableResponse<Response> response = requestLineCreation(lineRequest1);

        // then
        LineResponse actualResponse = response.jsonPath().getObject(".", LineResponse.class);
        LineResponse expectedResponse = LineResponse.of(lineRequest1.toLine(extractIdFromHeader(response)));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성하는 경우 상태코드 400 오류가 발생한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        requestLineCreation(lineRequest1);

        // when
        ExtractableResponse<Response> response = requestLineCreation(lineRequest1);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> requestLineCreation(LineRequest request) {
        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then()
                .log().all()
                .extract();
    }

    @DisplayName("지하철 노선 전체를 조회한다.")
    @Test
    void getLines() {
        /// given
        ExtractableResponse<Response> createResponse1 = requestLineCreation(lineRequest1);
        ExtractableResponse<Response> createResponse2 = requestLineCreation(lineRequest2);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<LineResponse> expectedResponse = Stream.of(createResponse1, createResponse2)
                .map(it -> it.jsonPath().getObject(".", LineResponse.class))
                .collect(Collectors.toList());
        List<LineResponse> actualResponse = response.jsonPath().getList(".", LineResponse.class);
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @DisplayName("id 값에 해당하는 지하철 노선을 조회한다.")
    @Test
    void getLine() {
        /// given
        ExtractableResponse<Response> createResponse = requestLineCreation(lineRequest1);

        // when
        ExtractableResponse<Response> response = requestLineByUri(extractLocationFromHeader(createResponse));

        // then
        LineResponse actualResponse = response.jsonPath().getObject(".", LineResponse.class);
        LineResponse expectedResponse = createResponse.jsonPath().getObject(".", LineResponse.class);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @DisplayName("존재하지 않는 지하철 노선을 조회하는 경우 상태코드 404 오류가 발생한다.")
    @Test
    void getLineNotExists() {
        // given

        // when
        ExtractableResponse<Response> response = requestLineByUri("/lines/1");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    private ExtractableResponse<Response> requestLineByUri(String uri) {
        return RestAssured.given().log().all()
                .when()
                .get(uri)
                .then().log().all()
                .extract();
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        /// given
        ExtractableResponse<Response> createResponse1 = requestLineCreation(lineRequest1);

        // when
        ExtractableResponse<Response> response = requestLineUpdate(extractLocationFromHeader(createResponse1), lineRequest2);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("존재하지 않는 지하철 노선을 수정하는 경우 상태코드 404 오류가 발생한다.")
    @Test
    void updateLineNotExists() {
        // given

        // when
        ExtractableResponse<Response> response = requestLineUpdate("/lines/1", lineRequest1);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 수정하는 경우 상태코드 400 오류가 발생한다.")
    @Test
    void updateLineWithDuplicateName() {
        /// given
        ExtractableResponse<Response> createResponse1 = requestLineCreation(lineRequest1);

        requestLineCreation(lineRequest2);

        // when
        ExtractableResponse<Response> response = requestLineUpdate(extractLocationFromHeader(createResponse1), lineRequest2);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> requestLineUpdate(String uri, LineRequest request) {
        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(uri)
                .then().log().all()
                .extract();
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> createResponse = requestLineCreation(lineRequest1);

        // when
        ExtractableResponse<Response> response = requestLineDeletionByUri(extractLocationFromHeader(createResponse));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("존재하지 않는 지하철 노선을 제거하는 경우 상태코드 404 오류가 발생한다.")
    @Test
    void deleteLineNotExists() {
        // given

        // when
        ExtractableResponse<Response> response = requestLineDeletionByUri("/lines/1");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    private ExtractableResponse<Response> requestLineDeletionByUri(String uri) {
        return RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();
    }
}
