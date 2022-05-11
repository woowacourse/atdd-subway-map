package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class SectionAcceptanceTest extends AcceptanceTest {

    @BeforeEach
    void createStationAndLine() {
        requestToCreateStation("강남역");
        requestToCreateStation("사당역");
        requestToCreateStation("선릉역");
        requestToCreateStation("잠실역");

        requestToCreateLine("신분당선", "red", "2", "3", "8");
    }

    @DisplayName("새로운 구간을 노선의 앞 뒤로 연결하는 것에 성공하면 200을 반환한다.")
    @ParameterizedTest
    @CsvSource({"1, 2", "3, 4"})
    void createNewSectionBackOrForth(String upStationId, String downStationId) {
        ExtractableResponse<Response> response =
                requestToConnectNewSection(1L, upStationId, downStationId, "2");

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("새로운 구간을 노선의 기존 구간 사이에 연결하는 것에 성공하면 200을 반환한다.")
    @ParameterizedTest
    @CsvSource({"2, 1", "1, 3"})
    void createNewSectionBetween_success(String upStationId, String downStationId) {
        ExtractableResponse<Response> response =
                requestToConnectNewSection(1L, upStationId, downStationId, "2");

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("새로 연결하는 구간의 상행선과 하행선이 노선에 둘 다 존재하거나, 둘 다 존재하지 않느면 400을 반환한다.")
    @ParameterizedTest
    @CsvSource({"1, 4", "4, 1", "2, 3", "3, 2"})
    void createNewSection_badRequest_duplicatedOrNonExistenceStations(String upStationId, String downStationId) {
        ExtractableResponse<Response> response =
                requestToConnectNewSection(1L, upStationId, downStationId, "2");

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선의 기존 구간 사이에 새 구간을 연결하려고 할 때, 새 구간의 길이가 더 길거나 같으면 400을 반환한다.")
    @ParameterizedTest
    @CsvSource({"2, 1", "1, 3"})
    void createNewSection_badRequest_LargerDistance(String upStationId, String downStationId) {
        ExtractableResponse<Response> response =
                requestToConnectNewSection(1L, upStationId, downStationId, "8");

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("구간을 삭제할 수 있다.")
    @ParameterizedTest
    @ValueSource(longs = {2, 3, 4})
    void delete(Long stationId) {
        requestToConnectNewSection(1L, "3", "4", "2");

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/lines/1/sections?stationId=" + stationId)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private ExtractableResponse<Response> requestToConnectNewSection(Long lineId, String upStationId,
                                                                     String downStationId, String distance) {
        Map<String, String> param = Map.of(
                "upStationId", upStationId,
                "downStationId", downStationId,
                "distance", distance
        );
        return RestAssured.given().log().all()
                .body(param)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();
    }

    private void requestToCreateStation(String stationName) {
        RestAssured.given().log().all()
                .body(Map.of("name", stationName))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> requestToCreateLine(String name, String color, String upStationId,
                                                              String downStationId, String distance) {
        Map<String, String> params = Map.of(
                "name", name,
                "color", color,
                "upStationId", upStationId,
                "downStationId", downStationId,
                "distance", distance);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }
}
