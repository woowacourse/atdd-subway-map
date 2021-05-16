package wooteco.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static wooteco.subway.util.RestfulOrder.DEFAULT_MEDIA_TYPE;
import static wooteco.subway.util.RestfulOrder.testResponse;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.AcceptanceTest;

public class LineAcceptanceTest extends AcceptanceTest {

    static Map<String, String> defaultLineParams;
    static Map<String, String> defaultLineParams2;

    @BeforeAll
    static void fillDefaultDataParams() {
        defaultLineParams = new HashMap<>();
        defaultLineParams.put("name", "신분당선");
        defaultLineParams.put("color", "bg-red-600");
        defaultLineParams.put("upStationId", "1");
        defaultLineParams.put("downStationId", "2");
        defaultLineParams.put("distance", "10");

        defaultLineParams2.put("name", "1호선");
        defaultLineParams2.put("color", "bg-red-600");
        defaultLineParams2.put("upStationId", "1");
        defaultLineParams2.put("downStationId", "2");
        defaultLineParams2.put("distance", "10");
    }

    @Autowired
    private LineDao lineDao;


    @DisplayName("지하철 노선을 생성한다.")
    @Test
    public void createLine() {
        // given

        // when
        ExtractableResponse<Response> response = testResponse(defaultLineParams, DEFAULT_MEDIA_TYPE,
            "/lines");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }


    @DisplayName("중복된 노선 이름 추가시 예외 처리")
    @Test
    void nameDuplication() {
        testResponse(defaultLineParams, DEFAULT_MEDIA_TYPE, "/lines");

        ExtractableResponse<Response> createResponse2 = testResponse(defaultLineParams,
            DEFAULT_MEDIA_TYPE,
            "/lines");

        assertThat(createResponse2.statusCode())
            .isEqualTo(HttpStatus.CONFLICT.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        /// given
        ExtractableResponse<Response> createResponse1 = testResponse(defaultLineParams,
            DEFAULT_MEDIA_TYPE,
            "/lines");
        ExtractableResponse<Response> createResponse2 = testResponse(defaultLineParams2,
            DEFAULT_MEDIA_TYPE,
            "/lines");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .get("/lines")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
            .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
            .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
            .map(LineResponse::getId)
            .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("하나의 지하철 노선을 상세 조회한다.")
    @Test
    void getLineDetail() {
        createStation("강남역");
        createStation("서초역");
        getLines();
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .contentType(DEFAULT_MEDIA_TYPE)
            .when()
            .get("/lines/1")
            .then().log().all()
            .extract();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("없는 지하철 노선 조회 시 예외 메시지를 출력한다")
    @Test
    void invalidLineId() {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/lines/1")
            .then().log().all()
            .extract();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 수정")
    @Test
    void modifyLine() {
        lineDao.save(new Line(1L, "name", "yellow darken-4"));

        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(defaultLineParams)
            .when()
            .put("/lines/1")
            .then().log().all()
            .extract();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선 삭제")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> createResponse = testResponse(defaultLineParams,
            DEFAULT_MEDIA_TYPE,
            "/lines");

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .delete(uri)
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("라인 삽입시 인자가 모자란 경우")
    @Test
    void wrongParamsTest() {
        // given
        Map<String, String> wrongParams = new HashMap<>();
        wrongParams.put("name", "2호선");
        ExtractableResponse<Response> response = testResponse(wrongParams,
            DEFAULT_MEDIA_TYPE,
            "/lines");
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void createStation(String stationName) {
        Map<String, String> params = new HashMap<>();
        params.put("name", stationName);
        ExtractableResponse<Response> response = testResponse(params, DEFAULT_MEDIA_TYPE,
            "/stations");
        // when

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }
}
