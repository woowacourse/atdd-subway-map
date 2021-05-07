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

class LineAcceptanceTest extends AcceptanceTest {

    @Test
    @DisplayName("노선을 생성하는 요청을 보낸다.")
    void createLineTest() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-red-600");
        params.put("name", "신분당선");

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

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-skyblue-600");
        params.put("name", "4호선");
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

    @Test
    @DisplayName("노선 목록을 조회하는 요청을 보낸다.")
    void showLines() {
        // given
        Map<String, String> params1 = new HashMap<>();
        params1.put("color", "bg-red-600");
        params1.put("name", "신분당선");
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

    @Test
    @DisplayName("노선을 조회하는 요청을 보낸다.")
    void getLineTest() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-green-600");
        params.put("name", "7호선");
        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get(createResponse.header("Location"))
                .then().log().all()
                .extract();

        String name = response.jsonPath().get("name");
        String color = response.jsonPath().get("color");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(name).isEqualTo("7호선");
        assertThat(color).isEqualTo("bg-green-600");
    }

    @Test
    @DisplayName("노선을 수정하는 요청을 보낸다.")
    void updateLine() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-green-600");
        params.put("name", "7호선");
        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // when
        Map<String, String> params2 = new HashMap<>();
        params2.put("color", "bg-blue-600");
        params2.put("name", "구분당선");
        RestAssured.given().log().all()
                .body(params2)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(createResponse.header("Location"))
                .then().log().all()
                .extract();

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get(createResponse.header("Location"))
                .then().log().all()
                .extract();

        String name = response.jsonPath().get("name");
        String color = response.jsonPath().get("color");

        // then
        assertThat(name).isEqualTo("구분당선");
        assertThat(color).isEqualTo("bg-blue-600");
    }

    @Test
    @DisplayName("노선을 삭제하는 요청을 보낸다.")
    void deleteLine() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "bg-red-600");
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
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}