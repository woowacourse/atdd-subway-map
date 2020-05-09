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

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

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
        LineResponse line = createLine("2호선");
        StationResponse station1 = createStation("강남역");
        StationResponse station2 = createStation("역삼역");

        //when
        addStationToLine(line.getId(), null, station1.getId());
        addStationToLine(line.getId(), station1.getId(), station2.getId());
        //then
        assertThat(getStationsOfLine(line.getId()).size()).isEqualTo(2);

        //when
        List<StationResponse> stations = getStationsOfLine(line.getId());
        //then
        assertThat(stations.get(0).getName()).isEqualTo(station1.getName());
        assertThat(stations.get(1).getName()).isEqualTo(station2.getName());

        //when
        removeLineStation(line.getId(), station2.getId());
        //then
        assertThat(getStationsOfLine(line.getId()).size()).isEqualTo(1);
    }

    private void removeLineStation(Long lineId, Long stationId) {
        given().when()
                .delete("/lines/" + lineId + "/stations/" + stationId)
                .then()
                .log().all().statusCode(HttpStatus.NO_CONTENT.value());
    }

    private void addStationToLine(Long lineId, Long preStationId, Long stationId) {
        Map<String, String> params = new HashMap<>();
        params.put("preStationId", String.valueOf(preStationId));
        params.put("stationId", String.valueOf(stationId));
        params.put("distance", "1");
        params.put("duration", "1");

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

    private List<StationResponse> getStationsOfLine(Long lineId) {
        return given().
                when().
                get("/lines/" + lineId + "/stations").
                then().
                log().all().
                extract().
                jsonPath().getList(".", StationResponse.class);
    }

    private LineResponse createLine(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("startTime", LocalTime.of(5, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("endTime", LocalTime.of(23, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("intervalTime", "10");
        params.put("bgColor", "bg-blue-700");

        return given().
                body(params).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                accept(MediaType.APPLICATION_JSON_VALUE).
                when().
                post("/lines").
                then().
                log().all().
                statusCode(HttpStatus.CREATED.value()).
                extract().as(LineResponse.class);
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
}
