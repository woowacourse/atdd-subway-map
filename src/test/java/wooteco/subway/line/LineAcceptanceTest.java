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
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.TestUtils;
import wooteco.subway.line.controller.dto.LineRequest;
import wooteco.subway.line.controller.dto.LineResponse;
import wooteco.subway.section.controller.dto.SectionRequest;
import wooteco.subway.station.controller.dto.StationResponse;

@DisplayName("지하철 노선 관련 기능")
@Sql("classpath:stationInit.sql")
class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선 등록 성공")
    @Test
    void createLine() {
        // given
        final LineRequest lineTwoRequest = TestUtils.LINE_TWO_REQUEST;

        // when
        final ExtractableResponse<Response> lineTwoResponse = TestUtils.postLine(lineTwoRequest);

        // then
        final LineResponse lineResponse = lineTwoResponse.body().as(LineResponse.class);
        assertThat(lineTwoResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(lineResponse.getId()).isEqualTo(1L);
        assertThat(lineResponse.getName()).isEqualTo(lineTwoRequest.getName());
        assertThat(lineResponse.getColor()).isEqualTo(lineTwoRequest.getColor());
    }

    @DisplayName("지하철 노선 등록 실패 - 선택된 역 존재하지 않음")
    @Test
    void createLine_fail_oneStationOnly() {
        // given
        final LineRequest lineTwoRequest = TestUtils.LINE_TWO_REQUEST;
        TestUtils.postLine(lineTwoRequest);

        // when
        final ExtractableResponse<Response> lineTwoResponse = TestUtils.postLine(lineTwoRequest);

        // then
        assertThat(lineTwoResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 등록 실패 - 중복된 노선 존재")
    @Test
    void createLine_fail_duplicateName() {
        // given
        final LineRequest lineTwoRequest = TestUtils.LINE_TWO_REQUEST;
        TestUtils.postLine(lineTwoRequest);

        // when
        final ExtractableResponse<Response> lineTwoResponse = TestUtils.postLine(lineTwoRequest);

        // then
        assertThat(lineTwoResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선 조회 성공")
    @Test
    void showLine() {
        // given
        final LineRequest lineTwoRequest = TestUtils.LINE_TWO_REQUEST;
        final LineResponse lineTwoResponse = TestUtils.postLine(lineTwoRequest)
                .as(LineResponse.class);
        final Long lineId = lineTwoResponse.getId();

        final SectionRequest yangjaeSectionRequest = TestUtils.STATION_ONE_TO_THREE_SECTION_REQUEST;
        TestUtils.postSection(lineId, yangjaeSectionRequest);

        // when
        ExtractableResponse<Response> getLineResponse = TestUtils.getLine(lineId);

        // then
        final LineResponse responseWithStations = getLineResponse.as(LineResponse.class);
        final List<String> stationNames = responseWithStations.getStations()
                .stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());
        final List<String> expectedStationNames = Arrays.asList(
                TestUtils.JAMSIL_STATION_REQUEST.getName(),
                TestUtils.YANGJAE_STATION_REQUEST.getName(),
                TestUtils.GANGNAM_STATION_REQUEST.getName()
        );
        assertThat(stationNames).containsAll(expectedStationNames);
    }

    @DisplayName("노선 목록 조회 성공")
    @Test
    void showLines() {
        // given
        final LineRequest lineTwoRequest = TestUtils.LINE_TWO_REQUEST;
        final ExtractableResponse<Response> lineTwoResponse = TestUtils.postLine(lineTwoRequest);

        final LineRequest lineNewBundangRequest = TestUtils.LINE_NEW_BUNDANG_REQUEST;
        final ExtractableResponse<Response> lineNewBundangResponse = TestUtils.postLine(lineNewBundangRequest);

        // when
        final ExtractableResponse<Response> response = TestUtils.getLines();

        // then
        final List<Long> resultLineIds = resultLineIds(response);
        final List<Long> expectedLineIds = Arrays.asList(lineId(lineTwoResponse), lineId(lineNewBundangResponse));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선 이름 변경 성공")
    @Test
    void updateLine() {
        // given
        final LineRequest lineTwoRequest = TestUtils.LINE_TWO_REQUEST;
        final ExtractableResponse<Response> lineTwoResponse = TestUtils.postLine(lineTwoRequest);

        // when
        final String uri = lineTwoResponse.header("Location");
        final LineRequest updateRequest = new LineRequest(
                "분당선",
                "bg-yellow-600"
        );
        final ExtractableResponse<Response> updateResponse = TestUtils.updateLine(uri, updateRequest);

        // then
        final Long lineId = lineId(lineTwoResponse);
        final ExtractableResponse<Response> showLineResponse = TestUtils.getLine(lineId);
        final LineResponse showLineResult = showLineResponse.body().as(LineResponse.class);

        assertAll(
                () -> assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.OK.value()),
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
        final LineRequest lineTwoRequest = TestUtils.LINE_TWO_REQUEST;
        final ExtractableResponse<Response> lineTwoResponse = TestUtils.postLine(lineTwoRequest);

        // when
        final String uri = lineTwoResponse.header("Location");
        final ExtractableResponse<Response> deleteLineResponse = TestUtils.deleteLine(uri);

        // then
        assertThat(deleteLineResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
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
}
