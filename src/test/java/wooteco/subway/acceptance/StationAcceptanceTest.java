package wooteco.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.acceptance.template.StationRequest;
import wooteco.subway.controller.dto.request.StationRequestDto;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
@Transactional
class StationAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // given, when
        String stationName = "강남역";
        ExtractableResponse<Response> response
                = StationRequest.createStationRequestAndReturnResponse(new StationRequestDto(stationName));
        JsonPath jsonPath = response.jsonPath();
        Long id = jsonPath.getLong("id");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isEqualTo("/stations/" + id);
        assertThat(jsonPath.getString("name")).isEqualTo(stationName);
    }


    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        StationRequestDto dto = new StationRequestDto("강남역");
        StationRequest.createStationRequestAndReturnId(dto);

        // when
        ExtractableResponse<Response> response = StationRequest.createStationRequestAndReturnResponse(dto);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        // given
        StationRequest.createStationRequestAndReturnId(new StationRequestDto("강남역"));
        StationRequest.createStationRequestAndReturnId(new StationRequestDto("길동역"));

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/stations")
                .then().log().all()
                .extract();
        JsonPath actualJsonPath = response.body().jsonPath();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actualJsonPath.getList("")).hasSize(2);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        ExtractableResponse<Response> createResponse
                = StationRequest.createStationRequestAndReturnResponse(new StationRequestDto("강남역"));
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
