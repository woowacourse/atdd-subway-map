package wooteco.subway.line;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.line.controller.dto.LineRequest;
import wooteco.subway.line.controller.dto.LineResponse;

@DisplayName("지하철 노선 관련 기능")
class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선 등록 성공")
    @Test
    void createLine() {
        // given
        final LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600");

        // when
        final ExtractableResponse<Response> response = postLine(lineRequest);

        // then
        final LineResponse lineResponse = response.body().as(LineResponse.class);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(lineResponse.getName()).isEqualTo(lineRequest.getName());
        assertThat(lineResponse.getColor()).isEqualTo(lineRequest.getColor());
    }

    @DisplayName("지하철 노선 등록 실패 - 중복된 노선 존재")
    @Test
    void createLineWithDuplicateName() {
        // given
        final LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600");
        postLine(lineRequest);

        // when
        final ExtractableResponse<Response> response = postLine(lineRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선 목록 조회 성공")
    @Test
    void showLines() {
        // given
        final LineRequest lineRequest1 = new LineRequest("신분당선", "bg-red-600");
        final ExtractableResponse<Response> createResponse1 = postLine(lineRequest1);

        final LineRequest lineRequest2 = new LineRequest("2호선", "bg-green-600");
        final ExtractableResponse<Response> createResponse2 = postLine(lineRequest2);

        // when
        final ExtractableResponse<Response> response = getLines();

        // then
        final List<Long> resultLineIds = resultLineIds(response);
        final List<Long> expectedLineIds = Arrays.asList(lineId(createResponse1), lineId(createResponse2));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선 이름 변경 성공")
    @Test
    void updateLine() {
        // given
        final LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600");
        final ExtractableResponse<Response> createResponse = postLine(lineRequest);

        // when
        final String uri = createResponse.header("Location");
        final LineRequest updateRequest = new LineRequest("분당선", "bg-red-600");
        final ExtractableResponse<Response> response = updateLine(uri, updateRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선 제거 성공")
    @Test
    void deleteLine() {
        // given
        final LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600");
        final ExtractableResponse<Response> createResponse = postLine(lineRequest);

        // when
        final String uri = createResponse.header("Location");
        final ExtractableResponse<Response> response = deleteLine(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> postLine(final LineRequest lineRequest) {
        return RestAssured
                .given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> getLine(final Long id) {
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when()
                .get("/lines/{id}", id)
                .then().log().all()
                .extract();
        return response;
    }

    private ExtractableResponse<Response> getLines() {
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();
        return response;
    }

    private ExtractableResponse<Response> updateLine(final String uri, final LineRequest updateRequest) {
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(updateRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(uri)
                .then().log().all()
                .extract();
        return response;
    }

    private ExtractableResponse<Response> deleteLine(final String uri) {
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when()
                .delete(uri)
                .then()
                .extract();
        return response;
    }

    private Long lineId(final ExtractableResponse<Response> response) {
        return Long.parseLong(response.header("Location").split("/")[2]);
    }

    private List<Long> resultLineIds(final ExtractableResponse<Response> response) {
        final JsonPath jsonPath = response.jsonPath();
        final List<LineResponse> lineResponses = jsonPath.getList(".", LineResponse.class);

        return lineResponses.stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
    }
}
