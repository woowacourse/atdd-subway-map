package wooteco.subway.admin.acceptance;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.print.attribute.standard.Media;

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
import wooteco.subway.admin.dto.LineStationResponse;
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
        LineResponse lineResponse1 = createLine("1호선");
        StationResponse stationResponse1 = createStation("유안역");
        StationResponse stationResponse2 = createStation("디디역");

        //when
        LineStationResponse lineStationResponse1 = createLineStation(lineResponse1.getId(), null, stationResponse1.getName());
        LineStationResponse lineStationResponse2 = createLineStation(lineResponse1.getId(), stationResponse1.getName(), stationResponse2.getName());

        //then
        List<LineResponse> lines = getLineStations();
        LineResponse lineResponse = getLineResponseById(lineResponse1, lines);
        List<Station> stations = lineResponse.getStations();

        assertThat(stations.size()).isEqualTo(2);
        assertThat(stations.stream()
            .anyMatch(station -> station.getId().equals(lineStationResponse1.getStationId())))
            .isTrue();

        deleteLineStation(lineResponse1.getId(), lineStationResponse1.getStationId());
        List<LineResponse> linesAfterDelete = getLineStations();
        LineResponse lineResponseAfterDelete = getLineResponseById(lineResponse1, linesAfterDelete);
        List<Station> stationsAfterDelete = lineResponseAfterDelete.getStations();
        assertThat(stationsAfterDelete.size()).isEqualTo(1);

        assertThat(stationsAfterDelete.stream()
            .anyMatch(station -> station.getId().equals(lineStationResponse1.getStationId())))
            .isFalse();
    }

    private LineResponse getLineResponseById(LineResponse lineResponse1, List<LineResponse> lines) {
        return lines.stream()
            .filter(line -> line.getId().equals(lineResponse1.getId()))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("잘못된 id입니다."));
    }

    private void deleteLineStation(Long lineId, Long stationId) {
        Map<String, String> params = new HashMap<>();
        params.put("lineId", String.valueOf(lineId));
        params.put("stationId", String.valueOf(stationId));

        given().
            body(params).
            contentType(MediaType.APPLICATION_JSON_VALUE).
            accept(MediaType.APPLICATION_JSON_VALUE).
        when().
            delete("lineStations/delete/" + lineId + "/" + stationId).
        then().
            log().all().
            statusCode(HttpStatus.OK.value());
    }

    private List<LineResponse> getLineStations() {
        return given().
        when().
            get("/lineStations").
        then().
            log().all().
            statusCode(HttpStatus.OK.value()).
            extract().jsonPath().getList(".", LineResponse.class);

    }

    private LineStationResponse createLineStation(Long lineId, String preStationName, String stationName) {
        Map<String, String> params = new HashMap<>();
        params.put("lineId", String.valueOf(lineId));
        params.put("preStationName", preStationName);
        params.put("stationName", stationName);
        params.put("duration", "10");
        params.put("distance", "10");

        return given().
            body(params).
            contentType(MediaType.APPLICATION_JSON_VALUE).
            accept(MediaType.APPLICATION_JSON_VALUE).
        when().
            post("/lineStations").
        then().
            log().all().
            statusCode(HttpStatus.CREATED.value())
            .extract().as(LineStationResponse.class);
    }

    private StationResponse createStation(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        return given().
            body(params).
            contentType(MediaType.APPLICATION_JSON_VALUE).
            accept(MediaType.APPLICATION_JSON_VALUE).
            when().
            post("/stations").
            then().
            log().all().
            statusCode(HttpStatus.CREATED.value()).
            extract().as(StationResponse.class);
    }

    private LineResponse createLine(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("startTime", LocalTime.of(5, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("endTime", LocalTime.of(23, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("intervalTime", "10");
        params.put("bgColor", "bg-orange-500");

        return given().
            body(params).
            contentType(MediaType.APPLICATION_JSON_VALUE).
            accept(MediaType.APPLICATION_JSON_VALUE).
            when().
            post("/lines").
            then().
            log().all().
            statusCode(HttpStatus.CREATED.value())
            .extract().as(LineResponse.class);
    }

}
