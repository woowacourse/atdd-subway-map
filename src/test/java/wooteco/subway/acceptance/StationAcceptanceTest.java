package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static wooteco.subway.acceptance.TestFixtures.extractDeleteResponse;
import static wooteco.subway.acceptance.TestFixtures.extractGetResponse;
import static wooteco.subway.acceptance.TestFixtures.extractPostResponse;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
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
        StationRequest stationRequest = new StationRequest("강남역");

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
        ExtractableResponse<Response> response = extractPostResponse(stationRequest, "/stations");
        ExtractableResponse<Response> repeatedResponse = extractPostResponse(stationRequest, "/stations");

        // then
        assertThat(repeatedResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStationsTest() {
        //given
        StationRequest stationRequest1 = new StationRequest("강남역");
        StationRequest stationRequest2 = new StationRequest("역삼역");

        // when
        ExtractableResponse<Response> createResponse1 = extractPostResponse(stationRequest1, "/stations");
        ExtractableResponse<Response> createResponse2 = extractPostResponse(stationRequest2, "/stations");
        
        ExtractableResponse<Response> response = extractGetResponse("/stations");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedStationIds = Arrays.asList(createResponse1, createResponse2).stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultStationIds = response.jsonPath().getList(".", StationResponse.class).stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());
        assertThat(resultStationIds).containsAll(expectedStationIds);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStationTest() {
        //given
        StationRequest stationRequest = new StationRequest("강남역");

        // when
        ExtractableResponse<Response> response = extractPostResponse(stationRequest, "/stations");

        String uri = response.header("Location");
        ExtractableResponse<Response> deleteResponse = extractDeleteResponse(uri);

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
