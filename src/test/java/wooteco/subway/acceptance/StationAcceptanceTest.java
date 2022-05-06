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

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import wooteco.subway.controller.dto.station.StationResponse;

@DisplayName("지하철역 관련 기능")
class StationAcceptanceTest extends AcceptanceTest {

    private static final String PREFIX_URL = "/stations";

    private final AcceptanceHandler acceptanceHandler = new AcceptanceHandler(PREFIX_URL);

    @DisplayName("지하철 역을 생성한다.")
    @Test
    void createStation() {
        // given
        // when
        ExtractableResponse<Response> response = acceptanceHandler.save(Map.of("name", "광교역"));

        // then
        assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
            assertThat(response.header("Location")).isNotBlank();
        });
    }

    @DisplayName("기존에 존재하는 지하철 역 이름으로 지하철 역을 생성한다.")
    @ParameterizedTest
    @ValueSource(strings = {"강남역", "선릉역"})
    void createStationWithDuplicateName(String name) {
        // given
        acceptanceHandler.save(Map.of("name", name));

        // when
        ExtractableResponse<Response> response = acceptanceHandler.save(Map.of("name", name));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 역 목록을 조회한다.")
    @Test
    void getStations() {
        // given
        Long createdId1 = extractId(acceptanceHandler.save(Map.of("name", "강남역")));
        Long createdId2 = extractId(acceptanceHandler.save(Map.of("name", "선릉역")));

        // when
        ExtractableResponse<Response> response = acceptanceHandler.findAll();

        // then
        assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(extractIds(response)).containsAll(List.of(createdId1, createdId2));
        });
    }

    @DisplayName("지하철 역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        Long createdId = extractId(acceptanceHandler.save(Map.of("name", "강남역")));

        // when
        ExtractableResponse<Response> response = acceptanceHandler.remove(createdId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private Long extractId(ExtractableResponse<Response> response) {
        return response.jsonPath()
                .getObject(".", StationResponse.class)
                .getId();
    }

    private List<Long> extractIds(ExtractableResponse<Response> response) {
        return response.jsonPath()
                .getList(".", StationResponse.class)
                .stream()
                .map(StationResponse::getId)
                .collect(Collectors.toUnmodifiableList());
    }
}
