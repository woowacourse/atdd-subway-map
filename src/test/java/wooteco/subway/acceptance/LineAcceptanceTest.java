package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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

        final long upStationId2 = createAndGetStationId(new StationRequest("왕십리"));
        final long downStationId2 = createAndGetStationId(new StationRequest("답십리역"));
        final ExtractableResponse<Response> expected2 = createLine(
                new LineRequest("5호선", "bg-violet-600", upStationId2, downStationId2, 7));

        // when
        final ExtractableResponse<Response> actual = RestAssured.given().log().all()
                .when()
                .get(LINE_PATH_PREFIX)
                .then().log().all()
                .extract();

        // then
        final List<Long> expectedLineIds = Stream.of(expected1, expected2)
                .map(this::extractId)
                .collect(Collectors.toList());

        final List<Long> actualLineIds = actual.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());

        assertThat(actual.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actualLineIds).containsAll(expectedLineIds);
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

        final LineResponse lineResponse = actual.body().as(LineResponse.class);
        assertAll(() -> {
            assertThat(lineResponse.getId()).isEqualTo(id);
            assertThat(lineResponse.getName()).isEqualTo(lineRequest.getName());
            assertThat(lineResponse.getColor()).isEqualTo(lineRequest.getColor());
        });
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
