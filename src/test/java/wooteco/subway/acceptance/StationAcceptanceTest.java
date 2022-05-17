package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import wooteco.subway.service.dto.StationRequest;
import wooteco.subway.service.dto.StationResponse;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        StationRequest body = new StationRequest("노원역");

        ExtractableResponse<Response> response = postWithBody("/stations", body);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성시 BAD REQUEST를 반환한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        StationRequest requestBody = new StationRequest("강남역");

        postWithBody("/stations", requestBody);

        // when
        ExtractableResponse<Response> duplicatedNameResponse = postWithBody("/stations", requestBody);

        // then
        assertThat(duplicatedNameResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        StationRequest 강남역_생성_바디 = new StationRequest("강남역");

        ExtractableResponse<Response> 강남역_생성_응답 = postWithBody("/stations", 강남역_생성_바디);

        StationRequest 역삼역_생성_바디 = new StationRequest("역삼역");

        ExtractableResponse<Response> 역삼역_생성_응답 = postWithBody("/stations", 역삼역_생성_바디);

        // when

        ExtractableResponse<Response> 저장된_역_조회_응답 = get("/stations");

        // then
        assertThat(저장된_역_조회_응답.statusCode()).isEqualTo(HttpStatus.OK.value());

        List<Long> expectedLineIds = Stream.of(강남역_생성_응답, 역삼역_생성_응답)
                .map(this::getIdFromLocation)
                .collect(Collectors.toList());

        List<Long> resultLineIds = 저장된_역_조회_응답.jsonPath().getList(".", StationResponse.class).stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        StationRequest requestBody = new StationRequest("노원역");

        ExtractableResponse<Response> 생성_응답 = postWithBody("/stations", requestBody);
        // when
        String uri = 생성_응답.header("Location");

        ExtractableResponse<Response> response = delete(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
