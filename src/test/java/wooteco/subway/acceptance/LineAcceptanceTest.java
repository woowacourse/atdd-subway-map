package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import wooteco.subway.dto.response.LineResponse;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        createStationForTest("강남역");
        createStationForTest("선릉역");

        // when
        ExtractableResponse<Response> response = RequestFrame.post(
            BodyCreator.makeLineBodyForPost("2호선", "green", "1", "2", "10"),
            "/lines"
        );

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.(400에러)")
    @Test
    void createLineWithDuplicateName() {
        // given
        createStationForTest("강남역");
        createStationForTest("선릉역");

        RequestFrame.post(
            BodyCreator.makeLineBodyForPost("2호선", "green", "1", "2", "10"),
            "/lines"
        );

        ExtractableResponse<Response> response = RequestFrame.post(
            BodyCreator.makeLineBodyForPost("2호선", "green", "1", "2", "10"),
            "/lines"
        );

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 전체를 조회한다.")
    @Test
    void getLines() {
        /// given
        createStationForTest("강남역");
        createStationForTest("선릉역");

        ExtractableResponse<Response> createResponse1 = RequestFrame.post(
            BodyCreator.makeLineBodyForPost("2호선", "green", "1", "2", "10"),
            "/lines"
        );

        ExtractableResponse<Response> createResponse2 = RequestFrame.post(
            BodyCreator.makeLineBodyForPost("3호선", "blue", "1", "2", "10"),
            "/lines"
        );

        // when
        ExtractableResponse<Response> response = RequestFrame.get("/lines");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
            .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
            .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
            .map(LineResponse::getId)
            .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        /// given
        createStationForTest("강남역");
        createStationForTest("선릉역");

        ExtractableResponse<Response> createResponse1 = RequestFrame.post(
            BodyCreator.makeLineBodyForPost("2호선", "green", "1", "2", "10"),
            "/lines"
        );

        // when
        String uri = createResponse1.header("Location");
        ExtractableResponse<Response> response = RequestFrame.get(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        Long expectedLineId = Long.parseLong(createResponse1.header("Location").split("/")[2]);
        assertThat(expectedLineId).isEqualTo(response.jsonPath().getLong("id"));
        assertThat(createResponse1.jsonPath().getLong("id")).isEqualTo(response.jsonPath().getLong("id"));
        assertThat(createResponse1.jsonPath().getString("name")).isEqualTo(response.jsonPath().getString("name"));
        assertThat(createResponse1.jsonPath().getString("color")).isEqualTo(response.jsonPath().getString("color"));
    }

    @DisplayName("존재하지 않는 지하철 노선을 조회한다.(400에러)")
    @Test
    void getLineNotExists() {
        // given

        // when
        ExtractableResponse<Response> response = RequestFrame.get("/lines/1");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        /// given
        createStationForTest("강남역");
        createStationForTest("선릉역");

        ExtractableResponse<Response> createResponse1 = RequestFrame.post(
            BodyCreator.makeLineBodyForPost("2호선", "green", "1", "2", "10"),
            "/lines"
        );
        String uri = createResponse1.header("Location");

        // when
        ExtractableResponse<Response> response = RequestFrame.put(makeBodyForPut("다른분당선", "green"), uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("존재하지 않는 지하철 노선을 수정한다.(400에러)")
    @Test
    void updateLineNotExists() {
        // given

        // when
        ExtractableResponse<Response> response = RequestFrame.put(makeBodyForPut("다른분당선", "green"), "/lines/1");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 수정한다.(400에러)")
    @Test
    void updateLineWithDuplicateName() {
        /// given
        createStationForTest("강남역");
        createStationForTest("선릉역");

        ExtractableResponse<Response> createResponse1 = RequestFrame.post(
            BodyCreator.makeLineBodyForPost("2호선", "green", "1", "2", "10"),
            "/lines"
        );
        String uri = createResponse1.header("Location");

        ExtractableResponse<Response> createResponse2 = RequestFrame.post(
            BodyCreator.makeLineBodyForPost("다른분당선", "blue", "1", "2", "10"),
            "/lines"
        );

        // when
        ExtractableResponse<Response> response = RequestFrame.put(makeBodyForPut("다른분당선", "green"), uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        createStationForTest("강남역");
        createStationForTest("선릉역");

        ExtractableResponse<Response> createResponse = RequestFrame.post(
            BodyCreator.makeLineBodyForPost("다른분당선", "blue", "1", "2", "10"),
            "/lines"
        );

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = RequestFrame.delete(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("존재하지 않는 지하철 노선을 제거한다.(400에러)")
    @Test
    void deleteLineNotExists() {
        // given

        // when
        ExtractableResponse<Response> response = RequestFrame.delete("/lines/1");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private Map<String, String> makeBodyForPut(String name, String color) {
        Map<String, String> body = new HashMap<>();
        body.put("name", name);
        body.put("color", color);
        return body;
    }

    private void createStationForTest(String stationName) {
        Map<String, String> params = new HashMap<>();
        params.put("name", stationName);

        ExtractableResponse<Response> response = RequestFrame.post(params, "/stations");
    }
}
