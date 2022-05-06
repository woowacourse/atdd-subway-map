package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import wooteco.subway.ui.response.LineResponse;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @Test
    @DisplayName("지하철 노선을 등록한다.")
    void createLine() {
        //given
        Map<String, String> params = new HashMap<>();
        params.put("name", "7호선");
        params.put("color", "khaki");

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @ParameterizedTest
    @CsvSource(value = {"라:0", "라:31"}, delimiter = ':')
    @DisplayName("유효하지 않는 이름으로 노선을 등록할 경우 400 응답을 던진다.")
    void createLineWithInvalidName(String name, int repeatCount) {
        //given
        Map<String, String> params = new HashMap<>();
        params.put("name", name.repeat(repeatCount));
        params.put("color", "khaki");

        //when, then
        RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("message", is("이름은 1~30 자 이내여야 합니다."));
    }

    @ParameterizedTest
    @CsvSource(value = {"라:0", "라:21"}, delimiter = ':')
    @DisplayName("유효하지 않는 색상으로 노선을 등록할 경우 400 응답을 던진다.")
    void createLineWithInvalidColor(String color, int repeatCount) {
        //given
        Map<String, String> params = new HashMap<>();
        params.put("name", "7호선");
        params.put("color", color.repeat(repeatCount));

        //when, then
        RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("message", is("색상은 1~20 자 이내여야 합니다."));
    }

    @Test
    @DisplayName("기존에 존재하는 이름으로 노선을 등록하면 400 상태 응답을 던진다.")
    void createLineWithDuplicateName() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "4호선");
        params.put("color", "sky-blue");
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
    @DisplayName("모든 노선을 조회한다.")
    void getLines() {
        // given
        Map<String, String> params1 = new HashMap<>();
        params1.put("name", "4호선");
        params1.put("color", "sky-blue");
        ExtractableResponse<Response> createResponse1 = RestAssured.given().log().all()
            .body(params1)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        Map<String, String> params2 = new HashMap<>();
        params2.put("name", "7호선");
        params2.put("color", "khaki");
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
        List<LineResponse> expectedLineResponses = Stream.of(createResponse1, createResponse2)
            .map(it -> it.jsonPath().getObject(".", LineResponse.class))
            .collect(Collectors.toList());
        List<LineResponse> lineResponses = response.jsonPath().getList(".", LineResponse.class);
        assertThat(expectedLineResponses).containsAll(lineResponses);
    }

    @Test
    @DisplayName("단일 노선을 조회한다.")
    void getLine() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "4호선");
        params.put("color", "sky-blue");
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
            .get(uri)
            .then().log().all()
            .extract();

        // then
        LineResponse lineResponse = response.body().jsonPath().getObject(".", LineResponse.class);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(lineResponse.getName()).isEqualTo("4호선");
        assertThat(lineResponse.getColor()).isEqualTo("sky-blue");
    }

    @Test
    @DisplayName("존재하지 않는 노선을 조회할 경우 404 응답을 던진다.")
    void getLineNotExists() {
        RestAssured.given().log().all()
            .when()
            .get("/lines/1")
            .then().log().all()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .body("message", is("존재하지 않는 노선입니다."));
    }

    @Test
    @DisplayName("노선 정보를 수정한다.")
    void update() {
        // given
        Map<String, String> params1 = new HashMap<>();
        params1.put("name", "4호선");
        params1.put("color", "sky-blue");
        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
            .body(params1)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        //when
        String uri = createResponse.header("Location");
        Map<String, String> params2 = new HashMap<>();
        params2.put("name", "2호선");
        params2.put("color", "green");
        RestAssured.given().log().all()
            .body(params2)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put(uri)
            .then()
            .log().all()
            .statusCode(HttpStatus.OK.value());

        //then
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .get(uri)
            .then()
            .log().all()
            .extract();

        LineResponse lineResponse = response.body().jsonPath().getObject(".", LineResponse.class);
        assertThat(lineResponse.getName()).isEqualTo("2호선");
        assertThat(lineResponse.getColor()).isEqualTo("green");
    }

    @Test
    @DisplayName("이미 존재하는 이름으로 수정할 경우 400 응답을 던진다.")
    void updateWithDuplicatedName() {
        // given
        Map<String, String> params1 = new HashMap<>();
        params1.put("name", "4호선");
        params1.put("color", "sky-blue");
        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
            .body(params1)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        //when, then
        String uri = createResponse.header("Location");
        Map<String, String> params2 = new HashMap<>();
        params2.put("name", "4호선");
        params2.put("color", "green");
        RestAssured.given().log().all()
            .body(params2)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put("/lines/" + 3)
            .then()
            .log().all()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("노선을 제거한다.")
    void deleteLine() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "7호선");
        params.put("color", "khaki");
        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        // when, then
        String uri = createResponse.header("Location");
        RestAssured.given().log().all()
            .when()
            .delete(uri)
            .then().log().all()
            .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("존재하지 않는 id 로 노선을 제거할 경우 404 응답을 던진다.")
    void deleteLineWithIdNotExists() {
        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .delete("/lines/1")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }
}
