package wooteco.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.controller.dto.line.LineResponse;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("노선 관련 기능")
public class LineAcceptance extends AcceptanceTest {

    @DisplayName("노선을 생성한다.")
    @Test
    void createLine() {
        // given
        Map<String, String> lineParams = makeParamsLine("1호선", "blue", 1L, 2L, 10);

        // when
        ExtractableResponse<Response> response = createPostLineResponse(lineParams);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.header("Location")).isNotBlank(),
                () -> assertThat(response.body().jsonPath().get("name").toString()).isEqualTo("1호선"),
                () -> assertThat(response.body().jsonPath().get("color").toString()).isEqualTo("blue")
        );
    }

    @DisplayName("기존에 존재하는 노선 이름으로 생성시 예외가 발생한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        Map<String, String> request = makeParamsLine("1호선", "blue", 1L, 2L, 10);

        // when
        createPostLineResponse(request);
        ExtractableResponse<Response> response = createPostLineResponse(request);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("특정 노선을 조회한다.")
    @Test
    void showLine() {
        /// given
        Map<String, String> request = makeParamsLine("1호선", "blue", 1L, 2L, 10);
        ExtractableResponse<Response> createResponse = createPostLineResponse(request);
        String id = createResponse.header("Location").split("/")[2];

        // when
        ExtractableResponse<Response> response = createGetLineResponseById(id);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().jsonPath().get("id").toString()).isEqualTo(id);
        assertThat(response.body().jsonPath().get("name").toString()).isEqualTo("1호선");
        assertThat(response.body().jsonPath().get("color").toString()).isEqualTo("blue");
    }

    @DisplayName("노선을 조회한다.")
    @Test
    void getLines() {
        /// given
        Map<String, String> params1 = makeParamsLine("1호선", "blue", 1L, 2L, 10);
        Map<String, String> params2 = makeParamsLine("2호선", "green", 2L, 3L, 10);
        ExtractableResponse<Response> createResponse1 = createPostLineResponse(params1);
        ExtractableResponse<Response> createResponse2 = createPostLineResponse(params2);

        // when
        ExtractableResponse<Response> response = createGetLinesResponse();
        List<Long> expectedLineIds = postIds(createResponse1, createResponse2);
        List<Long> resultLineIds = responseIds(response);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(resultLineIds).containsAll(expectedLineIds)
        );
    }

    @DisplayName("노선을 업데이트한다.")
    @Test
    void updateLine() {
        /// given
        Map<String, String> originParams = makeParamsLine("1호선", "blue", 1L, 2L, 10);
        Map<String, String> newParams = makeParamsLine("2호선", "green", 1L, 2L, 10);

        // when
        ExtractableResponse<Response> createResponse = createPostLineResponse(originParams);
        String id = createResponse.header("Location").split("/")[2];
        ExtractableResponse<Response> response = createPutLineResponse(id, newParams);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("노선 업데이트에 실패한다.")
    @Test
    void failUpdateLine() {
        /// given
        Map<String, String> originParams1 = makeParamsLine("1호선", "blue", 1L, 2L, 10);
        Map<String, String> originParams2 = makeParamsLine("2호선", "green", 1L, 2L, 10);
        Map<String, String> newParams = makeParamsLine("1호선", "blue", 1L, 2L, 10);

        // when
        ExtractableResponse<Response> createResponse1 = createPostLineResponse(originParams1);
        ExtractableResponse<Response> createResponse2 = createPostLineResponse(originParams2);
        String id = createResponse2.header("Location").split("/")[2];
        ExtractableResponse<Response> response = createPutLineResponse(id, newParams);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선삭제")
    @Test
    void deleteLine() {
        /// given
        Map<String, String> originParams = makeParamsLine("1호선", "blue", 1L, 2L, 10);

        // when
        ExtractableResponse<Response> createResponse = createPostLineResponse(originParams);
        String id = createResponse.header("Location").split("/")[2];
        ExtractableResponse<Response> response = createDeleteLineResponseById(id);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("없는 노선 삭제시 예외 발생")
    @Test
    void invalidLine() {
        // when
        ExtractableResponse<Response> response = createDeleteLineResponseById(-1L);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> createPostLineResponse(Map<String, String> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> createGetLineResponseById(String id) {
        return RestAssured.given().log().all()
                .when()
                .get("/lines/" + id)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> createGetLinesResponse() {
        return RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();
    }

    private List<Long> postIds(ExtractableResponse<Response>... createResponse) {
        return Arrays.asList(createResponse).stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
    }

    private List<Long> responseIds(ExtractableResponse<Response> response) {
        return response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
    }

    private ExtractableResponse<Response> createPutLineResponse(String id, Map<String, String> newParams) {
        return RestAssured.given().log().all()
                .body(newParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + id)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> createDeleteLineResponseById(Long id) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/lines/" + id)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> createDeleteLineResponseById(String id) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/lines/" + id)
                .then().log().all()
                .extract();
    }

    private Map<String, String> makeParamsLine(String name, String color, Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", String.valueOf(upStationId));
        params.put("downStationId", String.valueOf(downStationId));
        params.put("distance", String.valueOf(distance));
        return params;
    }
}

