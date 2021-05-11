package wooteco.subway.station;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.AcceptanceTest;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    private static final StationRequest 강남역 = new StationRequest("강남역");
    private static final StationRequest 역삼역 = new StationRequest("역삼역");

    public static ExtractableResponse<Response> 지하철역_등록(final StationRequest stationRequest) {
        return RestAssured.given().log().all()
            .body(stationRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then().log().all()
            .extract();
    }

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        final ExtractableResponse<Response> response = 지하철역_등록(강남역);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
        지하철역_생성값_검증(response, 강남역);
    }

    private void 지하철역_생성값_검증(final ExtractableResponse<Response> response, final StationRequest stationRequest) {
        final StationResponse stationResponse = response.body().as(StationResponse.class);
        assertThat(stationResponse.getId()).isEqualTo(getCreatedId(response));
        assertThat(stationResponse.getName()).isEqualTo(stationRequest.getName());
    }

    private long getCreatedId(final ExtractableResponse<Response> response) {
        return Long.parseLong(response.header("Location").split("/")[2]);
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성하면 400 에러가 발생한다.")
    @Test
    void createStationWithDuplicateName() {
        지하철역_등록(강남역);

        final ExtractableResponse<Response> response = 지하철역_등록(강남역);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        final ExtractableResponse<Response> createdResponse1 = 지하철역_등록(강남역);
        final ExtractableResponse<Response> createdResponse2 = 지하철역_등록(역삼역);

        final ExtractableResponse<Response> response = 지하철역_조회();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        final List<Long> expectedStationIds = makeExpectedStationIds(
            Arrays.asList(createdResponse1, createdResponse2)
        );
        final List<Long> resultStationIds = makeResultStationIds(response);

        assertThat(resultStationIds).containsAll(expectedStationIds);
    }

    private List<Long> makeExpectedStationIds(final List<ExtractableResponse<Response>> responses) {
        return responses.stream()
            .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
            .collect(Collectors.toList());
    }

    private List<Long> makeResultStationIds(final ExtractableResponse<Response> response) {
        return response.jsonPath().getList(".", StationResponse.class).stream()
            .map(StationResponse::getId)
            .collect(Collectors.toList());
    }

    private ExtractableResponse<Response> 지하철역_조회() {
        return RestAssured.given().log().all()
            .when()
            .get("/stations")
            .then().log().all()
            .extract();
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        final ExtractableResponse<Response> createResponse = 지하철역_등록(강남역);

        final ExtractableResponse<Response> response = 지하철역_제거(createResponse.header("Location"));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> 지하철역_제거(final String uri) {
        return RestAssured.given().log().all()
            .when()
            .delete(uri)
            .then().log().all()
            .extract();
    }
}
