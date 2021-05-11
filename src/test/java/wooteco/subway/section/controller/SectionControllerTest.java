package wooteco.subway.section.controller;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.line.controller.LineRequest;
import wooteco.subway.line.controller.LineResponse;
import wooteco.subway.station.controller.StationRequest;
import wooteco.subway.station.controller.StationResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static wooteco.subway.line.controller.LineControllerTestUtils.지하철노선을_생성한다;
import static wooteco.subway.station.controller.StationControllerTestUtils.지하철역을_생성한다;

@DisplayName("구간 기능 테스트")
class SectionControllerTest extends AcceptanceTest {
    private static final String TEST_LINE_NAME = "2호선";
    private static final String TEST_COLOR_NAME = "orange darken-4";
    private static final int TEST_DISTANCE = 10;
    private static LineRequest lineRequestBody;

    private static StationRequest GANG_SAM_STATION_REQUEST = new StationRequest("강남역");
    private static StationRequest JAM_SIL_STATION_REQUEST = new StationRequest("잠실역");
    private static StationRequest YEOK_SAM_STATION_REQUEST = new StationRequest("역삼역");

    private static Long LINE_ID;
    private static Long GANG_NAM_ID;
    private static Long JAM_SIL_ID;
    private static Long YEOK_SAM_ID;


    @BeforeEach
    @Transactional
    void insertDummyData() {
        GANG_NAM_ID = 지하철역을_생성한다(GANG_SAM_STATION_REQUEST).as(StationResponse.class).getId();
        JAM_SIL_ID = 지하철역을_생성한다(JAM_SIL_STATION_REQUEST).as(StationResponse.class).getId();
        YEOK_SAM_ID = 지하철역을_생성한다(YEOK_SAM_STATION_REQUEST).as(StationResponse.class).getId();

        lineRequestBody = new LineRequest(TEST_LINE_NAME, TEST_COLOR_NAME, GANG_NAM_ID, JAM_SIL_ID, TEST_DISTANCE);
        LINE_ID = 지하철노선을_생성한다(lineRequestBody).as(LineResponse.class).getId();
    }

    @DisplayName("구간을 생성한다.")
    @Transactional
    @Test
    void createSection() {
        // given
        SectionRequest sectionRequest = new SectionRequest(JAM_SIL_ID, YEOK_SAM_ID, 10);

        // when
        ExtractableResponse<Response> response = 노선을_생성한다(LINE_ID, sectionRequest);

        LineResponse lineResponse = response.as(LineResponse.class);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(lineResponse.getName()).isEqualTo(TEST_LINE_NAME);
        assertThat(lineResponse.getColor()).isEqualTo(TEST_COLOR_NAME);

        String firstStationName = lineResponse.getStations().get(0).getName();
        String secondStationName = lineResponse.getStations().get(1).getName();

        assertThat(firstStationName).isEqualTo(GANG_SAM_STATION_REQUEST.getName());
        assertThat(secondStationName).isEqualTo(JAM_SIL_STATION_REQUEST.getName());
    }

    private ExtractableResponse<Response> 노선을_생성한다(long lineId, SectionRequest sectionRequest) {
        return RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();
    }

}