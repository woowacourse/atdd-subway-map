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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.dao.JdbcStationDao;
import wooteco.subway.dto.LineResponse;

public class LineAcceptanceTest extends AcceptanceTest {

    private JdbcStationDao jdbcStationDao;
    private String upStationId;
    private String downStationId;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void set() {
        jdbcStationDao = new JdbcStationDao(jdbcTemplate);
        upStationId = String.valueOf(jdbcStationDao.save("강남역"));
        downStationId = String.valueOf(jdbcStationDao.save("역삼역"));
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "bg-red-600");
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", "10");

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("전체 지하철 노선을 조회한다.")
    @Test
    void getLines() {
        Map<String, String> params1 = new HashMap<>();
        params1.put("name", "신분당선");
        params1.put("color", "bg-red-600");
        params1.put("upStationId", upStationId);
        params1.put("downStationId", downStationId);
        params1.put("distance", "10");

        ExtractableResponse<Response> createResponse1 = RestAssured.given().log().all()
                .body(params1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        Map<String, String> params2 = new HashMap<>();
        params2.put("name", "분당선");
        params2.put("color", "bg-green-600");
        params2.put("upStationId", upStationId);
        params2.put("downStationId", downStationId);
        params2.put("distance", "10");

        ExtractableResponse<Response> createResponse2 = RestAssured.given().log().all()
                .body(params2)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선 단일 조회한다.")
    @Test
    void getLine() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "bg-red-600");
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", "10");

        ExtractableResponse<Response> createResponse1 = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
        String value = createResponse1.header("Location").split("/")[2];
        int expected = Integer.parseInt(value);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines/" + expected)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        Map<String, Object> values = response.jsonPath().get();
        assertThat(values.get("id")).isEqualTo(expected);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "bg-red-600");
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", "10");

        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
        String value = createResponse.header("Location").split("/")[2];
        int expected = Integer.parseInt(value);

        Map<String, String> newParams = new HashMap<>();
        newParams.put("name", "분당선");
        newParams.put("color", "bg-green-600");
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", "10");

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(newParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + expected)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteLine() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "bg-red-600");
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", "10");
        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
        String value = createResponse.header("Location").split("/")[2];
        int expected = Integer.parseInt(value);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete("/lines/" + expected)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
