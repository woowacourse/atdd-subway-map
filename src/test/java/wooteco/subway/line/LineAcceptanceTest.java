package wooteco.subway.line;

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
import wooteco.subway.dto.StationRequest;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;


public class LineAcceptanceTest extends AcceptanceTest {
    @BeforeEach
    void setUpStationAndLine() {
        saveByStationName("강남역");
        saveByStationName("잠실역");
        saveByStationName("잠실새내역");
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", 1L, 3L, 10);

            // when
        ExtractableResponse<Response> response = saveLine(lineRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }


    @DisplayName("중복된 노선 이름 추가시 예외 처리")
    @Test
    void nameDuplication() {
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", 1L, 3L, 10);
        saveLine(lineRequest);

        ExtractableResponse<Response> response = saveLine(lineRequest);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        /// given
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", 1L, 3L, 10);
        ExtractableResponse<Response> firstLineResponse = saveLine(lineRequest);
        assertThat(firstLineResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        LineRequest lineRequest2 = new LineRequest("2호선", "bg-red-600", 2L, 3L, 10);
        ExtractableResponse<Response> secondLineResponse = saveLine(lineRequest2);
        assertThat(secondLineResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // when
        ExtractableResponse<Response> linesResponse = lookUpLines();

        // then
        assertThat(linesResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

        List<Long> resultLineIds = Stream.of(firstLineResponse, secondLineResponse)
                .map(response -> Long.parseLong(response.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> expectedLineIds = Arrays.asList(1L, 2L);

        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("하나의 지하철 노선을 상세 조회한다.")
    @Test
    void getLineDetail() {
        // given
        ExtractableResponse<Response> saveResponse = saveLine(new LineRequest("신분당선", "bg-red-600", 1L, 3L, 10));
        assertThat(saveResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // when
        ExtractableResponse<Response> lookUpResponse = lookUpLine(1L);

        // then
        assertThat(lookUpResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("없는 지하철 노선 조회 시 예외가 발생한다.")
    @Test
    void invalidLineId() {
        // when
        ExtractableResponse<Response> lookUpResponse = lookUpLine(1L);

        // then
        assertThat(lookUpResponse.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("지하철 노선 수정")
    @Test
    void modifyLineTest() {
        // given
        ExtractableResponse<Response> saveResponse = saveLine(new LineRequest("신분당선", "bg-red-600", 1L, 3L, 10));
        assertThat(saveResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // when
        LineRequest modificationRequest = new LineRequest("2호선", "bg-red-600", 2L, 3L, 20);
        ExtractableResponse<Response> modificationResponse = modifyLine(1L, modificationRequest);

        // then
        assertThat(modificationResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선 삭제 성공")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> saveResponse = saveLine(new LineRequest("신분당선", "bg-red-600", 1L, 3L, 10));
        assertThat(saveResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // when
        String uri = saveResponse.header("Location");
        ExtractableResponse<Response> deletionResponse = deleteLine(uri);

        // then
        assertThat(deletionResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("지하철 노선 삭제 실패")
    @Test
    void failDeleteLine() {
        // when
        ExtractableResponse<Response> deletionResponse = deleteLine("/lines/1");

        // then
        assertThat(deletionResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
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

    private ExtractableResponse<Response> lookUpLines() {
        return RestAssured.given().log().all()
                .when()
                .get("/lines/")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> lookUpLine(Long id) {
        return RestAssured.given().log().all()
                .when()
                .get("/lines/" + id)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> modifyLine(Long id, LineRequest lineRequest) {
        return RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + id)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> deleteLine(String uri) {
        return RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then()
                .log().all()
                .extract();
    }

    private ExtractableResponse<Response> saveByStationName(String stationName) {
        StationRequest stationRequest = new StationRequest(stationName);
        return saveStation(stationRequest);
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
