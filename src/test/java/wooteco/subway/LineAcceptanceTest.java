package wooteco.subway;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.controller.dto.request.LineRequest;
import wooteco.subway.controller.dto.request.StationRequest;
import wooteco.subway.controller.dto.request.UpdateLineRequest;
import wooteco.subway.controller.dto.response.LineResponse;
import wooteco.subway.controller.dto.response.StationResponse;

public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철노선을 생성한다.")
    @Test
    void createLine() {
        // given
        StationResponse 오리역 = 지하철역_생성결과_추출(지하철역_생성("오리역"));
        StationResponse 한티역 = 지하철역_생성결과_추출(지하철역_생성("한티역"));
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", 오리역.getId(), 한티역.getId(),
            5);

        // when
        ExtractableResponse<Response> response = 지하철노선_생성(lineRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("지하철역 테이블에 없는 역으로 구간 등록 시도시 예외처리")
    @Test
    public void insertNullStation() {
        // given
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 5);

        // when
        ExtractableResponse<Response> response = 지하철노선_생성(lineRequest);

        //then
        assertThat(response.statusCode()).isEqualTo(404);
    }

    @DisplayName("기존에 존재하는 지하철노선 이름으로 지하철노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        StationResponse 오리역 = 지하철역_생성결과_추출(지하철역_생성("오리역"));
        StationResponse 한티역 = 지하철역_생성결과_추출(지하철역_생성("한티역"));
        StationResponse 상봉역 = 지하철역_생성결과_추출(지하철역_생성("상봉역"));
        StationResponse 군자역 = 지하철역_생성결과_추출(지하철역_생성("군자역"));
        LineRequest lineRequest1 = new LineRequest("신분당선", "bg-blue-600", 오리역.getId(), 한티역.getId(),
            5);
        LineRequest lineRequest2 = new LineRequest("신분당선", "bg-red-200", 상봉역.getId(), 군자역.getId(),
            25);

        지하철노선_생성(lineRequest1);

        // when
        ExtractableResponse<Response> duplicateResponse = 지하철노선_생성(lineRequest2);

        // then
        assertThat(duplicateResponse.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    @DisplayName("기존에 존재하는 지하철선 색으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicateColor() {
        // given
        StationResponse 오리역 = 지하철역_생성결과_추출(지하철역_생성("오리역"));
        StationResponse 한티역 = 지하철역_생성결과_추출(지하철역_생성("한티역"));
        StationResponse 상봉역 = 지하철역_생성결과_추출(지하철역_생성("상봉역"));
        StationResponse 군자역 = 지하철역_생성결과_추출(지하철역_생성("군자역"));
        LineRequest lineRequest1 = new LineRequest("신분당선", "bg-red-200", 오리역.getId(), 한티역.getId(),
            5);
        LineRequest lineRequest2 = new LineRequest("경의중앙선", "bg-red-200", 상봉역.getId(), 군자역.getId(),
            25);

        지하철노선_생성(lineRequest1);

        // when
        ExtractableResponse<Response> duplicateResponse = 지하철노선_생성(lineRequest2);

        // then
        assertThat(duplicateResponse.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    @DisplayName("기존에 존재하는 지하철 노선을 전체 조회한다.")
    @Test
    void checkAllLines() {
        // given
        StationResponse 오리역 = 지하철역_생성결과_추출(지하철역_생성("오리역"));
        StationResponse 한티역 = 지하철역_생성결과_추출(지하철역_생성("한티역"));
        StationResponse 상봉역 = 지하철역_생성결과_추출(지하철역_생성("상봉역"));
        StationResponse 군자역 = 지하철역_생성결과_추출(지하철역_생성("군자역"));
        LineRequest lineRequest1 = new LineRequest("신분당선", "bg-red-200", 오리역.getId(), 한티역.getId(),
            5);
        LineRequest lineRequest2 = new LineRequest("경의중앙선", "bg-blue-800", 상봉역.getId(), 군자역.getId(),
            25);

        ExtractableResponse<Response> response1 = 지하철노선_생성(lineRequest1);
        ExtractableResponse<Response> response2 = 지하철노선_생성(lineRequest2);

        // when
        ExtractableResponse<Response> response = 전체_지하철노선_조회();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        List<Long> expectedLineIds = Stream.of(response1, response2)
            .map(this::지하철노선_ID_추출)
            .collect(Collectors.toList());

        List<Long> resultLineIds = response.jsonPath()
            .getList(".", LineResponse.class)
            .stream()
            .map(LineResponse::getId)
            .collect(Collectors.toList());

        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("특정 노선을 조회한다.")
    @Test
    public void specificLine() {
        // given
        String name = "신분당선";
        String color = "bg-red-200";

        StationResponse 오리역 = 지하철역_생성결과_추출(지하철역_생성("오리역"));
        StationResponse 한티역 = 지하철역_생성결과_추출(지하철역_생성("한티역"));
        LineRequest lineRequest = new LineRequest(name, color, 오리역.getId(), 한티역.getId(), 5);

        ExtractableResponse<Response> lineCreateResponse = 지하철노선_생성(lineRequest);
        Long responseId = 지하철노선_ID_추출(lineCreateResponse);

        // when
        ExtractableResponse<Response> response = 지하철노선_조회(responseId);

        // then
        LineResponse lineResponse = response.jsonPath().getObject(".", LineResponse.class);
        assertThat(lineResponse.getName()).isEqualTo(name);
        assertThat(lineResponse.getColor()).isEqualTo(color);
    }

    @DisplayName("존재하지 않는 노선을 조회한다.")
    @Test
    public void voidLine() {
        // given

        // when
        ExtractableResponse<Response> response = 지하철노선_조회(999L);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("특정 노선을 수정한다.")
    @Test
    public void updateLine() {
        // given
        StationResponse 오리역 = 지하철역_생성결과_추출(지하철역_생성("오리역"));
        StationResponse 한티역 = 지하철역_생성결과_추출(지하철역_생성("한티역"));
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-200", 오리역.getId(), 한티역.getId(),
            5);

        ExtractableResponse<Response> lineCreateResponse = 지하철노선_생성(lineRequest);
        Long responseId = 지하철노선_ID_추출(lineCreateResponse);

        final String color = "bg-purple-406";
        final String name = "대구선";

        // when
        ExtractableResponse<Response> response = 지하철노선_수정(responseId, name, color);
        ExtractableResponse<Response> checkLineResponse = 지하철노선_조회(responseId);
        LineResponse lineResponse = checkLineResponse.jsonPath().getObject(".", LineResponse.class);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(lineResponse.getName()).isEqualTo(name);
        assertThat(lineResponse.getColor()).isEqualTo(color);
    }

    @DisplayName("존재하지 않는 노선을 수정한다.")
    @Test
    public void updateVoidLine() {
        // given

        // when
        ExtractableResponse<Response> response = 지하철노선_수정(999L, "대구선", "bg-purple-406");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("이미 다른 노선이 사용중인 색깔로 바꾼다")
    @Test
    public void updateLineToExistedColor() {
        // given
        String firstLineName = "신분당선";
        String firstLineColor = "bg-green-900";
        String secondLineName = "경의중앙선";
        String secondLineColor = "bg-red-100";

        StationResponse 오리역 = 지하철역_생성결과_추출(지하철역_생성("오리역"));
        StationResponse 한티역 = 지하철역_생성결과_추출(지하철역_생성("한티역"));
        StationResponse 상봉역 = 지하철역_생성결과_추출(지하철역_생성("상봉역"));
        StationResponse 군자역 = 지하철역_생성결과_추출(지하철역_생성("군자역"));
        LineRequest lineRequest1 = new LineRequest(firstLineName, firstLineColor, 오리역.getId(), 한티역.getId(),
            5);
        LineRequest lineRequest2 = new LineRequest(secondLineName, secondLineColor, 상봉역.getId(), 군자역.getId(),
            25);

        지하철노선_생성(lineRequest1);
        ExtractableResponse<Response> secondLineCreateResponse = 지하철노선_생성(lineRequest2);
        Long responseId = 지하철노선_ID_추출(secondLineCreateResponse);

        // when
        ExtractableResponse<Response> response = 지하철노선_수정(responseId, secondLineName, firstLineColor);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    @DisplayName("이미 다른 노선이 사용중인 이름으로 바꾼다")
    @Test
    public void updateLineToExistedName() {
        // given
        String firstLineName = "신분당선";
        String firstLineColor = "bg-green-900";
        String secondLineName = "경의중앙선";
        String secondLineColor = "bg-red-100";

        StationResponse 오리역 = 지하철역_생성결과_추출(지하철역_생성("오리역"));
        StationResponse 한티역 = 지하철역_생성결과_추출(지하철역_생성("한티역"));
        StationResponse 상봉역 = 지하철역_생성결과_추출(지하철역_생성("상봉역"));
        StationResponse 군자역 = 지하철역_생성결과_추출(지하철역_생성("군자역"));
        LineRequest lineRequest1 = new LineRequest(firstLineName, firstLineColor, 오리역.getId(), 한티역.getId(),
            5);
        LineRequest lineRequest2 = new LineRequest(secondLineName, secondLineColor, 상봉역.getId(), 군자역.getId(),
            25);

        지하철노선_생성(lineRequest1);
        ExtractableResponse<Response> secondLineCreateResponse = 지하철노선_생성(lineRequest2);
        Long responseId = 지하철노선_ID_추출(secondLineCreateResponse);

        // when
        ExtractableResponse<Response> response = 지하철노선_수정(responseId, firstLineName, secondLineColor);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    @DisplayName("특정 노선을 삭제한다.")
    @Test
    public void deleteSpecificLine() {
        // given
        StationResponse 오리역 = 지하철역_생성결과_추출(지하철역_생성("오리역"));
        StationResponse 한티역 = 지하철역_생성결과_추출(지하철역_생성("한티역"));
        LineRequest lineRequest = new LineRequest("신분당선", "bg-green-900", 오리역.getId(), 한티역.getId(),
            5);

        ExtractableResponse<Response> formResponse = 지하철노선_생성(lineRequest);
        Long responseId = 지하철노선_ID_추출(formResponse);

        // when
        ExtractableResponse<Response> response = 지하철노선_제거(responseId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("존재하지 않는 노선 제거요청시 예외처리")
    @Test
    public void deleteVoidLine() {
        // given

        // when
        ExtractableResponse<Response> response = 지하철노선_제거(999L);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    private ExtractableResponse<Response> 지하철노선_생성(LineRequest lineRequest) {
        return RestAssured.given().log().all()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 전체_지하철노선_조회() {
        return RestAssured.given().log().all()
            .when()
            .get("/lines")
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 지하철노선_조회(Long id) {
        return RestAssured.given().log().all()
            .when()
            .get("/lines/" + id)
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 지하철노선_수정(Long id, String name, String color) {
        return RestAssured.given().log().all()
            .body(new UpdateLineRequest(name, color))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put("/lines/" + id)
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 지하철노선_제거(Long id) {
        return RestAssured.given().log().all()
            .when()
            .delete("/lines/" + id)
            .then().log().all()
            .extract();
    }

    private long 지하철노선_ID_추출(ExtractableResponse<Response> secondLineCreateResponse) {
        return Long.parseLong(secondLineCreateResponse.header("Location").split("/")[2]);
    }

    private ExtractableResponse<Response> 지하철역_생성(String name) {
        return RestAssured.given().log().all()
            .body(new StationRequest(name))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then().log().all()
            .extract();
    }

    private StationResponse 지하철역_생성결과_추출(ExtractableResponse<Response> response) {
        return response.as(StationResponse.class);
    }
}
