package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationRequest;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private LineRequest 신분당선;

    private ValidatableResponse getLineById(int lineId) {
        return RestAssured.given().log().all()
                .when()
                .get("/lines/" + lineId)
                .then().log().all();
    }

    private List<Long> getResultLineIds(ExtractableResponse<Response> response) {
        return response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
    }

    private void putLine(int lineId, LineRequest lineRequest) {
        RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + lineId)
                .then().log().all()
                .extract();
    }

    @BeforeEach
    void beforeEach() {
        신분당선 = new LineRequest("신분당선", "bg-red-600", postStationId(대흥역), postStationId(공덕역), 10);
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given & when
        ExtractableResponse<Response> response = postLineResponse(신분당선);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        postLineResponse(신분당선);

        // when
        LineRequest 초록신분당선 = new LineRequest("신분당선", "bg-green-600", 1L, 2L, 10);
        ExtractableResponse<Response> response = postLineResponse(초록신분당선);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("기존에 존재하는 지하철 노선 색상으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicateColor() {
        // given
        postLineResponse(신분당선);

        // when
        LineRequest 다른신분당선 = new LineRequest("다른신분당선", "bg-red-600", 1L, 2L, 10);
        ExtractableResponse<Response> response = postLineResponse(다른신분당선);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("전체 지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        /// given
        ExtractableResponse<Response> createResponse1 = postLineResponse(신분당선);

        StationRequest 광흥창역 = new StationRequest("광흥창역");
        StationRequest 상수역 = new StationRequest("상수역");
        LineRequest 분당선 = new LineRequest("분당선", "bg-green-600",
                postStationId(광흥창역), postStationId(상수역), 10);
        ExtractableResponse<Response> createResponse2 = postLineResponse(분당선);

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

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        /// given
        int expectedLineId = Integer.parseInt(postLineResponse(신분당선).header("Location").split("/")[2]);

        // when & then
        getLineById(expectedLineId)
                .body("id", equalTo(expectedLineId));
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        int expectedLineId = Integer.parseInt(postLineResponse(신분당선).header("Location").split("/")[2]);

        // when
        StationRequest 광흥창역 = new StationRequest("광흥창역");
        StationRequest 상수역 = new StationRequest("상수역");
        LineRequest 초록다른분당선 = new LineRequest("다른분당선", "bg-green-600",
                postStationId(광흥창역), postStationId(상수역), 20);

        putLine(expectedLineId, 초록다른분당선);

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
        ExtractableResponse<Response> createResponse = postLineResponse(신분당선);

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
