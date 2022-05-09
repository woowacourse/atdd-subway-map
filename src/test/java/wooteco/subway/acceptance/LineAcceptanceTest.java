package wooteco.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
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

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("노선을 생성한다.")
    @Test
    void createLine() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "bg-red-600");

        // when
        ExtractableResponse<Response> response = createLine(params);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.header("Location")).isNotBlank()
        );
    }

    @DisplayName("노션을 조회한다.")
    @Test
    void getLines() {
        // given
        Map<String, String> params1 = createParam("신분당선", "bg-red-600");
        ExtractableResponse<Response> createResponse1 = createLine(params1);

        Map<String, String> params2 = createParam("분당선", "bg-green-600");
        ExtractableResponse<Response> createResponse2 = createLine(params2);

        // when
        ExtractableResponse<Response> response = getAllLines();
        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> actualLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(actualLineIds).containsAll(expectedLineIds)
        );
    }

    @DisplayName("개별 노선을 ID 값으로 조회한다.")
    @Test
    void getLineById() {
        // given
        Map<String, String> params1 = createParam("신분당선", "bg-red-600");

        ExtractableResponse<Response> createResponse = createLine(params1);
        Long createId = createResponse.jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = getLineById(createId);

        // then
        Long id = response.jsonPath().getLong("id");
        String name = response.jsonPath().getString("name");
        String color = response.jsonPath().getString("color");

        assertAll(
                () -> assertThat(id).isEqualTo(createId),
                () -> assertThat(name).isEqualTo("신분당선"),
                () -> assertThat(color).isEqualTo("bg-red-600")
        );
    }

    @DisplayName("노선 정보를 수정한다.")
    @Test
    void updateLineById() {
        // given
        Map<String, String> params = createParam("신분당선", "bg-red-600");

        ExtractableResponse<Response> createResponse = createLine(params);
        Long createdId = getIdFromResponse(createResponse);

        // when
        Map<String, String> newParams = createParam("다른분당선", "bg-red-600");
        updateLine(createdId, newParams);
        ExtractableResponse<Response> response = getLineById(createdId);

        // then
        String responseName = response.jsonPath().getString("name");
        String responseColor = response.jsonPath().getString("color");

        assertAll(
                () -> assertThat(responseName).isEqualTo(newParams.get("name")),
                () -> assertThat(responseColor).isEqualTo(newParams.get("color"))
        );
    }

    @DisplayName("노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        Map<String, String> params = createParam("신분당선", "bg-red-600");
        ExtractableResponse<Response> createResponse = createLine(params);
        Long createdId = getIdFromResponse(createResponse);

        // when
        ExtractableResponse<Response> response = deleteLine(createdId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철역을 생성할 경우 BAD REQUEST가 반환된다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        Map<String, String> params = createParam("신분당선", "bg-red-600");
        createLine(params);

        // when
        ExtractableResponse<Response> response = createLine(params);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("존재하지 않은 지하철 노선을 조회하려 할 경우 NOT FOUND가 반환된다.")
    @Test
    void getLine_returnsBadRequestWithNotExistingId() {
        // given & when
        ExtractableResponse<Response> response = getLineById(3L);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("존재하지 않은 지하철 노선을 수정하려 할 경우 NOT FOUND가 반환된다.")
    @Test
    void updateLine_returnsBadRequestWithNotExistingId() {
        // given & when
        ExtractableResponse<Response> response = updateLine(3L, createParam("신분당선", "bg-red-600"));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("존재하지 않은 지하철 노선을 제거하려 할 경우 NOT FOUND가 반환된다.")
    @Test
    void deleteLine_returnsBadRequestWithNotExistingId() {
        // given & when
        ExtractableResponse<Response> response = deleteLine(3L);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    private ExtractableResponse<Response> createLine(Map<String, String> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> getLineById(Long createdId) {
        return RestAssured.given().log().all()
                .when()
                .get("/lines/" + createdId)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> getAllLines() {
        return RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> updateLine(Long createdId, Map<String, String> newParams) {
        return RestAssured.given().log().all()
                .body(newParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + createdId)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> deleteLine(Long createdId) {
        return RestAssured.given().log().all()
                .when()
                .delete("/lines/" + createdId)
                .then().log().all()
                .extract();
    }

    private Long getIdFromResponse(ExtractableResponse<Response> response) {
        return Long.parseLong(response.header("Location").split("lines/")[1]);
    }

    private Map<String, String> createParam(String name, String color) {
        Map<String, String> param = new HashMap<>();
        param.put("name", name);
        param.put("color", color);
        return param;
    }
}
