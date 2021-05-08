package wooteco.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.dto.response.LineResponse;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        Map<String, String> params = setLine("bg-red-600", "1호선");

        // when
        ExtractableResponse<Response> response = extractResponseWhenPost(params);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 노선 이름으로 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        Map<String, String> params = setLine("bg-red-600", "1호선");
        extractResponseWhenPost(params);

        // when
        ExtractableResponse<Response> response = extractResponseWhenPost(params);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("기존에 존재하는 색으로 노선을 생성한다.")
    @Test
    void createLineWithDuplicateColor() {
        // given
        Map<String, String> params1 = setLine("bg-red-600", "1호선");
        extractResponseWhenPost(params1);

        // when
        Map<String, String> params2 = setLine("bg-red-600", "2호선");
        ExtractableResponse<Response> response = extractResponseWhenPost(params2);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("올바르지 않은 이름으로 노선을 생성한다.")
    @Test
    void createLineWithWrongName() {
        // given
        Map<String, String> params = setLine("bg-red-600", "1호");
        ExtractableResponse<Response> response = extractResponseWhenPost(params);

        // when - then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선을 조회한다.")
    @Test
    void getLines() {
        // given
        Map<String, String> params1 = setLine("bg-red-600", "1호선");
        ExtractableResponse<Response> createResponse1 = extractResponseWhenPost(params1);

        Map<String, String> params2 = setLine("bg-yellow-600", "2호선");
        ExtractableResponse<Response> createResponse2 = extractResponseWhenPost(params2);

        // when
        ExtractableResponse<Response> response = extractResponseWhenGet("lines");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("특정 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        Map<String, String> params1 = setLine("bg-red-600", "1호선");
        extractResponseWhenPost(params1);

        Map<String, String> params2 = setLine("bg-yellow-600", "2호선");
        ExtractableResponse<Response> createResponse2 = extractResponseWhenPost(params2);

        // when
        String uri = createResponse2.header("Location");
        ExtractableResponse<Response> response = extractResponseWhenGet(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().jsonPath().get("id").toString()).isEqualTo(uri.split("/")[2]);
        assertThat(response.body().jsonPath().get("name").toString()).isEqualTo("2호선");
        assertThat(response.body().jsonPath().get("color").toString()).isEqualTo("bg-yellow-600");
    }

    @DisplayName("노선 업데이트한다.")
    @Test
    void updateLine() {
        // given
        Map<String, String> params1 = setLine("bg-red-600", "1호선");
        extractResponseWhenPost(params1);

        //when
        Map<String, String> params2 = setLine("bg-yellow-600", "2호선");
        ExtractableResponse<Response> response = extractResponseWhenPut(params2);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.header("Date")).isNotBlank();
    }

    @DisplayName("노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        Map<String, String> params = setLine("bg-red-600", "3호선");
        ExtractableResponse<Response> createResponse = extractResponseWhenPost(params);

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = extractResponseWhenDelete(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> extractResponseWhenGet(String path) {
        return RestAssured.given().log().all()
                .when()
                .get(path)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> extractResponseWhenPost(Map<String, String> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> extractResponseWhenPut(Map<String, String> params2) {
        return RestAssured.given().log().all()
                .body(params2)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/1")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> extractResponseWhenDelete(String uri) {
        return RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();
    }

    private Map<String, String> setLine(String color, String name) {
        Map<String, String> params = new HashMap<>();
        params.put("color", color);
        params.put("name", name);
        return params;
    }
}
