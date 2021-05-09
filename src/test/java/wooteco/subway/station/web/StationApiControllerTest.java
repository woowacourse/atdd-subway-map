package wooteco.subway.station.web;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.station.dao.StationDao;

@DisplayName("지하철역 관련 기능")
class StationApiControllerTest extends AcceptanceTest {

    @Autowired
    StationDao stationDao;

    @AfterEach
    void afterEach() {
        stationDao.removeAll();
    }

    @DisplayName("지하철역 생성 - 성공")
    @Test
    void createStation_success() {
        // given
        StationRequest stationRequest = StationRequest.create("강남역");
        // when
        final ExtractableResponse<Response> response = 지하철역_생성(stationRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotEmpty();
    }

    @DisplayName("지하철역 생성 - 실패(중복 이름)")
    @Test
    void createStation_fail_duplicatedName() {
        // given
        final String stationName = "강남역";
        final StationRequest stationRequest = StationRequest.create(stationName);
        지하철역_생성(stationRequest);

        // when
        ExtractableResponse<Response> response = 지하철역_생성(StationRequest.create(stationName));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역 생성 - 실패(유효하지 않는 request)")
    @Test
    void createStation_fail_invalidRequest() {
        // given
        final StationRequest stationRequest = StationRequest.create("");

        // when
        final ExtractableResponse<Response> response = 지하철역_생성(stationRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        response.body().path("field");
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        String stationName1 = "강남역";
        String stationName2 = "역삼역";

        지하철역_생성(StationRequest.create(stationName1));
        지하철역_생성(StationRequest.create(stationName2));

        // when
        ExtractableResponse<Response> response = 지하철역_전체조회();

        // then
        final List<StationResponse> stationResponses =
            Arrays.asList(response.body().as(StationResponse[].class));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(stationResponses).hasSize(2);
        assertThat(stationResponses).extracting("name")
            .containsExactlyInAnyOrder(stationName1, stationName2);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        final ExtractableResponse<Response> createResponse = 지하철역_생성(StationRequest.create("강남역"));
        String uri = createResponse.header("Location");

        // when
        ExtractableResponse<Response> response = 지하철역_삭제(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> 지하철역_생성(StationRequest stationRequest) {
        return RestAssured.given()
            .body(stationRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 지하철역_전체조회() {
        return RestAssured.given()
            .when()
            .get("/stations")
            .then()
            .extract();
    }

    private ExtractableResponse<Response> 지하철역_삭제(String uri) {
        return RestAssured.given()
            .when()
            .delete(uri)
            .then()
            .extract();
    }
}