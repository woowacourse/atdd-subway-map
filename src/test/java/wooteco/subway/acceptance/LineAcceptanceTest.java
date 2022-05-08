package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.line.LineRequest;
import wooteco.subway.dto.line.LineResponse;
import wooteco.subway.dto.station.StationRequest;
import wooteco.subway.dto.station.StationResponse;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private final String upStationName = "선릉역";
    private final String downStationName = "역삼역";
    private Long upStationId;
    private Long downStationId;
    private LineRequest lineRequest;

    @BeforeEach
    void setUpData() {
        upStationId = createAndGetStationId(new StationRequest(upStationName));
        downStationId = createAndGetStationId(new StationRequest(downStationName));
        lineRequest = new LineRequest("1호선", "bg-red-600", upStationId, downStationId, 10);
    }

    @Test
    @DisplayName("지하철 노선과 구간을 생성한다.")
    void CreateLine_WithSection_Success() {
        // given
        final LineRequest request = new LineRequest("1호선", "bg-red-600", upStationId, downStationId, 10);

        // when
        final ExtractableResponse<Response> actual = createLine(request);
        final long lineId = extractId(actual);

        // then
        assertThat(actual.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(actual.header(LOCATION)).isEqualTo(LINE_PATH_PREFIX + SLASH + lineId);

        final LineResponse actualResponse = actual.body().as(LineResponse.class);
        final List<StationResponse> stations = actualResponse.getStations();
        final StationResponse upStationResponse = stations.get(0);
        final StationResponse downStationResponse = stations.get(1);

        assertAll(() -> {
            assertThat(actualResponse.getId()).isNotNull();
            assertThat(actualResponse.getName()).isEqualTo(request.getName());
            assertThat(actualResponse.getColor()).isEqualTo(request.getColor());

            assertThat(stations).hasSize(2);
            assertThat(upStationResponse.getName()).isEqualTo(upStationName);
            assertThat(downStationResponse.getName()).isEqualTo(downStationName);
        });
    }

    @DisplayName("모든 노선을 조회한다.")
    @Test
    void Show_Lines() {
        // given
        final ExtractableResponse<Response> expected1 = createLine(lineRequest);
        final long lineId1 = extractId(expected1);

        final long upStationId2 = createAndGetStationId(new StationRequest("왕십리역"));
        final long downStationId2 = createAndGetStationId(new StationRequest("답십리역"));
        final ExtractableResponse<Response> expected2 = createLine(
                new LineRequest("5호선", "bg-violet-600", upStationId2, downStationId2, 7));
        final long lineId2 = extractId(expected2);

        // when
        final ExtractableResponse<Response> actual = RestAssured.given().log().all()
                .when()
                .get(LINE_PATH_PREFIX)
                .then().log().all()
                .extract();


        // then
        final List<LineResponse> actualLines = actual.jsonPath().getList(".", LineResponse.class);
        assertThat(actual.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actualLines).hasSize(2);

        // 첫 번째 노선
        final LineResponse actualLine1 = actualLines.get(0);
        assertThat(actualLine1.getId()).isEqualTo(lineId1);
        assertThat(actualLine1.getName()).isEqualTo(lineRequest.getName());
        assertThat(actualLine1.getColor()).isEqualTo(lineRequest.getColor());

        final List<String> actualStationNames1 = actualLine1.getStations()
                .stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());
        assertThat(actualStationNames1).containsExactly(upStationName, downStationName);

        // 두 번째 노선
        final LineResponse actualLine2 = actualLines.get(1);
        assertThat(actualLine2.getId()).isEqualTo(lineId2);
        assertThat(actualLine2.getName()).isEqualTo("5호선");
        assertThat(actualLine2.getColor()).isEqualTo("bg-violet-600");

        final List<String> actualStationNames2 = actualLine2.getStations()
                .stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());
        assertThat(actualStationNames2).containsExactly("왕십리역", "답십리역");
    }

    @DisplayName("id로 노선을 조회한다.")
    @Test
    void ShowLine() {
        // given
        final long id = createAndGetLineId(lineRequest);

        // when
        final ExtractableResponse<Response> actual = RestAssured.given().log().all()
                .when()
                .get(LINE_PATH_PREFIX + SLASH + id)
                .then().log().all()
                .extract();

        // then
        assertThat(actual.statusCode()).isEqualTo(HttpStatus.OK.value());

        final LineResponse actualResponse = actual.body().as(LineResponse.class);
        assertThat(actualResponse.getId()).isEqualTo(id);
        assertThat(actualResponse.getName()).isEqualTo(actualResponse.getName());
        assertThat(actualResponse.getColor()).isEqualTo(actualResponse.getColor());

        final List<StationResponse> stations = actualResponse.getStations();
        assertThat(stations).hasSize(2);

        final StationResponse upStationResponse = stations.get(0);
        assertThat(upStationResponse.getName()).isEqualTo(upStationName);

        final StationResponse downStationResponse = stations.get(1);
        assertThat(downStationResponse.getName()).isEqualTo(downStationName);
    }

    @Test
    @DisplayName("존재하지 않은 id로 조회하면 NOT_FOUND를 반환한다.")
    void ShowLine_NotExistId_NotFound() {
        // when
        final ExtractableResponse<Response> actual = RestAssured.given().log().all()
                .when()
                .get(LINE_PATH_PREFIX + SLASH + 999)
                .then().log().all()
                .extract();

        // then
        assertThat(actual.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("지하철 노선 정보를 수정한다.")
    void UpdateLine() {
        // given
        final long id = createAndGetLineId(lineRequest);

        // when
        final ExtractableResponse<Response> actual = RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(LINE_PATH_PREFIX + SLASH + id)
                .then().log().all()
                .extract();

        // then
        assertThat(actual.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("수정하려는 노선 이름이 중복되면 BAD_REQUEST를 반환한다.")
    void UpdateLine_DuplicateName_BadRequest() {
        // given
        createLine(lineRequest);
        final long upStationId2 = createAndGetStationId(new StationRequest("왕십리"));
        final long downStationId2 = createAndGetStationId(new StationRequest("답십리역"));
        final long id = createAndGetLineId(
                new LineRequest("5호선", "bg-violet-600", upStationId2, downStationId2, 7));

        // when
        final ExtractableResponse<Response> actual = RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(LINE_PATH_PREFIX + SLASH + id)
                .then().log().all()
                .extract();

        // then
        assertThat(actual.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("수정하려는 노선 id가 존재하지 않으면 404를 반환한다.")
    void UpdateLine_NotExistId_BadRequest() {
        // when
        final ExtractableResponse<Response> actual = RestAssured.given().log().all()
                .body(new LineRequest("1호선", "bg-red-600"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(LINE_PATH_PREFIX + SLASH + 999)
                .then().log().all()
                .extract();

        // then
        assertThat(actual.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("지하철 노선 정보를 삭제한다.")
    void DeleteLine() {
        // given
        final long id = createAndGetLineId(lineRequest);

        // when
        final ExtractableResponse<Response> updateResponse = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete(LINE_PATH_PREFIX + SLASH + id)
                .then().log().all()
                .extract();

        // then
        assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("존재하지 않는 노선을 제거하면 404를 반환한다.")
    void DeleteLine_NotExistId_BadRequest() {
        // when
        final ExtractableResponse<Response> updateResponse = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete(LINE_PATH_PREFIX + SLASH + 999)
                .then().log().all()
                .extract();

        // then
        assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }
}
