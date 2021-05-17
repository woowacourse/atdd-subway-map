package wooteco.subway.station.controller;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.controller.web.station.StationRequest;
import wooteco.subway.controller.web.station.StationResponse;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static wooteco.subway.station.controller.StationControllerTestUtils.지하철역을_생성한다;

@DisplayName("지하철역 관련 기능")
public class StationControllerTest extends AcceptanceTest {
    public static final String TEST_STATION_NAME = "강남역";
    public static final StationRequest REQUEST_BODY = new StationRequest(TEST_STATION_NAME);

    @DisplayName("지하철역을 생성한다.")
    @Transactional
    @Test
    void createStation() {
        // given
        // when
        ExtractableResponse<Response> response = 지하철역을_생성한다(REQUEST_BODY);

        final StationResponse stationResponse = response.body().as(StationResponse.class);
        // then
        assertThat(REQUEST_BODY.getName()).isEqualTo(stationResponse.getName());

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();

    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Transactional
    @Test
    void createStationWithDuplicateName() {
        // given
        RestAssured.given().log().all()
                .body(REQUEST_BODY)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();

        // when
        ExtractableResponse<Response> response = 지하철역을_생성한다(REQUEST_BODY);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역을 조회한다.")
    @Transactional
    @Test
    void getStations() {
        /// given
        ExtractableResponse<Response> createResponse1 = 지하철역을_생성한다(REQUEST_BODY);

        StationRequest anotherResponse = new StationRequest("역삼역");
        ExtractableResponse<Response> createResponse2 = RestAssured.given().log().all()
                .body(anotherResponse)
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
        List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", StationResponse.class).stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철역을 제거한다.")
    @Transactional
    @Test
    void deleteStation() {
        // given
        ExtractableResponse<Response> createResponse = 지하철역을_생성한다(REQUEST_BODY);

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("없는 ID의 지하철역을 삭제하려고 하면 예외")
    @Transactional
    @Test
    void whenTryDeleteWrongIdStation() {
        // given
        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete("stations/99999")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
