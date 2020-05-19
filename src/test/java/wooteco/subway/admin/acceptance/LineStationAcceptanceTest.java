package wooteco.subway.admin.acceptance;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.dto.StationResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wooteco.subway.admin.acceptance.LineAcceptanceTest.*;
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
    void manageLineStation() throws JsonProcessingException {
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
        addLineStation(lines.get(0).getId(), stations.get(1).getId(), stations.get(2).getId());
        //Then 지하철역이 노선에 추가 되었다.
        LineResponse lineResponse = getLine(lines.get(0).getId());
        assertThat(lineResponse.getStations()[0].contains(stations.get(0).getName())).isTrue();
        assertThat(lineResponse.getStations()[1].contains(stations.get(1).getName())).isTrue();

        //When 지하철 노선의 지하철역 목록 조회 요청을 한다.
        List<StationResponse> stationResponses = getStationsByLineId(lines.get(0).getId());
        //Then 지하철역 목록을 응답 받는다.
        assertThat(stationResponses.size()).isEqualTo(2);
        //And 새로 추가한 지하철역을 목록에서 찾는다.
        assertThat(stationResponses.get(0).getId()).isEqualTo(1L);
        assertThat(stationResponses.get(1).getId()).isEqualTo(2L);

        //When 지하철 노선에 포함된 특정 지하철역을 제외하는 요청을 한다.
        deleteLineStation(lines.get(0).getId(), stations.get(0).getId());
        //Then 지하철역이 노선에서 제거 되었다.
        stationResponses = getStationsByLineId(lines.get(0).getId());
        assertThat(stationResponses.size()).isEqualTo(1);

        //When 지하철 노선의 지하철역 목록 조회 요청을 한다.
        stationResponses = getStationsByLineId(lines.get(0).getId());
        //Then 지하철역 목록을 응답 받는다.
        assertThat(stationResponses.size()).isEqualTo(1);
        //And 제외한 지하철역이 목록에 존재하지 않는다.
        for (StationResponse stationResponse : stationResponses) {
            assertThat(stationResponse.getId().equals(1L)).isFalse();
        }
    }

    private void addLineStation(Long lineId, Long stationId, Long preStationId) {
        LineStationCreateRequest lineStationCreateRequest
                = new LineStationCreateRequest(preStationId, stationId, 10, 10);

        given().
                body(lineStationCreateRequest).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                accept(MediaType.APPLICATION_JSON_VALUE).
        when().
                post("/lines/" + lineId + "/stations").
        then().
                log().all().
                statusCode(HttpStatus.CREATED.value());
    }

    private List<StationResponse> getStationsByLineId(Long lineId) {
        return given().
                when().
                        get("/lines/" + lineId + "/stations").
                then().
                        log().all().
                        extract().
                        jsonPath().getList(".", StationResponse.class);
    }

    private void deleteLineStation(Long lineId, Long stationId) {
        given().
        when().
                delete("/lines/" + lineId + "/stations/" + stationId).
        then().
                log().all();
    }
}
