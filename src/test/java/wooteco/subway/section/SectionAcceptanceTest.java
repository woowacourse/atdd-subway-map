package wooteco.subway.section;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.web.dto.LineResponse;
import wooteco.subway.web.dto.StationResponse;

@DisplayName("구간 인수 테스트")
public class SectionAcceptanceTest extends AcceptanceTest {

    private static final String SECTIONS_PATH = "/sections/";
    private static final String STATIONS_PATH = "/stations/";
    private static final String LINES_PATH = "/lines/";
    private static final String NAME = "name";
    private static final String COLOR = "color";
    private static final String UP_STATION_ID = "upStationId";
    private static final String DOWN_STATION_ID = "downStationId";
    private static final String DISTANCE = "distance";

    private Long stationA;
    private Long stationB;
    private Long stationC;
    private Long newStationId1;
    private Long newStationId2;
    private Long lineId;
    private Long lineId_hasOneSection;

    @BeforeEach
    public void setUp() {
        super.setUp();
        // set station
        stationA = createStationAndGetId("A역");
        stationB = createStationAndGetId("B역");
        stationC = createStationAndGetId("C역");
        newStationId1 = createStationAndGetId("NEW1역");
        newStationId2 = createStationAndGetId("NEW2역");

        // set line
        Map<String, Object> lineData = lineData("1호선", stationA, stationB);
        Map<String, Object> lineData2 = lineData("2호선", stationA, stationB);
        lineId = postLine(lineData);
        lineId_hasOneSection = postLine(lineData2);

        // set section
        Map<String, Object> sectionData = sectionData(stationB, stationC, 3);
        postSection(sectionData, lineId);
    }

    private Long createStationAndGetId(String name) {
        HashMap<String, Object> data = new HashMap<>();
        data.put(NAME, name);

        return getRequestSpecification()
                .body(data)
                .post(STATIONS_PATH)
                .as(StationResponse.class)
                .getId();
    }

    private Long postLine(Map<String, Object> data) {
        return getRequestSpecification()
                .body(data)
                .post(LINES_PATH)
                .as(LineResponse.class)
                .getId();
    }

    private Map<String, Object> sectionData(Long upStationId, Long downStationId,
            Integer distance) {
        Map<String, Object> data = new HashMap<>();
        data.put(UP_STATION_ID, upStationId);
        data.put(DOWN_STATION_ID, downStationId);
        data.put(DISTANCE, distance);
        return data;
    }

    private static Map<String, Object> lineData(String name, Long upStationId, Long downStationId) {
        Map<String, Object> data = new HashMap<>();

        data.put(NAME, name);
        data.put(COLOR, "bg-red-600");
        data.put(UP_STATION_ID, upStationId);
        data.put(DOWN_STATION_ID, downStationId);
        data.put(DISTANCE, 3);

        return data;
    }

