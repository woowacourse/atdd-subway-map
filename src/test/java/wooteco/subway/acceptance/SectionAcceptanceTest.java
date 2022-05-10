package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

public class SectionAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 구간을 생성한다.")
    @Test
    void createSection() {
        // given
        Long upStationId = postStations(new StationRequest("강남역")).as(StationResponse.class).getId();
        Long downStationId = postStations(new StationRequest("역삼역")).as(StationResponse.class).getId();
        Long lineId = postLines(new LineRequest("신분당선", "bg-red-200", upStationId, downStationId, 10))
                .as(LineResponse.class).getId();
        Long newStationId = postStations(new StationRequest("사이역")).body().as(StationResponse.class).getId();
        SectionRequest sectionRequest = new SectionRequest(upStationId, newStationId, 5);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
