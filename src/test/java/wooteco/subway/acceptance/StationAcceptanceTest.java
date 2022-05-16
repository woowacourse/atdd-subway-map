package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static wooteco.subway.acceptance.SimpleRestAssured.*;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import wooteco.subway.dto.StationResponse;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("/stations에 대한 인수테스트")
class StationAcceptanceTest extends AcceptanceTest {

    @DisplayName("POST /stations - 지하철역 생성 테스트")
    @Nested
    class CreateStationTest extends AcceptanceTest {

        @Test
        void 지하철역을_생성한다() {
            Map<String, Object> params = makeStationJson("강남역");

            ExtractableResponse<Response> response = post(params, "/stations");

            assertAll(() -> {
                assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
                assertThat(response.header("Location")).isNotBlank();
            });
        }

        @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성하면 예외가 발생한다.")
        @Test
        void createStationWithDuplicateName() {
            Map<String, Object> params = makeStationJson("강남역");
            postStation(params);

            ExtractableResponse<Response> response = SimpleRestAssured.post(params, "/stations");

            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }
    }

    @DisplayName("GET /stations - 지하철역 조회 테스트")
    @Test
    void 지하철역을_조회한다() {
        ExtractableResponse<Response> createResponse1 = post(makeStationJson("강남역"), "/stations");
        ExtractableResponse<Response> createResponse2 = post(makeStationJson("역삼역"), "/stations");

        ExtractableResponse<Response> response = get("/stations");

        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
            .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
            .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath()
            .getList(".", StationResponse.class)
            .stream()
            .map(StationResponse::getId)
            .collect(Collectors.toList());
        assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(resultLineIds).containsExactlyElementsOf(expectedLineIds);
        });
    }

    @DisplayName("DELETE /stations/:id - 지하철역 제거 테스트")
    @Nested
    class DeleteStationTest extends AcceptanceTest {

        @Test
        void 지하철역을_제거한다() {
            Map<String, Object> params = makeStationJson("강남역");
            ExtractableResponse<Response> createResponse = post(params, "/stations");
            String uri = createResponse.header("Location");

            ExtractableResponse<Response> response = delete(uri);

            assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        }

        @DisplayName("존재하지 않는 id로 지하철역을 제거하려는 경우 예외가 발생한다.")
        @Test
        void deleteNonExistingStation() {
            ExtractableResponse<Response> response = delete("/stations/1");

            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }
    }
}
