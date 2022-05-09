package wooteco.subway.acceptance;

import static io.restassured.RestAssured.get;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.domain.Station;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // given
        Station station = new Station("잠실역");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(station)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        Long resultStationId = Long.parseLong(response.header("Location").split("/")[2]);
        get("/stations/" + resultStationId).then()
                .assertThat()
                .body("id", equalTo(resultStationId.intValue()))
                .body("name", equalTo("잠실역"));
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        Station station = new Station("잠실역");
        RestAssured.given().log().all()
                .body(station)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(station)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then()
                .log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        Station station1 = new Station("강남역");
        ExtractableResponse<Response> createResponse1 = RestAssured.given().log().all()
                .body(station1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();

        Station station2 = new Station("역삼역");
        ExtractableResponse<Response> createResponse2 = RestAssured.given().log().all()
                .body(station2)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/stations")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        Long resultStationId1 = Long.parseLong(createResponse1.header("Location").split("/")[2]);
        get("/stations/" + resultStationId1).then()
                .assertThat()
                .body("id", equalTo(resultStationId1.intValue()))
                .body("name", equalTo("강남역"));

        Long resultStationId2 = Long.parseLong(createResponse2.header("Location").split("/")[2]);
        get("/stations/" + resultStationId2).then()
                .assertThat()
                .body("id", equalTo(resultStationId2.intValue()))
                .body("name", equalTo("역삼역"));
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        Station station1 = new Station("강남역");
        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(station1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();

        // when
        String uri = createResponse.header("Location");
        String resultLineId = uri.split("/")[2];
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        get("/stations/" + resultLineId).then()
                .assertThat()
                .body("message", equalTo("해당하는 역이 존재하지 않습니다."));
    }
}
