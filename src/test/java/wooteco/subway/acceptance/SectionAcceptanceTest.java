package wooteco.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    @DisplayName("구간을 생성한다.")
    @Test
    void createSection() {
        // given
        Map<String, Object> createdLine = new HashMap<>();
        createdLine.put("name", "1호선");
        createdLine.put("color", "red");
        createdLine.put("upStationId", 1L);
        createdLine.put("downStationId", 2L);
        createdLine.put("distance", 10);

        RestAssured.given().log().all()
                .body(createdLine)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        Map<String, Object> newSection = new HashMap<>();
        newSection.put("upStationId", 2L);
        newSection.put("downStationId", 3L);
        newSection.put("distance", 10);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(newSection)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/1/sections")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("구간을 제거한다.")
    @Test
    void deleteSection() {
        // given
        Map<String, Object> existSection = new HashMap<>();
        existSection.put("upStationId", 1L);
        existSection.put("downStationId", 2L);
        existSection.put("distance", 10);

        RestAssured.given().log().all()
                .body(existSection)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/1/sections")
                .then().log().all()
                .extract();

        Map<String, Object> newSection = new HashMap<>();
        newSection.put("upStationId", 2L);
        newSection.put("downStationId", 3L);
        newSection.put("distance", 10);

        RestAssured.given().log().all()
                .body(newSection)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/1/sections")
                .then().log().all()
                .extract();

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .queryParams("stationId", 2L)
                .when()
                .delete("/lines/1/sections")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
