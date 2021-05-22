package wooteco.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.line.controller.dto.LineRequest;
import wooteco.subway.line.controller.dto.LineResponse;
import wooteco.subway.line.controller.dto.SectionRequest;
import wooteco.subway.station.controller.dto.StationRequest;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    private LineRequest 이호선_생성요청 = new LineRequest("2호선", "bg-red-600", 1L, 2L, 10);
    private LineRequest 삼호선_생성요청 = new LineRequest("3호선", "bg-blue-600", 2L, 3L, 10);

    @DisplayName("지하철 노선 생성한다.")
    @Test
    void createLine() {
        // given
        Long stationsId1 = 지하철역_생성(new StationRequest("강남역"));
        Long stationsId2 = 지하철역_생성(new StationRequest("양재역"));
        LineRequest lineRequest = new LineRequest("2호선", "bg-red-600", stationsId1, stationsId2, 10);

        //when
        ExtractableResponse<Response> response = 노선_저장(lineRequest);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("지하철 노선 목록을 보여준다.")
    @Test
    void showLines() {
        // given
        ExtractableResponse<Response> createResponse1 = 노선_저장(new LineRequest("2호선", "bg-red-600", 1L, 2L, 10));
        ExtractableResponse<Response> createResponse2 = 노선_저장(new LineRequest("3호선", "bg-blue-600", 2L, 3L, 10));

        // when
        ExtractableResponse<Response> response = 노선_전체조회();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = 라인생성_응답에서_ID_가져오기(createResponse1, createResponse2);
        List<Long> resultLineIds = 라인조회_응답에서_ID_가져오기(response);
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선 1개를 보여준다.")
    @Test
    void showLine() {
        // given
        Long stationsId1 = 지하철역_생성(new StationRequest("강남역"));
        Long stationsId2 = 지하철역_생성(new StationRequest("양재역"));
        LineRequest lineRequest = new LineRequest("2호선", "bg-red-600", stationsId1, stationsId2, 10);
        ExtractableResponse<Response> createResponse = 노선_저장(lineRequest);

        // when
        String uri = getUri(createResponse);
        ExtractableResponse<Response> response = 노선_1개_조회(uri);
        Long expectedLineId = 라인생성_응답에서_ID_가져오기(createResponse);
        Long resultLineId = 라인조회_응답에서_ID_1개_가져오기(response);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(resultLineId).isEqualTo(expectedLineId);
    }

    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteLine() {
        // given
        노선_저장(이호선_생성요청);
        ExtractableResponse<Response> createResponse = 노선_저장(삼호선_생성요청);

        // when
        String uri = getUri(createResponse);
        ExtractableResponse<Response> response = 노선_삭제(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        노선_삭제_확인();
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> createResponse = 노선_저장(이호선_생성요청);
        LineRequest updateRequest = new LineRequest("3호선", "bg-red-600", 1L, 2L, 20);

        // when
        String uri = getUri(createResponse);
        ExtractableResponse<Response> response = 노선_수정(updateRequest, uri);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 구간 추가한다.")
    @Test
    void addSection() {
        //given
        Long stationId1 = 지하철역_생성(new StationRequest("강남역"));
        Long stationId2 = 지하철역_생성(new StationRequest("양재역"));
        Long lineId = 라인생성_응답에서_ID_가져오기(노선_저장(new LineRequest("1호선", "빨간맛", stationId1, stationId2, 20)));
        Long stationId3 = 지하철역_생성(new StationRequest("우테코역"));

        ExtractableResponse<Response> response = 구간_추가(lineId, new SectionRequest(stationId3, stationId2, 10));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("지하철 구간 삭제한다.")
    @Test
    void deleteSection() {
        //given
        Long stationId1 = 지하철역_생성(new StationRequest("강남역"));
        Long stationId2 = 지하철역_생성(new StationRequest("양재역"));
        Long lineId = 라인생성_응답에서_ID_가져오기(노선_저장(new LineRequest("1호선", "빨간맛", stationId1, stationId2, 20)));
        Long stationId3 = 지하철역_생성(new StationRequest("우테코역"));
        구간_추가(lineId, new SectionRequest(stationId3, stationId2, 10));

        //when
        ExtractableResponse<Response> response = 구간_삭제(lineId, stationId3);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> 노선_저장(LineRequest lineRequest) {
        return RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    private String getUri(ExtractableResponse<Response> createResponse) {
        return createResponse.header("Location");
    }

    private Long 라인조회_응답에서_ID_1개_가져오기(ExtractableResponse<Response> response) {
        return response.jsonPath().getObject(".", LineResponse.class).getId();
    }

    private List<Long> 라인조회_응답에서_ID_가져오기(ExtractableResponse<Response> response) {
        return response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
    }

    private List<Long> 라인생성_응답에서_ID_가져오기(ExtractableResponse<Response> createResponse1, ExtractableResponse<Response> createResponse2) {
        return Stream.of(createResponse1, createResponse2)
                .map(this::라인생성_응답에서_ID_가져오기)
                .collect(Collectors.toList());
    }

    private Long 라인생성_응답에서_ID_가져오기(ExtractableResponse<Response> createResponse) {
        return Long.parseLong(createResponse.header("Location").split("/")[2]);
    }

    private Long 지하철역_생성(StationRequest stationRequest) {
        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(stationRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
        return Long.parseLong(createResponse.header("Location").split("/")[2]);
    }

    private ExtractableResponse<Response> 노선_전체조회() {
        return RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 노선_1개_조회(String uri) {
        return RestAssured.given().log().all()
                .when()
                .get(uri)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 노선_삭제(String uri) {
        return RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();
    }

    private void 노선_삭제_확인() {
        RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .body("size()", is(1));
    }

    private ExtractableResponse<Response> 노선_수정(LineRequest updateRequest, String uri) {
        return RestAssured.given().log().all()
                .body(updateRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(uri)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 구간_추가(Long lineId, SectionRequest sectionRequest) {
        return RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .pathParam("lineId", lineId)
                .post("/lines/{lineId}/sections")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 구간_삭제(Long lineId, Long stationId) {
        return RestAssured.given().log().all()
                .when()
                .pathParam("lineId", lineId)
                .queryParam("stationId", stationId)
                .delete("/lines/{lineId}/sections")
                .then().log().all()
                .extract();
    }
}
