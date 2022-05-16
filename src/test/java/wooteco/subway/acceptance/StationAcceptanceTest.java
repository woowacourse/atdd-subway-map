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
import wooteco.subway.dto.response.StationResponse;

@DisplayName("지하철 역 관리 API")
public class StationAcceptanceTest extends AcceptanceTest {

    /*
        Scenario: 지하철 역 등록
            When: 지하철 역 등록을 요청한다.
            Then: 지하철 역이 생성된다.
            And: 201 상태, 지하철 역 정보, 저장 경로를 응답 받는다.
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // given
        final Map<String, String> params = new HashMap<>();
        params.put("name", "강남역");

        // when
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    /*
        Scenario: 중복된 지하철 역 등록
            When: 같은 이름의 지하철 역 생성을 요청한다.
            Then: 역이 생성되지 않는다.
            And: 400 상태, 에러 메시지를 응답 받는다.
     */
    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        final Map<String, String> params = new HashMap<>();
        params.put("name", "강남역");
        RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();

        // when
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then()
                .log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /*
        Scenario: 지하철 역 조회
            When: 지하철 역 조회를 요청한다.
            Then: 200 상태, 지하철 역 정보를 응답 받는다.
     */
    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        // given
        final Map<String, String> params1 = new HashMap<>();
        params1.put("name", "강남역");
        final ExtractableResponse<Response> createResponse1 = RestAssured.given().log().all()
                .body(params1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();

        final Map<String, String> params2 = new HashMap<>();
        params2.put("name", "역삼역");
        final ExtractableResponse<Response> createResponse2 = RestAssured.given().log().all()
                .body(params2)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();

        // when
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/stations")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        final List<Long> expectedStationIds = Arrays.asList(createResponse1, createResponse2).stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        final List<Long> resultStationIds = response.jsonPath().getList(".", StationResponse.class).stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());
        assertThat(resultStationIds).containsAll(expectedStationIds);
    }

    /*
        Scenario: 지하철 역 제거
            When: 지하철 역 제거를 요청한다.
            Then: 역이 제거된다.
            And: 204 상태를 응답 받는다.
     */
    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        final Map<String, String> params = new HashMap<>();
        params.put("name", "강남역");
        final ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
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
}
