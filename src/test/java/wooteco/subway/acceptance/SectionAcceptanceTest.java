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
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationRequest;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 구간 관련 기능")
class SectionAcceptanceTest extends AcceptanceTest {

    private Long downStationId;
    private Long lineId;
    private SectionRequest sectionRequest;

    @BeforeEach
    void setup() {
        Long upStationId = createStation(new StationRequest("아차산역"));
        downStationId = createStation(new StationRequest("군자역"));
        Long newDownStationId = createStation(new StationRequest("마장역"));
        lineId = createLine(new LineRequest("5호선", "bg-purple-600", upStationId, downStationId, 10));

        sectionRequest = new SectionRequest(downStationId, newDownStationId, 5);
    }

    @DisplayName("특정 노선의 구간을 추가한다.")
    @Test
    void createSection() {
        final ExtractableResponse<Response> response = AcceptanceTestFixture.post("/lines/" + lineId + "/sections", sectionRequest);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("특정 노선의 구간을 추가할 때 입력값이 올바르지 않으면 예외를 발생한다.")
    @ParameterizedTest
    @MethodSource("thrownArguments")
    void thrown_invalidArguments(SectionRequest newSectionRequest, String errorMessage) {
        final ExtractableResponse<Response> response = AcceptanceTestFixture.post("/lines/" + lineId + "/sections", newSectionRequest);

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response.jsonPath().getString("message")).isEqualTo(errorMessage)
        );
    }

    private static Stream<Arguments> thrownArguments() {
        return Stream.of(
                Arguments.of(new SectionRequest(null, 2L, 10), "상행역은 비어있을 수 없습니다."),
                Arguments.of(new SectionRequest(1L, null, 10), "하행역은 비어있을 수 없습니다."),
                Arguments.of(new SectionRequest(1L, 2L, 0), "거리는 양수이어야 합니다.")
        );
    }

    @DisplayName("특정 노선의 구간을 삭제한다.")
    @Test
    void deleteSection() {
        // given
        AcceptanceTestFixture.post("/lines/" + lineId + "/sections", sectionRequest);

        // delete
        final ExtractableResponse<Response> response = AcceptanceTestFixture.delete("/lines/" + lineId + "/sections?stationId=" + downStationId);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private Long createStation(final StationRequest stationRequest) {
        final ExtractableResponse<Response> response = AcceptanceTestFixture.post("/stations", stationRequest);

        return Long.parseLong(response.header("Location").split("/")[2]);
    }

    private Long createLine(final LineRequest lineRequest) {
        final ExtractableResponse<Response> response = AcceptanceTestFixture.post("/lines", lineRequest);

        return response.jsonPath().getLong("id");
    }
}
