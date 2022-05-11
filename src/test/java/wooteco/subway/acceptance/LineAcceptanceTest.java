package wooteco.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("노선 관련 기능")
class LineAcceptanceTest extends AcceptanceTest {

    private Long upStationId;
    private Long downStationId;
    private LineRequest lineRequest;

    @BeforeEach
    void setup() {
        upStationId = createStation(new StationRequest("아차산역"));
        downStationId = createStation(new StationRequest("군자역"));
        lineRequest = new LineRequest("5호선", "bg-purple-600", upStationId, downStationId, 10);
    }

    private Long createStation(final StationRequest stationRequest) {
        final ExtractableResponse<Response> response = AcceptanceTestFixture.post("/stations", stationRequest);

        return Long.parseLong(response.header("Location").split("/")[2]);
    }

    @DisplayName("라인을 등록한다.")
    @Test
    void createLine() {
        // when
        final ExtractableResponse<Response> response = AcceptanceTestFixture.post("/lines", lineRequest);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.header("Location")).isNotBlank(),
                () -> assertThat(response.body().jsonPath().getLong("id")).isNotNull(),
                () -> assertThat(response.body().jsonPath().getString("name")).isEqualTo(lineRequest.getName()),
                () -> assertThat(response.body().jsonPath().getString("color")).isEqualTo(lineRequest.getColor())
        );
    }

    @DisplayName("기존에 존재하는 노선 이름으로 노선을 생성하면 예외를 발생한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "경의중앙선");
        params.put("color", "rgb-mint-600");
        AcceptanceTestFixture.post("/lines", params);

        // when
        final ExtractableResponse<Response> response = AcceptanceTestFixture.post("/lines", params);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response.body().jsonPath().getString("message")).isEqualTo("이미 같은 이름의 노선이 존재합니다.")
        );
    }

    @DisplayName("전체 노선들을 조회한다.")
    @Test
    void findAllLines() {
        final ExtractableResponse<Response> createResponse1 = AcceptanceTestFixture.post("/lines", lineRequest);

        LineRequest lineRequest2 = new LineRequest("신분당선", "rgb-yellow-600", upStationId, downStationId, 10);
        final ExtractableResponse<Response> createResponse2 = AcceptanceTestFixture.post("/lines", lineRequest2);

        // when
        final ExtractableResponse<Response> response = AcceptanceTestFixture.get("/lines");

        // then
        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(resultLineIds).containsAll(expectedLineIds)
        );
    }

    @DisplayName("특정 노선을 조회한다.")
    @Test
    void findLine() {
        final ExtractableResponse<Response> createResponse = AcceptanceTestFixture.post("/lines", lineRequest);
        long id = Long.parseLong(createResponse.header("Location").split("/")[2]);

        // when
        final ExtractableResponse<Response> response = AcceptanceTestFixture.get("/lines/" + id);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getLong("id")).isEqualTo(id),
                () -> assertThat(response.jsonPath().getString("name")).isEqualTo("5호선"),
                () -> assertThat(response.jsonPath().getString("color")).isEqualTo("bg-purple-600")
        );
    }

    @DisplayName("특정 노선을 수정한다.")
    @Test
    void updateLine() {
        /// given
        final ExtractableResponse<Response> createResponse = AcceptanceTestFixture.post("/lines", lineRequest);
        long id = Long.parseLong(createResponse.header("Location").split("/")[2]);

        // when
        Map<String, String> params2 = new HashMap<>();
        params2.put("name", "6호선");
        params2.put("color", "rgb-brown-600");

        final ExtractableResponse<Response> response = AcceptanceTestFixture.put("/lines/" + id, params2);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        final ExtractableResponse<Response> createResponse = AcceptanceTestFixture.post("/lines", lineRequest);
        String uri = createResponse.header("Location");

        // when
        final ExtractableResponse<Response> response = AcceptanceTestFixture.delete(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
