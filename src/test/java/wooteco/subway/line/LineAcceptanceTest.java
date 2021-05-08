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
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.line.repository.LineRepository;

import java.util.List;
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
        LineRequest 분당선 =
                new LineRequest("분당선", "bg-red-600", null, null, 0);


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
        LineRequest 분당선 =
                new LineRequest("분당선", "bg-red-600", null, null, 0);

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
        LineRequest 분당선 =
                new LineRequest("분당선", "bg-red-600", null, null, 0);
        ExtractableResponse<Response> createResponse1 = createPostResponse(분당선);

        LineRequest 신분당선 =
                new LineRequest("신분당선", "bg-red-600", null, null, 0);
        ExtractableResponse<Response> createResponse2 = createPostResponse(신분당선);

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
        LineRequest 분당선 =
                new LineRequest("분당선", "bg-red-600", null, null, 0);
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
        LineRequest 이호선 =
                new LineRequest("이호선", "bg-blue-600", null, null, 0);

        LineRequest 신분당선 =
                new LineRequest("신분당선", "bg-red-600", null, null, 0);

        createPostResponse(이호선);

        // when
        ExtractableResponse<Response> expectedResponse = createPutResponse("/lines/1", 신분당선);
        ExtractableResponse<Response> updatedResponse = createGetResponse("/lines/1");

        // then
        assertThat(expectedResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(updatedResponse.body().as(LineResponse.class).getName())
                .isEqualTo("신분당선");
        assertThat(updatedResponse.body().as(LineResponse.class).getColor())
                .isEqualTo("bg-red-600");
    }

    @DisplayName("이미 존재하는 이름으로 수정 시 BAD_REQUEST를 응답한다.")
    @Test
    void updateLineWhenDuplicateName() {
        // given
        LineRequest 이호선 =
                new LineRequest("이호선", "bg-blue-600", null, null, 0);

        LineRequest 신분당선 =
                new LineRequest("신분당선", "bg-red-600", null, null, 0);

        createPostResponse(이호선);
        createPostResponse(신분당선);

        // when
        ExtractableResponse<Response> expectedResponse = createPutResponse("/lines/1", 신분당선);

        // then
        assertThat(expectedResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        LineRequest 분당선 =
                new LineRequest("분당선", "bg-red-600", null, null, 0);

        ExtractableResponse<Response> createResponse = createPostResponse(분당선);
        int originalSize = lineRepository.findAll().size();

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = createDeleteResponse(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(lineRepository.findAll()).hasSize(originalSize - 1);
    }

    private ExtractableResponse<Response> createPostResponse(LineRequest lineRequest) {
        return RestAssured.given().log().all()
                .body(lineRequest)
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

    private ExtractableResponse<Response> createPutResponse(String path, LineRequest params) {
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
