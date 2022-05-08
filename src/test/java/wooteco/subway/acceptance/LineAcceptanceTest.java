package wooteco.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dto.LineResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
@Transactional
public class LineAcceptanceTest extends AcceptanceTest {

    @Test
    @DisplayName("지하철 노선을 생성한다.")
    void createLine() {
        // given
        Map<String, String> param = new HashMap<>();
        param.put("name", "신분당선");
        param.put("color", "red");

        // when
        ExtractableResponse<Response> response = post("/lines", param);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        Map<String, String> param = new HashMap<>();
        param.put("name", "2호선");
        param.put("color", "green");

        post("/lines", param);

        // when
        ExtractableResponse<Response> response = post("/lines", param);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }


    @DisplayName("지하철 전체 노선을 조회한다.")
    @Test
    void getLines() {
        /// given
        Map<String, String> firstCreateParam = new HashMap<>();
        firstCreateParam.put("name", "신분당선");
        firstCreateParam.put("color", "red");
        ExtractableResponse<Response> firstCreateResponse = post("/lines", firstCreateParam);

        Map<String, String> secondCreateParam = new HashMap<>();
        secondCreateParam.put("name", "2호선");
        secondCreateParam.put("color", "green");
        ExtractableResponse<Response> createResponse2 = post("/lines", secondCreateParam);

        // when
        ExtractableResponse<Response> response = get("/lines");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Stream.of(firstCreateResponse, createResponse2)
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
        Map<String, String> firstCreateParam = new HashMap<>();
        firstCreateParam.put("name", "신분당선");
        firstCreateParam.put("color", "red");
        ExtractableResponse<Response> firstCreateResponse = post("/lines", firstCreateParam);

        Map<String, String> secondCreateParam = new HashMap<>();
        secondCreateParam.put("name", "2호선");
        secondCreateParam.put("color", "green");
        post("/lines", secondCreateParam);

        Long expectedId = Long.parseLong(firstCreateResponse.header("Location").split("/")[2]);

        // when
        ExtractableResponse<Response> response = get("/lines/" + expectedId);
        Long resultId = response.jsonPath().getLong("id");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(resultId).isEqualTo(expectedId);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        Map<String, String> param = new HashMap<>();
        param.put("name", "신분당선");
        param.put("color", "red");
        ExtractableResponse<Response> createResponse = post("/lines", param);

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = delete(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(200);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        Map<String, String> postParam = new HashMap<>();
        postParam.put("name", "신분당선");
        postParam.put("color", "red");
        ExtractableResponse<Response> createResponse = post("/lines", postParam);

        // when
        String uri = createResponse.header("Location");
        Map<String, String> putParam = new HashMap<>();
        putParam.put("name", "2호선");
        putParam.put("color", "green");
        ExtractableResponse<Response> updateResponse = update(uri, putParam);

        // then
        ExtractableResponse<Response> findResponse = get(uri);
        String name = findResponse.jsonPath().getString("name");
        String color = findResponse.jsonPath().getString("color");

        assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(name).isEqualTo("2호선");
        assertThat(color).isEqualTo("green");
    }

    @DisplayName("기존에 존재하는 지하철 노선명으로 지하철 노선명을 수정한다.")
    @Test
    void updateLineWithDuplicateName() {
        /// given
        Map<String, String> existParam = new HashMap<>();
        existParam.put("name", "신분당선");
        existParam.put("color", "red");
        post("/lines", existParam);

        Map<String, String> alreadyExistParam = new HashMap<>();
        alreadyExistParam.put("name", "2호선");
        alreadyExistParam.put("color", "green");
        ExtractableResponse<Response> alreadyExistResponse = post("/lines", alreadyExistParam);

        // when
        String uri = alreadyExistResponse.header("Location");
        ExtractableResponse<Response> updateResponse = update(uri, existParam);

        // then
        assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
