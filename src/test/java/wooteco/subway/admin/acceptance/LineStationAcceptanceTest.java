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
import wooteco.subway.admin.dto.LineStationCreateRequest;
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

    @DisplayName("지하철 노선에서 지하철역 추가 / 제외")
    @Test
    void manageLineStation() {
        // given
        StationResponse jamsil = createStation("잠실역");
        StationResponse jamsilSaenae = createStation("잠실새내역");
        StationResponse seoknam = createStation("석남역");
        StationResponse sindorim = createStation("신도림역");
        StationResponse bupeyong = createStation("부평역");
        Long lineId = createLine("2호선");

        // when
        // then
        register(lineId, jamsil, jamsilSaenae);
        register(lineId, jamsilSaenae, seoknam);
        register(lineId, seoknam, sindorim);
        register(lineId, sindorim, bupeyong);

        // when
        List<StationResponse> stations = getStationsByLineId(lineId);

        // then
        assertThat(stations).contains(jamsil);
        assertThat(stations).contains(jamsilSaenae);
        assertThat(stations).contains(seoknam);
        assertThat(stations).contains(sindorim);
        assertThat(stations).contains(bupeyong);

        // when
        // then
        deleteStationOnLine(lineId, jamsil);

        // when
        List<StationResponse> deletedStations = getStationsByLineId(lineId);
        assertThat(deletedStations).doesNotContain(jamsil);
    }

    private void deleteStationOnLine(Long lineId, StationResponse jamsil) {
        given().when()
            .delete("/line-stations/" + lineId + "/" + jamsil.getId())
            .then()
            .statusCode(HttpStatus.NO_CONTENT.value());
    }

    private List<StationResponse> getStationsByLineId(Long lineId) {
        return given().when().
            get("/line-stations/" + lineId).
            then().
            log().all().
            extract().
            jsonPath().getList(".", StationResponse.class);
    }

    private void register(Long lineId, StationResponse preStation, StationResponse station) {
        LineStationCreateRequest lineStationCreateRequest =
            new LineStationCreateRequest(preStation.getId(), station.getId(), 10, 10);

        given().
            body(lineStationCreateRequest).
            contentType(MediaType.APPLICATION_JSON_VALUE).
            accept(MediaType.APPLICATION_JSON_VALUE).
            when().
            post("/line-stations/" + lineId).
            then().
            log().all().
            statusCode(HttpStatus.CREATED.value());
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
            extract().
            jsonPath().getObject(".", StationResponse.class);
    }

    private Long createLine(String name) {
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
}
