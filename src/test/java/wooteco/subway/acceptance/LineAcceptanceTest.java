package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

@Sql("/lineInitSchema.sql")
public class LineAcceptanceTest extends AcceptanceTest {

    private LineRequest createLineRequest(String name, String color) {
        return new LineRequest(name, color, 1L, 2L, 5);
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLineTest() {
        // given
        LineRequest lineRequest = createLineRequest("신분당선", "red");

        // when
        ExtractableResponse<Response> response = requestHttpPost(lineRequest, "/lines");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 정보로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicateInfoTest() {
        // given
        LineRequest lineRequest = createLineRequest("신분당선", "red");
        requestHttpPost(lineRequest, "/lines");

        // when
        ExtractableResponse<Response> response = requestHttpPost(lineRequest, "/lines");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getStations() {
        /// given
        LineRequest lineRequest1 = createLineRequest("신분당선", "red");
        ExtractableResponse<Response> createResponse1 = requestHttpPost(lineRequest1, "/lines");

        LineRequest lineRequest2 = createLineRequest("분당선", "green");
        ExtractableResponse<Response> createResponse2 = requestHttpPost(lineRequest2, "/lines");

        // when
        ExtractableResponse<Response> response = requestHttpGet("/lines");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        List<Long> expectedLineIds = extractLineIdsWithUri(createResponse1, createResponse2);
        List<Long> resultLineIds = extractLineIdsWithJson(response);
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    private List<Long> extractLineIdsWithUri(ExtractableResponse<Response> createResponse1,
                                             ExtractableResponse<Response> createResponse2) {
        return Stream.of(createResponse1, createResponse2)
            .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
            .collect(Collectors.toList());
    }

    private List<Long> extractLineIdsWithJson(ExtractableResponse<Response> response) {
        return response.jsonPath().getList(".", LineResponse.class).stream()
            .map(LineResponse::getId)
            .collect(Collectors.toList());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteStation() {
        // given
        LineRequest lineRequest = createLineRequest("신분당선", "red");
        ExtractableResponse<Response> createResponse = requestHttpPost(lineRequest, "/lines");

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = requestHttpDelete(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("아이디를 받아 지하철 노선을 단 건 조회한다.")
    @Test
    void getStationByIdTest() {
        /// given
        LineRequest lineRequest = createLineRequest("분당선", "red");
        ExtractableResponse<Response> createResponse = requestHttpPost(lineRequest, "/lines");

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = requestHttpGet(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        Long expectedLineId = Long.parseLong(createResponse.header("Location").split("/")[2]);
        Long resultLineId = response.jsonPath().getObject(".", LineResponse.class).getId();
        assertThat(resultLineId).isEqualTo(expectedLineId);
    }

    @DisplayName("지하철 노선을 업데이트한다.")
    @Test
    void updateLine() {
        /// given
        LineRequest lineRequest = createLineRequest("신분당선", "red");
        ExtractableResponse<Response> createResponse = requestHttpPost(lineRequest, "/lines");

        // when, then
        LineRequest newLineRequest = createLineRequest("분당선", "green");

        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response =
            requestHttpPut(newLineRequest, uri);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
