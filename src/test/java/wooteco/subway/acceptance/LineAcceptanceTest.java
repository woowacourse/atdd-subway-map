package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private final StationRequest stationRequest1 = new StationRequest("신대방");
    private final StationRequest stationRequest2 = new StationRequest("선릉");
    private final StationRequest stationRequest3 = new StationRequest("강남");
    private final StationRequest stationRequest4 = new StationRequest("판교");
    private final LineRequest lineRequest1 = new LineRequest("2호선", "bg-yellow-500", 1L, 2L, 10);

    @Test
    @DisplayName("지하철 노선을 생성한다.")
    void createLine() {
        // given
        final long upStationId = Long.parseLong(createStation(stationRequest1));
        final long downStationId = Long.parseLong(createStation(stationRequest2));
        createSection(new SectionRequest(upStationId, downStationId, 10));

        // when
        final ExtractableResponse<Response> response = createLineResponse(
                new LineRequest("2호선", "bg-yellow-500", upStationId, downStationId, 10));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @Test
    @DisplayName("기존에 존재하는 노선 이름으로 생성하면, 예외를 발생한다.")
    void createLineWithDuplicateName() {
        final long upStationId = Long.parseLong(createStation(stationRequest1));
        final long downStationId = Long.parseLong(createStation(stationRequest2));
        createSection(new SectionRequest(upStationId, downStationId, 10));

        // given
        createLineResponse(new LineRequest("2호선", "bg-yellow-500", upStationId, downStationId, 10));

        // when
        final ExtractableResponse<Response> response = createLineResponse(
                new LineRequest("2호선", "bg-yellow-500", upStationId, downStationId, 10));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("모든 지하철 노선을 조회한다.")
    void getLines() {
        // given
        final long upStationId = Long.parseLong(createStation(stationRequest1));
        final long downStationId = Long.parseLong(createStation(stationRequest2));
        final long upStationId2 = Long.parseLong(createStation(stationRequest3));
        final long downStationId2 = Long.parseLong(createStation(stationRequest4));
        createSection(new SectionRequest(upStationId, downStationId, 10));
        createSection(new SectionRequest(upStationId2, downStationId2, 7));
        createLineResponse(new LineRequest("2호선", "bg-yellow-500", upStationId, downStationId, 10));
        createLineResponse(new LineRequest("신분당선", "bg-red-600", upStationId2, downStationId2, 7));

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();
        final List<LineResponse> lineResponses = response.jsonPath().getList(".", LineResponse.class);

        // then
        assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(lineResponses).hasSize(2);
            assertThat(lineResponses.get(0).getName()).isEqualTo("2호선");
            assertThat(lineResponses.get(0).getStations().get(0).getName()).isEqualTo("신대방");
            assertThat(lineResponses.get(0).getStations().get(1).getName()).isEqualTo("선릉");
            assertThat(lineResponses.get(1).getName()).isEqualTo("신분당선");
            assertThat(lineResponses.get(1).getStations().get(0).getName()).isEqualTo("강남");
            assertThat(lineResponses.get(1).getStations().get(1).getName()).isEqualTo("판교");
        });
    }

    @Test
    @DisplayName("지하철 노선을 조회한다.")
    void getLine() {
        // given
        final long upStationId = Long.parseLong(createStation(stationRequest1));
        final long downStationId = Long.parseLong(createStation(stationRequest2));
        createSection(new SectionRequest(upStationId, downStationId, 10));
        final ExtractableResponse<Response> lineResponse = createLineResponse(
                new LineRequest("2호선", "bg-yellow-500", upStationId, downStationId, 10));
        final String id = lineResponse.header("Location").split("/")[2];

        // when
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines/" + id)
                .then().log().all()
                .extract();

        // then
        final String name = response.body().jsonPath().getString("name");
        final String color = response.body().jsonPath().getString("color");
        final List<StationResponse> stations = response.body().jsonPath().getList("stations", StationResponse.class);
        assertAll(() -> {
            assertThat(name).isEqualTo(lineRequest1.getName());
            assertThat(color).isEqualTo(lineRequest1.getColor());
            assertThat(stations).hasSize(2);
            assertThat(stations.get(0).getName()).isEqualTo("신대방");
            assertThat(stations.get(1).getName()).isEqualTo("선릉");
        });
    }

    @Test
    @DisplayName("존재하지 않는 ID로 조회한다면, 예외를 발생한다.")
    void getLineNotExistId() {
        // given
        final long id = 1L;

        // when
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines/" + id)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("노선을 업데이트 한다.")
    void updateLine() {
        // given
        final long upStationId = Long.parseLong(createStation(stationRequest1));
        final long downStationId = Long.parseLong(createStation(stationRequest2));
        createSection(new SectionRequest(upStationId, downStationId, 10));
        final ExtractableResponse<Response> lineResponse = createLineResponse(
                new LineRequest("2호선", "bg-yellow-500", upStationId, downStationId, 10));
        final String id = lineResponse.header("Location").split("/")[2];

        // when
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(lineRequest1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + id)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("존재하지 않는 ID로 업데이트 한다면, 예외를 발생한다.")
    void updateNotExistId() {
        // given
        final long id = 100L;
        final Map<String, String> params = new HashMap<>();
        params.put("name", "분당선");
        params.put("color", "bg-blue-500");

        // when
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + id)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("지하철 노선을 삭제한다.")
    void deleteLine() {
        // given
        final long upStationId = Long.parseLong(createStation(stationRequest1));
        final long downStationId = Long.parseLong(createStation(stationRequest2));
        createSection(new SectionRequest(upStationId, downStationId, 10));
        final ExtractableResponse<Response> lineResponse = createLineResponse(
                new LineRequest("2호선", "bg-yellow-500", upStationId, downStationId, 10));
        final String id = lineResponse.header("Location").split("/")[2];

        // when
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete("/lines/{id}", id)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("존재하지 않는 ID로 삭제한다면, 예외를 발생한다.")
    void deleteLineNotExistId() {
        // given
        final long id = 1L;

        // when
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete("/lines/" + id)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    private ExtractableResponse<Response> createLineResponse(final LineRequest request) {
        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    private String createStation(final StationRequest request) {
        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract()
                .header("Location")
                .split("/")[2];
    }

    private void createSection(final SectionRequest request) {
        RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/{id}/sections", 1L)
                .then().log().all()
                .extract();
    }
}
