package wooteco.subway.admin.acceptance;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.*;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationResponse;
import wooteco.subway.admin.dto.LineStationsResponse;
import wooteco.subway.admin.dto.StationResponse;

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

    @TestFactory
    Collection<DynamicTest> dynamicTests() {
        return Arrays.asList(
            dynamicTest("add stations", () -> {
                // given
                List<String> stationNames = Arrays.asList("잠실역", "종합운동장역", "선릉역", "강남역");

                // when
                for (String stationName : stationNames) {
                    createStation(stationName);
                }

                // then
                List<StationResponse> stations = getStations();
                assertThat(stations.size()).isEqualTo(4);
            }),
            dynamicTest("add line", () -> {
                // given
                String lineName = "신분당선";

                // when
                createLine(lineName);

                // then
                List<LineResponse> lines = getLines();
                assertThat(lines.size()).isEqualTo(1);
            }),
            dynamicTest("add stations to line", () -> {
                // given
                List<StationResponse> stations = getStations();
                LineResponse line = getLines().get(0);

                // when
                addLineStation(String.valueOf(line.getId()), null,
                    String.valueOf(stations.get(0).getId()),
                    "10", "10");
                addLineStation(String.valueOf(line.getId()),
                    String.valueOf(stations.get(0).getId()),
                    String.valueOf(stations.get(1).getId()), "10", "10");
                addLineStation(String.valueOf(line.getId()),
                    String.valueOf(stations.get(1).getId()),
                    String.valueOf(stations.get(2).getId()), "10", "10");
                addLineStation(String.valueOf(line.getId()), null,
                    String.valueOf(stations.get(3).getId()), "10", "10");

                // then
                LineStationsResponse lineStations = getLineStations(line.getId());
                assertThat(lineStations.getStations().size()).isEqualTo(4);
            }),
            dynamicTest("search added stations to line", () -> {
                // given
                LineResponse line = getLines().get(0);
                List<StationResponse> stations = getStations();
                LineStationsResponse lineStationsResponse = getLineStations(line.getId());

                // when
                List<LineStationResponse> lineStationResponses = lineStationsResponse.getStations();

                // then
                List<Long> lineStationIds = convertToStationIds(lineStationResponses);
                assertThat(lineStationIds.get(0)).isEqualTo(stations.get(3).getId());
                assertThat(lineStationIds.get(1)).isEqualTo(stations.get(0).getId());
                assertThat(lineStationIds.get(2)).isEqualTo(stations.get(1).getId());
                assertThat(lineStationIds.get(3)).isEqualTo(stations.get(2).getId());
            }),
            dynamicTest("delete specific station in line", () -> {
                // given
                LineResponse line = getLines().get(0);
                LineStationsResponse lineStationsResponse = getLineStations(line.getId());
                LineStationResponse lineStationResponse = lineStationsResponse.getStations().get(1);

                // when
                deleteLineStation(String.valueOf(line.getId()),
                    String.valueOf(lineStationResponse.getId()));

                // then
                List<LineStationResponse> lineStations = getLineStations(
                    line.getId()).getStations();
                assertThat(lineStations.size()).isEqualTo(3);
            }),
            dynamicTest("search deleted station in line", () -> {
                // given
                LineResponse line = getLines().get(0);
                List<StationResponse> stations = getStations();
                LineStationsResponse lineStationsResponse = getLineStations(line.getId());

                // when
                List<LineStationResponse> lineStations = lineStationsResponse.getStations();

                // then
                List<Long> lineStationIds = convertToStationIds(lineStations);
                assertThat(lineStationIds).doesNotContain(stations.get(0).getId());
                assertThat(lineStationIds.get(0)).isEqualTo(stations.get(3).getId());
                assertThat(lineStationIds.get(1)).isEqualTo(stations.get(1).getId());
                assertThat(lineStationIds.get(2)).isEqualTo(stations.get(2).getId());
            })
        );
    }

    private List<Long> convertToStationIds(List<LineStationResponse> lineStationResponses) {
        return lineStationResponses.stream()
            .map(LineStationResponse::getId)
            .collect(Collectors.toList());
    }

    private void createLine(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("startTime", LocalTime.of(5, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("endTime", LocalTime.of(23, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("intervalTime", "10");
        params.put("bgColor", "bg-green-700");

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

    private List<StationResponse> getStations() {
        return given().
            when().
            get("/stations").
            then().
            log().all().
            extract().
            jsonPath().getList(".", StationResponse.class);
    }

    private List<LineResponse> getLines() {
        return given().
            when().
            get("/lines").
            then().
            log().all().
            extract().
            jsonPath().getList(".", LineResponse.class);
    }

    private void addLineStation(String lineId, String preStationId, String stationId,
        String distance, String duration) {
        Map<String, String> params = new HashMap<>();
        params.put("preStationId", preStationId);
        params.put("stationId", stationId);
        params.put("distance", distance);
        params.put("duration", duration);

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

    private LineStationsResponse getLineStations(Long id) {
        return given().
            when().
            get("/lines/" + id + "/stations").
            then().
            log().all().
            extract().as(LineStationsResponse.class);
    }

    private void deleteLineStation(String lineId, String stationId) {
        given().
            when().
            delete("/lines/" + lineId + "/stations/" + stationId).
            then().
            log().all();
    }
}
