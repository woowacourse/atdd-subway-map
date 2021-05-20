package wooteco.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.dto.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class SectionAcceptanceTest extends AcceptanceTest {
    @BeforeEach
    void setUpStationAndLine() {
        saveByStationName("강남역");
        saveByStationName("잠실역");
        saveByStationName("잠실새내역");
        saveByLineName("2호선", 1L, 3L);
    }

    @DisplayName("새로운 상행역을 포함한 지하철 구간을 생성한다.")
    @Test
    void createSectionWithUpwardStation() {
        // given
        SectionRequest sectionRequest = new SectionRequest(2L, 1L, 5);

        // when
        ExtractableResponse<Response> sectionResponse = saveSection(1L, sectionRequest);

        // then
        assertThat(sectionResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(sectionResponse.header("Location")).isNotBlank();

        ExtractableResponse<Response> lookUpLineResponse = lookUpLineWithStations();
        LineResponse lineResponse = lookUpLineResponse.jsonPath().getObject(".", LineResponse.class);
        List<String> names = extractNames(lineResponse);

        assertThat(names).containsExactly("잠실역", "강남역", "잠실새내역");
    }

    @DisplayName("새로운 하행역을 포함한 지하철 구간을 생성한다.")
    @Test
    void createSectionWithDownwardStation() {
        // given
        SectionRequest sectionRequest = new SectionRequest(1L, 2L, 5);

        // when
        ExtractableResponse<Response> sectionResponse = saveSection(1L, sectionRequest);

        // then
        assertThat(sectionResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(sectionResponse.header("Location")).isNotBlank();

        ExtractableResponse<Response> lookUpLineResponse = lookUpLineWithStations();
        LineResponse lineResponse = lookUpLineResponse.jsonPath().getObject(".", LineResponse.class);
        List<String> names = extractNames(lineResponse);
        assertThat(names).containsExactly("강남역", "잠실역", "잠실새내역");
    }

    @DisplayName("구간 추가 시 구간에 포함된 두 역 모두 이미 노선에 존재할 경우 BAD REQUEST 응답")
    @Test
    void BothStationAlreadyExistsThrowException() {
        SectionRequest sectionRequest = new SectionRequest(3L, 1L, 5);
        ExtractableResponse<Response> sectionResponse = saveSection(1L, sectionRequest);
        assertThat(sectionResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("구간 추가 시 구간에 포함된 두 역 모두 노선에 존재하지 않을 경우 BAD REQUEST 응답")
    @Test
    void BothStationNotExistsThrowException() {
        saveByStationName("원인재역");
        saveByStationName("신연수역");
        SectionRequest sectionRequest = new SectionRequest(4L, 5L, 5);

        ExtractableResponse<Response> sectionResponse = saveSection(1L, sectionRequest);

        assertThat(sectionResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("구간 사이에 구간 추가 시 추가되는 구간의 거리가 더 크거나 같은 경우 BAD REQUEST 응답")
    @Test
    void validateDistanceFailTest() {
        SectionRequest sectionRequest = new SectionRequest(2L, 3L, 100);
        ExtractableResponse<Response> sectionResponse = saveSection(1L, sectionRequest);

        assertThat(sectionResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("구간 삭제 시 구간이 하나 뿐일 때 BAD REQUEST 응답")
    @Test
    void isNewStationDownwardTest() {
        ExtractableResponse<Response> response1 = deleteSection(1L, 1L);
        ExtractableResponse<Response> response2 = deleteSection(1L, 3L);

        assertThat(response1.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response2.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("중간 구간 삭제 시 양 옆 구간이 통합된다.")
    @Test
    void createMergedSectionAfterDeletionTest() {
        saveSection(1L, new SectionRequest(1L, 2L, 5));
        ExtractableResponse<Response> response = deleteSection(1L, 2L);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        ExtractableResponse<Response> lookUpLineResponse = lookUpLineWithStations();
        LineResponse lineResponse = lookUpLineResponse.jsonPath().getObject(".", LineResponse.class);
        List<String> names = extractNames(lineResponse);
        assertThat(names).containsExactly("강남역", "잠실새내역");
    }

    private List<String> extractNames(LineResponse lineResponse) {
        return lineResponse.getStations().stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());
    }

    private ExtractableResponse<Response> lookUpLineWithStations() {
        return RestAssured.given().log().all()
                .when()
                .get("/lines/1")
                .then().log().all()
                .extract();
    }

    private void saveByLineName(String lineName, Long upStationId, Long downStationId) {
        LineRequest lineRequest = new LineRequest(lineName, "bg-red-600", upStationId, downStationId, 100);
        ExtractableResponse<Response> lineResponse = saveLine(lineRequest);
        assertThat(lineResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    private ExtractableResponse<Response> saveByStationName(String stationName) {
        StationRequest stationRequest = new StationRequest(stationName);
        return saveStation(stationRequest);
    }

    private ExtractableResponse<Response> saveStation(StationRequest stationRequest) {
        return RestAssured.given().log().all()
                .body(stationRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> saveSection(Long lineId, SectionRequest sectionRequest) {
        return RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> deleteSection(Long lineId, Long stationId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/lines/" + lineId + "/sections?stationId=" + stationId)
                .then().log().all()
                .extract();
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
}
