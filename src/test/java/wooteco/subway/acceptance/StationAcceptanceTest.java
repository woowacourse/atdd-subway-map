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
    class CreateStationTest {

        @Test
        void 성공시_201_CREATED() {
            Map<String, String> params = jsonStationOf("강남역");

            ExtractableResponse<Response> response = HttpUtils.send(HttpMethod.POST, "/stations", params);
            StationResponse actualBody = extractSingleStationResponseBody(response);
            StationResponse expectedBody = new StationResponse(1L, "강남역");

            assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
            assertThat(response.header("Location")).isNotBlank();
            assertThat(actualBody).isEqualTo(expectedBody);
        }

        @Test
        void 이름_정보가_담기지_않은_경우_400_BAD_REQUEST() {
            Map<String, String> emptyParams = new HashMap<>();

            ExtractableResponse<Response> response = HttpUtils.send(HttpMethod.POST, "/stations", emptyParams);

            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        void 이름_정보가_공백으로_구성된_경우_400_BAD_REQUEST() {
            Map<String, String> blankParams = jsonStationOf("  ");

            ExtractableResponse<Response> response = HttpUtils.send(HttpMethod.POST, "/stations", blankParams);

            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        void 중복되는_이름의_지하철역_생성_시도시_400_BAD_REQUEST() {
            testFixtureManager.saveStations("강남역");
            Map<String, String> params = jsonStationOf("강남역");

            ExtractableResponse<Response> response = HttpUtils.send(HttpMethod.POST, "/stations", params);

            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }
    }

    @DisplayName("GET /stations - 지하철역 조회 테스트")
    @Nested
    class ShowStationsTest {

        @Test
        void 성공시_200_OK() {
            testFixtureManager.saveStations("강남역", "역삼역");

            ExtractableResponse<Response> response = HttpUtils.send(HttpMethod.GET, "/stations");
            List<StationResponse> actualBody = extractJsonBody(response);
            List<StationResponse> expectedBody = List.of(
                    new StationResponse(1L, "강남역"),
                    new StationResponse(2L, "역삼역"));

            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(actualBody).isEqualTo(expectedBody);
        }

        private List<StationResponse> extractJsonBody(ExtractableResponse<Response> response) {
            return response.jsonPath().getList(".", StationResponse.class);
        }
    }

    @DisplayName("DELETE /stations/:id - 지하철역 제거 테스트")
    @Nested
    class DeleteStationTest {

        @Test
        void 성공시_204_OK() {
            testFixtureManager.saveStations("강남역");

            ExtractableResponse<Response> response = HttpUtils.send(HttpMethod.DELETE, "/stations/1");

            assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        }

        @Test
        void 존재하지_않는_id로_지하철역을_제거하려는_경우_404_NOT_FOUND() {
            ExtractableResponse<Response> response = HttpUtils.send(HttpMethod.DELETE, "/stations/999");

            assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        }

        @Test
        void 등록된_지하철역을_제거하려는_경우_400_BAD_REQUEST() {
            testFixtureManager.saveStations("강남역", "선릉역");
            testFixtureManager.saveLine("신분당선", "노란색");
            testFixtureManager.saveSection(1L, 1L, 2L);

            ExtractableResponse<Response> response = HttpUtils.send(HttpMethod.DELETE, "/stations/1");

            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }
    }

    private HashMap<String, String> jsonStationOf(String name) {
        return new HashMap<>() {{
            put("name", name);
        }};
    }

    private StationResponse extractSingleStationResponseBody(ExtractableResponse<Response> response) {
        return response.jsonPath().getObject(".", StationResponse.class);
    }
}
