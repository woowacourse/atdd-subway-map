package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import wooteco.subway.controller.dto.line.LineResponse;

@DisplayName("지하철 노선 관련 기능")
class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void saveLine() {
        ExtractableResponse<Response> response = saveLine(Map.of(
                "name", "신분당선",
                "color", "bg-red-600"));

        assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
            assertThat(response.header("Location")).isNotBlank();
        });
    }

    @DisplayName("기존에 존재하는 지하철노선 이름으로 지하철노선을 생성한다.")
    @ParameterizedTest
    @ValueSource(strings = {"신분당선", "분당선"})
    void createLineWithDuplicateName(String name) {
        saveLine(Map.of(
                "name", name,
                "color", "bg-red-600"));

        ExtractableResponse<Response> response = saveLine(Map.of(
                "name", name,
                "color", "bg-red-601"));

        assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(response.body().asString()).isEqualTo("해당 이름의 지하철 노선이 이미 존재합니다");
        });
    }

    @DisplayName("기존에 존재하는 지하철노선 색상으로 지하철노선을 생성한다.")
    @ParameterizedTest
    @ValueSource(strings = {"bg-red-600", "bg-blue-808"})
    void createLineWithDuplicateColor(String color) {
        saveLine(Map.of(
                "name", "신분당선",
                "color", color));

        ExtractableResponse<Response> response = saveLine(Map.of(
                "name", "분당선",
                "color", color));

        assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(response.body().asString()).isEqualTo("해당 색상의 지하철 노선이 이미 존재합니다");
        });
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        Long createdId1 = extractId(saveLine(Map.of(
                "name", "신분당선",
                "color", "bg-red-600")));
        Long createdId2 = extractId(saveLine(Map.of(
                "name", "분당선",
                "color", "bg-red-601")));

        ExtractableResponse<Response> response = findLines();

        assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(extractIds(response)).containsAll(List.of(createdId1, createdId2));
        });
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void findLine() {
        Long createdId = extractId(saveLine(Map.of(
                "name", "신분당선",
                "color", "bg-red-600")));

        ExtractableResponse<Response> response = findLine(createdId);
        Long expectedId = extractId(response);

        assertAll(() -> {
            assertThat(expectedId).isEqualTo(createdId);
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        });
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        Long createdId = extractId(saveLine(Map.of(
                "name", "신분당선",
                "color", "bg-red-600")));

        ExtractableResponse<Response> updatedResponse = updateLine(Map.of(
                "name", "다른분당선",
                "color", "bg-red-600"), createdId);

        assertThat(updatedResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void removeLine() {
        Long createdId = extractId(saveLine(Map.of(
                "name", "신분당선",
                "color", "bg-red-600")));

        ExtractableResponse<Response> removedResponse = removeLine(createdId);

        assertThat(removedResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> saveLine(Map<String, String> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> findLine(Long id) {
        return RestAssured.given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines/" + id)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> findLines() {
        return RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> updateLine(Map<String, String> params, Long id) {
        return RestAssured.given().log().all()
                .body(params)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines/" + id)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> removeLine(Long id) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/lines/" + id)
                .then().log().all()
                .extract();
    }

    private Long extractId(ExtractableResponse<Response> response) {
        return response.jsonPath()
                .getObject(".", LineResponse.class)
                .getId();
    }

    private List<Long> extractIds(ExtractableResponse<Response> response) {
        return response.jsonPath()
                .getList(".", LineResponse.class)
                .stream()
                .map(LineResponse::getId)
                .collect(Collectors.toUnmodifiableList());
    }
}
