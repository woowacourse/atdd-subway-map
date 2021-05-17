package wooteco.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.exception.section.IncludedStationException;
import wooteco.subway.exception.station.StationDuplicationException;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.section.dto.SectionRequest;
import wooteco.subway.station.dto.StationRequest;
import wooteco.subway.station.dto.StationResponse;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
@Sql("/truncate.sql")
public class StationAcceptanceTest extends AcceptanceTest {

    private final StationRequest request = new StationRequest("강남역");

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        ExtractableResponse<Response> response = stationPostRequest(request);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        stationPostRequest(request);
        ExtractableResponse<Response> response = stationPostRequest(request);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString())
            .isEqualTo(new StationDuplicationException().getMessage());
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        ExtractableResponse<Response> createResponse1 = stationPostRequest(request);

        StationRequest request2 = new StationRequest("역삼역");
        ExtractableResponse<Response> createResponse2 = stationPostRequest(request2);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .get("/stations")
            .then().log().all()
            .extract();

        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
            .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
            .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", StationResponse.class).stream()
            .map(StationResponse::getId)
            .collect(Collectors.toList());

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(resultLineIds).containsExactlyElementsOf(expectedLineIds);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        ExtractableResponse<Response> createResponse = stationPostRequest(request);

        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = stationDeleteRequest(uri);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("구간에 포함되어 있는 지하철역을 제거하면 예외가 발생한다.")
    @Test
    void deleteIncludedStation() {
        StationRequest request2 = new StationRequest("역삼역");
        LineRequest lineRequest = new LineRequest("2호선", "초록색", 1L, 2L, 10);

        ExtractableResponse<Response> createResponse = stationPostRequest(request);
        stationPostRequest(request2);
        linePostRequest(lineRequest);

        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = stationDeleteRequest(uri);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString())
            .isEqualTo(new IncludedStationException().getMessage());
    }

    private ExtractableResponse<Response> stationPostRequest(StationRequest request) {
        return RestAssured.given().log().all()
            .body(request)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> stationDeleteRequest(String uri) {
        return RestAssured.given().log().all()
            .when()
            .delete(uri)
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> linePostRequest(LineRequest lineRequest) {
        return RestAssured.given().log().all()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
    }
}
