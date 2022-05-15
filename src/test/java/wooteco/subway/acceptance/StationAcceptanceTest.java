package wooteco.subway.acceptance;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // given
        StationRequest 역_요청 = new StationRequest("강남역");

        // when
        ExtractableResponse<Response> 역_응답 = httpPost("/stations", 역_요청);

        // then
        assertThat(역_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(역_응답.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        StationRequest 역_요청 = new StationRequest("강남역");
        ExtractableResponse<Response> 역_응답 = httpPost("/stations", 역_요청);

        // when
        ExtractableResponse<Response> 역_중복_응답 = httpPost("/stations", 역_요청);

        // then
        assertThat(역_중복_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        StationRequest 강남역_요청 = new StationRequest("강남역");
        ExtractableResponse<Response> 강남역_응답 = httpPost("/stations", 강남역_요청);

        StationRequest 역삼역_요청 = new StationRequest("역삼역");
        ExtractableResponse<Response> 역삼역_응답 = httpPost("/stations", 역삼역_요청);

        // when
        ExtractableResponse<Response> 역리스트_응답 = httpGet("/stations");

        // then
        List<Long> expectedLineIds = Stream.of(강남역_응답, 역삼역_응답)
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(toList());
        List<Long> resultLineIds = 역리스트_응답.jsonPath().getList(".", StationResponse.class).stream()
                .map(StationResponse::getId)
                .collect(toList());
        assertThat(역리스트_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        StationRequest 강남역_요청 = new StationRequest("강남역");
        ExtractableResponse<Response> 강남역_응답 = httpPost("/stations", 강남역_요청);

        // when
        String uri = 강남역_응답.header("Location");
        ExtractableResponse<Response> 삭제_응답 = httpDelete(uri);

        // then
        assertThat(삭제_응답.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
