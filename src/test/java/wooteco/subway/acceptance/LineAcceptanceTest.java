package wooteco.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.acceptance.request.LineAndStationRequest;
import wooteco.subway.acceptance.request.LineRequest;
import wooteco.subway.acceptance.request.StationRequest;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
@Transactional
class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("노선을 생성한다.")
    @Test
    void createLine() {
        // given, when
        Map<String, String> line = LineRequest.line1(1L, 2L);
        ExtractableResponse<Response> response = LineRequest.createLineRequest(line);
        JsonPath jsonPath = response.body().jsonPath();
        Long id = response.jsonPath().getLong("id");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isEqualTo("/lines/" + id);
        assertThat(response.contentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        assertThat(jsonPath.getLong("id")).isEqualTo(id);
    }

    @DisplayName("기존에 존재하는 노선 이름으로 지하철역을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        LineRequest.createLineRequest(LineRequest.line1(1L, 2L));

        // when
        ExtractableResponse<Response> response = LineRequest.createLineRequest(LineRequest.line1(1L, 2L));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선에 구간 추가")
    @Test
    void createSection() {
        // given
        ExtractableResponse<Response> createLineResponse = LineRequest.createLineRequest(LineRequest.line1(1L, 2L));
        Long lineId = createLineResponse.jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = LineRequest.createSectionRequest(LineRequest.section1(2L, 150L), lineId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("노선들의 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        ExtractableResponse<Response> stationRequest1 = StationRequest.createStationRequest(StationRequest.station1());
        ExtractableResponse<Response> stationRequest2 = StationRequest.createStationRequest(StationRequest.station2());
        ExtractableResponse<Response> stationRequest3 = StationRequest.createStationRequest(StationRequest.station3());
        ExtractableResponse<Response> stationRequest4 = StationRequest.createStationRequest(StationRequest.station4());
        long stationId1 = stationRequest1.jsonPath().getLong("id");
        long stationId2 = stationRequest2.jsonPath().getLong("id");
        long stationId3 = stationRequest3.jsonPath().getLong("id");
        long stationId4 = stationRequest4.jsonPath().getLong("id");
        Map<String, String> line1 = LineRequest.line1(stationId1, stationId2);
        Map<String, String> line2 = LineRequest.line2(stationId3, stationId4);
        ExtractableResponse<Response> lineRequest1 = LineRequest.createLineRequest(line1);
        ExtractableResponse<Response> lineRequest2 = LineRequest.createLineRequest(line2);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();
        JsonPath jsonPath = response.jsonPath();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.contentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        assertThat(jsonPath.getString("[0].id")).isEqualTo(lineRequest1.jsonPath().getString("id"));
        assertThat(jsonPath.getString("[0].name")).isEqualTo(line1.get("name"));
        assertThat(jsonPath.getString("[0].color")).isEqualTo(line1.get("color"));
        assertThat(jsonPath.getString("[1].id")).isEqualTo(lineRequest2.jsonPath().getString("id"));
        assertThat(jsonPath.getString("[1].name")).isEqualTo(line2.get("name"));
        assertThat(jsonPath.getString("[1].color")).isEqualTo(line2.get("color"));
    }

    @DisplayName("노선에 등록되어있는 역을 조회한다.")
    @Test
    void getLineWithSections() {
        // given
        Map<String, String> station1 = StationRequest.station1();
        Map<String, String> station2 = StationRequest.station2();
        ExtractableResponse<Response> createStationResponse1 = StationRequest.createStationRequest(StationRequest.station1());
        ExtractableResponse<Response> createStationResponse2 = StationRequest.createStationRequest(StationRequest.station2());
        Long stationId1 = createStationResponse1.jsonPath().getLong("id");
        Long stationId2 = createStationResponse2.jsonPath().getLong("id");

        Map<String, String> line = LineRequest.line1(stationId1, stationId2);
        ExtractableResponse<Response> createLineResponse = LineRequest.createLineRequest(line);
        Long lineId = createLineResponse.jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .pathParam("lineId", lineId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines/{lineId}")
                .then().log().all()
                .extract();
        JsonPath jsonPath = response.jsonPath();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.contentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        assertThat(jsonPath.getLong("id")).isEqualTo(lineId);
        assertThat(jsonPath.getString("name")).isEqualTo(line.get("name"));
        assertThat(jsonPath.getString("color")).isEqualTo(line.get("color"));
        assertThat(jsonPath.getLong("stations[0].id")).isEqualTo(stationId1);
        assertThat(jsonPath.getString("stations[0].name")).isEqualTo(station1.get("name"));
        assertThat(jsonPath.getLong("stations[1].id")).isEqualTo(stationId2);
        assertThat(jsonPath.getString("stations[1].name")).isEqualTo(station2.get("name"));
    }

    @DisplayName("노선에 있는 지하철 역을 제거한다.")
    @Test
    void deleteLine() {
        // given
        Map<String, Long> ids = LineAndStationRequest.createLineWithStationsAndSectionsRequest();

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .pathParam("lineId", ids.get("line"))
                .pathParam("stationId", ids.get("station2"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/lines/{lineId}/sections?stationId={stationId}")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}