package wooteco.subway.acceptance;

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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.dto.response.LineResponse;

@DisplayName("지하철 노선 관리 API")
public class LineAcceptanceTest extends AcceptanceTest {

    private static final String HYEHWA = "혜화역";
    private static final String SINSA = "신사역";
    private static final String LINE_2 = "2호선";
    private static final String RED = "bg-red-600";

    @Test
    @DisplayName("지하철 노선을 등록한다.")
    void create() {
        // given
        final Long upStationId = createStation(HYEHWA);
        final Long downStationId = createStation(SINSA);

        final Map<String, Object> params = new HashMap<>();
        params.put("name", LINE_2);
        params.put("color", RED);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", 10);

        // when
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
        final List<StationResponse> stationResponses = response.body().jsonPath()
                .getList("stations", StationResponse.class);

        // then
        assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
            assertThat(response.header("Location")).isNotBlank();
            assertThat(response.body().jsonPath().getString("name")).isEqualTo(LINE_2);
            assertThat(response.body().jsonPath().getString("color")).isEqualTo(RED);
            assertThat(stationResponses).hasSize(2);
            assertThat(stationResponses.get(0).getName()).isEqualTo(HYEHWA);
            assertThat(stationResponses.get(1).getName()).isEqualTo(SINSA);
        });
    }

    @Test
    @DisplayName("기존에 존재하는 노선 이름으로 생성하면, 예외를 발생한다.")
    void createWithDuplicateName() {
        // given
        final Long upStationId = createStation(HYEHWA);
        final Long downStationId = createStation(SINSA);

        final Map<String, Object> params = new HashMap<>();
        params.put("name", LINE_2);
        params.put("color", RED);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", 10);

        RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // when
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("모든 지하철 노선을 조회한다.")
    void getLines() {
        // given
        final Map<String, String> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "bg-red-600");
        final ExtractableResponse<Response> createResponse1 = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        final Map<String, String> params2 = new HashMap<>();
        params2.put("name", "분당선");
        params2.put("color", "bg-black-000");
        final ExtractableResponse<Response> createResponse2 = RestAssured.given().log().all()
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
        final List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        final List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @Test
    @DisplayName("지하철 노선 ID로 노선을 조회한다.")
    void show() {
        // given
        final Long upStationId = createStation(HYEHWA);
        final Long downStationId = createStation(SINSA);

        final Map<String, Object> params = new HashMap<>();
        params.put("name", LINE_2);
        params.put("color", RED);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", 10);
        final long id = Long.parseLong(RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract()
                .header("Location").split("/")[2]);

        // when
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines/" + id)
                .then().log().all()
                .extract();
        final List<StationResponse> stationResponses = response.body().jsonPath()
                .getList("stations", StationResponse.class);

        // then
        assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.body().jsonPath().getString("name")).isEqualTo(LINE_2);
            assertThat(response.body().jsonPath().getString("color")).isEqualTo(RED);
            assertThat(stationResponses).hasSize(2);
            assertThat(stationResponses.get(0).getName()).isEqualTo(HYEHWA);
            assertThat(stationResponses.get(1).getName()).isEqualTo(SINSA);
        });
    }

    @Test
    @DisplayName("존재하지 않는 지하철 노선 ID로 조회한다면, 예외를 발생한다.")
    void getLineNotExistId() {
        // given
        final long id = 1L;

        // when
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines/" + id)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("노선을 업데이트 한다.")
    void updateLine() {
        // given
        final Map<String, String> createParams = new HashMap<>();
        createParams.put("name", "신분당선");
        createParams.put("color", "bg-red-600");
        final long id = Long.parseLong(RestAssured.given().log().all()
                .body(createParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract()
                .header("Location")
                .split("/")[2]);
        final Map<String, String> params = new HashMap<>();
        params.put("name", "분당선");
        params.put("color", "bg-blue-500");

        // when
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + id)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("존재하지 않는 ID로 업데이트 한다면, 예외를 발생한다.")
    void updateNotExistId() {
        // given
        final long id = 100L;
        final Map<String, String> params = new HashMap<>();
        params.put("name", "분당선");
        params.put("color", "bg-blue-500");

        // when
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + id)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("지하철 노선을 삭제한다.")
    void deleteLine() {
        // given
        final Map<String, String> createParams = new HashMap<>();
        createParams.put("name", "신분당선");
        createParams.put("color", "bg-red-600");
        final ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(createParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // when
        final String uri = createResponse.header("Location");
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("존재하지 않는 ID로 삭제한다면, 예외를 발생한다.")
    void deleteLineNotExistId() {
        // given
        final long id = 1L;

        // when
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete("/lines/" + id)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    private Long createStation(final String name) {
        final Map<String, String> params = new HashMap<>();
        params.put("name", name);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract()
                .body().jsonPath().getLong("id");
    }
}
