package wooteco.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationRequest;

import static org.assertj.core.api.Assertions.assertThat;

public class SectionAcceptanceTest extends AcceptanceTest {
    @BeforeEach
    void setUpStationAndLine() {
        saveByStationName("강남역");
        saveByStationName("잠실역");
        saveByStationName("잠실새내역");
        saveByLineName("2호선");
    }

    @DisplayName("지하철 구간을 생성한다.")
    @Test
    void createSection() {
        // given
        SectionRequest sectionRequest = new SectionRequest(3, 1, 5);

        // when
        ExtractableResponse<Response> sectionResponse = saveSection(1, sectionRequest);

        // then
        assertThat(sectionResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(sectionResponse.header("Location")).isNotBlank();
    }

    private void saveByLineName(String lineName) {
        LineRequest lineRequest = new LineRequest(lineName, "bg-red-600", 1, 2, 5);
        ExtractableResponse<Response> lineResponse = saveLine(lineRequest);
        assertThat(lineResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    private ExtractableResponse<Response> saveByStationName(String stationName) {
        StationRequest stationRequest = new StationRequest(stationName);
        return saveStation(stationRequest);
    }

    private ExtractableResponse<Response> saveSection(long lineId, SectionRequest sectionRequest) {
        return RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> saveLine(LineRequest lineRequest) {
        return RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> saveStation(StationRequest stationRequest) {
        return RestAssured.given().log().all()
                .body(stationRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }
}
