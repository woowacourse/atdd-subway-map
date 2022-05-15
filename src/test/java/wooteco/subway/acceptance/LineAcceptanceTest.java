package wooteco.subway.acceptance;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

@DisplayName("노선 관련 기능")
@Sql("classpath:setUp_test_db.sql")
class LineAcceptanceTest extends AcceptanceTest {

    @Test
    @DisplayName("노선을 등록한다.")
    void createLine() {
        // given
        LineRequest 신분당선_param = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);

        // when
        ExtractableResponse<Response> response = httpPost("/lines", 신분당선_param);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.header("Location")).isNotBlank(),
                () -> assertThat(response.body().jsonPath().getString("name")).isEqualTo("신분당선"),
                () -> assertThat(response.body().jsonPath().getList("stations", LineResponse.class))
                        .extracting("name")
                        .containsExactly("강남역", "역삼역")
        );
    }

    @Test
    @DisplayName("기존에 존재하는 노선 이름으로 노선을 생성한다.")
    void createLineWithDuplicateName() {
        // given
        LineRequest 신분당선_param = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);
        httpPost("/lines", 신분당선_param);

        // when
        ExtractableResponse<Response> response = httpPost("lines", 신분당선_param);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("노선 전체를 조회한다.")
    void getLines() {
        // given
        LineRequest 신분당선_param = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);
        ExtractableResponse<Response> 신분당선_response = httpPost("/lines", 신분당선_param);

        LineRequest 분당선_param = new LineRequest("분당선", "bg-green-600", 1L, 2L, 10);
        ExtractableResponse<Response> 분당선_response = httpPost("/lines", 분당선_param);

        // when
        ExtractableResponse<Response> response = httpGet("/lines");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Stream.of(신분당선_response, 분당선_response)
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @Test
    @DisplayName("노선을 조회한다.")
    void getLine() {
        // given
        LineRequest 신분당선_param = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);
        ExtractableResponse<Response> 신분당선_response = httpPost("/lines", 신분당선_param);

        String savedId = 신분당선_response.header("Location").split("/")[2];

        // when
        ExtractableResponse<Response> 신분당선_조회_response = httpGet("/lines/" + savedId);

        // then
        Long findId = 신분당선_조회_response.response().jsonPath().getLong("id");
        assertAll(
                () -> assertThat(신분당선_조회_response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(Long.valueOf(savedId)).isEqualTo(findId)
        );
    }

    @Test
    @DisplayName("기존 노선의 이름과 색상을 변경할 수 있다.")
    void updateById() {
        // given
        LineRequest 신분당선_param = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);
        ExtractableResponse<Response> 신분당선_response = httpPost("lines", 신분당선_param);

        String savedId = 신분당선_response.header("Location").split("/")[2];

        // when
        LineRequest 다른분당선_param = new LineRequest("다른분당선", "bg-red-600", 1L, 2L, 10);
        ExtractableResponse<Response> response = httpPut("/lines/" + savedId, 다른분당선_param);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("기존 노선을 삭제할 수 있다.")
    void deleteById() {
        // given
        LineRequest 신분당선_param = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);
        ExtractableResponse<Response> 신분당선_response = httpPost("/lines", 신분당선_param);

        String savedId = 신분당선_response.header("Location").split("/")[2];

        // when
        ExtractableResponse<Response> response = httpDelete("/lines/" + savedId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
