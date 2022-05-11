package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationRequest;

public class SectionAcceptanceTest extends AcceptanceTest {

    @Autowired
    private ObjectMapper om;

    private void createStation1() throws JsonProcessingException {
        StationRequest stationRequest1 = new StationRequest("강남역");
        String params1 = om.writeValueAsString(stationRequest1);
        ExtractableResponse<Response> createResponse1 = RestAssured.given().log().all()
                .body(params1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();

        StationRequest stationRequest2 = new StationRequest("역삼역");
        String params2 = om.writeValueAsString(stationRequest2);
        ExtractableResponse<Response> createResponse2 = RestAssured.given().log().all()
                .body(params2)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();

        StationRequest stationRequest3 = new StationRequest("선릉역");
        String params3 = om.writeValueAsString(stationRequest3);
        ExtractableResponse<Response> createResponse3 = RestAssured.given().log().all()
                .body(params2)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }

    private void createLine() throws JsonProcessingException {
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);
        String params = om.writeValueAsString(lineRequest);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }
    @Test
    @DisplayName("지하철 구간을 생성한다.")
    void createSection() throws JsonProcessingException {
        createStation1();
        createLine();
        // given
        SectionRequest sectionRequest = new SectionRequest(2L, 3L, 10);
        String params = om.writeValueAsString(sectionRequest);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/1/sections")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
