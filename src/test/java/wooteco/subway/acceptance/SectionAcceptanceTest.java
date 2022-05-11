package wooteco.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    @BeforeEach
    void createStations(){
        Map<String, String> params1 = new HashMap<>();
        params1.put("name", "강남역");
        createStationResponseOf(params1);

        Map<String, String> params2 = new HashMap<>();
        params2.put("name", "역삼역");
        createStationResponseOf(params2);

        Map<String, String> params3 = new HashMap<>();
        params3.put("name", "선릉역");
        createStationResponseOf(params3);

        Map<String, Object> params4 = new HashMap<>();
        params4.put("name", "1호선");
        params4.put("color", "red");
        params4.put("upStationId", 1L);
        params4.put("downStationId", 2L);
        params4.put("distance", 10);

        createLineResponseOf(params4);
    }

    @DisplayName("구간을 생성한다.")
    @Test
    void createSection() {

        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", 2L);
        params.put("downStationId", 3L);
        params.put("distance", 10);

        // when
        ExtractableResponse<Response> response = createSectionResponseOf(params);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("기존 노선과 연결되지 않은 구간을 생성한다.")
    @Test
    void createNotConnectedSection() {
        // given
        Map<String, String> params5 = new HashMap<>();
        params5.put("name", "교대역");
        createStationResponseOf(params5);

        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", 3L);
        params.put("downStationId", 4L);
        params.put("distance", 10);

        // when
        ExtractableResponse<Response> response = createSectionResponseOf(params);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("구간을 제거한다.")
    @Test
    void deleteSection() {
        // given
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", 2L);
        params.put("downStationId", 3L);
        params.put("distance", 10);

        // when
        createSectionResponseOf(params);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete("lines/1/sections?stationId=1")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void createStationResponseOf(Map<String, String> params) {
        RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }

    private void createLineResponseOf(Map<String, Object> params) {
        RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> createSectionResponseOf(Map<String, Object> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/1/sections")
                .then().log().all()
                .extract();
    }
}
