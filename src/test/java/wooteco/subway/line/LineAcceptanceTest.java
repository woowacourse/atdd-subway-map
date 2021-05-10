package wooteco.subway.line;

import static org.assertj.core.api.Assertions.assertThat;

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
import wooteco.subway.AcceptanceTest;

@DisplayName("노선 관련 기능")
class LineAcceptanceTest extends AcceptanceTest {

    private static final LineRequest LINE_2 = new LineRequest(
        "2호선", "grey darken-1", 1L, 2L, 2, 500
    );

    private static final LineRequest LINE_3 = new LineRequest(
            "3호선", "grey darken-2", 5L, 6L, 12, 1500
    );

    @DisplayName("노선을 생성한다.")
    @Test
    void createLine() {
        final ExtractableResponse<Response> response = 노선_등록(LINE_2);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
        노선_생성값_검증(response, LINE_2);
    }

    private void 노선_생성값_검증(final ExtractableResponse<Response> response, final LineRequest lineRequest) {
        final LineResponse lineResponse = response.body().as(LineResponse.class);
        assertThat(lineResponse.getId()).isEqualTo(getCreatedId(response));
        assertThat(lineResponse.getName()).isEqualTo(lineRequest.getName());
        assertThat(lineResponse.getColor()).isEqualTo(lineRequest.getColor());
    }

    private void 노선_생성값_검증(final ExtractableResponse<Response> response, final LineRequest lineRequest,
        final Long createdId) {
        final LineResponse lineResponse = response.body().as(LineResponse.class);
        assertThat(lineResponse.getId()).isEqualTo(createdId);
        assertThat(lineResponse.getName()).isEqualTo(lineRequest.getName());
        assertThat(lineResponse.getColor()).isEqualTo(lineRequest.getColor());
    }

    private long getCreatedId(final ExtractableResponse<Response> response) {
        return Long.parseLong(response.header("Location").split("/")[2]);
    }

    private ExtractableResponse<Response> 노선_등록(final LineRequest lineRequest) {
        return RestAssured.given().log().all()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
    }

    @DisplayName("기존에 존재하는 노선의 이름으로 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        노선_등록(LINE_2);

        final ExtractableResponse<Response> response = 노선_등록(LINE_2);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선 목록을 조회한다.")
    @Test
    void showLines() {
        final ExtractableResponse<Response> createdResponse1 = 노선_등록(LINE_2);
        final ExtractableResponse<Response> createdResponse2 = 노선_등록(LINE_3);

        final ExtractableResponse<Response> response = 노선_조회();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        final List<Long> expectedLineIds = makeExpectedLineIds(Arrays.asList(createdResponse1, createdResponse2));
        final List<Long> resultLineIds = makeResultLineIds(response);
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    private List<Long> makeExpectedLineIds(final List<ExtractableResponse<Response>> responses) {
        return responses.stream()
            .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
            .collect(Collectors.toList());
    }

    private List<Long> makeResultLineIds(final ExtractableResponse<Response> response) {
        return response.jsonPath().getList(".", LineResponse.class).stream()
            .map(LineResponse::getId)
            .collect(Collectors.toList());
    }

    private ExtractableResponse<Response> 노선_조회() {
        return RestAssured.given().log().all()
            .when()
            .get("/lines")
            .then().log().all()
            .extract();
    }

    @DisplayName("노선을 조회한다.")
    @Test
    void showLine() {
        final ExtractableResponse<Response> createResponse = 노선_등록(LINE_2);
        final Long createdId = Long.parseLong(createResponse.header("Location").split("/")[2]);

        final ExtractableResponse<Response> response = 노선_조회(createdId);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        노선_생성값_검증(response, LINE_2, createdId);
    }

    private ExtractableResponse<Response> 노선_조회(final Long createId) {
        return RestAssured.given().log().all()
            .when()
            .get("/lines/" + createId)
            .then().log().all()
            .extract();
    }

    @DisplayName("없는 노선을 조회한다.")
    @Test
    void showNotExistLine() {
        final ExtractableResponse<Response> response = 노선_조회(2000000L);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("노선을 수정한다.")
    @Test
    void updateLine() {
        final ExtractableResponse<Response> createResponse = 노선_등록(LINE_2);
        final String uri = createResponse.header("Location");

        final LineRequest updatedRequest = new LineRequest("3호선", "grey darken-2");
        final ExtractableResponse<Response> response = 노선_수정(uri, updatedRequest);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> 노선_수정(final String uri, final LineRequest updatedRequest) {
        return RestAssured.given().log().all()
            .body(updatedRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put(uri)
            .then().log().all()
            .extract();
    }

    @DisplayName("없는 노선을 수정한다.")
    @Test
    void updateNotExistLine() {
        final LineRequest updatedRequest = new LineRequest("3호선", "grey darken-2");
        final ExtractableResponse<Response> response = 노선_수정("/lines/2000000", updatedRequest);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("노선을 제거한다.")
    @Test
    void deleteLine() {
        final ExtractableResponse<Response> createResponse = 노선_등록(LINE_2);
        final String uri = createResponse.header("Location");

        final ExtractableResponse<Response> response = 노선_제거(uri);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> 노선_제거(final String uri) {
        return RestAssured.given().log().all()
            .when()
            .delete(uri)
            .then().log().all()
            .extract();
    }

    @DisplayName("없는 노선을 제거한다.")
    @Test
    void deleteNotExistLine() {
        final ExtractableResponse<Response> response = 노선_제거("/lines/2000000");

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }
}
