package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

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

@DisplayName("지하철 구간 관련 기능")
class SectionAcceptanceTest extends AcceptanceTest {

    private Long upStationId;
    private Long downStationId;
    private Long newDownStationId;
    private Long lineId;
    private SectionRequest sectionRequest;

    @BeforeEach
    void setup() {
        upStationId = createStation(new StationRequest("아차산역"));
        downStationId = createStation(new StationRequest("군자역"));
        newDownStationId = createStation(new StationRequest("마장역"));
        lineId = createLine(new LineRequest("5호선", "bg-purple-600", upStationId, downStationId, 10));

        sectionRequest = new SectionRequest(downStationId, newDownStationId, 5);
    }

    @DisplayName("특정 노선의 구간을 추가한다.")
    @Test
    void createSection() {
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("특정 노선의 구간을 삭제한다.")
    @Test
    void deleteSection() {
        // given
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();

        // delete
        final ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .when()
                .delete("/lines/" + lineId + "/sections?stationId=" + downStationId)
                .then().log().all()
                .extract();

        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private Long createStation(final StationRequest stationRequest) {
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(stationRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();

        return Long.parseLong(response.header("Location").split("/")[2]);
    }

    private Long createLine(final LineRequest lineRequest) {
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
        return response.jsonPath().getLong("id");
    }
}
