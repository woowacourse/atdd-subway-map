package wooteco.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.line.api.dto.LineDetailsResponse;

@DisplayName("노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private static final String LINE_NAME = "2호선";
    private static final String LINE_COLOR = "green";
    private static final int DISTANCE = 1;

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

    @DisplayName("노선을 생성한다.")
    @Test
    void createLine() {
        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(createResponse.header("Location")).isNotBlank();
    }

    @DisplayName("노선 이름 중복 생성 불가 기능")
    @Test
    void duplicatedLineName() {
        // when
        String duplicateLineName = "2호선";
        String newLineColor = "red";
        ExtractableResponse<Response> response = 노선_저장_후_응답(duplicateLineName, newLineColor,
            upStationId, downStationId, DISTANCE);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString()).isEqualTo("이미 존재하는 노선 이름입니다.");
    }

    @DisplayName("잘못된 요청값으로 노선 생성 요청시, 예외처리")
    @Test
    void createLineFailByNotValidatedRequest() {
        // given
        String wrongLineName = "2";
        String wrongLineColor = "";

        //when
        ExtractableResponse<Response> response = 노선_저장_후_응답(wrongLineName, wrongLineColor,
            upStationId, downStationId, DISTANCE);

        // then
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
            () -> assertThat(response.body().jsonPath().getString("color"))
                .isEqualTo("노선 색을 지정해야합니다."),
            () -> assertThat(response.body().jsonPath().getString("name"))
                .isEqualTo("노선 이름은 최소 2글자 이상만 가능합니다.")
        );
    }

    @DisplayName("노선 색깔 중복 생성 불가 기능")
    @Test
    void duplicatedLineColor() {
        // given
        String newLineName = "3호선";
        String duplicateColor = "green";

        // when
        ExtractableResponse<Response> response = 노선_저장_후_응답(newLineName, duplicateColor,
            upStationId, downStationId, DISTANCE);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString()).isEqualTo("이미 존재하는 노선 색깔입니다.");
    }


    @DisplayName("전체 노선을 조회한다")
    @Test
    void getLines() {
        //given
        String lineName2 = "신분당선";
        String lineColor2 = "red";
        노선_저장_후_응답(lineName2, lineColor2, upStationId, downStationId, DISTANCE);

        //when
        ExtractableResponse<Response> response = 노선_조회_후_응답("/lines");

        //then
        List<String> expectedLineNames = Arrays.asList(LINE_NAME, lineName2);
        List<String> resultLineNames = response.jsonPath().getList(".", LineDetailsResponse.class)
            .stream()
            .map(LineDetailsResponse::getName)
            .collect(Collectors.toList());

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(resultLineNames).containsAll(expectedLineNames);
    }

    @DisplayName("단일 노선 조회")
    @Test
    void getLine() {
        //when
        Long id = createResponse.jsonPath().getObject(".", LineDetailsResponse.class).getId();
        ExtractableResponse<Response> findResponse = 노선_조회_후_응답("/lines/" + id);

        //then
        assertThat(findResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

        LineDetailsResponse lineDetailsResponse = findResponse.jsonPath()
            .getObject(".", LineDetailsResponse.class);
        assertThat(lineDetailsResponse.getName()).isEqualTo(LINE_NAME);
        assertThat(lineDetailsResponse.getColor()).isEqualTo(LINE_COLOR);
    }

    @DisplayName("노선을 수정하는 기능")
    @Test
    void updateLine() {
        //given
        String newLineName = "3호선";
        String newLineColor = "orange";
        Map<String, String> params = 노선_수정을_위한_요청_정보(newLineName, newLineColor);

        //when
        Long id = createResponse.jsonPath().getObject(".", LineDetailsResponse.class).getId();
        ExtractableResponse<Response> response = RestAssured
            .given().pathParam("lineId", id).log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().log().all()
            .put("/lines/{lineId}")
            .then().log().all()
            .extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("존재하지 않는 단일 노선 조회")
    @Test
    void getLineIfNotFoundId() {
        //when
        Long id = createResponse.jsonPath().getObject(".", LineDetailsResponse.class).getId();
        ExtractableResponse<Response> response = 노선_조회_후_응답("/lines/" + (id + 1L));

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString()).isEqualTo("존재하지 않는 노선 ID 입니다.");
    }

    @DisplayName("노선 삭제 기능")
    @Test
    void delete() {
        //when
        Long id = createResponse.body().jsonPath().getObject(".", LineDetailsResponse.class)
            .getId();
        ExtractableResponse<Response> response = 노선_삭제_후_응답(id);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("존재하지 않는 노선 삭제 요청 시, 예외 처리 기능")
    @Test
    void deleteIfNotExistLineId() {
        //when
        Long id = createResponse.body().jsonPath().getObject(".", LineDetailsResponse.class)
            .getId();
        ExtractableResponse<Response> response = 노선_삭제_후_응답(id + 1);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString()).isEqualTo("존재하지 않는 노선 ID 입니다.");
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

    private Map<String, String> 노선_수정을_위한_요청_정보(String lineName, String lineColor) {
        Map<String, String> params = new HashMap<>();
        params.put("color", lineColor);
        params.put("name", lineName);
        return params;
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

    private ExtractableResponse<Response> 노선_삭제_후_응답(Long id) {
        return RestAssured.given().pathParam("lineId", id).log().all()
            .when().log().all()
            .delete("/lines/{lineId}")
            .then()
            .log().all()
            .extract();
    }

    private ExtractableResponse<Response> 노선_조회_후_응답(String path) {
        return RestAssured.given().log().all()
            .when()
            .get(path)
            .then().log().all()
            .extract();
    }
}
