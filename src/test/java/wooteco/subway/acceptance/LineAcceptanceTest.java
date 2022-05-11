package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import wooteco.subway.dto.response.LineResponse;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private void createStationForTest(String stationName) {
        Map<String, String> params = new HashMap<>();
        params.put("name", stationName);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then().log().all()
            .extract();
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        createStationForTest("강남역");
        createStationForTest("선릉역");

        Map<String, String> params = new HashMap<>();
        params.put("name", "2호선");
        params.put("color", "green");
        params.put("upStationId", "1");
        params.put("downStationId", "2");
        params.put("distance", "10");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.(400에러)")
    @Test
    void createLineWithDuplicateName() {
        // given
        createStationForTest("강남역");
        createStationForTest("선릉역");

        Map<String, String> params = new HashMap<>();
        params.put("name", "2호선");
        params.put("color", "green");
        params.put("upStationId", "1");
        params.put("downStationId", "2");
        params.put("distance", "10");
        RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then()
            .log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 전체를 조회한다.")
    @Test
    void getLines() {
        /// given
        createStationForTest("강남역");
        createStationForTest("선릉역");

        Map<String, String> params1 = new HashMap<>();
        params1.put("name", "2호선");
        params1.put("color", "green");
        params1.put("upStationId", "1");
        params1.put("downStationId", "2");
        params1.put("distance", "10");
        ExtractableResponse<Response> createResponse1 = RestAssured.given().log().all()
            .body(params1)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        Map<String, String> params2 = new HashMap<>();
        params2.put("name", "3호선");
        params2.put("color", "blue");
        params2.put("upStationId", "1");
        params2.put("downStationId", "2");
        params2.put("distance", "10");
        ExtractableResponse<Response> createResponse2 = RestAssured.given().log().all()
            .body(params2)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

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

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        /// given
        createStationForTest("강남역");
        createStationForTest("선릉역");

        Map<String, String> params1 = new HashMap<>();
        params1.put("name", "2호선");
        params1.put("color", "green");
        params1.put("upStationId", "1");
        params1.put("downStationId", "2");
        params1.put("distance", "10");
        ExtractableResponse<Response> createResponse1 = RestAssured.given().log().all()
            .body(params1)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        // when
        String uri = createResponse1.header("Location");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .get(uri)
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        Long expectedLineId = Long.parseLong(createResponse1.header("Location").split("/")[2]);
        assertThat(expectedLineId).isEqualTo(response.jsonPath().getLong("id"));
        assertThat(createResponse1.jsonPath().getLong("id")).isEqualTo(response.jsonPath().getLong("id"));
        assertThat(createResponse1.jsonPath().getString("name")).isEqualTo(response.jsonPath().getString("name"));
        assertThat(createResponse1.jsonPath().getString("color")).isEqualTo(response.jsonPath().getString("color"));
    }

    @DisplayName("존재하지 않는 지하철 노선을 조회한다.(400에러)")
    @Test
    void getLineNotExists() {
        // given

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .get("/lines/1")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        /// given
        createStationForTest("강남역");
        createStationForTest("선릉역");

        Map<String, String> params1 = new HashMap<>();
        params1.put("name", "2호선");
        params1.put("color", "green");
        params1.put("upStationId", "1");
        params1.put("downStationId", "2");
        params1.put("distance", "10");
        ExtractableResponse<Response> createResponse1 = RestAssured.given().log().all()
            .body(params1)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
        String uri = createResponse1.header("Location");

        // when
        Map<String, String> params2 = new HashMap<>();
        params2.put("name", "다른분당선");
        params2.put("color", "green");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(params2)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put(uri)
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("존재하지 않는 지하철 노선을 수정한다.(400에러)")
    @Test
    void updateLineNotExists() {
        // given

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .put("/lines/1")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 수정한다.(400에러)")
    @Test
    void updateLineWithDuplicateName() {
        /// given
        createStationForTest("강남역");
        createStationForTest("선릉역");

        Map<String, String> params1 = new HashMap<>();
        params1.put("name", "2호선");
        params1.put("color", "green");
        params1.put("upStationId", "1");
        params1.put("downStationId", "2");
        params1.put("distance", "10");
        ExtractableResponse<Response> createResponse1 = RestAssured.given().log().all()
            .body(params1)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
        String uri = createResponse1.header("Location");

        Map<String, String> params2 = new HashMap<>();
        params2.put("name", "다른분당선");
        params2.put("color", "blue");
        params2.put("upStationId", "1");
        params2.put("downStationId", "2");
        params2.put("distance", "10");
        ExtractableResponse<Response> createResponse2 = RestAssured.given().log().all()
            .body(params2)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        // when
        Map<String, String> params3 = new HashMap<>();
        params3.put("name", "다른분당선");
        params3.put("color", "green");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(params3)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put(uri)
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        createStationForTest("강남역");
        createStationForTest("선릉역");

        Map<String, String> params = new HashMap<>();
        params.put("name", "2호선");
        params.put("color", "green");
        params.put("upStationId", "1");
        params.put("downStationId", "2");
        params.put("distance", "10");
        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

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

    @DisplayName("존재하지 않는 지하철 노선을 제거한다.(400에러)")
    @Test
    void deleteLineNotExists() {
        // given

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .delete("/lines/1")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("구간을 생성한다.")
    @Test
    void saveSection() {
        createStationForTest("강남역");
        createStationForTest("선릉역");
        createStationForTest("잠실역");

        ExtractableResponse<Response> createLineResponse = createLineForTest("2호선", "green", "1", "2", "10");

        Map<String, String> params = new HashMap<>();
        params.put("upStationId", "2");
        params.put("downStationId", "3");
        params.put("distance", "10");

        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines/" + createLineResponse.jsonPath().getLong("id") + "/sections")
            .then().log().all()
            .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

    }

    private ExtractableResponse<Response> createLineForTest(String name, String color, String upStationId,
        String downStationId, String distance) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);
        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        return createResponse;
    }
}
