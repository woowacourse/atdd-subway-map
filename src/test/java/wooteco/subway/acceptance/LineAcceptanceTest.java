package wooteco.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.LineResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("노선 관련 Api")
public class LineAcceptanceTest extends AcceptanceTest {

    private long 강남역_ID = 1L;
    private long 역삼역_ID = 2L;

    @BeforeEach
    void setUpData() {
        강남역_ID = extractId(createStation("강남역"));
        역삼역_ID = extractId(createStation("역삼역"));
        createStation("선릉역");
    }
    
    @DisplayName("정상적으로 노선이 등록되는 경우를 테스트한다.")
    @Test
    void createLineTest() {
        ExtractableResponse<Response> response = createLine("신분당선", "bg-red-600", 강남역_ID, 역삼역_ID, 5);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 노선 이름을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        createLine("2호선", "bg-red-600", 강남역_ID, 역삼역_ID, 5);
        ExtractableResponse<Response> response = createLine("2호선", "bg-red-600", 강남역_ID, 역삼역_ID, 5);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선들을 조회한다.")
    @Test
    void getLines() {
        ExtractableResponse<Response> createResponse1 = createLine("신분당선", "bg-red-600",
                강남역_ID, 역삼역_ID, 5);
        ExtractableResponse<Response> createResponse2 = createLine("다른분당선", "bg-blue-600",
                강남역_ID, 역삼역_ID, 5);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());

        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("특정 노선을 조회한다.")
    @Test
    void getLine() {
        ExtractableResponse<Response> createResponse = createLine("신분당선", "bg-red-600",
                강남역_ID, 역삼역_ID, 5);

        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .get(uri)
                .then().log().all()
                .extract();

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.body().jsonPath().get("name").toString()).isEqualTo("신분당선")
        );
    }

    @DisplayName("존재하지 않는 노선을 조회하는 경우 예외를 발생시킨다.")
    @Test
    void getLineNotExist() {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .get("/lines/9999")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("특정 노선을 업데이트한다")
    @Test
    public void updateLine() {
        ExtractableResponse<Response> createResponse = createLine("신분당선", "bg-red-600",
                강남역_ID, 역삼역_ID, 5);

        String uri = createResponse.header("Location");

        Map<String, String> params2 = new HashMap<>();
        params2.put("name", "인간분당선");
        params2.put("color", "bg-red-600");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params2)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(uri)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("존재하지 않는 노선을 업데이트하는 경우 예외를 발생시킨다.")
    @Test
    public void updateLineNotExist() {
        Map<String, String> params2 = new HashMap<>();
        params2.put("name", "인간분당선");
        params2.put("color", "bg-red-600");

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params2)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/9999")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선을 삭제한다.")
    @Test
    void deleteLine() {
        ExtractableResponse<Response> createResponse = createLine("신분당선", "bg-red-600", 강남역_ID, 역삼역_ID, 5);

        String uri = createResponse.header("Location");
        Long id = Long.parseLong(createResponse.header("Location").split("/")[2]);
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        ExtractableResponse<Response> linesResponse = RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();

        List<Long> resultLineIds = linesResponse.jsonPath().getList(".", LineResponse.class).stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());

        assertThat(resultLineIds.contains(id)).isFalse();
    }

    @DisplayName("존재하지 않는 노선을 삭제하는 경우 예외를 발생시킨다.")
    @Test
    void deleteLineNotExist() {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete("/lines/9999")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> createLine(final String name, final String color, final Long upStationId,
                                                     final Long downStationId, final Integer distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> createStation(final String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }

    private Long extractId(ExtractableResponse<Response> response) {
        return Long.parseLong(response.header("Location").split("/")[2]);
    }
}
