package wooteco.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.http.HttpStatus;
import wooteco.subway.dto.StationResponse;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 역 생성을 관리한다")
    @TestFactory
    Stream<DynamicTest> dynamicTestsFromCollection() {
        return Stream.of(
                dynamicTest("새로운 역 이름으로 역을 생성한다.", () -> {
                    // when
                    ExtractableResponse<Response> response = createStation("강남역");

                    // then
                    assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
                    assertThat(response.header("Location")).isNotBlank();
                }),

                dynamicTest("기존의 역 이름으로 역을 생성한다", () -> {
                    // when
                    ExtractableResponse<Response> response = createStation("강남역");

                    // then
                    assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                })
        );
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        ExtractableResponse<Response> createResponse1 = createStation("강남역");
        ExtractableResponse<Response> createResponse2 = createStation("역삼역");

        // when
        ExtractableResponse<Response> response = get("/stations");

        // then
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
        // given
        ExtractableResponse<Response> createResponse = createStation("강남역");
        String uri = createResponse.header("Location");

        // when
        ExtractableResponse<Response> response = delete(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("제거할 지하철 역이 없는 경우 예외가 발생한다.")
    @Test
    void deleteNotExistStation() {
        // given
        ExtractableResponse<Response> createResponse = createStation("강남역");
        String uri = createResponse.header("Location");
        delete(uri);

        // when
        ExtractableResponse<Response> response = delete(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
