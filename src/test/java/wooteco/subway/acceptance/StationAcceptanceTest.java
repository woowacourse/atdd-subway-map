package wooteco.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dto.station.StationResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
@Transactional
public class StationAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // given
        Map<String, String> param = new HashMap<>();
        param.put("name", "강남역");

        // when
        ExtractableResponse<Response> response = post("/stations", param);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        Map<String, String> param = new HashMap<>();
        param.put("name", "강남역");
        post("/stations", param);

        // when
        ExtractableResponse<Response> response = post("/stations", param);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        // given
        Long station1 = postStationAndGetId("구의역");
        Long station2 = postStationAndGetId("선릉역");

        // when
        ExtractableResponse<Response> response = get("/stations");

        // then
        List<Long> expectedLineIds = List.of(station1, station2);
        List<Long> resultLineIds = response.jsonPath().getList(".", StationResponse.class).stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        ExtractableResponse<Response> createResponse = post("/stations", Map.of("name", "강남역"));

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = delete(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(200);
    }

    @DisplayName("존재하지 않는 지하철역을 제거한다.")
    @Test
    void deleteStationWithWrongId() {
        // given

        // when
        ExtractableResponse<Response> response = delete("/stations/1");

        // then
        assertThat(response.statusCode()).isEqualTo(400);
    }

    @DisplayName("구간에 포함되어있는 지하철역을 제거한다.")
    @Test
    void deleteStationInSection() {
        // given
        Long upStation = postStationAndGetId("구의역");
        Long downStation = postStationAndGetId("선릉역");
        Long line = postLineAndGetId("2호선", "green", upStation, downStation, 10);

        // when
        ExtractableResponse<Response> response = delete("/stations/" + upStation);

        // then
        assertThat(response.statusCode()).isEqualTo(400);
    }
}
