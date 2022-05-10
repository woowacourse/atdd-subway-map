package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static wooteco.subway.acceptance.TestFixtures.extractDeleteResponse;
import static wooteco.subway.acceptance.TestFixtures.extractGetResponse;
import static wooteco.subway.acceptance.TestFixtures.extractPostResponse;
import static wooteco.subway.acceptance.TestFixtures.extractPutResponse;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLineTest() {
        //given, when
        LineRequest lineRequest = new LineRequest(
                "신분당선", "red", 1L, 2L, 0);
        ExtractableResponse<Response> response = extractPostResponse(lineRequest, "/lines");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }


    @DisplayName("기존에 존재하는 지하철 노선 정보로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicateInfoTest() {
        // given
        LineRequest lineRequest = new LineRequest(
                "신분당선", "red", 1L, 2L, 0);
        ExtractableResponse<Response> response = extractPostResponse(lineRequest, "/lines");

        // when
        ExtractableResponse<Response> repeatedResponse = extractPostResponse(lineRequest, "/lines");

        // then
        assertThat(repeatedResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getStations() {
        /// given
        LineRequest lineRequest1 = new LineRequest(
                "신분당선", "red", 1L, 2L, 0);
        ExtractableResponse<Response> createResponse1 = extractPostResponse(lineRequest1, "/lines");

        LineRequest lineRequest2 = new LineRequest(
                "분당선", "green", 1L, 2L, 0);
        ExtractableResponse<Response> createResponse2 = extractPostResponse(lineRequest2, "/lines");

        // when
        ExtractableResponse<Response> response = extractGetResponse("/lines");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteStation() {
        // given
        LineRequest lineRequest = new LineRequest(
                "신분당선", "red", 1L, 2L, 0);
        ExtractableResponse<Response> createResponse = extractPostResponse(lineRequest, "/lines");

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = extractDeleteResponse(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("아이디를 받아 지하철 노선을 단 건 조회한다.")
    @Test
    void getStationByIdTest() {
        // given
        LineRequest lineRequest = new LineRequest(
                "신분당선", "red", 1L, 2L, 0);
        ExtractableResponse<Response> createResponse = extractPostResponse(lineRequest, "/lines");

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = extractGetResponse(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        Long expectedLineId = Long.parseLong(createResponse.header("Location").split("/")[2]);
        Long resultLineId = response.jsonPath().getObject(".", LineResponse.class).getId();

        assertThat(resultLineId).isEqualTo(expectedLineId);
    }

    @DisplayName("지하철 노선을 업데이트한다.")
    @Test
    void updateLine() {
        // given
        LineRequest lineRequest = new LineRequest(
                "신분당선", "red", 1L, 2L, 0);
        ExtractableResponse<Response> createResponse = extractPostResponse(lineRequest, "/lines");

        // when, then
        LineRequest updateLineRequest = new LineRequest(
                "분당선", "green", 1L, 2L, 0);

        String uri = createResponse.header("Location");
        ExtractableResponse<Response> updateResponse = extractPutResponse(updateLineRequest, uri);
        ExtractableResponse<Response> getResponse = extractGetResponse(uri);

        assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(getResponse.jsonPath().getObject(".", LineResponse.class))
                .extracting("name", "color")
                .containsExactly("분당선", "green");
    }
}
