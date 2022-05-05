package wooteco.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.LineResponse;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철노선을 생성한다.")
    @Test
    void createLine() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "bg-red-600");

        // when
        ExtractableResponse<Response> response = createLineResponse(params);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철노선 이름으로 지하철노선을 생성할 때 예외를 발생시킨다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "bg-red-600");
        RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // when
        ExtractableResponse<Response> response = createLineResponse(params);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철노선 목록을 조회한다.")
    @Test
    void getLines() {
        /// given
        Map<String, String> params1 = new HashMap<>();
        params1.put("name", "신분당선");
        params1.put("color", "bg-red-600");
        ExtractableResponse<Response> createResponse1 = createLineResponse(params1);

        Map<String, String> params2 = new HashMap<>();
        params2.put("name", "분당선");
        params2.put("color", "bg-green-600");
        ExtractableResponse<Response> createResponse2 = createLineResponse(params2);

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

    @DisplayName("지하철 단일 노선을 조회한다.")
    @Test
    void getLineById() {
        /// given
        Map<String, String> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "bg-red-600");
        ExtractableResponse<Response> createResponse = createLineResponse(params);

        // when
        long resultLineId = Long.parseLong(createResponse.header("Location").split("/")[2]);
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines/" + resultLineId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        long expectedLineId = response.jsonPath().getLong("id");
        assertThat(resultLineId).isEqualTo(expectedLineId);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        Map<String, String> params1 = new HashMap<>();
        params1.put("name", "신분당선");
        params1.put("color", "bg-red-600");
        ExtractableResponse<Response> createResponse = createLineResponse(params1);

        // when
        Map<String, String> params2 = new HashMap<>();
        params2.put("name", "다른분당선");
        params2.put("color", "bg-red-600");

        long resultLineId = Long.parseLong(createResponse.header("Location").split("/")[2]);
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params2)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + resultLineId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("존재하지 않는 지하철노선을 수정할 때 예외를 발생시킨다.")
    @Test
    void updateInvalidLine() {
        Map<String, String> params1 = new HashMap<>();
        params1.put("name", "신분당선");
        params1.put("color", "bg-red-600");
        ExtractableResponse<Response> createResponse = createLineResponse(params1);

        // when
        Map<String, String> params2 = new HashMap<>();
        params2.put("name", "다른분당선");
        params2.put("color", "bg-red-600");
        long resultLineId = Long.parseLong(createResponse.header("Location").split("/")[2]) + 1L;
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params2)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + resultLineId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철노선을 제거한다.")
    @Test
    void deleteStation() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "bg-red-600");
        ExtractableResponse<Response> createResponse = createLineResponse(params);

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

    @DisplayName("존재하지 않는 지하철을 삭제하려할 때 예외를 발생시킨다.")
    @Test
    void deleteInvalidStation() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "bg-red-600");
        ExtractableResponse<Response> createResponse = createLineResponse(params);

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response1 = RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();

        ExtractableResponse<Response> response2 = RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();

        // then
        assertThat(response2.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> createLineResponse(Map<String, String> param) {
        return RestAssured.given().log().all()
                .body(param)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }
}
