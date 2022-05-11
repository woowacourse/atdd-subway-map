package wooteco.subway.acceptance;

import static wooteco.subway.acceptance.AcceptanceTestUtil.requestPostLine;
import static wooteco.subway.acceptance.AcceptanceTestUtil.requestPostStation;
import static wooteco.subway.testutils.Fixture.LINE_REQUEST_분당선_STATION_1_3;
import static wooteco.subway.testutils.Fixture.LINE_REQUEST_신분당선_STATION_1_2;
import static wooteco.subway.testutils.Fixture.STATION_REQUEST_강남역;
import static wooteco.subway.testutils.Fixture.STATION_REQUEST_역삼역;
import static wooteco.subway.testutils.Fixture.STATION_REQUEST_잠실역;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.request.SectionRequest;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    @DisplayName("새로운 하행 종점을 기존 구간에 등록한다.")
    @Test
    void addSection_new_station() {
        requestPostStation(STATION_REQUEST_강남역, "/stations");
        requestPostStation(STATION_REQUEST_잠실역, "/stations");
        requestPostLine(LINE_REQUEST_신분당선_STATION_1_2, "/lines");

        final SectionRequest sectionRequest = new SectionRequest(2L, 3L, 10);

        final ExtractableResponse<Response> response = requestPostSection(sectionRequest,
            "/lines/1/sections");

        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("새로운 역을 기존 구간 중간역으로 등록한다.")
    @Test
    void addSection_middle_station() {
        requestPostStation(STATION_REQUEST_강남역, "/stations");
        requestPostStation(STATION_REQUEST_잠실역, "/stations");
        requestPostStation(STATION_REQUEST_역삼역, "/stations");
        requestPostLine(LINE_REQUEST_분당선_STATION_1_3, "/lines");
        final SectionRequest sectionRequest = new SectionRequest(1L, 2L, 5);
        
        final ExtractableResponse<Response> response = requestPostSection(sectionRequest,
            "/lines/1/sections");

        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private ExtractableResponse<Response> requestPostSection(final SectionRequest sectionRequest, final String URI) {
        return RestAssured.given().log().all()
            .body(sectionRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post(URI)
            .then().log().all()
            .extract();
    }
}
