package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.*;

import java.net.URI;
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

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

@DisplayName("지하철 노선 관련 기능")
class LineAcceptanceTest extends AcceptanceTest {
    private Long stationId1;
    private Long stationId2;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;

        clearAllStations();
        clearAllLines();
        createStations();
    }

    private void createStations() {
        Map<String, String> params = new HashMap<>();

        params.put("name", "선릉역");
        ExtractableResponse<Response> response1 = RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then().log().all()
            .extract();
        String location1 = response1.header("Location"); // "/stations/1"
        stationId1 = Long.parseLong(location1.split("/")[2]);

        params.put("name", "강남역");
        ExtractableResponse<Response> response2 = RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then().log().all()
            .extract();
        String location2 = response2.header("Location");
        stationId2 = Long.parseLong(location2.split("/")[2]);
    }

    @Test
    @DisplayName("존재하지 않는 노선을 생성한다.")
    void createLine() {
        LineRequest lineRequest = new LineRequest(
            "3호선",
            "bg-orange-600",
            stationId1,
            stationId2,
            10
        );

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @Test
    @DisplayName("이미 존재하는 노선을 생성할 수 없다.")
    void createLineWithDuplicateName() {
        // given
        LineRequest lineRequest = new LineRequest(
            "3호선",
            "bg-orange-600",
            stationId1,
            stationId2,
            10
        );

        RestAssured.given().log().all()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then()
            .log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("존재하는 노선을 조회한다. 상태코드는 200이어야 한다.")
    void findLine() {
        // given
        LineRequest lineRequest = new LineRequest(
            "1호선",
            "bg-red-600",
            stationId1,
            stationId2,
            10
        );

        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        // when
        String uri = createResponse.header("Location");

        // then
        RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get(uri)
            .then().log().all()
            .statusCode(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("존재하지 않는 노선을 조회할 수 없다. 상태코드는 not found 이어야 한다.")
    void findWrongLine() {
        // given
        String uri = URI.create("/lines/") + "0";

        // when

        // then
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get(uri)
            .then().log().all()
            .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("노선들을 조회한다.")
    void getLines() {
        // given
        LineRequest lineRequest1 = new LineRequest(
            "1호선",
            "bg-red-600",
            stationId1,
            stationId2,
            10
        );

        ExtractableResponse<Response> createResponse1 = RestAssured.given().log().all()
            .body(lineRequest1)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        LineRequest lineRequest2 = new LineRequest(
            "3호선",
            "bg-orange-600",
            stationId1,
            stationId2,
            10
        );

        ExtractableResponse<Response> createResponse2 = RestAssured.given().log().all()
            .body(lineRequest2)
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

    @Test
    @DisplayName("존재하는 노선을 제거한다. 상태코드는 200 이어야 한다.")
    void deleteStation() {
        // given
        LineRequest lineRequest = new LineRequest(
            "1호선",
            "bg-red-600",
            stationId1,
            stationId2,
            10
        );

        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
            .body(lineRequest)
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
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("존재하지 않는 노선을 제거한다. 상태코드는 204 이어야 한다.")
    void deleteNonStation() {
        RestAssured.given().log().all()
            .when()
            .delete("lines/1")
            .then().log().all()
            .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("존재하는 노선을 수정한다. 상태코드는 200이어야 한다.")
    void updateLine() {
        // given
        LineRequest lineRequest = new LineRequest(
            "1호선",
            "bg-red-600",
            stationId1,
            stationId2,
            10
        );

        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        Map<String, String> params2 = new HashMap<>();
        params2.put("name", "2호선");
        params2.put("color", "bg-green-600");

        // when
        String uri = createResponse.header("Location");

        // then
        RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(params2)
            .when()
            .put(uri)
            .then().log().all()
            .statusCode(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("존재하지 않는 노선을 수정하면 not found 예외를 반환해야 한다.")
    void updateNonLine() {
        // given
        LineRequest lineRequest = new LineRequest(
            "1호선",
            "bg-red-600",
            stationId1,
            stationId2,
            10
        );

        RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(lineRequest)
            .when()
            .put("/lines/1")
            .then().log().all()
            .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("name을 지정하지 않고 요청하면 bad request 예외를 반환해야 한다.")
    void emptyName() {
        LineRequest lineRequest = new LineRequest(
            null,
            "bg-red-600",
            stationId1,
            stationId2,
            10
        );

        RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(lineRequest)
            .when()
            .post("/lines")
            .then().log().all()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }
}
