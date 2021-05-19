package wooteco.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.line.controller.dto.LineRequest;
import wooteco.subway.line.controller.dto.LineResponse;
import wooteco.subway.station.StationAcceptanceTest;
import wooteco.subway.station.controller.dto.StationRequest;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선 생성한다.")
    @Test
    void createLine() {
        // given
        Long stationsId1 = 지하철역_생성(new StationRequest("강남역"));
        Long stationsId2 = 지하철역_생성(new StationRequest("양재역"));
        LineRequest lineRequest = new LineRequest("2호선", "bg-red-600", stationsId1, stationsId2, 10);

        //when
        ExtractableResponse<Response> response = saveLine(lineRequest);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("지하철 노선 목록을 보여준다.")
    @Test
    void showLines() {
        // given
        ExtractableResponse<Response> createResponse1 = saveLine(new LineRequest("2호선", "bg-red-600", 1L, 2L, 10));
        ExtractableResponse<Response> createResponse2 = saveLine(new LineRequest("3호선", "bg-blue-600", 2L, 3L, 10));

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = getExpectedLineIds(createResponse1, createResponse2);
        List<Long> resultLineIds = getResultLineIds(response);
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선 1개를 보여준다.")
    @Test
    void showLine() {
        // given
        Long stationsId1 = 지하철역_생성(new StationRequest("강남역"));
        Long stationsId2 = 지하철역_생성(new StationRequest("양재역"));
        LineRequest lineRequest = new LineRequest("2호선", "bg-red-600", stationsId1, stationsId2, 10);
        ExtractableResponse<Response> createResponse = saveLine(lineRequest);

        // when
        String uri = getUri(createResponse);
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get(uri)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        Long expectedLineId = getExpectedLineId(createResponse);
        Long resultLineId = response.jsonPath().getObject(".", LineResponse.class).getId();
        assertThat(resultLineId).isEqualTo(expectedLineId);
    }

    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteLine() {
        // given
        LineRequest lineRequest1 = new LineRequest("2호선", "bg-red-600", 1L, 2L, 10);
        saveLine(lineRequest1);

        LineRequest lineRequest2 = new LineRequest("3호선", "bg-blue-600", 2L, 3L, 10);
        ExtractableResponse<Response> createResponse = saveLine(lineRequest2);

        // when
        String uri = getUri(createResponse);
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .body("size()", is(1));
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        LineRequest lineRequest = new LineRequest("2호선", "bg-red-600", 1L, 2L, 10);
        ExtractableResponse<Response> createResponse = saveLine(lineRequest);

        // when
        LineRequest updateRequest = new LineRequest("3호선", "bg-red-600", 2L, 3L, 20);

        String uri = getUri(createResponse);
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(updateRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(uri)
                .then().log().all()
                .extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private ExtractableResponse<Response> saveLine(LineRequest lineRequest) {
        return RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    private String getUri(ExtractableResponse<Response> createResponse) {
        return createResponse.header("Location");
    }

    private List<Long> getResultLineIds(ExtractableResponse<Response> response) {
        return response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
    }

    private long getExpectedLineId(ExtractableResponse<Response> createResponse) {
        return Long.parseLong(createResponse.header("Location").split("/")[2]);
    }

    private List<Long> getExpectedLineIds(ExtractableResponse<Response> createResponse1, ExtractableResponse<Response> createResponse2) {
        return Stream.of(createResponse1, createResponse2)
                .map(this::getExpectedLineId)
                .collect(Collectors.toList());
    }

    private Long 지하철역_생성(StationRequest stationRequest) {
        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(stationRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
        return Long.parseLong(createResponse.header("Location").split("/")[2]);
    }
}
