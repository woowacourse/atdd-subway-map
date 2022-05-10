package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

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
    void createStation() {
        StationRequest params = new StationRequest("강남역");

        ExtractableResponse<Response> response = httpPostTest(params, "/stations");

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        StationRequest params = new StationRequest("강남역");
        httpPostTest(params, "/stations");

        ExtractableResponse<Response> response = httpPostTest(params, "/stations");
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        StationRequest params1 = new StationRequest("강남역");
        ExtractableResponse<Response> createResponse1 = httpPostTest(params1, "/stations");

        StationRequest params2 = new StationRequest("역삼역");
        ExtractableResponse<Response> createResponse2 = httpPostTest(params2, "/stations");

        ExtractableResponse<Response> response = httpGetTest("/stations");
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", StationResponse.class).stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        StationRequest params = new StationRequest("강남역");
        ExtractableResponse<Response> createResponse = httpPostTest(params, "/stations");

        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = httpDeleteTest(uri);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
