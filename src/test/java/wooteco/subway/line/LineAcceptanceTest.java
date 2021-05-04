package wooteco.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.AcceptanceTest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-red-600");
        params.put("name", "1호선");

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

    @DisplayName("기존에 존재하는 노선 이름으로 노선을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-red-600");
        params.put("name", "1호선");
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

    @DisplayName("노선을 조회한다.")
    @Test
    void getStations() {
        /// given
        Map<String, String> params1 = new HashMap<>();
        params1.put("color", "bg-red-600");
        params1.put("name", "1호선");
        ExtractableResponse<Response> createResponse1 = RestAssured.given().log().all()
                .body(params1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        Map<String, String> params2 = new HashMap<>();
        params2.put("color", "bg-yellow-600");
        params2.put("name", "2호선");
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
                .map(it -> it.getId())
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("특정 노선을 조회한다.")
    @Test
    void getStation() {
        /// given
        Map<String, String> params1 = new HashMap<>();
        params1.put("color", "bg-red-600");
        params1.put("name", "1호선");
        ExtractableResponse<Response> createResponse1 = RestAssured.given().log().all()
                .body(params1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        Map<String, String> params2 = new HashMap<>();
        params2.put("color", "bg-yellow-600");
        params2.put("name", "2호선");
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
                .get("/lines/1")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().jsonPath().get("id").toString()).isEqualTo("1");
        assertThat(response.body().jsonPath().get("name").toString()).isEqualTo("1호선");
        assertThat(response.body().jsonPath().get("color").toString()).isEqualTo("bg-red-600");
    }
}
