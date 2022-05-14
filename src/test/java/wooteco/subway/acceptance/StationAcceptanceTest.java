package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.http.HttpStatus;

import wooteco.subway.acceptance.fixture.SimpleResponse;
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
        SimpleResponse response = SimpleRestAssured.post("/stations", params);
        // then
        Assertions.assertAll(
                () -> response.assertStatus(HttpStatus.CREATED),
                () -> assertThat(response.getHeader("Location")).isNotBlank()
        );
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        Map<String, String> params = Map.of("name", "강남역");
        SimpleRestAssured.post("/stations", params);
        // when
        SimpleResponse response = SimpleRestAssured.post("/stations", params);
        // then
        response.assertStatus(HttpStatus.BAD_REQUEST);
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        Map<String, String> params1 = Map.of("name", "강남역");
        Map<String, String> params2 = Map.of("name", "역삼역");
        SimpleResponse createResponse1 = SimpleRestAssured.post("/stations", params1);
        SimpleResponse createResponse2 = SimpleRestAssured.post("/stations", params2);

        // when
        SimpleResponse response = SimpleRestAssured.get("/stations");

        // then
        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
                .map(SimpleResponse::getIdFromLocation)
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.toList(StationResponse.class).stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());
        Assertions.assertAll(
                () -> response.assertStatus(HttpStatus.OK),
                () -> assertThat(resultLineIds).containsAll(expectedLineIds)
        );
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        Map<String, String> params = Map.of("name", "강남역");
        SimpleResponse createResponse = SimpleRestAssured.post("/stations", params);
        // when
        String uri = createResponse.getHeader("Location");
        SimpleResponse response = SimpleRestAssured.delete(uri);
        // then
        response.assertStatus(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("존재하지 않는 지하철 역을 삭제하면 예외를 던진다.")
    void deleteStation_throwsExceptionWithInvalidStation() {
        // given
        Map<String, String> params = Map.of("name", "강남역");
        SimpleResponse createResponse = SimpleRestAssured.post("/stations", params);
        // when
        final SimpleResponse deleteResponse = SimpleRestAssured.delete("/lines/100");
        // then
        deleteResponse.assertStatus(HttpStatus.BAD_REQUEST);
    }
}
