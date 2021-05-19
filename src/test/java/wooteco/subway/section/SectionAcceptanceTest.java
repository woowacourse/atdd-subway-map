package wooteco.subway.section;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.line.api.dto.LineDetailsResponse;

class SectionAcceptanceTest extends AcceptanceTest {

    private static final String LINE_NAME = "2호선";
    private static final String LINE_COLOR = "green";
    private static final int DISTANCE = 10;

    private Long upStationId;
    private Long downStationId;
    private Long newStationId;
    private ExtractableResponse<Response> createResponse;

    @DisplayName("상행역, 하행역 및 노선 생성 등의 초기 설정")
    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        upStationId = 지하철역_저장("강남역").body().jsonPath().getLong("id");
        downStationId = 지하철역_저장("잠실역").body().jsonPath().getLong("id");
        newStationId = 지하철역_저장("건대역").body().jsonPath().getLong("id");
        createResponse = 노선_저장_후_응답(LINE_NAME, LINE_COLOR, upStationId, downStationId, DISTANCE);
    }

    @DisplayName("노선에 구간을 추가하는 기능")
    @Test
    void addSection() {
        //given
        Map<String, Object> params = 구간_저장을_위한_요청정보(newStationId, upStationId, DISTANCE - 1);

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
        ExtractableResponse<Response> response = 구간_추가_후_응답(params);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString()).isEqualTo("추가될 수 없는 구간입니다.");
    }

    @DisplayName("section을 삭제하는 기능")
    @Test
    void deleteSection() {
        //given
        Long lineId = createResponse.jsonPath().getObject(".", LineDetailsResponse.class).getId();
        Map<String, Object> params = 구간_저장을_위한_요청정보(newStationId, upStationId, DISTANCE - 1);
        ExtractableResponse<Response> sectionResponse = 구간_추가_후_응답(params);

        //when
        ExtractableResponse<Response> response = RestAssured
            .given().pathParam("lineId", lineId).log().all()
            .queryParam("stationId", newStationId)
            .when().delete("/lines/{lineId}/sections")
            .then().log().all()
            .extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("노선의 구간이 1개 일 때, 구간을 삭제 요청 시 예외처리")
    @Test
    void validateMinSectionDelete() {
        //given
        Long lineId = createResponse.jsonPath().getObject(".", LineDetailsResponse.class).getId();

        //when
        ExtractableResponse<Response> response = RestAssured
            .given().pathParam("lineId", lineId).log().all()
            .queryParam("stationId", upStationId)
            .when().delete("/lines/{lineId}/sections")
            .then().log().all()
            .extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString()).isEqualTo("노선 내 최소한 2개의 역이 존재해야 합니다.");
    }

    @DisplayName("노선 내 존재하지 않는 구간 삭제 요청 시 예외처리")
    @Test
    void validateNotFoundSectionDelete() {
        //given
        구간_추가_후_응답(
            구간_저장을_위한_요청정보(newStationId, upStationId, DISTANCE - 1));
        long anonymousStationId = 지하철역_저장("신림역").body().jsonPath().getLong("id");
        Long lineId = createResponse.jsonPath().getObject(".", LineDetailsResponse.class).getId();

        //when
        ExtractableResponse<Response> response = RestAssured
            .given().pathParam("lineId", lineId).log().all()
            .queryParam("stationId", anonymousStationId)
            .when().delete("/lines/{lineId}/sections")
            .then().log().all()
            .extract();
        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString()).isEqualTo("노선 내 존재하는 역이 없습니다.");
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
        ExtractableResponse<Response> sectionResponse = RestAssured
            .given().pathParam("lineId", lineId).log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines/{lineId}/sections")
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

    private ExtractableResponse<Response> 노선_저장_후_응답(String lineName, String lineColor,
        Long upStationId,
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
