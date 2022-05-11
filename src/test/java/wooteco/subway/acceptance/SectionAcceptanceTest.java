package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.sql.Connection;
import java.util.HashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import wooteco.subway.test_utils.HttpMethod;
import wooteco.subway.test_utils.HttpUtils;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("인수테스트 - /lines/{lineId}/sections")
public class SectionAcceptanceTest extends AcceptanceTest {

    @BeforeEach
    public void cleanseAndSetUpFixture() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("section_test_fixture.sql"));
        }
    }

    @DisplayName("POST /lines/:id/sections - 지하철 구간 등록 테스트")
    @Nested
    class CreateSectionTest {

        @Test
        void 성공시_200_OK() {
            HashMap<String, Object> validParams = jsonSectionOf(1L, 2L, 10);
            ExtractableResponse<Response> response = HttpUtils.send(
                    HttpMethod.POST, toPath(1L), validParams);

            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        }

        @Test
        void 존재하지_않는_지하철역을_입력한_경우_404_NOT_FOUND() {
            HashMap<String, Object> params = jsonSectionOf(9999L, 3L, 10);
            ExtractableResponse<Response> response = HttpUtils.send(
                    HttpMethod.POST, toPath(1L), params);

            assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        }

        @Test
        void 상행역과_하행역_모두_노선에_등록되어있지_않은_경우_400_BAD_REQUEST() {
            HashMap<String, Object> params = jsonSectionOf(4L, 5L, 10);
            ExtractableResponse<Response> response = HttpUtils.send(
                    HttpMethod.POST, toPath(1L), params);

            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        void 상행역과_하행역_모두_노선에_이미_등록된_경우_400_BAD_REQUEST() {
            HashMap<String, Object> params = jsonSectionOf(3L, 1L, 2);
            ExtractableResponse<Response> response = HttpUtils.send(
                    HttpMethod.POST, toPath(1L), params);

            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        void 기존_구간_사이에_기존_구간보다_큰_구간_추가시도시_400_BAD_REQUEST() {
            int distance = 999999;
            HashMap<String, Object> params = jsonSectionOf(2L, 1L, distance);
            ExtractableResponse<Response> response = HttpUtils.send(
                    HttpMethod.POST, toPath(1L), params);

            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }

        private String toPath(Long lineId) {
            return String.format("/lines/%d/sections", lineId);
        }

        private HashMap<String, Object> jsonSectionOf(Long upStationId,
                                                      Long downStationId,
                                                      int distance) {
            return new HashMap<>() {{
                put("upStationId", upStationId);
                put("downStationId", downStationId);
                put("distance", distance);
            }};
        }
    }

    @DisplayName("DELETE /lines/:id/sections?stationId={stationId} - 지하철 구간 제거 테스트")
    @Nested
    class DeleteSectionTest {

        @Test
        void 성공시_200_OK() {
            ExtractableResponse<Response> response = HttpUtils.send(
                    HttpMethod.DELETE, toPath(2L, 1L));

            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        }

        @Test
        void 존재하지_않는_노선을_입력한_경우_404_NOT_FOUND() {
            ExtractableResponse<Response> response = HttpUtils.send(
                    HttpMethod.DELETE, toPath(99999L, 1L));

            assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        }

        @Test
        void 구간으로_등록되지_않은_지하철역을_입력한_경우_400_BAD_REQUEST() {
            ExtractableResponse<Response> response = HttpUtils.send(
                    HttpMethod.DELETE, toPath(1L, 2L));

            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        void 노선의_마지막_구간을_제거하려는_경우_400_BAD_REQUEST() {
            ExtractableResponse<Response> response = HttpUtils.send(
                    HttpMethod.DELETE, toPath(1L, 1L));

            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }

        private String toPath(Long lineId, Long stationId) {
            return String.format("/lines/%d/sections?stationId=%d", lineId, stationId);
        }
    }
}
