package wooteco.subway.admin.acceptance;

import static wooteco.subway.admin.acceptance.LineStationAcceptanceTest.*;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.StationResponse;

public class AcceptanceTest {

    public static LineResponse getLine(Long id) {
        return given().when().
            get("/lines/" + id).
            then().
            log().all().
            extract().as(LineResponse.class);
    }

    public static Long createLine(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", "bg-green-700");
        params.put("startTime", LocalTime.of(5, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("endTime", LocalTime.of(23, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("intervalTime", "10");

        return given().
            body(params).
            contentType(MediaType.APPLICATION_JSON_VALUE).
            accept(MediaType.APPLICATION_JSON_VALUE).
            when().
            post("/lines").
            then().
            log().all().
            statusCode(HttpStatus.CREATED.value()).
            extract()
            .body()
            .as(Long.class);
    }

    public static StationResponse createStation(String name) {
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
            extract().
            jsonPath().getObject(".", StationResponse.class);
    }
}
