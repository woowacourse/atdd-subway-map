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
import wooteco.subway.dto.LineResponse;

@DisplayName("노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @Test
    @DisplayName("노선을 등록한다.")
    void save() {
        // given
        long 잠실역 = saveStationAndGetId("잠실");
        long 강남역 = saveStationAndGetId("강남");
        Map<String, Object> params = lineParam("신분당선", "bg-red-600", 잠실역, 강남역);

        // when
        ExtractableResponse<Response> response = createLine(params);

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
        long 창동역 = saveStationAndGetId("창동");
        long 도봉역 = saveStationAndGetId("도봉");
        long id = saveLineAndGetId("1호선", "blue", 창동역, 도봉역);

        // when
        ExtractableResponse<Response> response = findLine(id);

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
        ExtractableResponse<Response> response = findLine(1);
        // then
        assertThat(response.statusCode()).isEqualTo(400);
    }

    @Test
    @DisplayName("노선 목록을 조회한다.")
    void showLines() {
        /// given
        saveLineAndGetId("1호선", "blue", saveStationAndGetId("잠실"), saveStationAndGetId("강남"));
        saveLineAndGetId("2호선", "green", saveStationAndGetId("창동"), saveStationAndGetId("의정부"));

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

        ExtractableResponse<Response> response = modifyLine(id, params2);

        // then
        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("노선 수정시 존재하지 않는 id인 경우 400응답을 한다.")
    void modifyNotfoundId() {
        // given
        long 잠실역 = saveStationAndGetId("잠실");
        long 강남역 = saveStationAndGetId("강남");
        long id = saveLineAndGetId("1호선", "blue", 잠실역, 강남역);

        // when
        Map<String, String> params2 = new HashMap<>();
        params2.put("name", "2호선");
        params2.put("color", "green");

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params2)
                .when()
                .put("/lines/{id}", 222)
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(400);
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

        ExtractableResponse<Response> response = modifyLine(id, params2);

        // then
        assertThat(response.statusCode()).isEqualTo(400);
    }

    @Test
    @DisplayName("노선 수정시 노선 이름이 중복될 경우 400응답을 한다.")
    void duplicateUpdate() {
        // given
        long line1 = saveLineAndGetId("1호선", "green", saveStationAndGetId("홍대"), saveStationAndGetId("건대"));
        long line2 = saveLineAndGetId("2호선", "blue", saveStationAndGetId("강남"), saveStationAndGetId("성수"));
        Map<String, Object> params = new HashMap<>();
        params.put("name", "1호선");
        params.put("color", "blue");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when()
                .put("/lines/{id}", line2)
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(400);
    }

    @Test
    @DisplayName("노선 수정시 노선 색깔이 중복될 경우 400응답을 한다.")
    void duplicateColorUpdate() {
        // given
        long line1 = saveLineAndGetId("1호선", "green", saveStationAndGetId("홍대"), saveStationAndGetId("건대"));
        long line2 = saveLineAndGetId("2호선", "blue", saveStationAndGetId("강남"), saveStationAndGetId("성수"));
        Map<String, Object> params = new HashMap<>();
        params.put("name", "2호선");
        params.put("color", "green");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when()
                .put("/lines/{id}", line2)
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
    @DisplayName("1호선에 갈래길 구간을 만든다.")
    void createSection() {
        // given
        long 잠실역 = saveStationAndGetId("잠실");
        long 강남역 = saveStationAndGetId("강남");
        long id = saveLineAndGetId("1호선", "blue", 잠실역, 강남역);
        long 미르역 = saveStationAndGetId("미르역");
        Map<String, Object> params = sectionParam(잠실역, 미르역, 5);
        // when
        ExtractableResponse<Response> response = requestCreateSection(id, params);
        // then
        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("노선에 기존에 있는 구간과 같은 구간을 추가시,400 에러가 난다.")
    void createDuplicateFalse() {
        // given
        long 잠실역 = saveStationAndGetId("잠실");
        long 강남역 = saveStationAndGetId("강남");
        long id = saveLineAndGetId("1호선", "blue", 잠실역, 강남역);

        Map<String, Object> params = sectionParam(잠실역, 강남역, 10);
        // when
        ExtractableResponse<Response> response = requestCreateSection(id, params);
        // then
        assertThat(response.statusCode()).isEqualTo(400);
    }

    private ExtractableResponse<Response> requestCreateSection(long id, Map<String, Object> params) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when()
                .post("/lines/{id}/sections", id)
                .then()
                .log().all().extract();
    }

    @Test
    @DisplayName("노선에 있는 구간 삽입을 할 때 기준이 되는 구간의 거리보다 추가하는 구간이 길 경우,400 에러가 난다.")
    void createOverDistanceFalse() {
        // given
        long 잠실역 = saveStationAndGetId("잠실");
        long 강남역 = saveStationAndGetId("강남");
        long 미르역 = saveStationAndGetId("미르역");
        long id = saveLineAndGetId("1호선", "blue", 잠실역, 강남역);
        Map<String, Object> params = sectionParam(잠실역, 미르역, 11);
        // when
        ExtractableResponse<Response> response = requestCreateSection(id, params);
        // then
        assertThat(response.statusCode()).isEqualTo(400);
    }

    @Test
    @DisplayName("구간내에서 맨 앞 역이 삭제된다.")
    void deleteFrontSection() {
        // given
        long 잠실역 = saveStationAndGetId("잠실");
        long 강남역 = saveStationAndGetId("강남");
        long 미르역 = saveStationAndGetId("미르역");
        long id = saveLineAndGetId("1호선", "blue", 잠실역, 강남역);
        Map<String, Object> params = sectionParam(잠실역, 미르역, 5);
        requestCreateSection(id, params);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when()
                .delete("/lines/{lineId}/sections?stationId={stationId}", id, 잠실역)
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("구간내에서 가운데 역이 삭제된다.")
    void deleteBetweenSection() {
        // given
        long 잠실역 = saveStationAndGetId("잠실");
        long 강남역 = saveStationAndGetId("강남");
        long 미르역 = saveStationAndGetId("미르역");
        long id = saveLineAndGetId("1호선", "blue", 잠실역, 강남역);
        Map<String, Object> params = sectionParam(잠실역, 미르역, 5);
        requestCreateSection(id, params);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when()
                .delete("/lines/{lineId}/sections?stationId={stationId}", id, 미르역)
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("구간내에서 맨끝 역이 삭제된다.")
    void deleteBackSection() {
        // given
        long 잠실역 = saveStationAndGetId("잠실");
        long 강남역 = saveStationAndGetId("강남");
        long 미르역 = saveStationAndGetId("미르역");
        long id = saveLineAndGetId("1호선", "blue", 잠실역, 강남역);
        Map<String, Object> params = sectionParam(잠실역, 미르역, 5);
        requestCreateSection(id, params);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when()
                .delete("/lines/{lineId}/sections?stationId={stationId}", id, 강남역)
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(200);
    }

    private long saveLineAndGetId(String name, String color, Long upStationId, Long downStationId) {
        Map<String, Object> params = lineParam(name, color, upStationId, downStationId);
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

    private Map<String, Object> sectionParam(Long upStationId, Long downStationId, int distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);
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

    private ExtractableResponse<Response> createLine(Map<String, Object> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> findLine(long id) {
        return RestAssured.given().log().all()
                .when()
                .get("/lines/{id}", id)
                .then()
                .log().all().extract();
    }

    private ExtractableResponse<Response> modifyLine(long id, Map<String, String> params2) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params2)
                .when()
                .put("/lines/{id}", id)
                .then()
                .log().all().extract();
    }
}
