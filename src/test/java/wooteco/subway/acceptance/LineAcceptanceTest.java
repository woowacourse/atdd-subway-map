package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import wooteco.subway.service.dto.response.LineResponse;

@DisplayName("노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @Test
    @DisplayName("노선을 등록한다.")
    void save() {
        // given
        long upStationId = saveStationAndGetId("잠실");
        long downStationId = saveStationAndGetId("강남");
        Map<String, Object> params = lineParam("신분당선", "bg-red-600", upStationId, downStationId);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(201);
        assertThat(response.contentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        assertThat(response.body().jsonPath().getString("name")).isEqualTo("신분당선");
        assertThat(response.body().jsonPath().getString("color")).isEqualTo("bg-red-600");
    }

    @Test
    @DisplayName("노선 생성시 빈 값일 경우 400응답을 한다.")
    void saveEmpty() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "");
        params.put("color", "");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(400);
    }

    @Test
    @DisplayName("노선을 id로 조회한다.")
    void showLine() {
        // given
        long upStationId = saveStationAndGetId("창동");
        long downStationId = saveStationAndGetId("도봉");
        long id = saveLineAndGetId("1호선", "blue", upStationId, downStationId);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .get("/lines/{id}", id)
            .then()
            .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.contentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        assertThat(response.body().jsonPath().getString("name")).isEqualTo("1호선");
        assertThat(response.body().jsonPath().getString("color")).isEqualTo("blue");
    }

    @Test
    @DisplayName("없는 노선을 조회시 에러")
    void notFindLine() {
        // given & when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .get("/lines/{id}", 1)
            .then()
            .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(404);
    }

    @Test
    @DisplayName("노선 목록을 조회한다.")
    void showLines() {
        /// given
        saveLineAndGetId("1호선", "blue", saveStationAndGetId("창동"), saveStationAndGetId("강남"));
        saveLineAndGetId("2호선", "green", saveStationAndGetId("도봉"), saveStationAndGetId("의정부"));

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .get("/lines")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.contentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        List<LineResponse> responses = response.body().jsonPath().getList(".", LineResponse.class);
        assertThat(responses).extracting("name").isEqualTo(List.of("1호선", "2호선"));
    }

    @Test
    @DisplayName("노선을 id로 수정한다.")
    void modify() {
        // given
        long id = saveLineAndGetId("1호선", "blue", saveStationAndGetId("잠실"), saveStationAndGetId("강남"));

        // when
        Map<String, String> params2 = new HashMap<>();
        params2.put("name", "2호선");
        params2.put("color", "green");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(params2)
            .when()
            .put("/lines/{id}", id)
            .then()
            .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("노선 수정시 존재하지 않는 id인 경우 400응답을 한다.")
    void modifyNotfoundId() {
        // given
        long id = saveLineAndGetId("1호선", "blue", saveStationAndGetId("잠실"), saveStationAndGetId("강남"));

        // when
        Map<String, String> params2 = new HashMap<>();
        params2.put("name", "2호선");
        params2.put("color", "green");

        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(params2)
            .when()
            .put("/lines/{id}", 2)
            .then()
            .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(404);
    }

    @Test
    @DisplayName("노선 수정시 빈 값일 경우 400응답을 한다.")
    void modifyEmpty() {
        // given
        long id = saveLineAndGetId("1호선", "blue", saveStationAndGetId("잠실"), saveStationAndGetId("강남"));

        // when
        Map<String, String> params2 = new HashMap<>();
        params2.put("name", "");
        params2.put("color", "");

        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(params2)
            .when()
            .put("/lines/{id}", id)
            .then()
            .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(400);
    }

    @Test
    @DisplayName("수정시 노선이 중복될 경우 400응답을 한다.")
    void duplicateUpdate() {
        // given
        saveLineAndGetId("1호선", "blue", saveStationAndGetId("창동"), saveStationAndGetId("도봉"));
        long id = saveLineAndGetId("2호선", "green", saveStationAndGetId("홍대"), saveStationAndGetId("건대"));

        Map<String, String> params = new HashMap<>();
        params.put("name", "1호선");
        params.put("color", "blue");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(params)
            .when()
            .put("/lines/{id}", id)
            .then()
            .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(400);
    }

    @Test
    @DisplayName("노선을 id로 삭제한다.")
    void deleteById() {
        // given
        long id = saveLineAndGetId("1호선", "blue", saveStationAndGetId("창동"), saveStationAndGetId("도봉"));

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .delete("/lines/{id}", id)
            .then()
            .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(204);
    }

    @Test
    @DisplayName("구간을 등록한다.")
    void saveSection() {
        // given
        long upStationId = saveStationAndGetId("의정부");
        long lineId = saveLineAndGetId("1호선", "blue", upStationId, saveStationAndGetId("인천"));
        long downStationId = saveStationAndGetId("광운대");

        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", 5);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines/{id}/sections", lineId)
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("구간을 삭제한다.")
    void deleteSection() {
        // given
        long upStationId = saveStationAndGetId("의정부");
        long downStationId = saveStationAndGetId("광운대");
        long addStationId = saveStationAndGetId("인천");
        long lineId = saveLineAndGetId("1호선", "blue", upStationId, downStationId);

        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", upStationId);
        params.put("downStationId", addStationId);
        params.put("distance", 3);

        RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines/{lineId}/sections", lineId)
            .then().log().all()
            .extract();

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .queryParam("stationId", addStationId)
            .delete("/lines/{id}/sections", lineId)
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(200);
    }

    private long saveLineAndGetId(String name, String color, Long upStation, Long downStation) {
        Map<String, Object> params = lineParam(name, color, upStation, downStation);
        ExtractableResponse<Response> savedResponse = RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
        return savedResponse.body().jsonPath().getLong("id");
    }

    private Map<String, Object> lineParam(String name, String color, Long upStationId, Long downStationId) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", 10);
        return params;
    }

    private long saveStationAndGetId(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        ExtractableResponse<Response> savedResponse = RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then().log().all()
            .extract();
        return savedResponse.body().jsonPath().getLong("id");
    }
}
