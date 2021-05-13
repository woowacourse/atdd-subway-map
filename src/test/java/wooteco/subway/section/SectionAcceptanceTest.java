package wooteco.subway.section;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private static final String EMPTY_STRING = " ";
    private static final long ZERO = 0L;
    private static final long INVALID_ID = Long.MAX_VALUE;

    private Long stationA;
    private Long stationB;
    private Long stationC;
    private Long newStationId;

    private Long lineId;

    @BeforeEach
    public void setUp() {
        super.setUp();
        // set station
        stationA = createStationAndGetId("A역");
        stationB = createStationAndGetId("B역");
        stationC = createStationAndGetId("C역");
        newStationId = createStationAndGetId("NEW역");

        // set line
        Map<String, Object> lineData = lineData("1호선", "bg-red-600", stationA, stationB, 3);
        lineId = postLine(lineData);

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

    private static Map<String, Object> lineData(String name, String color, Long upStationId,
            Long downStationId, Integer distance) {
        Map<String, Object> data = new HashMap<>();

        data.put(NAME, name);
        data.put(COLOR, color);
        data.put(UP_STATION_ID, upStationId);
        data.put(DOWN_STATION_ID, downStationId);
        data.put(DISTANCE, distance);

        return data;
    }

    @Test
    @DisplayName("구간 추가: 상행 종점")
    void create_upLastStation() {
        // given
        Map<String, Object> sectionData = sectionData(newStationId, stationA, 2);

        // when
        ExtractableResponse<Response> response = postSection(sectionData, lineId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        ExtractableResponse<Response> findResponse = getLine(lineId);
        List<StationResponse> stations = findResponse.as(LineResponse.class).getStations();
        assertThat(stations).size().isEqualTo(4);

        StationResponse stationResponse = stations.get(0);
        assertThat(stationResponse.getId()).isEqualTo(newStationId);
    }

    @Test
    @DisplayName("구간 추가: 하행 종점")
    void create_downLastStation() {
        // given
        Map<String, Object> sectionData = sectionData(stationC, newStationId, 2);

        // when
        ExtractableResponse<Response> response = postSection(sectionData, lineId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        ExtractableResponse<Response> findResponse = getLine(lineId);
        LineResponse result = findResponse.as(LineResponse.class);
        List<StationResponse> stations = result.getStations();
        assertThat(result.getStations()).size().isEqualTo(4);

        StationResponse stationResponse = stations.get(3);
        assertThat(stationResponse.getId()).isEqualTo(newStationId);
    }

    @Test
    @DisplayName("중간 구간 추가: 상행역 기준")
    void create_between_up() {
        // given
        Map<String, Object> sectionData = sectionData(stationA, newStationId, 2);

        // when
        ExtractableResponse<Response> response = postSection(sectionData, lineId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        ExtractableResponse<Response> findResponse = getLine(lineId);
        List<StationResponse> stations = findResponse.as(LineResponse.class).getStations();

        assertThat(stations).size().isEqualTo(4);

        StationResponse stationResponse = stations.get(1);
        assertThat(stationResponse.getId()).isEqualTo(newStationId);
    }

    @Test
    @DisplayName("중간 구간 추가: 하행역 기준")
    void create_between_down() {
        // given
        Map<String, Object> sectionData = sectionData(newStationId, stationB, 2);

        // when
        ExtractableResponse<Response> response = postSection(sectionData, lineId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        ExtractableResponse<Response> findResponse = getLine(lineId);
        List<StationResponse> stations = findResponse.as(LineResponse.class).getStations();

        assertThat(stations).size().isEqualTo(4);

        StationResponse stationResponse = stations.get(1);
        assertThat(stationResponse.getId()).isEqualTo(newStationId);
    }

    @Test
    @DisplayName("up/down 둘다 노선에 존재: 구간 추가불가")
    void createFail_bothStationExists() {
    }

    @Test
    @DisplayName("구간 삭제")
    void delete() {
        deleteSection(lineId, stationA);
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
        return String.format(LINES_PATH + "%d" + SECTIONS_PATH + "?stationId%d", lineId, stationId);
    }
}
