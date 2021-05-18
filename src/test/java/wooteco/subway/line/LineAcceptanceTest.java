package wooteco.subway.line;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.station.dto.StationRequest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
class LineAcceptanceTest extends AcceptanceTest {

    private String firstStationId;
    private String lastStationId;

    @BeforeEach
    void initStations() {
        StationRequest createUpStationRequest = new StationRequest("강남역");

        firstStationId = RestAssured.given().log().all()
                .body(createUpStationRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract().body().jsonPath().get("id").toString();

        StationRequest createDownStationRequest = new StationRequest("삼성역");

        lastStationId = RestAssured.given().log().all()
                .body(createDownStationRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract()
                .body().jsonPath().get("id").toString();
    }


    @DisplayName("지하철 노선 등록 성공")
    @Test
    void createLine() {

        // given
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-red-600");
        params.put("name", "신분당선");
        params.put("lastStationId", lastStationId);
        params.put("firstStationId", firstStationId);

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

    @DisplayName("지하철 노선 등록 실패 - 중복된 노선 존재")
    @Test
    void createLineWithDuplicateName() {

        // given
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-red-600");
        params.put("name", "신분당선");
        params.put("lastStationId", lastStationId);
        params.put("firstStationId", firstStationId);

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
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선 목록 조회 성공")
    @Test
    void showLines() {
        // given
        Map<String, String> params1 = new HashMap<>();
        params1.put("color", "bg-red-600");
        params1.put("name", "신분당선");
        params1.put("lastStationId", lastStationId);
        params1.put("firstStationId", firstStationId);
        params1.put("distance", "2");

        ExtractableResponse<Response> createResponse1 = RestAssured.given().log().all()
                .body(params1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        Map<String, String> params2 = new HashMap<>();
        params2.put("color", "bg-green-600");
        params2.put("name", "2호선");
        params2.put("lastStationId", lastStationId);
        params2.put("firstStationId", firstStationId);
        params2.put("distance", "2");

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

        List<Long> expectedLineIds = Arrays.asList(
                Long.parseLong(createResponse1.header("Location").split("/")[2]),
                Long.parseLong(createResponse2.header("Location").split("/")[2])
        );

        List<Long> resultLindIds = resultStationsIds(response);
        assertThat(resultLindIds).containsAll(expectedLineIds);
    }

    private List<Long> resultStationsIds(ExtractableResponse<Response> response) {
        final JsonPath jsonPath = response.jsonPath();
        final List<LineResponse> lineResponses = jsonPath.getList(".", LineResponse.class);

        return lineResponses.stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
    }

    @DisplayName("지하철 노선 이름 변경 성공")
    @Test
    void updateLine() {

        // given
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-red-600");
        params.put("name", "신분당선");
        params.put("lastStationId", lastStationId);
        params.put("firstStationId", firstStationId);
        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // when
        String uri = createResponse.header("Location");
        Map<String, String> updateParams = new HashMap<>();
        updateParams.put("color", "bg-red-600");
        updateParams.put("name", "분당선");
        updateParams.put("lastStationId", lastStationId);
        updateParams.put("firstStationId", firstStationId);
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(updateParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(uri)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선 제거 성공")
    @Test
    void deleteLine() {

        // given
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-red-600");
        params.put("name", "신분당선");
        params.put("lastStationId", lastStationId);
        params.put("firstStationId", firstStationId);

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
                .then()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
