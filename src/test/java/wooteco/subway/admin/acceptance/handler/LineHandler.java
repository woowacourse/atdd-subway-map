package wooteco.subway.admin.acceptance.handler;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import wooteco.subway.admin.dto.LineResponse;

@Component
public class LineHandler {
    // TODO: 2020-05-08 테스트 중복코드 분리가 적절한지? 궁금
    private static RequestSpecification given() {
        return RestAssured.given().log().all();
    }

    public LineResponse getLine(Long id) {
        return
            given()
                .when().
                get("/lines/" + id).
                then().
                log().all().
                extract().as(LineResponse.class);
    }

    public void createLine(String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("startTime", LocalTime.of(5, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("endTime", LocalTime.of(23, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("intervalTime", "10");
        params.put("color", color);

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

    public void createDuplicatedLine(String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("startTime", LocalTime.of(5, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("endTime", LocalTime.of(23, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("intervalTime", "10");
        params.put("color", color);

        given().
            body(params).
            contentType(MediaType.APPLICATION_JSON_VALUE).
            accept(MediaType.APPLICATION_JSON_VALUE).
            when().
            post("/lines").
            then().
            log().all().
            statusCode(HttpStatus.BAD_REQUEST.value());
    }

    public void updateLine(Long id, LocalTime startTime, LocalTime endTime) {
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

    public List<LineResponse> getLines() {
        return
            given().
                when().
                get("/lines").
                then().
                log().all().
                extract().
                jsonPath().getList(".", LineResponse.class);
    }

    public void deleteLine(Long id) {
        given().
            when().
            delete("/lines/" + id).
            then().
            log().all();
    }

    public void addLineStation(Long lineId, Long preStationId, Long stationId) {
        Map<String, String> params = new HashMap<>();
        params.put("preStationId", preStationId.toString());
        params.put("stationId", stationId.toString());
        params.put("distance", "10");
        params.put("duration", "10");

        given().
            body(params).
            contentType(MediaType.APPLICATION_JSON_VALUE).
            accept(MediaType.APPLICATION_JSON_VALUE).
            when().
            post("/line/" + lineId + "/stations").
            then().
            log().all().
            statusCode(HttpStatus.OK.value());
    }

    public void deleteLineStation(Long lineId, Long stationId) {
        given().
            when().
            delete("/line/" + lineId + "/stations/" + stationId).
            then().
            log().all();
    }
}
