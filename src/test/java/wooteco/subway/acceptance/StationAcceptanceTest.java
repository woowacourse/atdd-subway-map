package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import wooteco.subway.dto.response.StationResponse;
import wooteco.subway.test_utils.HttpMethod;
import wooteco.subway.test_utils.HttpUtils;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("인수테스트 - /stations")
public class StationAcceptanceTest extends AcceptanceTest {

    @DisplayName("POST /stations - 지하철역 생성 테스트")
    @Nested
    class CreateStationTest extends AcceptanceTest {

        @Test
        void 지하철역을_생성한다() {
            Map<String, String> params = jsonStationOf("강남역");

            ExtractableResponse<Response> response = HttpUtils.send(HttpMethod.POST, "/stations", params);

            assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
            assertThat(response.header("Location")).isNotBlank();
        }

        @Test
        void 중복되는_이름의_지하철역_생성_시도시_예외발생() {
            Map<String, String> params = jsonStationOf("강남역");
            postStation(params);

            ExtractableResponse<Response> response = HttpUtils.send(HttpMethod.POST, "/stations", params);

            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }
    }

    @DisplayName("GET /stations - 지하철역 조회 테스트")
    @Test
    void 지하철역을_조회한다() {
        postStations("강남역", "역삼역");

        ExtractableResponse<Response> response = HttpUtils.send(HttpMethod.GET, "/stations");

        List<StationResponse> responseBody = response.jsonPath()
                .getList(".", StationResponse.class);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(responseBody).hasSize(2);
    }

    @DisplayName("DELETE /stations/:id - 지하철역 제거 테스트")
    @Nested
    class DeleteStationTest extends AcceptanceTest {

        @Test
        void 지하철역을_제거한다() {
            postStations("강남역");

            ExtractableResponse<Response> response = HttpUtils.send(HttpMethod.DELETE, "/stations/1");

            assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        }

        @DisplayName("존재하지 않는 id로 지하철역을 제거하려는 경우 404 NOT FOUND")
        @Test
        void deleteNonExistingStation() {
            ExtractableResponse<Response> response = HttpUtils.send(HttpMethod.DELETE, "/stations/999");

            assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        }
    }


    private HashMap<String, String> jsonStationOf(String name) {
        return new HashMap<>() {{
            put("name", name);
        }};
    }

    private void postStations(String... names) {
        for (String name : names) {
            postStation(jsonStationOf(name));
        }
    }

    private void postStation(Map<String, String> params) {
        HttpUtils.send(HttpMethod.POST, "/stations", params);
    }
}
