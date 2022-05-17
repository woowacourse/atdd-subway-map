package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@DisplayName("지하철 노선 관련 기능")
class LineAcceptanceTest extends AcceptanceTest {

    private final LineRequestHandler lineRequestHandler = new LineRequestHandler();
    private final StationRequestHandler stationRequestHandler = new StationRequestHandler();
    private Long upStationId;
    private Long middleStationId;
    private Long downStationId;

    @BeforeEach
    void setUpStations() {
        this.upStationId = stationRequestHandler.extractId(
                stationRequestHandler.createStation(Map.of("name", "강남역")));
        this.middleStationId = stationRequestHandler.extractId(
                stationRequestHandler.createStation(Map.of("name", "선릉역")));
        this.downStationId = stationRequestHandler.extractId(
                stationRequestHandler.createStation(Map.of("name", "잠실역")));
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void saveLine() {
        // given
        // when
        ExtractableResponse<Response> response = lineRequestHandler.createLine(
                createParameters("신분당선", "color"));

        // then
        assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
            assertThat(response.header("Location")).isNotBlank();
        });
    }

    @DisplayName("기존에 존재하는 지하철노선 이름으로 지하철노선을 생성한다.")
    @ParameterizedTest
    @ValueSource(strings = {"신분당선", "분당선"})
    void createLineWithDuplicateName(String name) {
        // given
        lineRequestHandler.createLine(createParameters(name, "color1"));

        // when
        ExtractableResponse<Response> response = lineRequestHandler.createLine(createParameters(name, "color2"));

        // then
        assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(response.body().asString()).contains("해당 이름의 지하철노선은 이미 존재합니다");
        });
    }

    @DisplayName("기존에 존재하는 지하철노선 색상으로 지하철노선을 생성한다.")
    @ParameterizedTest
    @ValueSource(strings = {"bg-red-600", "bg-blue-808"})
    void createLineWithDuplicateColor(String color) {
        // given
        lineRequestHandler.createLine(createParameters("신분당선", color));

        // when
        ExtractableResponse<Response> response = lineRequestHandler.createLine(createParameters("분당선", color));

        // then
        assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(response.body().asString()).contains("해당 색상의 지하철노선은 이미 존재합니다");
        });
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        Long createdId1 = lineRequestHandler.extractId(
                lineRequestHandler.createLine(createParameters("신분당선", "color1")));
        Long createdId2 = lineRequestHandler.extractId(
                lineRequestHandler.createLine(createParameters("분당선", "color2")));


        // when
        ExtractableResponse<Response> response = lineRequestHandler.findLines();

        // then
        assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(lineRequestHandler.extractIds(response)).containsAll(List.of(createdId1, createdId2));
        });
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void findLine() {
        // given
        Long createdId = lineRequestHandler.extractId(
                lineRequestHandler.createLine(createParameters("신분당선", "color1")));

        // when
        ExtractableResponse<Response> response = lineRequestHandler.findLine(createdId);
        Long expectedId = lineRequestHandler.extractId(response);

        // then
        assertAll(() -> {
            assertThat(expectedId).isEqualTo(createdId);
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        });
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        Long createdId = lineRequestHandler.extractId(
                lineRequestHandler.createLine(createParameters("신분당선", "color1")));

        // when
        ExtractableResponse<Response> updatedResponse = lineRequestHandler.updateLine(createdId, Map.of(
                "name", "다른분당선",
                "color", "bg-red-600"));

        // then
        assertThat(updatedResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선에서 구간을 추가한다.")
    @Test
    void appendSection() {
        // given
        Long createdId = lineRequestHandler.extractId(
                lineRequestHandler.createLine(createParameters("신분당선", "color1")));

        // when
        ExtractableResponse<Response> response = lineRequestHandler.appendSection(createdId, Map.of(
                "upStationId", String.valueOf(upStationId),
                "downStationId", String.valueOf(middleStationId),
                "distance", String.valueOf(5)));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선에서 역을 제거한다.")
    @Test
    void removeStation() {
        // given
        Long createdId = lineRequestHandler.extractId(
                lineRequestHandler.createLine(createParameters("신분당선", "color1")));

        lineRequestHandler.appendSection(createdId, Map.of(
                "upStationId", String.valueOf(upStationId),
                "downStationId", String.valueOf(middleStationId),
                "distance", String.valueOf(5)));

        // when
        ExtractableResponse<Response> response = lineRequestHandler.removeStation(createdId, middleStationId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void removeLine() {
        // given
        Long createdId = lineRequestHandler.extractId(
                lineRequestHandler.createLine(createParameters("신분당선", "color1")));

        // when
        ExtractableResponse<Response> removedResponse = lineRequestHandler.removeLine(createdId);

        // then
        assertThat(removedResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private Map<String, String> createParameters(String name, String color, Long upStationId, Long downStationId, int distance) {
        return Map.of(
                "name", name,
                "color", color,
                "upStationId", String.valueOf(upStationId),
                "downStationId", String.valueOf(downStationId),
                "distance", String.valueOf(distance));
    }

    private Map<String, String> createParameters(String name, String color) {
        return createParameters(name, color, this.upStationId, this.downStationId, 10);
    }
}
