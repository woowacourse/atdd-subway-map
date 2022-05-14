package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import wooteco.subway.dto.StationResponse;

@DisplayName("지하철역 관련 기능")
class StationAcceptanceTest extends AcceptanceTest {

    private static final String DUPLICATE_STATION_ERROR_MESSAGE = "역이 이미 있습니다";

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // given && when
        ExtractableResponse<Response> response = createStationAndReturnResponse("station1");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        String stationName = "station1";
        createStation(stationName);

        // when
        ExtractableResponse<Response> response = createStationAndReturnResponse(stationName);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().jsonPath().getString("message")).contains(DUPLICATE_STATION_ERROR_MESSAGE);
    }

    @DisplayName("지하철역 이름을 빈 값으로 지하철역을 생성한다.")
    @Test
    void createStationWithNameBlank() {
        // given && when
        String name = "";
        ExtractableResponse<Response> response = createStationAndReturnResponse(name);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().jsonPath().getString("message")).contains(BLANK_OR_NULL_ERROR_MESSAGE);
    }

    @DisplayName("지하철역 이름을 null로 지하철역을 생성한다.")
    @Test
    void createStationWithNameNull() {
        // given && when
        String name = null;
        ExtractableResponse<Response> response = createStationAndReturnResponse(name);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().jsonPath().getString("message")).contains(BLANK_OR_NULL_ERROR_MESSAGE);
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        StationResponse station1 = createStation("station1");
        StationResponse station2 = createStation("station2");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/stations")
                .then().log().all()
                .extract();

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.body().jsonPath().getList(".", StationResponse.class))
                        .usingRecursiveComparison().isEqualTo(List.of(station1, station2))
        );


    }

    /*
    * given
    * 두 개의 지하철 역이 등록되어 있다.
    *
    * when
    * 하나를 제거한다.
    *
    * then
    * 성공을 응답한다.
    * 역 목록을 조회하면 삭제되지 않은 역 하나만 조회된다.
    * */
    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        StationResponse station1 = createStation("station1");

        // when
        ExtractableResponse<Response> deleteResponse = RestAssured.given()
                .when()
                .delete("/stations/" + station1.getId())
                .then()
                .extract();

        // then
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/stations")
                .then().log().all()
                .extract();

        assertAll(
                () -> assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),
                () -> assertThat(response.body().jsonPath().getList(".", StationResponse.class)).isEmpty()
        );
    }
}
