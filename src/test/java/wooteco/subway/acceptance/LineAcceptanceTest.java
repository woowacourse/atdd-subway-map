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
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

public class LineAcceptanceTest extends AcceptanceTest {

    private ExtractableResponse<Response> postStations(StationRequest stationRequest) {
        return RestAssured.given().log().all()
                .body(stationRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
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

    private ExtractableResponse<Response> getLineById(long expectId) {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines/" + expectId)
                .then().log().all()
                .extract();
        return response;
    }

    private ExtractableResponse<Response> getLines() {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();
        return response;
    }

    private ExtractableResponse<Response> putLine(long lineId, LineRequest lineRequest) {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + lineId)
                .then().log().all()
                .extract();
        return response;
    }

    private ExtractableResponse<Response> deleteLine(String uri) {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();
        return response;
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        //given
        Long upStationId = postStations(new StationRequest("강남역")).response().as(StationResponse.class).getId();
        Long downStationId = postStations(new StationRequest("역삼역")).response().as(StationResponse.class).getId();
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", upStationId, downStationId, 10);

        // when
        ExtractableResponse<Response> response = postLines(lineRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("지하철 노선을 이름없이 생성한다.")
    @Test
    void createEmptyNameLine() {
        // given
        Long upStationId = postStations(new StationRequest("강남역")).response().as(StationResponse.class).getId();
        Long downStationId = postStations(new StationRequest("역삼역")).response().as(StationResponse.class).getId();
        // when
        ExtractableResponse<Response> response = postLines(
                new LineRequest("", "bg-red-600", upStationId, downStationId, 10));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.header("Location")).isBlank();
    }

    @DisplayName("지하철 노선을 색깔없이 생성한다.")
    @Test
    void createEmptyColorLine() {
        // given
        Long upStationId = postStations(new StationRequest("강남역")).response().as(StationResponse.class).getId();
        Long downStationId = postStations(new StationRequest("역삼역")).response().as(StationResponse.class).getId();

        // when
        ExtractableResponse<Response> response = postLines(new LineRequest("신분당선", "", upStationId, downStationId, 10));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.header("Location")).isBlank();
    }

    @DisplayName("기존에 존재하는 노선 이름으로 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        Long upStationId = postStations(new StationRequest("강남역")).response().as(StationResponse.class).getId();
        Long downStationId = postStations(new StationRequest("역삼역")).response().as(StationResponse.class).getId();

        String name = "신분당선";
        String color = "bg-red-600";
        postLines(new LineRequest(name, color, upStationId, downStationId, 10));

        // when
        ExtractableResponse<Response> response = postLines(
                new LineRequest(name, color, upStationId, downStationId, 10));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선을 하나 조회한다.")
    @Test
    void findLine() {
        /// given
        Long upStationId = postStations(new StationRequest("강남역")).response().as(StationResponse.class).getId();
        Long downStationId = postStations(new StationRequest("역삼역")).response().as(StationResponse.class).getId();
        ExtractableResponse<Response> createResponse = postLines(
                new LineRequest("신분당선", "bg-red-600", upStationId, downStationId, 10));

        long expectId = Long.parseLong(createResponse.header("Location").split("/")[2]);

        // when
        ExtractableResponse<Response> response = getLineById(expectId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("노선을 조회한다.")
    @Test
    void findLines() {
        /// given
        Long upStationId = postStations(new StationRequest("강남역")).response().as(StationResponse.class).getId();
        Long downStationId = postStations(new StationRequest("역삼역")).response().as(StationResponse.class).getId();
        ExtractableResponse<Response> createResponse1 = postLines(
                new LineRequest("신분당선", "bg-red-600", upStationId, downStationId, 10));
        ExtractableResponse<Response> createResponse2 = postLines(
                new LineRequest("2호선", "bg-green-500", upStationId, downStationId, 10));

        // when
        ExtractableResponse<Response> response = getLines();

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

    @DisplayName("노선을 수정한다.")
    @Test
    void updateLine() {
        /// given
        Long upStationId = postStations(new StationRequest("강남역")).response().as(StationResponse.class).getId();
        Long downStationId = postStations(new StationRequest("역삼역")).response().as(StationResponse.class).getId();
        ExtractableResponse<Response> createResponse = postLines(
                new LineRequest("신분당선", "bg-red-600", upStationId, downStationId, 10));

        long lineId = Long.parseLong(createResponse.header("Location").split("/")[2]);

        // when
        LineRequest lineRequest = new LineRequest("분당선", "bg-yellow-400", upStationId, downStationId, 10);
        ExtractableResponse<Response> response = putLine(lineId, lineRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("노선을 제거한다.")
    @Test
    void deleteLine() {
        /// given
        Long upStationId = postStations(new StationRequest("강남역")).response().as(StationResponse.class).getId();
        Long downStationId = postStations(new StationRequest("역삼역")).response().as(StationResponse.class).getId();
        ExtractableResponse<Response> createResponse = postLines(
                new LineRequest("신분당선", "bg-red-600", upStationId, downStationId, 10));

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = deleteLine(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
