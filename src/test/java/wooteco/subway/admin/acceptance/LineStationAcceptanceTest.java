package wooteco.subway.admin.acceptance;

import static org.assertj.core.api.Assertions.*;
import static wooteco.subway.admin.acceptance.LineAcceptanceTest.*;
import static wooteco.subway.admin.acceptance.StationAcceptanceTest.*;

import java.util.List;

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
import wooteco.subway.admin.dto.LineDetailResponse;
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
        Long jamsilId = createStation("잠실역");
        Long jamsilSaenaeId = createStation("잠실새내역");
        Long seoknamId = createStation("석남역");
        Long sindorimId = createStation("신도림역");
        Long bupeyongId = createStation("부평역");
        Long lineId = createLine("2호선");

        // when
        // then
        register(lineId, null, jamsilId);
        register(lineId, jamsilId, jamsilSaenaeId);
        register(lineId, jamsilSaenaeId, seoknamId);
        register(lineId, seoknamId, sindorimId);
        register(lineId, sindorimId, bupeyongId);

        // when
        LineDetailResponse line = getLineDetail(lineId);
        List<StationResponse> stations = line.getStations();

        // then
        StationResponse jamsil = getStation(jamsilId);
        assertThat(stations).contains(jamsil);
        assertThat(stations).contains(getStation(jamsilSaenaeId));
        assertThat(stations).contains(getStation(seoknamId));
        assertThat(stations).contains(getStation(sindorimId));
        assertThat(stations).contains(getStation(bupeyongId));

        // when
        // then
        deleteStationOnLine(lineId, jamsilId);

        // when
        LineDetailResponse deletedLineResponse = getLineDetail(lineId);
        List<StationResponse> deletedStations = deletedLineResponse.getStations();
        assertThat(deletedStations).doesNotContain(jamsil);
    }

    private LineDetailResponse getLineDetail(Long lineId) {
        return given().when().
            get("/lines/" + lineId + "/detail").
            then().
            log().all().
            extract().as(LineDetailResponse.class);
    }

    private void register(Long lineId, Long preStationId, Long stationId) {
        LineStationCreateRequest lineStationCreateRequest =
            new LineStationCreateRequest(preStationId, stationId, 10, 10);

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

    private void deleteStationOnLine(Long lineId, Long stationId) {
        given().when()
            .delete("/lines/" + lineId + "/stations/" + stationId)
            .then()
            .statusCode(HttpStatus.NO_CONTENT.value());
    }
}
