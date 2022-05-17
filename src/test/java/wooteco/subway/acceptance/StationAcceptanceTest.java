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
        ExtractableResponse<Response> response = post("/stations", new StationRequest("강남역"));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        post("/stations", new StationRequest("강남역"));
        ExtractableResponse<Response> response = post("/stations", new StationRequest("강남역"));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        ExtractableResponse<Response> createResponse1 = post("/stations", new StationRequest("강남역"));
        ExtractableResponse<Response> createResponse2 = post("/stations", new StationRequest("잠실역"));

        ExtractableResponse<Response> response = get("/stations");

        List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", StationResponse.class).stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        ExtractableResponse<Response> createResponse = post("/stations", new StationRequest("강남역"));
        String url = createResponse.header("Location");

        ExtractableResponse<Response> response = delete(url);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("존재하지 않은 지하철역을 제거하려고 하는 경우에 상태코드 200을 반환한다.")
    @Test
    void deleteNotExistStation() {
        ExtractableResponse<Response> response = delete("/stations/1");

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
