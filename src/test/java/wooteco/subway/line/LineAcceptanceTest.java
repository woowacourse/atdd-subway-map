package wooteco.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.TestUtils;
import wooteco.subway.line.controller.dto.LineRequest;
import wooteco.subway.line.controller.dto.LineResponse;
import wooteco.subway.station.controller.dto.StationRequest;

@DisplayName("지하철 노선 관련 기능")
class LineAcceptanceTest extends AcceptanceTest {

    private static final Long firstStationId = 1L;
    private static final Long secondStationId = 2L;
    private static final Long thirdStationId = 3L;

    @DisplayName("지하철 노선 등록 성공")
    @Test
    void createLine() {
        // given
        createTwoStations();
        final LineRequest lineRequest = createLineRequest();

        // when
        final ExtractableResponse<Response> response = TestUtils.postLine(lineRequest);

        // then
        final LineResponse lineResponse = response.body().as(LineResponse.class);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(lineResponse.getId()).isEqualTo(1L);
        assertThat(lineResponse.getName()).isEqualTo(lineRequest.getName());
        assertThat(lineResponse.getColor()).isEqualTo(lineRequest.getColor());
    }

    @DisplayName("지하철 노선 등록 실패 - 중복된 노선 존재")
    @Test
    void createLineWithDuplicateName() {
        // given
        createTwoStations();
        final LineRequest lineRequest = createLineRequest();
        TestUtils.postLine(lineRequest);

        // when
        final ExtractableResponse<Response> response = TestUtils.postLine(lineRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선 목록 조회 성공")
    @Test
    void showLines() {
        // given
        createThreeStations();
        final LineRequest lineRequest1 = createLineRequest();
        final ExtractableResponse<Response> createResponse1 = TestUtils.postLine(lineRequest1);

        final LineRequest lineRequest2 = new LineRequest(
                "2호선",
                "bg-green-600",
                secondStationId,
                thirdStationId,
                10
        );
        final ExtractableResponse<Response> createResponse2 = TestUtils.postLine(lineRequest2);

        // when
        final ExtractableResponse<Response> response = TestUtils.getLines();

        // then
        final List<Long> resultLineIds = resultLineIds(response);
        final List<Long> expectedLineIds = Arrays.asList(lineId(createResponse1), lineId(createResponse2));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선 이름 변경 성공")
    @Test
    void updateLine() {
        // given
        createTwoStations();
        final LineRequest lineRequest = createLineRequest();
        final ExtractableResponse<Response> createResponse = TestUtils.postLine(lineRequest);

        // when
        final String uri = createResponse.header("Location");
        final LineRequest updateRequest = new LineRequest(
                "분당선",
                "bg-red-600"
                );
        final ExtractableResponse<Response> response = TestUtils.updateLine(uri, updateRequest);

        // then
        final Long lineId = lineId(createResponse);
        final ExtractableResponse<Response> showLineResponse = TestUtils.getLine(lineId);
        final LineResponse showLineResult = showLineResponse.body().as(LineResponse.class);

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(showLineResult.getId()).isEqualTo(lineId),
                () -> assertThat(showLineResult.getName()).isEqualTo(updateRequest.getName()),
                () -> assertThat(showLineResult.getColor()).isEqualTo(updateRequest.getColor()),
                () -> assertThat(showLineResult.getStations().size()).isEqualTo(2)
        );
    }

    @DisplayName("지하철 노선 제거 성공")
    @Test
    void deleteLine() {
        // given
        createTwoStations();
        final LineRequest lineRequest = createLineRequest();
        final ExtractableResponse<Response> createResponse = TestUtils.postLine(lineRequest);

        // when
        final String uri = createResponse.header("Location");
        final ExtractableResponse<Response> response = TestUtils.deleteLine(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private List<Long> resultLineIds(final ExtractableResponse<Response> response) {
        final JsonPath jsonPath = response.jsonPath();
        final List<LineResponse> lineResponses = jsonPath.getList(".", LineResponse.class);

        return lineResponses.stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
    }

    private Long lineId(final ExtractableResponse<Response> response) {
        return Long.parseLong(response.header("Location").split("/")[2]);
    }

    private LineRequest createLineRequest() {
        return new LineRequest(
                "신분당선",
                "bg-red-600",
                firstStationId,
                secondStationId,
                10
        );
    }

    private void createOneStation() {
        final StationRequest suwonStationRequest = new StationRequest("수원역");
        TestUtils.postStation(suwonStationRequest);
    }

    private void createTwoStations() {
        final StationRequest suwonStationRequest = new StationRequest("수원역");
        final StationRequest gangnamStationRequest = new StationRequest("강남역");
        TestUtils.postStation(suwonStationRequest);
        TestUtils.postStation(gangnamStationRequest);
    }

    private void createThreeStations() {
        final StationRequest suwonStationRequest = new StationRequest("수원역");
        final StationRequest gangnamStationRequest = new StationRequest("강남역");
        final StationRequest bundangStationRequest = new StationRequest("분당역");
        TestUtils.postStation(suwonStationRequest);
        TestUtils.postStation(gangnamStationRequest);
        TestUtils.postStation(bundangStationRequest);
    }
}
