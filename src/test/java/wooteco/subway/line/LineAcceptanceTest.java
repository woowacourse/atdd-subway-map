package wooteco.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.line.repository.LineRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("노선 관련 기능")
@Transactional
public class LineAcceptanceTest extends AcceptanceTest {

    @Autowired
    private LineRepository lineRepository;

    @DisplayName("노선을 생성한다.")
    @Test
    void createLine() {
        // given
        Map<String, String> 분당선 = new HashMap<>();
        분당선.put("name", "분당선");
        분당선.put("color", "bg-red-600");

        // when
        ExtractableResponse<Response> response = createPostResponse(분당선);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("이미 존재하는 노선의 이름으로 생성 요청 시 BAD_REQUEST를 응답한다.")
    @Test
    void createLineWhenDuplicateLineName() {
        // given
        Map<String, String> 분당선 = new HashMap<>();
        분당선.put("name", "분당선");
        분당선.put("color", "bg-red-600");

        // when
        createPostResponse(분당선);

        ExtractableResponse<Response> response = createPostResponse(분당선);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("모든 노선을 조회한다.")
    @Test
    void getLines() {
        /// given
        Map<String, String> 분당선 = new HashMap<>();
        분당선.put("name", "분당선");
        분당선.put("color", "bg-red-600");
        ExtractableResponse<Response> createResponse1 = createPostResponse(분당선);

        Map<String, String> 역삼역 = new HashMap<>();
        역삼역.put("name", "역삼역");
        역삼역.put("color", "bg-red-600");
        ExtractableResponse<Response> createResponse2 = createPostResponse(역삼역);

        // when
        ExtractableResponse<Response> response = createGetResponse("/lines");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());

        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("노선 하나를 조회한다.")
    @Test
    void getLine() {
        /// given
        Map<String, String> 분당선 = new HashMap<>();
        분당선.put("name", "분당선");
        분당선.put("color", "bg-red-600");
        ExtractableResponse<Response> createResponse1 = createPostResponse(분당선);

        // when
        ExtractableResponse<Response> response = createGetResponse("/lines/1");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        Long expectedLineId = Long.parseLong(createResponse1.header("Location").split("/")[2]);
        Long resultLineId = response.as(LineResponse.class).getId();

        assertThat(resultLineId).isEqualTo(expectedLineId);
    }

    @DisplayName("노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        Map<String, String> 이호선 = new HashMap<>();
        이호선.put("name", "2호선");
        이호선.put("color", "bg-blue-600");

        Map<String, String> 신분당선 = new HashMap<>();
        신분당선.put("name", "신분당");
        신분당선.put("color", "bg-red-600");

        createPostResponse(이호선);

        // when
        ExtractableResponse<Response> expectedResponse = createPutResponse(신분당선, "/lines/1");
        ExtractableResponse<Response> updatedResponse = createGetResponse("/lines/1");

        // then
        assertThat(expectedResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(updatedResponse.body().jsonPath().getString("name"))
                .isEqualTo("신분당");
        assertThat(updatedResponse.body().jsonPath().getString("color"))
                .isEqualTo("bg-red-600");
    }

    @DisplayName("이미 존재하는 이름으로 수정 시 BAD_REQUEST를 응답한다.")
    @Test
    void updateLineWhenDuplicateName() {
        // given
        Map<String, String> 이호선 = new HashMap<>();
        이호선.put("name", "2호선");
        이호선.put("color", "bg-blue-600");

        Map<String, String> 신분당선 = new HashMap<>();
        신분당선.put("name", "신분당선");
        신분당선.put("color", "bg-blue-600");

        createPostResponse(이호선);
        createPostResponse(신분당선);

        // when
        ExtractableResponse<Response> expectedResponse = createPutResponse(신분당선, "/lines/1");

        // then
        assertThat(expectedResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        Map<String, String> 분당선 = new HashMap<>();
        분당선.put("name", "분당선");
        분당선.put("color", "bg-red-600");

        ExtractableResponse<Response> createResponse = createPostResponse(분당선);
        int originalSize = lineRepository.findAll().size();

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = createDeleteResponse(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(lineRepository.findAll()).hasSize(originalSize - 1);
    }

    private ExtractableResponse<Response> createPostResponse(Map<String, String> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> createGetResponse(String path) {
        return RestAssured.given().log().all()
                .when()
                .get(path)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> createPutResponse(Map<String, String> params, String path) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(path)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> createDeleteResponse(String uri) {
        return RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();
    }
}