    @Test
    @DisplayName("구간 추가: 상행 종점")
    void create_last_up() {
        // given
        Map<String, Object> sectionData = sectionData(newStationId1, stationA, 2);

        // when
        ExtractableResponse<Response> response = postSection(sectionData, lineId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertLinesStations(lineId, newStationId1, stationA, stationB, stationC);
    }

    @Test
    @DisplayName("구간 추가: 하행 종점")
    void create_last_down() {
        // given
        Map<String, Object> sectionData = sectionData(stationC, newStationId1, 2);

        // when
        ExtractableResponse<Response> response = postSection(sectionData, lineId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertLinesStations(lineId, stationA, stationB, stationC, newStationId1);
    }

    @Test
    @DisplayName("구간 추가: 중간 - 상행역 기준")
    void create_between_up() {
        // given
        Map<String, Object> sectionData = sectionData(stationA, newStationId1, 2);

        // when
        ExtractableResponse<Response> response = postSection(sectionData, lineId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertLinesStations(lineId, stationA, newStationId1, stationB, stationC);
    }

    @Test
    @DisplayName("구간 추가: 중간 - 하행역 기준")
    void create_between_down() {
        // given
        Map<String, Object> sectionData = sectionData(newStationId1, stationB, 2);

        // when
        ExtractableResponse<Response> response = postSection(sectionData, lineId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertLinesStations(lineId, stationA, newStationId1, stationB, stationC);
    }

    @Test
    @DisplayName("구간 추가 실패: 상/하행역 둘다 노선에 존재")
    void createFail_bothStationExists() {
        // given
        Map<String, Object> sectionData = sectionData(stationA, stationB, 2);

        // when
        ExtractableResponse<Response> response = postSection(sectionData, lineId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertLinesStations(lineId, stationA, stationB, stationC);
    }

    @Test
    @DisplayName("구간 추가 실패: 상/하행역 둘다 노선에 없음")
    void createFail_bothStationNotExists() {
        // given
        Map<String, Object> sectionData = sectionData(newStationId1, newStationId2, 2);

        // when
        ExtractableResponse<Response> response = postSection(sectionData, lineId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertLinesStations(lineId, stationA, stationB, stationC);
    }

    @Test
    @DisplayName("구간 삭제: 상행 종점")
    void delete_last_up() {
        // when
        ExtractableResponse<Response> response = deleteSection(lineId, stationA);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertLinesStations(lineId, stationB, stationC);
    }

    @Test
    @DisplayName("구간 삭제: 하행 종점")
    void delete_last_down() {
        // when
        ExtractableResponse<Response> response = deleteSection(lineId, stationA);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertLinesStations(lineId, stationB, stationC);
    }

    @Test
    @DisplayName("구간 삭제: 중간")
    void delete_between() {
        // when
        ExtractableResponse<Response> response = deleteSection(lineId, stationB);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertLinesStations(lineId, stationA, stationC);
    }

    @Test
    @DisplayName("구간 삭제 실패: 노선에 구간이 하나밖에 없음")
    void deleteFail_lineHasOnlyOneSection() {
        // when
        ExtractableResponse<Response> response = deleteSection(lineId_hasOneSection, stationA);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertLinesStations(lineId_hasOneSection, stationA, stationB);
    }

    @Test
    @DisplayName("구간 삭제 실패: 역이 노선에 없음")
    void deleteFail_stationNotExist() {
        // when
        ExtractableResponse<Response> response = deleteSection(lineId, newStationId1);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertLinesStations(lineId, stationA, stationB, stationC);
    }

    private void assertLinesStations(Long lineId, Long... stationIds) {
        List<StationResponse> stations = getStationsByLineId(lineId);
        assertThat(toIds(stations)).containsExactly(stationIds);
    }

    private List<Long> toIds(List<StationResponse> stations) {
        return stations.stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());
    }

    private List<StationResponse> getStationsByLineId(Long lineId) {
        ExtractableResponse<Response> findResponse = getLine(lineId);
        return findResponse.as(LineResponse.class).getStations();
    }

    private ExtractableResponse<Response> postSection(Map<String, Object> data, Long lineId) {
        return getRequestSpecification()
                .body(data)
                .post(sectionPathForPost(lineId))
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> deleteSection(Long lineId, Long stationId) {
        return getRequestSpecification()
                .delete(sectionPathForDelete(lineId, stationId))
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> getLine(Long lineId) {
        return getRequestSpecification()
                .get(LINES_PATH + lineId)
                .then().log().all()
                .extract();
    }

    private RequestSpecification getRequestSpecification() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE);
    }

    private static String sectionPathForPost(Long lineId) {
        return String.format(LINES_PATH + "%d" + SECTIONS_PATH, lineId);
    }

    private static String sectionPathForDelete(Long lineId, Long stationId) {
        return String.format(LINES_PATH + "%d" + SECTIONS_PATH + "?stationId=%d",
                lineId, stationId);
    }
}
