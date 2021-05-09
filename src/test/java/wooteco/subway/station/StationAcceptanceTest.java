package wooteco.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.dto.StationResponse;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    @Autowired
    private StationDao stationDao;

    @AfterEach
    void afterEach() {
        stationDao.removeAll();
    }

    @DisplayName("지하철역 생성 - 성공")
    @Test
    void createStation() {
        // given
        Map<String, String> params = 역_정보("강남역");

        // when
        ExtractableResponse<Response> response = 역_추가(params);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("지하철역 생성 - 실패(기존에 존재하는 지하철역 이름으로 지하철역을 생성)")
    @Test
    void createStationWithDuplicateName() {
        // given
        Map<String, String> params = 역_정보("강남역");
        역_추가(params);

        // when
        ExtractableResponse<Response> response = 역_추가(params);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역 조회 - 성공")
    @Test
    void getStations() {
        /// given
        Map<String, String> params1 = 역_정보("강남역");
        ExtractableResponse<Response> createResponse1 = 역_추가(params1);
        Map<String, String> params2 = 역_정보("역삼역");
        ExtractableResponse<Response> createResponse2 = 역_추가(params2);

        // when
        ExtractableResponse<Response> response = 역_조회();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", StationResponse.class).stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철역 제거 - 성공")
    @Test
    void deleteStation() {
        // given
        Map<String, String> params = 역_정보("강남역");
        ExtractableResponse<Response> createResponse = 역_추가(params);

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = 역_삭제(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> 역_삭제(String uri) {
        return RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 역_조회() {
        return RestAssured.given().log().all()
                .when()
                .get("/stations")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 역_추가(Map<String, String> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }

    private Map<String, String> 역_정보(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        return params;
    }
}
