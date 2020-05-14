package wooteco.subway.admin.acceptance;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.repository.StationRepository;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/truncate.sql")
public class LineStationResponseAcceptanceTest {
    @Autowired
    StationRepository stationRepository;
    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    private void createLine(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("title", name);
        params.put("startTime", LocalTime.of(5, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("endTime", LocalTime.of(23, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("intervalTime", "10");
        params.put("bgColor","yellow");

        given().
                body(params).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                accept(MediaType.APPLICATION_JSON_VALUE).
                when().
                post("/lines").
                then().
                log().all().
                statusCode(HttpStatus.CREATED.value());
    }

    private void createStation(String name) {
        Station station = stationRepository.save(new Station(name));

        given().
                body(station).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                accept(MediaType.APPLICATION_JSON_VALUE).
                when().
                post("/stations").
                then().
                log().all().
                statusCode(HttpStatus.CREATED.value());
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
        // Given 지하철역이 여러 개 추가되어있다.
        stationRepository.deleteAll();
        createStation("미금역");
        createStation("정자역");
        createStation("수내역");
        // And 지하철 노선이 추가되어있다.
        createLine("2호선");
        createLine("3호선");

        //When 지하철 노선에 지하철역을 등록하는 요청을 한다.
        addLineStation(2L, null, 5L, 10, 10);
        addLineStation(2L, 5L, 6L, 10, 10);
        addLineStation(2L, 6L, 7L, 10, 10);

        //Then 지하철역이 노선에 추가 되었다.
        LineResponse lineStationsByLineId = findLineStationsByLineId(2L);
        assertThat(lineStationsByLineId.getTitle()).isEqualTo("3호선");

        //When 지하철 노선의 지하철역 목록 조회 요청을 한다.
        //Then 지하철 역 목록을 응답 받는다.
        List<Station> stations = lineStationsByLineId.getStations();
        assertThat(stations).hasSize(3);
        //And 새로 추가한 지하철역을 목록에서 찾는다.

        //When 지하철역 노선에 포함된 특정 지하철역을 제외하는 요청을 한다.
//        deleteStationByLineId(1L, 3L);
//        LineResponse deleteLineStationsByLineId = findLineStationsByLineId(1L);
        //Then 지하철역이 노선에서 제거 되었다.
//        assertThat(deleteLineStationsByLineId.getStations()).hasSize(2);

        //When 지하철 노선의 자하철역 목록 조회 요청을 한다.
        //Then 지하철역 목록을 응답 받는다.
//        List<Station> deleteStations = deleteLineStationsByLineId.getStations();
        //ANd 제외한 지하철역이 목록에 존재하지 않는다.
//        for (Station deleteStation : deleteStations) {
//            assertThat(deleteStation.getId()).isNotEqualTo(3L);
//        }

    }

    private void deleteStationByLineId(long lineId, long stationId) {
        given().
                when()
                    .delete("/lines/" + lineId + "/stations/" + stationId).
                then()
                    .log().all();
    }

    private LineStationCreateRequest addLineStation(Long lineId, Long preStationId, Long stationId, int distance, int duration) {
        LineStationCreateRequest lineStationCreateRequest =
                new LineStationCreateRequest(preStationId, stationId, distance,duration);
        Map<String, String> params = new HashMap<>();
        params.put("preStationId",String.valueOf(preStationId));
        params.put("stationId",String.valueOf(stationId));
        params.put("distance",String.valueOf(distance));
        params.put("duration",String.valueOf(duration));
        given().
                body(params).
                    contentType(MediaType.APPLICATION_JSON_VALUE).
                    accept(MediaType.APPLICATION_JSON_VALUE).
                when().
                    post("/lines/" + lineId + "/stations").
                then().
                    log().all().statusCode(HttpStatus.CREATED.value());


        return lineStationCreateRequest;
    }

    private LineResponse findLineStationsByLineId(long lineId) {
        return given().
                when().
                    get("/lines/" + lineId + "/stations").
                then().
                    log().all()
                    .extract().as(LineResponse.class);
    }
}
