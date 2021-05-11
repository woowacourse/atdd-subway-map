package wooteco.subway.section.api;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.line.api.dto.LineDetailsResponse;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class SectionAcceptanceTest extends AcceptanceTest {
    private static final String LINE_NAME = "2호선";
    private static final String LINE_COLOR = "green";
    private static final int DISTANCE = 10;

    private Long upStationId;
    private Long downStationId;
    private ExtractableResponse<Response> createResponse;

    @DisplayName("상행역, 하행역 및 노선 생성 등의 초기 설정")
    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        upStationId = 지하철역_저장("강남역").body().jsonPath().getLong("id");
        downStationId = 지하철역_저장("잠실역").body().jsonPath().getLong("id");
        createResponse = 노선_저장_후_응답(LINE_NAME, LINE_COLOR, upStationId, downStationId, DISTANCE);
    }

    @DisplayName("노선에 구간을 추가하는 기능")
    @Test
    void addSection() {
        //given
        long newDownStationId = 지하철역_저장("건대역").body().jsonPath().getLong("id");
        Map<String, Object> params = 구간_저장을_위한_요청정보(newDownStationId, upStationId, DISTANCE - 1);

        //when
        ExtractableResponse<Response> sectionResponse = 구간_추가_후_응답(params);

        //then
        assertThat(sectionResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(sectionResponse.header("Location")).isNotBlank();
    }

    @DisplayName("연결되지 않는 구간을 추가할 때 예외 처리")
    @Test
    void unconnectableSection() {
        //given
        Map<String, Object> params = 구간_저장을_위한_요청정보(downStationId, upStationId, DISTANCE - 1);

        //when
        ExtractableResponse<Response> sectionResponse = 구간_추가_후_응답(params);

        //then
        assertThat(sectionResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(sectionResponse.body().asString()).isEqualTo("추가될 수 없는 구간입니다.");
    }

    private Map<String, Object> 구간_저장을_위한_요청정보(long downStationId, long upStationId, int distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("downStationId", downStationId);
        params.put("upStationId", upStationId);
        params.put("distance", distance);
        return params;
    }

    private ExtractableResponse<Response> 구간_추가_후_응답(Map<String, Object> params) {
        Long lineId = createResponse.jsonPath().getObject(".", LineDetailsResponse.class).getId();
        ExtractableResponse<Response> sectionResponse = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/{lineId}/sections", lineId)
                .then().log().all()
                .extract();
        return sectionResponse;
    }

    private ExtractableResponse<Response> 지하철역_저장(String stationName) {
        Map<String, String> params = new HashMap<>();
        params.put("name", stationName);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 노선_저장_후_응답(String lineName, String lineColor, Long upStationId,
                                                     Long downStationId, int distance) {
        Map<String, Object> params = 노선_저장을_위한_요청_정보(lineName, lineColor, upStationId,
                downStationId, distance);

        // when
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    private Map<String, Object> 노선_저장을_위한_요청_정보(String lineName, String lineColor, Long upStationId,
                                                Long downStationId, int distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("color", lineColor);
        params.put("name", lineName);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        return params;
    }
}