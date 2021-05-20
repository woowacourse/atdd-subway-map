package wooteco.subway.station.ui;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.station.dto.StationRequest;
import wooteco.subway.station.dto.StationResponse;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {
    private static final String BASE_URL = "/stations";
    private static final String BASE_URL_WITH_ID = "/stations/{id}";

    private static final String 흑기역 = "흑기역";
    private static final String 백기역 = "백기역";

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // given

        // when
        ExtractableResponse<Response> response = 지하철역_생성_요청(흑기역);

        // then
        지하철역_생성됨(response);
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성하면 예외가 발생한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        지하철역_생성_되어있음(흑기역);

        // when
        ExtractableResponse<Response> response = 지하철역_생성_요청(흑기역);

        // then
        지하철역_생성_실패됨(response);
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        // given
        StationResponse response1 = 지하철역_생성_되어있음(흑기역);
        StationResponse response2 = 지하철역_생성_되어있음(백기역);

        // when
        ExtractableResponse<Response> response = 지하철역_목록_조회_요청();

        // then
        지하철_목록_정상_응답됨(response);
        지하철_목록_정상_포함됨(response, Arrays.asList(response1, response2));
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        StationResponse stationResponse = 지하철역_생성_되어있음(흑기역);

        // when
        ExtractableResponse<Response> response = 지하철역_삭제_요청(stationResponse);

        // then
        지하철역_정상_삭제됨(response);
    }

    @DisplayName("역 제거시 없는 노선이면 예외가 발생한다.")
    @Test
    void deleteStationException() {
        // given
        StationResponse stationResponse = 지하철역_생성_되어있음(흑기역);

        // when
        지하철역_삭제_요청(stationResponse);
        ExtractableResponse<Response> response = 지하철역_삭제_요청(stationResponse);

        // then
        지하철_삭제_실패됨(response);
    }

    public static StationResponse 지하철역_생성_되어있음(String name) {
        return 지하철역_생성_요청(name).as(StationResponse.class);
    }

    private static ExtractableResponse<Response> 지하철역_생성_요청(String name) {
        StationRequest request = new StationRequest(name);
        return 지하철역_생성_요청(request);
    }

    private static ExtractableResponse<Response> 지하철역_생성_요청(StationRequest request) {
        return post_요청을_보냄(BASE_URL, request);
    }

    private ExtractableResponse<Response> 지하철역_삭제_요청(StationResponse response) {
        return delete_요청을_보냄(BASE_URL_WITH_ID, response.getId());
    }

    private ExtractableResponse<Response> 지하철역_목록_조회_요청() {
        return get_요청을_보냄(BASE_URL);
    }

    private void 지하철역_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    private void 지하철역_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void 지하철_목록_정상_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철역_정상_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private void 지하철_목록_정상_포함됨(ExtractableResponse<Response> response, List<StationResponse> stationResponses) {
        List<Long> resultStationIds = stationResponses.stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        List<Long> expectStationIds = response.jsonPath().getList(".", StationResponse.class).stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());
        assertThat(resultStationIds.containsAll(expectStationIds)).isEqualTo(true);
    }

    private void 지하철_삭제_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
