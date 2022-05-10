package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static wooteco.subway.testutils.Fixture.LINE_REQUEST_1호선;
import static wooteco.subway.testutils.Fixture.LINE_REQUEST_분당선;
import static wooteco.subway.testutils.Fixture.LINE_REQUEST_신분당선;
import static wooteco.subway.testutils.Fixture.STATION_REQUEST_강남역;
import static wooteco.subway.testutils.Fixture.STATION_REQUEST_잠실역;

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

    @Test
    @DisplayName("노선을 추가한다.")
    void createLine() {
        //given & when
        AcceptanceTestUtil.requestPostStation(STATION_REQUEST_강남역, "/stations");
        AcceptanceTestUtil.requestPostStation(STATION_REQUEST_잠실역, "/stations");
        ExtractableResponse<Response> response = requestPostLine(LINE_REQUEST_신분당선, "/lines");

        System.out.println("response.body() = " + response.body());

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

    public ExtractableResponse<Response> requestPostLine(final LineRequest requestBody, final String URI) {
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
