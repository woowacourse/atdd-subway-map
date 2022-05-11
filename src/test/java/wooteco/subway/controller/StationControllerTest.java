package wooteco.subway.controller;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class StationControllerTest extends AcceptanceTest {

    @Autowired
    private StationDao stationDao;

    private Station savedStation1;
    private Station savedStation2;
    private final String basicPath = "/stations";

    @BeforeEach
    void setUpStations() {
        savedStation1 = stationDao.insert(new Station("선릉역"));
        savedStation2 = stationDao.insert(new Station("선정릉역"));
    }

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        StationRequest request = new StationRequest("강남역");

        ExtractableResponse<Response> response =
                RestAssuredConvenienceMethod.postRequest(request, MediaType.APPLICATION_JSON_VALUE, basicPath);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("비어있는 값으로 이름을 생성하면 400번 코드를 반환한다.")
    @Test
    void createStationWithInvalidDataSize() {
        StationRequest request = new StationRequest("");

        ExtractableResponse<Response> response =
                RestAssuredConvenienceMethod.postRequest(request, MediaType.APPLICATION_JSON_VALUE, basicPath);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성하면 400번 코드를 반환한다.")
    @Test
    void createStationWithDuplicateName() {
        StationRequest request = new StationRequest("선릉역");

        ExtractableResponse<Response> response =
                RestAssuredConvenienceMethod.postRequest(request, MediaType.APPLICATION_JSON_VALUE, basicPath);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        ExtractableResponse<Response> response = RestAssuredConvenienceMethod.getRequest(basicPath);

        List<Long> resultLineIds = response.jsonPath().getList(".", StationResponse.class).stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(List.of(savedStation1.getId(), savedStation2.getId()));
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        ExtractableResponse<Response> response =
                RestAssuredConvenienceMethod.deleteRequest("/stations/" + savedStation1.getId());

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("존재하지 않는 지하철역을 삭제하려하면 400번 코드를 반환한다.")
    @Test
    void deleteStationWithNotExistData() {
        ExtractableResponse<Response> response =
                RestAssuredConvenienceMethod.deleteRequest("/stations/" + 100L);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
