package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationRequest;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private static StationRequest 대흥역 = new StationRequest("대흥역");
    private static StationRequest 공덕역 = new StationRequest("공덕역");

    private Long postStation(StationRequest stationRequest) {
        return Long.valueOf(RestAssured.given().log().all()
                .body(stationRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract()
                .header("Location")
                .split("/")[2]);
    }

    private ExtractableResponse<Response> postLines(LineRequest lineRequest) {
        return RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    private ValidatableResponse getLineById(int lineId) {
        return RestAssured.given().log().all()
                .when()
                .get("/lines/" + lineId)
                .then().log().all();
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        LineRequest 신분당선 =
                new LineRequest("신분당선", "bg-red-600", postStation(대흥역), postStation(공덕역), 10);

        // when
        ExtractableResponse<Response> response = postLines(신분당선);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        LineRequest 신분당선 =
                new LineRequest("신분당선", "bg-red-600", postStation(대흥역), postStation(공덕역), 10);
        postLines(신분당선);

        // when
        LineRequest 초록신분당선 = new LineRequest("신분당선", "bg-green-600", 1L, 2L, 10);
        ExtractableResponse<Response> response = postLines(초록신분당선);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("기존에 존재하는 지하철 노선 색상으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicateColor() {
        // given
        LineRequest 신분당선 =
                new LineRequest("신분당선", "bg-red-600", postStation(대흥역), postStation(공덕역), 10);
        postLines(신분당선);

        // when
        LineRequest 다른신분당선 = new LineRequest("다른신분당선", "bg-red-600", 1L, 2L, 10);
        ExtractableResponse<Response> response = postLines(다른신분당선);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("전체 지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        /// given
        Long upStationId = postStation(대흥역);
        Long downStationId = postStation(공덕역);

        LineRequest 신분당선 =
                new LineRequest("신분당선", "bg-red-600", upStationId, downStationId, 10);
        ExtractableResponse<Response> createResponse1 = postLines(신분당선);

        LineRequest 분당선 = new LineRequest("분당선", "bg-green-600", upStationId, downStationId, 10);
        ExtractableResponse<Response> createResponse2 = postLines(분당선);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        /// given
        LineRequest 신분당선 =
                new LineRequest("신분당선", "bg-red-600", postStation(대흥역), postStation(공덕역), 10);
        ExtractableResponse<Response> createResponse = postLines(신분당선);

        int expectedLineId = Integer.parseInt(createResponse.header("Location").split("/")[2]);

        // when & then
        getLineById(expectedLineId)
                .body("id", equalTo(expectedLineId));
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        LineRequest 신분당선 =
                new LineRequest("신분당선", "bg-red-600", postStation(대흥역), postStation(공덕역), 10);
        ExtractableResponse<Response> createResponse = postLines(신분당선);
        int expectedLineId = Integer.parseInt(createResponse.header("Location").split("/")[2]);

        // when
        StationRequest 광흥창역 = new StationRequest("광흥창역");
        StationRequest 상수역 = new StationRequest("상수역");
        LineRequest 초록다른분당선 = new LineRequest("다른분당선", "bg-green-600",
                postStation(광흥창역), postStation(상수역), 20);
        RestAssured.given().log().all()
                .body(초록다른분당선)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + expectedLineId)
                .then().log().all()
                .extract();

        // then
        getLineById(expectedLineId)
                .body("id", equalTo(expectedLineId))
                .body("name", equalTo("다른분당선"))
                .body("color", equalTo("bg-green-600"));
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        LineRequest 신분당선 =
                new LineRequest("신분당선", "bg-red-600", postStation(대흥역), postStation(공덕역), 10);
        ExtractableResponse<Response> createResponse = postLines(신분당선);

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
