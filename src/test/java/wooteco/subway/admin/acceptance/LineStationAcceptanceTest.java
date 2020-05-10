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
import wooteco.subway.admin.dto.res.LineResponse;
import wooteco.subway.admin.dto.res.StationResponse;

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
     * Given 지하철역이 여러 개 추가되어있다.
     * And 지하철 노선이 추가되어있다.
     * <p>
     * When 지하철 노선에 지하철역을 등록하는 요청을 한다.
     * Then 지하철역이 노선에 추가 되었다.
     * <p>
     * When 지하철 노선의 지하철역 목록 조회 요청을 한다.
     * Then 지하철역 목록을 응답 받는다.
     * And 새로 추가한 지하철역을 목록에서 찾는다.
     * <p>
     * When 지하철 노선에 포함된 특정 지하철역을 제외하는 요청을 한다.
     * Then 지하철역이 노선에서 제거 되었다.
     * <p>
     * When 지하철 노선의 지하철역 목록 조회 요청을 한다.
     * Then 지하철역 목록을 응답 받는다.
     * And 제외한 지하철역이 목록에 존재하지 않는다.
     */
    @DisplayName("지하철 노선에서 지하철역 추가 / 제외")
    @Test
    void manageLineStation() {
        //given
        createStation("강남");
        createStation("서초");
        createStation("구의");
        createLine("2호선");
        //when
        register(getStations(), 2L);
        LineResponse line = getLine(2L);
        //then
        assertThat(line.getStations().size()).isEqualTo(3);
        //when
        final Station station = line.getStations().get(0);
        //then
        assertThat(station.getName()).isEqualTo("강남");
        //when
        final int statusCode = deleteEdge(line.getId(), line.getStations().get(0).getId());
        //then
        assertThat(statusCode).isEqualTo(204);
        //then
        final LineResponse edgesAfterDelete = getLine(2L);
        assertThat(edgesAfterDelete.getStations().size()).isEqualTo(2);
    }

    private int deleteEdge(Long lineId, Long stationId) {
        return given()
            .when()
            .delete("/edges/" + lineId + "/" + stationId)
            .then()
            .log()
            .all()
            .extract()
            .statusCode();
    }

    private void register(List<StationResponse> stations, Long id) {
        for (int i = 0; i < stations.size(); i++) {
            StationResponse station = stations.get(i);
            Map<String, String> params = new HashMap<>();
            String previousId;
            try {
                previousId = String.valueOf(stations.get(i - 1).getId());
            } catch (IndexOutOfBoundsException e) {
                previousId = null;
            }
            params.put("preStationId", previousId);
            params.put("stationId", String.valueOf(station.getId()));
            params.put("distance", "2");
            params.put("duration", "2");

            given().
                body(params).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                accept(MediaType.APPLICATION_JSON_VALUE).
                when().
                post("/edges/" + id).
                then().
                log().all().
                statusCode(HttpStatus.CREATED.value());
        }
    }

    private LineResponse getLine(Long id) {
        return given().when().
            get("/lines/" + id).
            then().
            log().all().
            extract().as(LineResponse.class);
    }

    private List<StationResponse> getStations() {
        return given().when()
            .get("/stations")
            .then()
            .extract()
            .jsonPath()
            .getList(".", StationResponse.class);
    }

    private void createStation(String station) {
        Map<String, String> params = new HashMap<>();
        params.put("name", station);

        given().
            body(params).
            contentType(MediaType.APPLICATION_JSON_VALUE).
            accept(MediaType.APPLICATION_JSON_VALUE).
            when().
            post("/stations").
            then().
            log().all().
            statusCode(HttpStatus.CREATED.value());
    }

    private void createLine(String line) {
        Map<String, String> params = new HashMap<>();
        params.put("name", line);
        params.put("startTime", LocalTime.of(5, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("endTime", LocalTime.of(23, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("intervalTime", "10");

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
}
