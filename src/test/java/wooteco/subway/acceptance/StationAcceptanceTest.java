package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import wooteco.subway.acceptance.fixture.SimpleRestAssured;
import wooteco.subway.dto.response.StationResponse;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // given
        Map<String, String> params = Map.of("name", "강남역");

        // when
        ExtractableResponse<Response> response = SimpleRestAssured.post("/stations", params);

        // then
        Assertions.assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
            () -> assertThat(response.header("Location")).isNotBlank()
        );
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        Map<String, String> params = Map.of("name", "강남역");
        SimpleRestAssured.post("/stations", params);

        // when
        ExtractableResponse<Response> response = SimpleRestAssured.post("/stations", params);
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        Map<String, String> params1 = Map.of("name", "강남역");
        Map<String, String> params2 = Map.of("name", "역삼역");
        ExtractableResponse<Response> createResponse1 = SimpleRestAssured.post("/stations", params1);
        ExtractableResponse<Response> createResponse2 = SimpleRestAssured.post("/stations", params2);

        // when
        ExtractableResponse<Response> response = SimpleRestAssured.get("/stations");

        // then
        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
            .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
            .collect(Collectors.toList());
        System.out.println();
        List<Long> resultLineIds = response.jsonPath().getList(".", StationResponse.class).stream()
            .map(StationResponse::getId)
            .collect(Collectors.toList());
        Assertions.assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(resultLineIds).containsAll(expectedLineIds)
        );
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        Map<String, String> params = Map.of("name", "강남역");
        ExtractableResponse<Response> createResponse = SimpleRestAssured.post("/stations", params);
        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = SimpleRestAssured.delete(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("존재하지 않는 지하철 역을 삭제하면 예외를 던진다.")
    void deleteStation_throwsExceptionWithInvalidStation() {
        // given
        Map<String, String> params = Map.of("name", "강남역");
        ExtractableResponse<Response> createResponse = SimpleRestAssured.post("/stations", params);
        // when
        final ExtractableResponse<Response> deleteResponse = SimpleRestAssured.delete("/stations/100");
        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
