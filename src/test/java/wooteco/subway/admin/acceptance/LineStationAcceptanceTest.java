package wooteco.subway.admin.acceptance;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.StationResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/truncate.sql")
public class LineStationAcceptanceTest {

    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    public static RequestSpecification given() {
        return RestAssured.given().log().all();
    }

    /**
     *     Given 지하철역이 여러 개 추가되어있다.
     *     And 지하철 노선이 추가되어있다.
     *
     *     When 지하철 노선에 지하철역을 등록하는 요청을 한다.
     *     Then 지하철역이 노선에 추가 되었다.
     *
     *     When 지하철 노선의 지하철역 목록 조회 요청을 한다.
     *     Then 지하철역 목록을 응답 받는다.
     *     And 새로 추가한 지하철역을 목록에서 찾는다.
     *
     *     When 지하철 노선에 포함된 특정 지하철역을 제외하는 요청을 한다.
     *     Then 지하철역이 노선에서 제거 되었다.
     *
     *     When 지하철 노선의 지하철역 목록 조회 요청을 한다.
     *     Then 지하철역 목록을 응답 받는다.
     *     And 제외한 지하철역이 목록에 존재하지 않는다.
     */
    @DisplayName("지하철 노선에서 지하철역 추가 / 제외")
    @Test
    void manageLineStation() {
        //given
        createStation("일원역");
        createStation("이대역");
        createStation("삼성역");
        createLine("55호선");
        final LineResponse line = getLines().get(0);
        final StationResponse station = getStations().get(0);

        //when
        addLineStation(line.getId(), station.getId());
        //then
        List<StationResponse> stationResponse = findLineStationsById(line.getId());
        assertThat(stationResponse.size()).isEqualTo(1);

        //when
        addLineStation(line.getId(), getStations().get(1).getId());
        List<StationResponse> stationResponses = findLineStationsById(line.getId());
        //then
        assertThat(stationResponses).isNotNull();
        assertThat(stationResponses).contains(StationResponse.of(new Station("이대역")));

        //when
        deleteLineStation(line.getId(), getStations().get(0).getId());
        //then
        assertThat(findLineStationsById(line.getId()).size()).isEqualTo(1);

        //when
        List<StationResponse> stationResponsesAfterDelete = findLineStationsById(line.getId());
        //then
        assertThat(stationResponsesAfterDelete).doesNotContain(
            StationResponse.of(new Station("일원역")));
    }

    private List<StationResponse> findLineStationsById(Long id) {
        return
            given().
                when().
                get("/api/lines/" + id + "/stations").
                then().
                log().all().
                extract().
                jsonPath().getList(".", StationResponse.class);
    }

    private void createStation(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        given().
            body(params).
            contentType(MediaType.APPLICATION_JSON_VALUE).
            accept(MediaType.APPLICATION_JSON_VALUE).
            when().
            post("/api/stations").
            then().
            log().all().
            statusCode(HttpStatus.CREATED.value());
    }

    private void createLine(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("bgColor", "red");
        params.put("startTime", LocalTime.of(5, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("endTime", LocalTime.of(23, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("intervalTime", "10");

        given().
            body(params).
            contentType(MediaType.APPLICATION_JSON_VALUE).
            accept(MediaType.APPLICATION_JSON_VALUE).
            when().
            post("/api/lines").
            then().
            log().all().
            statusCode(HttpStatus.CREATED.value());
    }

    public void addLineStation(Long lineId, Long stationId) {
        Map<String, Long> params = new HashMap<>();
        params.put("lineId", lineId);
        params.put("stationId", stationId);

        given().
            body(params).
            contentType(MediaType.APPLICATION_JSON_VALUE).
            accept(MediaType.APPLICATION_JSON_VALUE).
            when().
            post("/api/lines/" + lineId + "/stations/" + stationId).
            then().
            log().all().
            statusCode(HttpStatus.CREATED.value());
    }

    private void deleteLineStation(Long lineId, Long stationId) {
        given().
            when().
            delete("/api/lines/" + lineId + "/stations/" + stationId).
            then().
            log().all();
    }

    private List<LineResponse> getLines() {
        return
            given().
                when().
                get("/api/lines").
                then().
                log().all().
                extract().
                jsonPath().getList(".", LineResponse.class);
    }

    private List<StationResponse> getStations() {
        return given().
            when().
            get("/api/stations").
            then().
            log().all().
            extract().
            jsonPath().getList(".", StationResponse.class);
    }
}
