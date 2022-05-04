package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import wooteco.subway.acceptance.fixture.SimpleRestAssured;
import wooteco.subway.dto.LineResponse;

public class LineAcceptanceTest extends AcceptanceTest {

    @Test
    @DisplayName("노선을 생성한다.")
    public void createLine() {
        // given
        Map<String, String> params =
            Map.of("name", "신분당선", "color", "bg-red-600");
        // when
        final ExtractableResponse<Response> response = SimpleRestAssured.post("/lines", params);
        // then
        Assertions.assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
            () -> assertThat(response.header("Location")).isNotBlank()
        );
    }

    @Test
    @DisplayName("전체 노선 목록을 조회한다.")
    void getLines() {
        /// given
        Map<String, String> params1 = Map.of("name", "신분당선", "color", "bg-red-600");
        Map<String, String> params2 = Map.of("name", "경의중앙선", "color", "bg-red-800");

        ExtractableResponse<Response> createResponse1 = SimpleRestAssured.post("/lines", params1);
        ExtractableResponse<Response> createResponse2 = SimpleRestAssured.post("/lines", params2);

        // when
        ExtractableResponse<Response> response = SimpleRestAssured.get("/lines");

        // then
        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
            .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
            .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
            .map(LineResponse::getId)
            .collect(Collectors.toList());
        Assertions.assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(resultLineIds).containsAll(expectedLineIds)
        );
    }

    @Test
    @DisplayName("ID값으로 노선을 조회한다.")
    public void getLine() {
        // given
        Map<String, String> params1 = Map.of("name", "신분당선", "color", "bg-red-600");
        ExtractableResponse<Response> createdResponse = SimpleRestAssured.post("/lines", params1);
        // when
        final String uri = createdResponse.header("Location");
        final ExtractableResponse<Response> foundResponse = SimpleRestAssured.get(uri);

        final LineResponse createdLineResponse = createdResponse.jsonPath().getObject(".", LineResponse.class);
        final LineResponse foundLineResponse = foundResponse.jsonPath().getObject(".", LineResponse.class);
        // then
        Assertions.assertAll(
            () -> assertThat(foundResponse.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(foundLineResponse.getId()).isEqualTo(createdLineResponse.getId())
        );

    }

    @Test
    @DisplayName("ID값으로 노선을 수정한다.")
    public void modifyLine() {
        // given
        Map<String, String> params1 = Map.of("name", "신분당선", "color", "bg-red-600");
        ExtractableResponse<Response> createdResponse = SimpleRestAssured.post("/lines", params1);

        // when
        final Map<String, String> modificationParam =
            Map.of("name", "구분당선", "color", "bg-red-800");
        final String uri = createdResponse.header("Location");
        final ExtractableResponse<Response> modifiedResponse = SimpleRestAssured.put(uri, modificationParam);

        // then
        assertThat(modifiedResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("ID값으로 노선을 제거한다.")
    public void deleteLine() {
        // given
        Map<String, String> params1 = Map.of("name", "신분당선", "color", "bg-red-600");
        ExtractableResponse<Response> createdResponse = SimpleRestAssured.post("/lines", params1);
        // when
        final String uri = createdResponse.header("Location");
        final ExtractableResponse<Response> deleteResponse = SimpleRestAssured.delete(uri);

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
