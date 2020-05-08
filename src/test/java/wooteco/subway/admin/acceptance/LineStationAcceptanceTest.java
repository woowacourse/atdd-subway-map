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
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.StationResponse;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        addLineToStation(lines.get(0).getId(), stations.get(0).getId(), stations.get(1).getId());

         //Then 지하철역이 노선에 추가 되었다.

         //When 지하철 노선의 지하철역 목록 조회 요청을 한다.
         //Then 지하철역 목록을 응답 받는다.
         //And 새로 추가한 지하철역을 목록에서 찾는다.

         //When 지하철 노선에 포함된 특정 지하철역을 제외하는 요청을 한다.
         //Then 지하철역이 노선에서 제거 되었다.

         //When 지하철 노선의 지하철역 목록 조회 요청을 한다.
         //Then 지하철역 목록을 응답 받는다.
         //And 제외한 지하철역이 목록에 존재하지 않는다.


    }

    private void addLineToStation( Long lineId, Long stationId, Long preStationId) {
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
}
