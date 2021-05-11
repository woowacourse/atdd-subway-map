package wooteco.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.line.Line;
import wooteco.subway.line.LineResponse;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationResponse;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철구간 관련 기능")
@Sql("/data.sql")
public class SectionAcceptanceTest extends AcceptanceTest {

    private final SectionRequest sectionRequest = new SectionRequest(2L, 3L, 10);

    @DisplayName("지하철구간을 생성한다.")
    @Test
    void createSection() {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/1/sections")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        findLineForCreateSection();
    }

    private void findLineForCreateSection() {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines/1")
                .then().log().all()
                .extract();

        StationResponse stationResponse1 = new StationResponse(new Station(1L, "강남역"));
        StationResponse stationResponse2 = new StationResponse(new Station(2L, "역삼역"));
        StationResponse stationResponse3 = new StationResponse(new Station(3L, "아차산역"));

        LineResponse lineResponse = new LineResponse(new Line(1L, "2호선", "초록색"),
                Arrays.asList(stationResponse1, stationResponse2, stationResponse3));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        LineResponse resultResponse = response.jsonPath().getObject(".", LineResponse.class);

        assertThat(resultResponse).usingRecursiveComparison()
                .isEqualTo(lineResponse);
    }

    @DisplayName("지하철구간을 삭제한다.")
    @Test
    void deleteSection() {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .queryParam("stationId", 5)
                .delete("/lines/2/sections")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        findLineForDeleteSection();
    }

    private void findLineForDeleteSection() {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines/2")
                .then().log().all()
                .extract();

        StationResponse stationResponse1 = new StationResponse(new Station(4L, "탄현역"));
        StationResponse stationResponse2 = new StationResponse(new Station(6L, "홍대입구역"));

        LineResponse lineResponse = new LineResponse(new Line(2L, "경의중앙선", "하늘색"),
                Arrays.asList(stationResponse1, stationResponse2));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        LineResponse resultResponse = response.jsonPath().getObject(".", LineResponse.class);

        assertThat(resultResponse).usingRecursiveComparison()
                .isEqualTo(lineResponse);
    }
}
