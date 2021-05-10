package wooteco.subway.line.controller;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.section.dto.SectionRequest;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.dto.StationRequest;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LineAcceptanceTest extends AcceptanceTest {
    private ExtractableResponse<Response> response;
    private LineRequest firstLineRequest;
    private StationRequest firstStationRequest;
    private StationRequest secondStationRequest;
    private String url;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        firstStationRequest = new StationRequest("잠실역");
        secondStationRequest = new StationRequest("잠실새내역");

        saveStation(firstStationRequest);
        saveStation(secondStationRequest);

        firstLineRequest = new LineRequest(
                "신분당선",
                "bg-red-600",
                1L,
                2L,
                10);
        response = saveLine(firstLineRequest);

        url = response.header("Location");
    }

    @DisplayName("line 추가하는데 성공하면 201 created와 생성된 line 정보를 반환한다")
    @Test
    void createLine() {
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(url).isNotBlank();

        LineResponse responseBody = response.body().as(LineResponse.class);
        LineResponse expectedResponseBody = LineResponse.toDto(new Line(1L, "bg-red-600", "신분당선", Arrays.asList(
                new Station(1L, firstStationRequest.getName()),
                new Station(2L, secondStationRequest.getName()))
                )
        );

        assertThat(responseBody).usingRecursiveComparison().isEqualTo(expectedResponseBody);
    }

    @DisplayName("전체 line을 조회하면 저장된 모든 line들을 반환한다 ")
    @Test
    void getLines() {
        LineRequest secondLineRequest = new LineRequest("2호선", "bg-green-600");
        saveLine(secondLineRequest);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();

        List<LineResponse> lineResponses = response.jsonPath().getList(".", LineResponse.class);
        List<LineRequest> lineRequests = Arrays.asList(firstLineRequest, secondLineRequest);

        RecursiveComparisonConfiguration configuration = RecursiveComparisonConfiguration.builder()
                .withIgnoredFields("upStationId", "downStationId", "distance", "id", "stations")
                .withIgnoredCollectionOrderInFields()
                .build();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(lineResponses).usingRecursiveFieldByFieldElementComparator(configuration).isEqualTo(lineRequests);
    }

    @DisplayName("id를 통해 line을 조회하면, 해당 line 정보를 반환한다.")
    @Test
    void getLine() {
        StationRequest stationRequest = new StationRequest("해운대역");
        saveStation(stationRequest);

        SectionRequest sectionRequest = new SectionRequest(1L, 3L, 2);
        saveSection(sectionRequest);

        ExtractableResponse<Response> getResponse = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(url)
                .then().log().all()
                .extract();

        LineResponse expectedLineResponse = LineResponse.toDto(
                new Line(
                        1L,
                        "bg-red-600",
                        "신분당선",
                        Arrays.asList(
                                new Station(1L, firstStationRequest.getName()),
                                new Station(3L, stationRequest.getName()),
                                new Station(2L, secondStationRequest.getName())
                        )
                )
        );

        assertThat(getResponse.as(LineResponse.class)).usingRecursiveComparison().
                isEqualTo(expectedLineResponse);
    }

    @DisplayName("id를 통해 line을 변경하면, payload대로 line 수정한다")
    @Test
    void updateLine() {
        LineRequest lineUpdateRequest = new LineRequest("구분당선", "bg-blue-600");
        ExtractableResponse<Response> getResponse = RestAssured.given().log().all()
                .body(lineUpdateRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(url)
                .then().log().all()
                .extract();

        assertThat(getResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("id를 통해 line을 삭제하면, payload대로 line을 삭제한다")
    @Test
    void deleteLine() {
        ExtractableResponse<Response> getResponse = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete(url)
                .then().log().all()
                .extract();

        assertThat(getResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("이미 존재하는 이름의 line을 저장하려하면 bad request를 반환한다")
    @Test
    void save_DuplicateLineNameException() {
        LineRequest lineRequest = new LineRequest("신분당선", "bg-black-600");

        ExtractableResponse<Response> shinBunDangResponse = saveLine(lineRequest);

        assertThat(shinBunDangResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("없는 id의 line을 가져오려하면 NoSuchLineException을 반환한다")
    @Test
    void getLine_NoSuchLineException() {
        ExtractableResponse<Response> line3Response = RestAssured.given().log().all()
                .when()
                .get("/lines/3")
                .then().log().all()
                .extract();

        assertThat(line3Response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("없는 id의 line을 삭제하려하면 NoSuchLineException을 반환한다")
    @Test
    void deleteById_NoSuchLineException() {
        ExtractableResponse<Response> line3Response = RestAssured.given().log().all()
                .when()
                .delete("/lines/3")
                .then().log().all()
                .extract();

        assertThat(line3Response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("없는 id의 line을 수정하려하면 NoSuchLineException을 반환한다")
    @Test
    void update_NoSuchLineException() {
        LineRequest lineRequest = new LineRequest("지노선", "bg-yellow-600");

        ExtractableResponse<Response> line3Response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(lineRequest)
                .when()
                .put("/lines/3")
                .then().log().all()
                .extract();

        assertThat(line3Response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("구간이 1개만 존재하는 line에 대한 구간 삭제 요청이 들어왔을 때, bad request를 반환한다")
    @Test
    void deleteSection_onlyOneSectionExists_throwException() {
        ExtractableResponse<Response> deleteResponse = RestAssured.given().log().all()
                .param("stationId", 1L)
                .when().log().all()
                .delete(url + "/sections")
                .then().log().all()
                .extract();

        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("종점역에 대한 구간 삭제 요청이 들어왔을 때, 종점이 변경된 상태로 구간들을 새롭게 조정한다")
    @Test
    void deleteSection_endStation_deleteSectionContainingTheEndStation() {
        StationRequest thirdStationRequest = new StationRequest("몽촌토성역");
        saveStation(thirdStationRequest); // 잠실역 - 잠실새내역 - 몽촌토성역

        SectionRequest sectionRequest = new SectionRequest(2L, 3L, 5);
        saveSection(sectionRequest);

        deleteSection(1L); // 잠실역

        // 잠실새내역 - 몽촌토성역
        LineResponse responseBody = getLineResponse();
        LineResponse expectedResponseBody = LineResponse.toDto(new Line(
                1L,
                firstLineRequest.getColor(),
                firstLineRequest.getName(),
                Arrays.asList(
                        new Station(2L, secondStationRequest.getName()),
                        new Station(3L, thirdStationRequest.getName())
                )));

        assertThat(responseBody).usingRecursiveComparison().isEqualTo(expectedResponseBody);
    }

    @DisplayName("중간역에 대한 구간 삭제 요청이 들어왔을 때, 삭제된 구간 양옆 구간을 합치면서 구간들을 새롭게 조정한다")
    @Test
    void deleteSection_middleStation_deleteSectionsAndCombineRelevantTwoStations() {
        StationRequest thirdStationRequest = new StationRequest("몽촌토성역");
        saveStation(thirdStationRequest); // 잠실역 - 잠실새내역 - 몽촌토성역

        SectionRequest sectionRequest = new SectionRequest(2L, 3L, 5);
        saveSection(sectionRequest);

        deleteSection(2L);

        // 잠실역 - 몽촌토성역
        LineResponse responseBody = getLineResponse();
        LineResponse expectedResponseBody = LineResponse.toDto(new Line(
                1L,
                firstLineRequest.getColor(),
                firstLineRequest.getName(),
                Arrays.asList(
                        new Station(1L, firstStationRequest.getName()),
                        new Station(3L, thirdStationRequest.getName())
                )));

        assertThat(responseBody).usingRecursiveComparison().isEqualTo(expectedResponseBody);
    }

    private void saveStation(final StationRequest station) {
        RestAssured.given().log().all()
                .body(station)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all();
    }

    private ExtractableResponse<Response> saveLine(final LineRequest lineRequest) {
        return RestAssured.given()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then()
                .extract();
    }

    private void deleteSection(final Long stationId) {
        RestAssured.given().log().all()
                .param("stationId", stationId)
                .when().log().all()
                .delete(url + "/sections")
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    private void saveSection(final SectionRequest sectionRequest) {
        RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(sectionRequest)
                .when()
                .post(url + "/sections")
                .then();
    }

    private LineResponse getLineResponse() {
        ExtractableResponse<Response> lineResponse = RestAssured.given()
                .when()
                .get(url)
                .then()
                .extract();

        return lineResponse.body().as(LineResponse.class);
    }
}