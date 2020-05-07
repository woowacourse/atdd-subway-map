package wooteco.subway.admin.acceptance;

import static org.assertj.core.api.Assertions.*;

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
import wooteco.subway.admin.dto.LineStationCreateRequest;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        // when
        createStation("잠실역");
        createStation("종합운동장역");
        createStation("선릉역");
        createStation("강남역");
        // and
        createLine("1호선");

        List<Line> lines = getLines();
        Long firstLineId = lines.get(0).getId();
        System.out.println("id : " + firstLineId);
        Line line = getLine(firstLineId);

        createLineStation(firstLineId);
        Set<LineStation> stations = getStations(firstLineId);
        assertThat(stations.size()).isEqualTo(1);
        System.out.println("stations : " + stations.toString());



    }

    private Set getStations(Long id) {
        return given().
        when().
            get("/lines/" + id + "/stations").
        then().
            log().all().
            statusCode(HttpStatus.OK.value()).
            extract().
            as(Set.class);

    }

    private void createLineStation(Long id) {
        Map<String, String> params = new HashMap<>();
        params.put("stationId", "1");
        params.put("preStationId", "2");
        params.put("distance", "1");
        params.put("duration", "1");

        given().
            body(params).
            contentType(MediaType.APPLICATION_JSON_VALUE).
            accept(MediaType.APPLICATION_JSON_VALUE).
        when().
            post("/lines/" + id + "/station-add").
        then().
            log().all().
            statusCode(HttpStatus.OK.value());
    }

    private void createStation(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

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

    private void createLine(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("startTime", LocalTime.of(5, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("endTime", LocalTime.of(23, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("intervalTime", "10");
        params.put("backgroundColor", "bg-red-800");

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

    private List<Line> getLines() {
        return
            given().
                when().
                get("/lines").
                then().
                log().all().
                extract().
                jsonPath().getList(".", Line.class);
    }

    private Line getLine(Long id) {
        return given().when().
            get("/lines/" + id).
            then().
            log().all().
            extract().as(Line.class);
    }
}
