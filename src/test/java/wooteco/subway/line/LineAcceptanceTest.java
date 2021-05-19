package wooteco.subway.line;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.station.dto.StationRequest;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
class LineAcceptanceTest extends AcceptanceTest {

    private Long firstStationId;
    private Long lastStationId;
    private LineRequest sampleRequest1;
    private LineRequest sampleRequest2;

    @BeforeEach
    void initStations() {
        StationRequest createUpStationRequest = new StationRequest("강남역");

        int resultId1 = RestAssured.given().log().all()
                .body(createUpStationRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract().body().jsonPath().get("id");

        firstStationId = Long.valueOf(resultId1);

        StationRequest createDownStationRequest = new StationRequest("삼성역");

        int resultId2 = RestAssured.given().log().all()
                .body(createDownStationRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract()
                .body().jsonPath().get("id");

        lastStationId = Long.valueOf(resultId2);

        sampleRequest1 = new LineRequest("코기선", "black", firstStationId, lastStationId,1);
        sampleRequest2 = new LineRequest("진환선", "black", firstStationId, lastStationId,1);
    }

    @DisplayName("지하철 노선 등록 성공")
    @Test
    void createLine() {
        ExtractableResponse<Response> createResponse =  createLine(sampleRequest1);

        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(createResponse.header("Location")).isNotBlank();
    }

    @DisplayName("지하철 노선 등록 실패 - 유효하지 않은 요청 정보")
    @Test
    void createLineWithInvalidRequest() {
        final LineRequest requestWithOutName = new LineRequest("", "black", firstStationId, lastStationId, 1);
        ExtractableResponse<Response> createResponse =  createLine(requestWithOutName);
        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 등록 실패 - 중복된 노선 존재")
    @Test
    void createLineWithDuplicateName() {
        createLine(sampleRequest1);
        ExtractableResponse<Response> createResponse =  createLine(sampleRequest1);

        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선 목록 조회 성공")
    @Test
    void showLines() {
        ExtractableResponse<Response> createResponse1 =  createLine(sampleRequest1);
        ExtractableResponse<Response> createResponse2 =  createLine(sampleRequest2);
        ExtractableResponse<Response> response = getLines();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        List<Long> expectedLineIds = expectedLineIds(createResponse1, createResponse2);
        List<Long> resultLindIds = resultStationsIds(response);
        assertThat(resultLindIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선 이름 변경 성공")
    @Test
    void updateLine() {
        ExtractableResponse<Response> createResponse = createLine(sampleRequest1);

        String uri = createResponse.header("Location");

        ExtractableResponse<Response> response = updateLine(uri, sampleRequest2);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선 제거 성공")
    @Test
    void deleteLine() {
        ExtractableResponse<Response> createResponse = createLine(sampleRequest1);

        final Long createdLineId = Long.valueOf(createResponse.body().jsonPath().get("id")+"");
        ExtractableResponse<Response> response = deleteLine(createdLineId);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> createLine(final LineRequest lineRequest){
        return RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> updateLine(final String uri, final LineRequest updateRequest) {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(updateRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(uri)
                .then().log().all()
                .extract();
        return response;
    }

    private ExtractableResponse<Response> getLines() {
        return RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> deleteLine(final Long stationId) {
        return RestAssured.given().log().all()
                .when()
                .delete("/lines/"+stationId)
                .then()
                .extract();
    }

    private List<Long> expectedLineIds(ExtractableResponse<Response> createResponse1, ExtractableResponse<Response> createResponse2) {
        return Arrays.asList(
                Long.parseLong(createResponse1.header("Location").split("/")[2]),
                Long.parseLong(createResponse2.header("Location").split("/")[2])
        );
    }

    private List<Long> resultStationsIds(ExtractableResponse<Response> response) {
        final JsonPath jsonPath = response.jsonPath();
        final List<LineResponse> lineResponses = jsonPath.getList(".", LineResponse.class);

        return lineResponses.stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
    }
}
