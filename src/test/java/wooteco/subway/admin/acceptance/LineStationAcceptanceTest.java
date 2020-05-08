package wooteco.subway.admin.acceptance;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationResponse;
import wooteco.subway.admin.dto.StationResponse;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static wooteco.subway.admin.acceptance.LineAcceptanceTest.createLine;
import static wooteco.subway.admin.acceptance.LineAcceptanceTest.getLines;
import static wooteco.subway.admin.acceptance.StationAcceptanceTest.createStation;
import static wooteco.subway.admin.acceptance.StationAcceptanceTest.getStations;

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


    @DisplayName("지하철 노선에서 지하철역 추가 / 제외")
    @Test
    void manageLineStation() {
        //Given 지하철역이 여러 개 추가되어있다
        createStation("잠실역");
        createStation("종합운동장역");
        createStation("선릉역");
        createStation("강남역");
        //And 지하철 노선이 추가되어있다.
        createLine("신분당선");
        createLine("1호선");
        createLine("2호선");
        createLine("3호선");

        //When 지하철 노선에 지하철역을 등록하는 요청을 한다.
        List<LineResponse> lines = getLines();
        List<StationResponse> stations = getStations();
        addLineStation(lines.get(0).getId(), stations.get(0).getId(), stations.get(1).getId());
        //Then 지하철역이 노선에 추가 되었다.
        LineStation mockLineStation = new LineStation(1L, 2L, 3, 4);
        Line mockLine = new Line("아무거나", LocalTime.of(5, 30), LocalTime.of(23, 30), 10, "5");
        mockLine.getStations().add(mockLineStation);
        assertThat(mockLine.getStations().size()).isEqualTo(1);

        //When 지하철 노선의 지하철역 목록 조회 요청을 한다.
        List<LineStationResponse> mocklineStations = getLineStations(lines.get(0).getId());
        //Then 지하철역 목록을 응답 받는다.
        assertThat(mocklineStations.size()).isEqualTo(1);
        //And 새로 추가한 지하철역을 목록에서 찾는다.
        assertThat(mocklineStations.get(0).getPreStationId()).isEqualTo(1L);
        assertThat(mocklineStations.get(0).getStationId()).isEqualTo(2L);
        assertThat(mocklineStations.get(0).getDistance()).isEqualTo(3);
        assertThat(mocklineStations.get(0).getDuration()).isEqualTo(4);

        //When 지하철 노선에 포함된 특정 지하철역을 제외하는 요청을 한다.
        mocklineStations = new ArrayList<>();
        deleteLine(lines.get(0).getId(), stations.get(0).getId());
        //Then 지하철역이 노선에서 제거 되었다.
        assertThat(mocklineStations.size()).isEqualTo(0);

        //When 지하철 노선의 지하철역 목록 조회 요청을 한다.
        //Then 지하철역 목록을 응답 받는다.
        //And 제외한 지하철역이 목록에 존재하지 않는다.
    }

    private void addLineStation(Long lineId, Long stationId, Long preStationId) {
        Map<String, String> params = new HashMap<>();
        params.put("preStationId", String.valueOf(preStationId));
        params.put("stationId", String.valueOf(stationId));
        params.put("distance", String.valueOf(30));
        params.put("duration", String.valueOf(40));

        given().
                body(params).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                accept(MediaType.APPLICATION_JSON_VALUE).
                when().
                post("/lines/" + lineId + "/stations").
                then().
                log().all().
                statusCode(HttpStatus.CREATED.value());
    }

    private List<LineStationResponse> getLineStations(Long lineId) {
        return given().
                when().
                get("/lines/" + lineId + "/stations").
                then().
                log().all().
                extract().
                jsonPath().getList(".", LineStationResponse.class);
    }

    private void deleteLine(Long lineId, Long stationId) {
        given().
                when().
                delete("/lines/" + lineId + "/stations/" + stationId).
                then().
                log().all();
    }
}
