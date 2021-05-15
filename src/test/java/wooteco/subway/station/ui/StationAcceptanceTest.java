package wooteco.subway.station.ui;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.domain.StationDao;
import wooteco.subway.station.dto.StationRequest;
import wooteco.subway.station.dto.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {
    @Autowired
    private StationDao stationDao;

    private Station station1;
    private Station station2;

    @BeforeEach
    void init() {
        this.station1 = stationDao.save(new Station("백기역"));
        this.station2 = stationDao.save(new Station("흑기역"));
    }

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // given
        String name = "강남역";
        StationRequest stationRequest = new StationRequest(name);

        // when
        ExtractableResponse<Response> response = saveStationToHTTP(stationRequest);
        StationResponse saveResponse = response.body().as(StationResponse.class);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
        assertThat(saveResponse.getName()).isEqualTo(name);
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성하면 예외가 발생한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        StationRequest stationRequest = new StationRequest(station1.rawName());

        // when
        ExtractableResponse<Response> response = saveStationToHTTP(stationRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        // given
        String name = "강남역";
        StationRequest stationRequest = new StationRequest(name);

        // when
        ExtractableResponse<Response> saveStationResponse = saveStationToHTTP(stationRequest);
        ExtractableResponse<Response> findStationResponse = findAllStationToHTTP();

        StationResponse stationResponse = saveStationResponse.body().as(StationResponse.class);
        List<Long> resultLineIds = findStationResponse.jsonPath().getList(".", StationResponse.class).stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        // then
        assertThat(findStationResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(resultLineIds).containsExactly(stationResponse.getId(), station2.id(), station1.id());
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given

        // when
        ExtractableResponse<Response> response = deleteStationByIdToHTTP(station1.id());

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("역 제거시 없는 노선이면 예외가 발생한다.")
    @Test
    void deleteStationException() {
        // given

        // when
        ExtractableResponse<Response> response = deleteStationByIdToHTTP(-1L);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> saveStationToHTTP(StationRequest stationRequest) {
        return RestAssured.given().log().all()
                .body(stationRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> findAllStationToHTTP() {
        return RestAssured.given().log().all()
                .when()
                .get("/stations")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> deleteStationByIdToHTTP(Long stationId) {
        return RestAssured.given().log().all()
                .when()
                .delete("/stations/{id}", stationId)
                .then().log().all()
                .extract();
    }
}
