package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import wooteco.subway.ui.request.StationRequest;
import wooteco.subway.ui.response.StationResponse;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    private final String defaultUri = "/stations";

    @Test
    @DisplayName("지하철역을 등록한다.")
    void createStation() {
        // given
        StationRequest request = new StationRequest("강남역");

        // when
        ExtractableResponse<Response> response = getExtractablePostResponse(request, defaultUri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @ParameterizedTest
    @CsvSource(value = {"라:0", "라:31"}, delimiter = ':')
    @DisplayName("유효하지 않는 이름으로 지하철역을 등록할 경우 400 응답을 던진다.")
    void createStationWithInvalidName(String name, int repeatCount) {
        // given
        StationRequest request = new StationRequest(name.repeat(repeatCount));

        // when
        ExtractableResponse<Response> response = getExtractablePostResponse(request, defaultUri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().jsonPath().getString("message")).isEqualTo("이름은 1~30 자 이내여야 합니다.");
    }

    @Test
    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 등록할 경우 400 응답을 던진다.")
    void createStationWithDuplicateName() {
        // given
        StationRequest request = new StationRequest("강남역");

        // when
        getExtractablePostResponse(request, "/stations");
        ExtractableResponse<Response> response = getExtractablePostResponse(request, defaultUri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("모든 지하철역을 조회한다.")
    void getStations() {
        // given
        StationRequest firstRequest = new StationRequest("강남역");
        ExtractableResponse<Response> firstCreateResponse = getExtractablePostResponse(firstRequest, defaultUri);

        StationRequest secondRequest = new StationRequest("역삼역");
        ExtractableResponse<Response> secondCreateResponse = getExtractablePostResponse(secondRequest, defaultUri);

        List<StationResponse> expectedStationResponses = Stream.of(firstCreateResponse, secondCreateResponse)
            .map(it -> it.jsonPath().getObject(".", StationResponse.class))
            .collect(Collectors.toList());

        // when
        ExtractableResponse<Response> response = getExtractableGetResponse(defaultUri);
        List<StationResponse> actualStationResponses = response.jsonPath().getList(".", StationResponse.class);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actualStationResponses).isEqualTo(expectedStationResponses);
    }

    @Test
    @DisplayName("지하철역을 제거한다.")
    void deleteStation() {
        // given
        StationRequest request = new StationRequest("강남역");
        ExtractableResponse<Response> createResponse = getExtractablePostResponse(request, defaultUri);

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = getExtractableDeleteResponse(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("존재하지 않는 id 로 지하철역을 제거할 경우 404 응답을 던진다.")
    void deleteStationWithIdNotExists() {
        // when
        ExtractableResponse<Response> response = getExtractableDeleteResponse(defaultUri + "/1");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }
}
