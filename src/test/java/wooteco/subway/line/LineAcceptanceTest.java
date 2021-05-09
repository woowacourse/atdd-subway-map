package wooteco.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.dto.request.LineCreateRequest;
import wooteco.subway.line.dto.request.LineUpdateRequest;
import wooteco.subway.line.dto.response.LineResponse;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static wooteco.subway.line.LineRequestForm.*;

@DisplayName("노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @Autowired
    private LineDao lineDao;

    @DisplayName("노선을 생성한다.")
    @Test
    void createLine() {
        // given
        LineCreateRequest 분당선_RED = new LineCreateRequest("분당선", "bg-red-600");

        // when
        ExtractableResponse<Response> response = createRequest(분당선_RED);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("이미 존재하는 노선의 이름으로 생성 요청 시 BAD_REQUEST를 응답한다.")
    @Test
    void createLineWhenDuplicateLineName() {
        // given
        LineCreateRequest 분당선_RED = new LineCreateRequest("분당선", "bg-red-600");
        createRequest(분당선_RED);

        // when
        ExtractableResponse<Response> response = createRequest(분당선_RED);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("모든 노선을 조회한다.")
    @Test
    void getLines() {
        /// given
        LineCreateRequest 분당선_RED = new LineCreateRequest("분당선", "bg-red-600");
        LineCreateRequest 신분당선_YELLOW = new LineCreateRequest("신분당선", "bg-yellow-600");
        ExtractableResponse<Response> 분당선생성 = createRequest(분당선_RED);
        ExtractableResponse<Response> 신분당선생성 = createRequest(신분당선_YELLOW);

        // when
        ExtractableResponse<Response> response = findAllRequest();
        List<Long> expectedLineIds = expectedLineIdsList(Arrays.asList(분당선생성, 신분당선생성));
        List<Long> resultLineIds = resultLineIdsList(response);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    private List<Long> expectedLineIdsList(List<ExtractableResponse<Response>> lines) {
        return Stream.of(lines.get(0), lines.get(1))
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
    }

    private List<Long> resultLineIdsList(ExtractableResponse<Response> response) {
        return response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
    }

    @DisplayName("노선 하나를 조회한다.")
    @Test
    void getLine() {
        /// given
        LineCreateRequest 분당선_RED = new LineCreateRequest("분당선", "bg-red-600");
        ExtractableResponse<Response> 분당선생성 = createRequest(분당선_RED);

        // when
        ExtractableResponse<Response> response = findByIdRequest("1");
        Long expectedLineId = Long.parseLong(분당선생성.header("Location").split("/")[2]);
        Long resultLineId = response.as(LineResponse.class).getId();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(resultLineId).isEqualTo(expectedLineId);
    }

    @DisplayName("노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        LineCreateRequest 분당선_RED = new LineCreateRequest("분당선", "bg-red-600");
        LineUpdateRequest 신분당선_YELLOW = new LineUpdateRequest("신분당선", "bg-yellow-600");
        createRequest(분당선_RED);

        // when
        ExtractableResponse<Response> expectedResponse = updateRequest("1", 신분당선_YELLOW);
        ExtractableResponse<Response> updatedResponse = findByIdRequest("1");

        // then
        assertThat(expectedResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(updatedResponse.body().jsonPath().getString("name"))
                .isEqualTo("신분당선");
        assertThat(updatedResponse.body().jsonPath().getString("color"))
                .isEqualTo("bg-yellow-600");
    }

    @DisplayName("이미 존재하는 이름으로 수정 시 BAD_REQUEST를 응답한다.")
    @Test
    void updateLineWhenDuplicateName() {
        // given
        LineCreateRequest 분당선_RED = new LineCreateRequest("분당선", "bg-red-600");
        LineCreateRequest 신분당선_YELLOW = new LineCreateRequest("신분당선", "bg-yellow-600");
        createRequest(분당선_RED);
        createRequest(신분당선_YELLOW);

        // when
        ExtractableResponse<Response> response = updateRequest("1", new LineUpdateRequest("신분당선", "bg-yellow-600"));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        LineCreateRequest 분당선_RED = new LineCreateRequest("분당선", "bg-red-600");
        ExtractableResponse<Response> 분당선생성 = createRequest(분당선_RED);
        int originalSize = lineDao.findAll().size();

        // when
        String uri = 분당선생성.header("Location");
        ExtractableResponse<Response> response = deleteRequest(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(lineDao.findAll()).hasSize(originalSize - 1);
    }
}
