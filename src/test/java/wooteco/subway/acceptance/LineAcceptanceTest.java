package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.LineResponse;

public class LineAcceptanceTest extends AcceptanceTest {


    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        ExtractableResponse<Response> stationResponse1 = post("/stations", Map.of("name", "강남역"));
        ExtractableResponse<Response> stationResponse2 = post("/stations", Map.of("name", "잠실역"));

        Map<String, Object> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "bg-red-600");
        params.put("upStationId", getId(stationResponse1));
        params.put("downStationId", getId(stationResponse2));
        params.put("distance", 10);

        ExtractableResponse<Response> response = post("/lines", params);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        ExtractableResponse<Response> stationResponse1 = post("/stations", Map.of("name", "강남역"));
        ExtractableResponse<Response> stationResponse2 = post("/stations", Map.of("name", "잠실역"));
        // given
        Map<String, Object> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "bg-red-600");
        params.put("upStationId", getId(stationResponse1));
        params.put("downStationId", getId(stationResponse2));
        params.put("distance", 10);

        post("/lines", params);
        ExtractableResponse<Response> response = post("/lines", params);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("전체 지하철 노선을 조회한다.")
    @Test
    void getLines() {
        ExtractableResponse<Response> stationResponse1 = post("/stations", Map.of("name", "강남역"));
        ExtractableResponse<Response> stationResponse2 = post("/stations", Map.of("name", "잠실역"));
        ExtractableResponse<Response> stationResponse3 = post("/stations", Map.of("names", "선릉역"));

        Map<String, Object> params1 = new HashMap<>();
        params1.put("name", "신분당선");
        params1.put("color", "bg-red-600");
        params1.put("upStationId", getId(stationResponse1));
        params1.put("downStationId", getId(stationResponse2));
        params1.put("distance", 10);

        ExtractableResponse<Response> createdResponse1 = post("/lines", params1);

        Map<String, Object> params2 = new HashMap<>();
        params2.put("name", "분당선");
        params2.put("color", "bg-green-600");
        params2.put("upStationId", getId(stationResponse1));
        params2.put("downStationId", getId(stationResponse2));
        params2.put("distance", 3);

        ExtractableResponse<Response> createdResponse2 = post("/lines", params2);

        ExtractableResponse<Response> response = get("/lines");

        List<Long> expectedLineIds = Arrays.asList(createdResponse1, createdResponse2).stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());

        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선 단일 조회한다.")
    @Test
    void getLine() {
        ExtractableResponse<Response> stationResponse1 = post("/stations", Map.of("name", "강남역"));
        ExtractableResponse<Response> stationResponse2 = post("/stations", Map.of("name", "잠실역"));

        Map<String, Object> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "bg-red-600");
        params.put("upStationId", getId(stationResponse1));
        params.put("downStationId", getId(stationResponse2));
        params.put("distance", 10);
        ExtractableResponse<Response> createResponse1 = post("/lines", params);

        String value = createResponse1.header("Location").split("/")[2];
        int id = Integer.parseInt(value);

        ExtractableResponse<Response> response = get("/lines/" + id);
        Map<String, Object> values = response.jsonPath().get();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(values.get("id")).isEqualTo(id);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        ExtractableResponse<Response> stationResponse1 = post("/stations", Map.of("name", "강남역"));
        ExtractableResponse<Response> stationResponse2 = post("/stations", Map.of("name", "잠실역"));

        Map<String, Object> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "bg-red-600");
        params.put("upStationId", getId(stationResponse1));
        params.put("downStationId", getId(stationResponse2));
        params.put("distance", 10);
        ExtractableResponse<Response> createResponse = post("/lines", params);

        String value = createResponse.header("Location").split("/")[2];
        int expected = Integer.parseInt(value);

        Map<String, Object> newParams = new HashMap<>();
        newParams.put("name", "분당선");
        newParams.put("color", "bg-green-600");
        newParams.put("upStationId", getId(stationResponse1));
        newParams.put("downStationId", getId(stationResponse2));
        newParams.put("distance", 3);

        ExtractableResponse<Response> response = put("/lines/" + expected, newParams);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("기존에 존재하는 지하철 노선으로 수정할 시 상태 코드 400을 반환한다.")
    @Test
    void updateLineWithDuplicateName() {
        ExtractableResponse<Response> stationResponse1 = post("/stations", Map.of("name", "강남역"));
        ExtractableResponse<Response> stationResponse2 = post("/stations", Map.of("name", "잠실역"));
        ExtractableResponse<Response> stationResponse3 = post("/stations", Map.of("names", "선릉역"));

        // given
        Map<String, Object> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "bg-red-600");
        params.put("upStationId", getId(stationResponse1));
        params.put("downStationId", getId(stationResponse2));
        params.put("distance", 3);

        post("/lines", params);

        Map<String, Object> params2 = new HashMap<>();
        params2.put("name", "분당선");
        params2.put("color", "bg-green-600");
        params2.put("upStationId", getId(stationResponse1));
        params2.put("downStationId", getId(stationResponse2));

        ExtractableResponse<Response> createdResponse = post("/lines", params2);
        String value = createdResponse.header("Location").split("/")[2];
        int id = Integer.parseInt(value);

        // when
        Map<String, Object> newParams = new HashMap<>();
        newParams.put("name", "신분당선");
        newParams.put("color", "bg-red-600");
        newParams.put("upStationId", getId(stationResponse1));
        newParams.put("downStationId", getId(stationResponse2));
        newParams.put("distance", 3);

        ExtractableResponse<Response> response = put("/lines/" + id, newParams);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteLine() {
        ExtractableResponse<Response> stationResponse1 = post("/stations", Map.of("name", "강남역"));
        ExtractableResponse<Response> stationResponse2 = post("/stations", Map.of("name", "잠실역"));

        Map<String, Object> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "bg-red-600");
        params.put("upStationId", getId(stationResponse1));
        params.put("downStationId", getId(stationResponse2));
        params.put("distance", 10);

        ExtractableResponse<Response> createResponse = post("/lines", params);
        String uri = createResponse.header("Location");

        ExtractableResponse<Response> response = delete(uri);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("존재하지 않은 지하철 노선을 삭제할시 상태코드 400을 반환한다.")
    @Test
    void deleteNotExistLine() {
        ExtractableResponse<Response> response = delete("/lines/1");

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> post(String url, Map<String, Object> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(url)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> get(String url) {
        return RestAssured.given().log().all()
                .when()
                .get(url)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> put(String url, Map<String, Object> newParams) {
        return RestAssured.given().log().all()
                .body(newParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(url)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> delete(String url) {
        return RestAssured.given().log().all()
                .when()
                .delete(url)
                .then().log().all()
                .extract();
    }

    private Long getId(ExtractableResponse<Response> response) {
        String location = response.header("Location");
        String value = location.split("/")[2];
        return Long.parseLong(value);
    }
}
