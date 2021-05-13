package wooteco.subway.section;

import static org.assertj.core.api.Assertions.assertThat;
import static wooteco.subway.util.RestfulOrder.DEFAULT_MEDIA_TYPE;
import static wooteco.subway.util.RestfulOrder.testResponse;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.line.SectionRequest;

public class SectionAcceptanceTest extends AcceptanceTest {

    @DisplayName("구간을 생성한다.")
    @Test
    void createSection() {
        createStation("강남역");
        createStation("서초역");
        createStation("송파역");
        createDefaultLine();

        ExtractableResponse<Response> response = sectionPostRequest(
            new SectionRequest(2L, 3L, 10), "/lines/1/sections");

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    private void createDefaultLine() {
        Map<String, String> defaultLineParams = new HashMap<>();
        defaultLineParams.put("name", "신분당선");
        defaultLineParams.put("color", "bg-red-600");
        defaultLineParams.put("upStationId", "1");
        defaultLineParams.put("downStationId", "2");
        defaultLineParams.put("distance", "10");
        ExtractableResponse<Response> response = testResponse(defaultLineParams, DEFAULT_MEDIA_TYPE,
            "/lines");

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    private void createStation(String stationName) {
        Map<String, String> params = new HashMap<>();
        params.put("name", stationName);
        ExtractableResponse<Response> response = testResponse(params, DEFAULT_MEDIA_TYPE,
            "/stations");
        // when

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }


    private ExtractableResponse<Response> sectionPostRequest(SectionRequest sectionRequest,
        String path) {
        return RestAssured.given().log().all()
            .body(sectionRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post(path)
            .then().log().all()
            .extract();
    }
}
