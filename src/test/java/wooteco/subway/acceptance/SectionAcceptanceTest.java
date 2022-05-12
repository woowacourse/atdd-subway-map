package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationRequest;

public class SectionAcceptanceTest extends AcceptanceTest {

    private String uri;

    @BeforeEach
    void beforeEach() {
        StationRequest stationRequest1 = new StationRequest("강남역");
        StationRequest stationRequest2 = new StationRequest("역삼역");
        StationRequest stationRequest3 = new StationRequest("선릉역");
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);

        postStations(stationRequest1);
        postStations(stationRequest2);
        postStations(stationRequest3);
        ExtractableResponse<Response> response = postLines(lineRequest);
        uri = response.header("Location");
    }

    @Test
    @DisplayName("지하철 구간을 생성한다.")
    void createSection() {
        // given
        SectionRequest sectionRequest = new SectionRequest(2L, 3L, 10);

        // when
        ExtractableResponse<Response> response = postSections(sectionRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("지하철 구간에서 역을 삭제한다.")
    void deleteSection() {
        // given
        SectionRequest sectionRequest = new SectionRequest(2L, 3L, 10);
        ExtractableResponse<Response> createResponse = postSections(sectionRequest);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete(uri + "/sections?stationId=2")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private ExtractableResponse<Response> postSections(SectionRequest sectionRequest) {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(uri + "/sections")
                .then().log().all()
                .extract();
        return response;
    }
}
