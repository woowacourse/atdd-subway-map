package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.LineResponse;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "2호선");
        params.put("color", "green");

        // when
        ExtractableResponse<Response> response = post("/lines", params);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 노선의 이름으로 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "2호선");
        params.put("color", "green");

        post("/lines", params);

        // when
        ExtractableResponse<Response> response = post("/lines", params);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        /// given
        Map<String, String> params1 = new HashMap<>();
        params1.put("name", "2호선");
        params1.put("color", "green");
        ExtractableResponse<Response> createResponse1 = post("/lines", params1);

        Map<String, String> params2 = new HashMap<>();
        params2.put("name", "3호선");
        params2.put("color", "orange");
        ExtractableResponse<Response> createResponse2 = post("/lines", params2);

        // when
        ExtractableResponse<Response> response = get("/lines");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        /// given
        Map<String, String> params = new HashMap<>();
        params.put("name", "2호선");
        params.put("color", "green");
        ExtractableResponse<Response> createResponse = post("/lines", params);

        // when
        long expectedLineId = Long.parseLong(createResponse.header("Location").split("/")[2]);

        ExtractableResponse<Response> response = get("/lines/" + expectedLineId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        Long resultLineId = response.jsonPath().getObject(".", LineResponse.class).getId();
        assertThat(resultLineId).isEqualTo(expectedLineId);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "2호선");
        params.put("color", "green");

        // when
        ExtractableResponse<Response> response = post("/lines", params);

        long savedLineId = Long.parseLong(response.header("Location").split("/")[2]);

        Map<String, String> updateParams = new HashMap<>();
        updateParams.put("name", "3호선");
        updateParams.put("color", "orange");

        ExtractableResponse<Response> updateResponse = put("/lines/" + savedLineId, updateParams);

        // then
        assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "2호선");
        params.put("color", "green");
        ExtractableResponse<Response> createResponse = post("/lines", params);

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = delete(uri);
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("제거할 지하철 노선이 없는 경우 예외가 발생한다.")
    @Test
    void deleteNotExistLine() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "2호선");
        params.put("color", "green");
        ExtractableResponse<Response> createResponse = post("/lines", params);

        String uri = createResponse.header("Location");
        delete(uri);

        // when
        ExtractableResponse<Response> response = delete(uri);
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
