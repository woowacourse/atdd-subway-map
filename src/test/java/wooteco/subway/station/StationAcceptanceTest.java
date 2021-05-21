package wooteco.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.AcceptanceTest;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 역 관련 기능")
@Sql("/truncate.sql")
public class StationAcceptanceTest extends AcceptanceTest {
    private StationRequest 강남역 = new StationRequest("강남역");
    private StationRequest 역삼역 = new StationRequest("역삼역");

    private ExtractableResponse<Response> stationResponse1;
    private ExtractableResponse<Response> stationResponse2;

    @BeforeEach
    void initialize() {
        stationResponse1 = stationResponse(강남역);
        stationResponse2 = stationResponse(역삼역);
    }

    @DisplayName("역을 생성한다.")
    @Test
    void createStation() {
        assertThat(stationResponse1.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(stationResponse1.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 역 이름으로 역을 생성하면 예외가 발생한다.")
    @Test
    void createStationDuplicateName() {
        ExtractableResponse<Response> response = stationResponse(강남역);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("역을 조회한다.")
    @Test
    void getStations() {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/stations")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        List<Long> stationIds = Stream.of(stationResponse1, stationResponse2)
                .map(res -> Long.parseLong(res.header("Location").split("/")[2]))
                .collect(toList());
        List<Long> resultIds = response.jsonPath().getList(".", StationResponse.class).stream()
                .map(StationResponse::getId)
                .collect(toList());

        assertThat(resultIds).containsAll(stationIds);
    }

    @DisplayName("역을 제거한다.")
    @Test
    void deleteStation() {
        String uri = stationResponse1.header("Location");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> stationResponse(StationRequest stationRequest) {
        return RestAssured.given().log().all()
                .body(stationRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }
}
