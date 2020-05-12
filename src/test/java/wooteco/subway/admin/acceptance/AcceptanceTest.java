package wooteco.subway.admin.acceptance;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineWithOrderedStationsResponse;
import wooteco.subway.admin.dto.StationResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AcceptanceTest {

    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    public static RequestSpecification given() {
        return RestAssured.given().log().all();
    }

    LineResponse getLine(Long id) {
        return given().when().
            get("/lines/" + id).
            then().
            log().all().
            extract().as(LineResponse.class);
    }

    List<LineResponse> getLines() {
        return
            given().
            when().
                get("/lines").
            then().
                log().all().
                extract().
                jsonPath().getList(".", LineResponse.class);
    }

    void createLine(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("startTime", LocalTime.of(5, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("endTime", LocalTime.of(23, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("intervalTime", "10");
        params.put("color", "test-color");

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

    void updateLine(Long id, LocalTime startTime, LocalTime endTime) {
        Map<String, String> params = new HashMap<>();
        params.put("startTime", startTime.format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("endTime", endTime.format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("intervalTime", "10");

        given().
            body(params).
            contentType(MediaType.APPLICATION_JSON_VALUE).
            accept(MediaType.APPLICATION_JSON_VALUE).
        when().
            put("/lines/" + id).
        then().
            log().all().
            statusCode(HttpStatus.OK.value());
    }

    void deleteLine(Long id) {
        given().
        when().
            delete("/lines/" + id).
        then().
            log().all();
    }

    List<StationResponse> getStations() {
        return
            given().
                when().
                get("/stations").
                then().
                log().all().
                extract().
                jsonPath().getList(".", StationResponse.class);
    }

    void createStation(String name) {
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

    void deleteStation(String name) {
        given().
            when().
            delete("/stations/" + name).
            then().
            log().all();
    }

    LineWithOrderedStationsResponse getLineWithStations(Long lineId) {
        return
            given().
                when().
                get("/lineStations/" + lineId).
                then().
                log().all().
                extract().
                jsonPath().getObject(".", LineWithOrderedStationsResponse.class);
    }

    void createLineStation(Long lineId, Long stationId, Long preStationId) {
        Map<String, Object> params = new HashMap<>();
        params.put("stationId", stationId.toString());
        params.put("preStationId", preStationId);
        params.put("distance", "100");
        params.put("duration", "2");

        given().
            body(params).
            contentType(MediaType.APPLICATION_JSON_VALUE).
            accept(MediaType.APPLICATION_JSON_VALUE).
        when().
            post("/lineStations/" + lineId).
        then().
            log().all().
            statusCode(HttpStatus.OK.value());
    }

    void deleteLineStation(Long lineId, Long stationId) {
        given().
        when().
            delete("/lines/" + lineId + "/lineStations/" + stationId).
        then().
            log().all();
    }
}
