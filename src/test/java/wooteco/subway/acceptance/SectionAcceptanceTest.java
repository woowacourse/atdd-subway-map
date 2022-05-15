package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import wooteco.subway.dto.LineCreateRequest;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationRequest;

class SectionAcceptanceTest extends AcceptanceTest {

    @BeforeEach
    void set() {
        StationRequest upStationRequest = new StationRequest("강남역");
        StationAcceptanceTest.postStations(upStationRequest);
        StationRequest downStationRequest = new StationRequest("선릉역");
        StationAcceptanceTest.postStations(downStationRequest);
        LineCreateRequest lineRequest = new LineCreateRequest("2호선", "green", 1L, 2L, 5);
        LineAcceptanceTest.postLines(lineRequest);
    }

    private ExtractableResponse<Response> postSections(SectionRequest request) {
        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/1/sections")
                .then().log().all()
                .extract();
    }

    @Test
    @DisplayName("구간을 추가한다.")
    void createSection() {
        // given
        StationRequest downStationRequest = new StationRequest("잠실역");
        StationAcceptanceTest.postStations(downStationRequest);
        SectionRequest request = new SectionRequest(1L, 3L, 3);

        // when
        ExtractableResponse<Response> response = postSections(request);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("구간을 삭제한다.")
    void deleteSection() {
        // given
        StationRequest downStationRequest = new StationRequest("잠실역");
        StationAcceptanceTest.postStations(downStationRequest);
        SectionRequest request = new SectionRequest(2L, 3L, 3);
        postSections(request);

        // when
        RestAssured.given().log().all()
                .when()
                .queryParam("stationId", 2L)
                .delete("/lines/1/sections")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
    }
}
