package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class SectionAcceptanceTest extends AcceptanceTest {

    private ExtractableResponse<Response> createResponse;

    @BeforeEach
    void setUpSection() {
        createStation("사당역");
        createStation("강남역");
        createLine("신분당선", "red", 1L, 2L, 10);
    }

    private void createLine(String name, String color, Long upStationId, Long downStationId, int distance) {
        Map<String, Object> createParams = new HashMap<>();
        createParams.put("name", name);
        createParams.put("color", color);
        createParams.put("upStationId", upStationId);
        createParams.put("downStationId", downStationId);
        createParams.put("distance", distance);

        RestAssured.given().log().all()
                .body(createParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    private void createStation(String stationName) {
        Map<String, String> params = new HashMap<>();
        params.put("name", stationName);
        RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> crateSection(Long lineId, Long upStationId, Long downStationId,
            int distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();
        return createResponse;
    }


    @DisplayName("구간을 생성한다.")
    @Test
    void create() {
        createStation("선릉역");
        createStation("잠실역");

        createResponse = crateSection(1L, 2L, 3L, 5);

        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(createResponse.header("Location")).isNotBlank();
    }

    @DisplayName("서로 다른 노선에 같은 구간이 생성될 수 있다.")
    @Test
    void createSection_anotherLine() {
        createLine("분당선", "orange", 1L, 2L, 10);
        createStation("선릉역");
        createStation("잠실역");

        ExtractableResponse<Response> createResponse2 = crateSection(2L, 2L, 3L, 5);

        assertThat(createResponse2.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(createResponse2.header("Location")).isNotBlank();
    }

    @DisplayName("같은 노선에 같은 구간이 생성될 수 없다.")
    @Test
    void createSection_exception() {
        ExtractableResponse<Response> createResponse = crateSection(1L, 1L, 2L, 10);

        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("같은 노선에 같은 구간이 생성될 수 없다.")
    @Test
    void createSection_exception2() {
        ExtractableResponse<Response> createResponse = crateSection(1L, 2L, 1L, 10);

        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("구간을 삭제한다.")
    @Test
    void remove() {
        createStation("선릉역");
        createStation("잠실역");

        createResponse = crateSection(1L, 2L, 3L, 5);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete("/lines/1/sections?stationId=1")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
