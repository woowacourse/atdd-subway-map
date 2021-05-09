package wooteco.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.acceptance.step.StationRequest;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
@Transactional
class StationAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // given, when
        Map<String, String> station = StationRequest.station1();
        ExtractableResponse<Response> response = StationRequest.createStationRequest(station);
        JsonPath jsonPath = response.jsonPath();
        Long id = jsonPath.getLong("id");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isEqualTo("/stations/" + id);
        assertThat(jsonPath.getLong("id")).isEqualTo(id);
        assertThat(jsonPath.getString("name")).isEqualTo(station.get("name"));
    }


    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        Map<String, String> station = StationRequest.station1();
        StationRequest.createStationRequest(station);

        // when
        ExtractableResponse<Response> response = StationRequest.createStationRequest(station);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        // given
        Map<String, String> station1 = StationRequest.station1();
        Map<String, String> station2 = StationRequest.station2();
        ExtractableResponse<Response> createStationResponse1 = StationRequest.createStationRequest(station1);
        ExtractableResponse<Response> createStationResponse2 = StationRequest.createStationRequest(station2);
        JsonPath jsonPath1 = createStationResponse1.jsonPath();
        JsonPath jsonPath2 = createStationResponse2.jsonPath();

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/stations")
                .then().log().all()
                .extract();
        JsonPath actualJsonPath = response.body().jsonPath();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actualJsonPath.getMap("[0]")).isEqualTo(jsonPath1.getMap(""));
        assertThat(actualJsonPath.getMap("[1]")).isEqualTo(jsonPath2.getMap(""));
        assertThat(actualJsonPath.getList("")).hasSize(2);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        Map<String, String> station = StationRequest.station1();
        ExtractableResponse<Response> createResponse
                = StationRequest.createStationRequest(station);
        String uri = createResponse.header("Location");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
