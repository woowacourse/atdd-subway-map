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
import wooteco.subway.dto.request.LineRequest;
import wooteco.subway.dto.response.LineResponse;

@DisplayName("지하철 노선 관련 기능")
class LineAcceptanceTest extends AcceptanceTest {

    private static final LineRequest LINE_REQUEST_신분당선 = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);
    private static final LineRequest LINE_REQUEST_분당선 = new LineRequest("분당선", "bg-red-601", 3L, 4L, 12);
    private static final LineRequest LINE_REQUEST_1호선 = new LineRequest("1호선", "bg-red-602", 5L, 6L, 14);

    @Test
    @DisplayName("노선을 추가한다.")
    void createLine() {
        // when
        ExtractableResponse<Response> response = requestPostLine(LINE_REQUEST_신분당선, "/lines");

        // then
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
            () -> assertThat(response.header("Location")).isNotBlank()
        );
    }

    @Test
    @DisplayName("전체 노선 목록을 조회한다.")
    void findAllLine() {
        //given
        ExtractableResponse<Response> createResponse1 = requestPostLine(LINE_REQUEST_신분당선, "/lines");

        ExtractableResponse<Response> createResponse2 = requestPostLine(LINE_REQUEST_분당선, "/lines");

        //when
        ExtractableResponse<Response> response = requestGetLines("/lines");
        List<Long> expectedLineIds = getExpectedLineIds(createResponse1, createResponse2);
        List<Long> resultLineIds = getResultLineIds(response);

        //then
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(resultLineIds).containsAll(expectedLineIds)
        );
    }

    @Test
    @DisplayName("단일 노선을 조회한다.")
    void findLineById() {
        //given
        requestPostLine(LINE_REQUEST_신분당선, "/lines");

        //when
        ExtractableResponse<Response> response = requestGetLines("/lines/1");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("노선을 수정한다.")
    void update() {
        //given
        requestPostLine(LINE_REQUEST_신분당선, "/lines");

        //when
        ExtractableResponse<Response> response = requestPutLine(LINE_REQUEST_1호선, "/lines/1");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("노선을 삭제한다.")
    void delete() {
        //given
        requestPostLine(LINE_REQUEST_신분당선, "/lines");

        //when
        ExtractableResponse<Response> response = requestDeleteLine("/lines/1");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> requestPostLine(final LineRequest requestBody, final String URI) {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(requestBody)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post(URI)
            .then().log().all()
            .extract();
        return response;
    }

    private ExtractableResponse<Response> requestGetLines(final String URI) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get(URI)
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> requestPutLine(final LineRequest requestBody, final String URI) {
        return RestAssured.given().log().all()
            .body(requestBody)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put(URI)
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> requestDeleteLine(final String URI) {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .delete(URI)
            .then().log().all()
            .extract();
        return response;
    }

    private List<Long> getExpectedLineIds(final ExtractableResponse<Response> createResponse1,
                                          final ExtractableResponse<Response> createResponse2) {
        return Stream.of(createResponse1, createResponse2)
            .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
            .collect(Collectors.toList());
    }

    private List<Long> getResultLineIds(final ExtractableResponse<Response> response) {
        return response.jsonPath().getList(".", LineResponse.class).stream()
            .map(LineResponse::getId)
            .collect(Collectors.toList());
    }
}
