package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.*;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationRequest;

@DisplayName("지하철 노선 관련 기능")
class LineAcceptanceTest extends AcceptanceTest {
    private Long stationId1;
    private Long stationId2;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;

        clearAllStations();
        clearAllLines();
        createStations();
    }

    private void createStations() {
        StationRequest stationRequest1 = new StationRequest("선릉역");
        ExtractableResponse<Response> response1 = createStation(stationRequest1);
        stationId1 = getSavedStationIdByResponse(response1);

        StationRequest stationRequest2 = new StationRequest("강남역");
        ExtractableResponse<Response> response2 = createStation(stationRequest2);
        stationId2 = getSavedStationIdByResponse(response2);
    }

    private long getSavedStationIdByResponse(ExtractableResponse<Response> response1) {
        return Long.parseLong(response1.header("Location").split("/")[2]);
    }

    private ExtractableResponse<Response> createStation(StationRequest stationRequest1) {
        return RestAssured.given().log().all()
            .body(stationRequest1)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then().log().all()
            .extract();
    }

    @Test
    @DisplayName("존재하지 않는 노선을 생성한다.")
    void createLine() {
        LineRequest lineRequest = createLine3();

        // when
        ExtractableResponse<Response> response = extractCreateLineRequest(lineRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @Test
    @DisplayName("이미 존재하는 노선을 생성할 수 없다.")
    void createLineWithDuplicateName() {
        // given
        LineRequest lineRequest = createLine3();
        extractCreateLineRequest(lineRequest);

        // when
        ExtractableResponse<Response> response = extractCreateLineRequest(lineRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("존재하는 노선을 조회한다. 상태코드는 200이어야 한다.")
    void findLine() {
        // given
        LineRequest lineRequest = createLine1();

        ExtractableResponse<Response> createResponse = extractCreateLineRequest(lineRequest);

        // when
        String uri = createResponse.header("Location");

        // then
        getLineRequest(uri)
            .statusCode(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("존재하지 않는 노선을 조회할 수 없다. 상태코드는 not found 이어야 한다.")
    void findWrongLine() {
        // given
        String uri = URI.create("/lines/") + "0";

        // when

        // then
        ExtractableResponse<Response> response = getLineRequest(uri)
            .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("노선들을 조회한다.")
    void getLines() {
        // given
        LineRequest lineRequest1 = createLine1();
        ExtractableResponse<Response> createResponse1 = extractCreateLineRequest(lineRequest1);
        LineRequest lineRequest2 = createLine3();
        ExtractableResponse<Response> createResponse2 = extractCreateLineRequest(lineRequest2);

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

    @Test
    @DisplayName("존재하는 노선을 제거한다. 상태코드는 200 이어야 한다.")
    void deleteStation() {
        // given
        LineRequest lineRequest = createLine1();
        ExtractableResponse<Response> createResponse = extractCreateLineRequest(lineRequest);

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = extractDeleteLineRequest(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("존재하지 않는 노선을 제거한다. 상태코드는 204 이어야 한다.")
    void deleteNonStation() {
        extractDeleteLineRequest("lines/1");
    }

    private ExtractableResponse<Response> extractDeleteLineRequest(String uri) {
        return RestAssured.given().log().all()
            .when()
            .delete(uri)
            .then().log().all()
            .extract();
    }

    @Test
    @DisplayName("존재하는 노선을 수정한다. 상태코드는 200이어야 한다.")
    void updateLine() {
        // given
        LineRequest lineRequest = createLine1();
        ExtractableResponse<Response> createResponse = extractCreateLineRequest(lineRequest);
        LineRequest lineRequest1 = createLine2();

        // when
        String uri = createResponse.header("Location");

        // then
        putLineRequest(lineRequest1, uri)
            .statusCode(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("존재하지 않는 노선을 수정하면 not found 예외를 반환해야 한다.")
    void updateNonLine() {
        // given
        LineRequest lineRequest = createLine1();

        putLineRequest(lineRequest, "/lines/1")
            .statusCode(HttpStatus.NOT_FOUND.value());
    }

    private ValidatableResponse putLineRequest(LineRequest lineRequest, String uri) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(lineRequest)
            .when()
            .put(uri)
            .then().log().all();
    }

    @Test
    @DisplayName("name을 지정하지 않고 요청하면 bad request 예외를 반환해야 한다.")
    void emptyName() {
        LineRequest lineRequest = new LineRequest(null, "bg-red-600", stationId1, stationId2, 10);
        createLineRequest(lineRequest)
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("color를 지정하지 않고 요청하면 bad request 예외를 반환해야 한다.")
    void emptyColor() {
        LineRequest lineRequest = new LineRequest("1호선", null, stationId1, stationId2, 10);
        createLineRequest(lineRequest)
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("upStationId를 지정하지 않고 요청하면 bad request 예외를 반환해야 한다.")
    void emptyUpStationId() {
        LineRequest lineRequest = new LineRequest("1호선", "bg-red-600", null, stationId2, 10);
        createLineRequest(lineRequest)
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("downStationId를 지정하지 않고 요청하면 bad request 예외를 반환해야 한다.")
    void emptyDownStationId() {
        LineRequest lineRequest = new LineRequest("1호선", "bg-red-600", stationId1, null, 10);
        createLineRequest(lineRequest)
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("distance가 1보다 작으면 bad request 예외를 반환해야 한다.")
    void notPositiveDistance() {
        LineRequest lineRequest = new LineRequest("1호선", "bg-red-600", stationId1, stationId2, 0);
        createLineRequest(lineRequest)
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("section 삽입이 성공하면 상태코드 200을 반환해야 한다.")
    void insertSection() {
        LineRequest lineRequest = new LineRequest("1호선", "bg-red-600", stationId1, stationId2, 5);
        ExtractableResponse<Response> createResponse = extractCreateLineRequest(lineRequest);

        StationRequest newStationRequest = new StationRequest("교대역");
        ExtractableResponse<Response> response = createStation(newStationRequest);
        Long newDownStationId = getSavedStationIdByResponse(response);

        // when
        SectionRequest sectionRequest = new SectionRequest(stationId1, newDownStationId, 3);
        String uri = createResponse.header("Location");
        createSectionRequest(sectionRequest, uri)
            .statusCode(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("section 삽입이 실패하면 bad request를 반환해야 한다.")
    void insertInvalidSection() {
        LineRequest lineRequest = new LineRequest("1호선", "bg-red-600", stationId1, stationId2, 5);
        ExtractableResponse<Response> createResponse = extractCreateLineRequest(lineRequest);

        Long newUpStationId = createNewStation("잠실역");
        Long newDownStationId = createNewStation("교대역");

        // when
        SectionRequest sectionRequest = new SectionRequest(newUpStationId, newDownStationId, 3);
        String uri = createResponse.header("Location");
        createSectionRequest(sectionRequest, uri)
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    private ValidatableResponse createSectionRequest(SectionRequest sectionRequest, String uri) {
        return RestAssured.given().log().all()
            .body(sectionRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post(uri + "/sections")
            .then().log().all();
    }

    private Long createNewStation(String stationName) {
        StationRequest newStationRequest = new StationRequest(stationName);
        ExtractableResponse<Response> response = createStation(newStationRequest);
        return getSavedStationIdByResponse(response);
    }

    private List<Long> getExpectedLineIds(ExtractableResponse<Response> createResponse1,
        ExtractableResponse<Response> createResponse2) {
        return Arrays.asList(createResponse1, createResponse2).stream()
            .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
            .collect(Collectors.toList());
    }

    private List<Long> getResultLineIds(ExtractableResponse<Response> response) {
        return response.jsonPath().getList(".", LineResponse.class).stream()
            .map(LineResponse::getId)
            .collect(Collectors.toList());
    }

    private ExtractableResponse<Response> extractCreateLineRequest(LineRequest lineRequest) {
        return RestAssured.given().log().all()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
    }

    private LineRequest createLine1() {
        return new LineRequest(
            "1호선",
            "bg-red-600",
            stationId1,
            stationId2,
            10
        );
    }

    private LineRequest createLine2() {
        return new LineRequest(
            "2호선",
            "bg-green-600",
            stationId1,
            stationId2,
            10
        );
    }

    private LineRequest createLine3() {
        return new LineRequest(
            "3호선",
            "bg-orange-600",
            stationId1,
            stationId2,
            10
        );
    }

    private ValidatableResponse getLineRequest(String uri) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get(uri)
            .then().log().all();
    }

    private ValidatableResponse createLineRequest(LineRequest lineRequest) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(lineRequest)
            .when()
            .post("/lines")
            .then().log().all();
    }
}
