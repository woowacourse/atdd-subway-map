package wooteco.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.http.HttpStatus;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // given
        StationRequest stationRequest = new StationRequest("아차산역");

        // when
        final ExtractableResponse<Response> response = AcceptanceTestFixture.post("/stations", stationRequest);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.header("Location")).isNotBlank()
        );
    }

    @DisplayName("지하철역을 생성할 때 이름이 공백이면 예외를 발생한다.")
    @NullAndEmptySource
    @ParameterizedTest
    void thrown_nameBlank(String name) {
        // given
        StationRequest stationRequest = new StationRequest(name);

        final ExtractableResponse<Response> response = AcceptanceTestFixture.post("/stations", stationRequest);

        assertThat(response.jsonPath().getString("message")).isEqualTo("역 이름은 공백일 수 없습니다.");
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성하면 예외를 발생한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        StationRequest stationRequest = new StationRequest("강남역");
        AcceptanceTestFixture.post("/stations", stationRequest);

        // when
        final ExtractableResponse<Response> response = AcceptanceTestFixture.post("/stations", stationRequest);

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
        StationRequest firstStation = new StationRequest("아차산역");
        final ExtractableResponse<Response> createResponse1 = AcceptanceTestFixture.post("/stations", firstStation);

        StationRequest secondStation = new StationRequest("역삼역");
        final ExtractableResponse<Response> createResponse2 = AcceptanceTestFixture.post("/stations", secondStation);

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
        StationRequest stationRequest = new StationRequest("강남역");
        final ExtractableResponse<Response> createResponse = AcceptanceTestFixture.post("/stations", stationRequest);
        String uri = createResponse.header("Location");

        // when
        final ExtractableResponse<Response> response = AcceptanceTestFixture.delete(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
