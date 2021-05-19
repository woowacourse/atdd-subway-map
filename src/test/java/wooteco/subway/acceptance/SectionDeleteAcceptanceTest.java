package wooteco.subway.acceptance;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import wooteco.subway.controller.dto.LineResponse;
import wooteco.subway.controller.dto.StationResponse;

import static org.assertj.core.api.Assertions.assertThat;

public class SectionDeleteAcceptanceTest extends AcceptanceTest {

    private final StationResponse gangnam = new StationResponse(1L, "강남역");
    private final StationResponse yeoksam = new StationResponse(2L, "역삼역");
    private final StationResponse seolleung = new StationResponse(3L, "선릉역");

    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", "2");
        params.put("downStationId", "3");
        params.put("distance", "10");

        RestAssuredHelper.jsonPost(params, "/lines/1");
    }

    @DisplayName("구간 제거 성공 - 상행 종점역 제거")
    @Test
    void deleteTopStation() {

        // when
        ExtractableResponse<Response> response = RestAssuredHelper.jsonDelete("/lines/1/sections?stationId=1");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        final LineResponse lineResponse = RestAssuredHelper.jsonGet("/lines/1").body().as(LineResponse.class);
        assertThat(lineResponse.getStations()).containsExactly(yeoksam, seolleung);
    }

    @DisplayName("구간 제거 성공 - 하행 종점역 제거")
    @Test
    void deleteBottomStation() {

        // when
        ExtractableResponse<Response> response = RestAssuredHelper.jsonDelete("/lines/1/sections?stationId=3");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        final LineResponse lineResponse = RestAssuredHelper.jsonGet("/lines/1").body().as(LineResponse.class);
        assertThat(lineResponse.getStations()).containsExactly(gangnam, yeoksam);
    }

    @DisplayName("구간 제거 성공 - 중간역 제거")
    @Test
    void deleteMiddleStation() {

        // when
        ExtractableResponse<Response> response = RestAssuredHelper.jsonDelete("/lines/1/sections?stationId=2");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        final LineResponse lineResponse = RestAssuredHelper.jsonGet("/lines/1").body().as(LineResponse.class);
        assertThat(lineResponse.getStations()).containsExactly(gangnam, seolleung);
    }
}
