package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static wooteco.subway.acceptance.TestFixtures.extractDeleteResponse;
import static wooteco.subway.acceptance.TestFixtures.extractGetResponse;
import static wooteco.subway.acceptance.TestFixtures.extractPostResponse;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStationTest() {
        //given
        StationRequest stationRequest = new StationRequest("잠실역");

        // when
        ExtractableResponse<Response> response = extractPostResponse(stationRequest, "/stations");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateNameTest() {
        //given
        StationRequest stationRequest = new StationRequest("강남역");

        // when
        ExtractableResponse<Response> repeatedResponse = extractPostResponse(stationRequest, "/stations");

        // then
        assertThat(repeatedResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStationsTest() {
        ExtractableResponse<Response> response = extractGetResponse("/stations");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> resultStationIds = response.jsonPath().getList(".", StationResponse.class).stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());
        assertThat(resultStationIds).containsAll(List.of(1L, 2L, 3L));
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStationTest() {
        ExtractableResponse<Response> deleteResponse = extractDeleteResponse("/stations/1");
        ExtractableResponse<Response> getResponse = extractGetResponse("/stations");
        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        List<Long> resultStationIds = getResponse.jsonPath().getList(".", StationResponse.class).stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());
        assertThat(resultStationIds).containsAll(List.of(2L, 3L));
    }
}
