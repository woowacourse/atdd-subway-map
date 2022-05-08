package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.StationResponse;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "상일동역");

        // when
        final ExtractableResponse<Response> response = AcceptanceTestFixture.post("/stations", params);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.header("Location")).isNotBlank()
        );
    }

    @DisplayName("지하철역을 생성할 때 입력값이 잘못되면 예외를 발생한다.")
    @ParameterizedTest
    @MethodSource("badStationRequest")
    void createStationWithBadInput(String name, String errorMessage) {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        // when
        final ExtractableResponse<Response> response = AcceptanceTestFixture.post("/stations", params);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response.body().jsonPath().getString("message")).isEqualTo(errorMessage)
        );
    }

    private static Stream<Arguments> badStationRequest() {
        return Stream.of(
                Arguments.of(new String(new char[256]), "역이름은 255자를 초과할 수 없습니다."),
                Arguments.of("", "역이름은 비어있을 수 없습니다."),
                Arguments.of(null, "역이름은 비어있을 수 없습니다.")
        );
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성하면 예외를 발생한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "강남역");
        AcceptanceTestFixture.post("/stations", params);

        // when
        final ExtractableResponse<Response> response = AcceptanceTestFixture.post("/stations", params);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response.body().jsonPath().getString("message")).isEqualTo("이미 같은 이름의 지하철역이 존재합니다.")
        );
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        Map<String, String> params1 = new HashMap<>();
        params1.put("name", "아차산역");
        final ExtractableResponse<Response> createResponse1 = AcceptanceTestFixture.post("/stations", params1);

        Map<String, String> params2 = new HashMap<>();
        params2.put("name", "역삼역");
        final ExtractableResponse<Response> createResponse2 = AcceptanceTestFixture.post("/stations", params2);

        // when
        final ExtractableResponse<Response> response = AcceptanceTestFixture.get("/stations");

        // then
        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", StationResponse.class).stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(resultLineIds).containsAll(expectedLineIds)
        );
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "강남역");

        final ExtractableResponse<Response> createResponse = AcceptanceTestFixture.post("/stations", params);
        String uri = createResponse.header("Location");

        // when
        final ExtractableResponse<Response> response = AcceptanceTestFixture.delete(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
