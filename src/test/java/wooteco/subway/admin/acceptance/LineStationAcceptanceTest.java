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
import wooteco.subway.admin.dto.LineResponse;
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
        register(lineId, null, jamsil.getId());
        register(lineId, jamsil.getId(), jamsilSaenae.getId());
        register(lineId, jamsilSaenae.getId(), seoknam.getId());
        register(lineId, seoknam.getId(), sindorim.getId());
        register(lineId, sindorim.getId(), bupeyong.getId());

        // when
        LineResponse line = getLine(lineId);
        List<StationResponse> stations = line.getStations();

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
        LineResponse deletedLineResponse = getLine(lineId);
        List<StationResponse> deletedStations = deletedLineResponse.getStations();
        assertThat(deletedStations).doesNotContain(jamsil);
    }

    private void deleteStationOnLine(Long lineId, StationResponse jamsil) {
        given().when()
            .delete("/line-stations/" + lineId + "/" + jamsil.getId())
            .then()
            .statusCode(HttpStatus.NO_CONTENT.value());
    }

    private void register(Long lineId, Long preStationId, Long stationId) {
        LineStationCreateRequest lineStationCreateRequest =
            new LineStationCreateRequest(preStationId, stationId, 10, 10);

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
}
