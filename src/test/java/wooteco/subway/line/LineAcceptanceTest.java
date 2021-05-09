package wooteco.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.dto.response.LineResponse;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static wooteco.subway.line.LineRequest.*;

@DisplayName("노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @Autowired
    private LineDao lineDao;

    @DisplayName("노선을 생성한다.")
    @Test
    void createLine() {
        // given
        Map<String, String> 분당선_Red = LineRequestForm("분당선", "bg-red-600");

        // when
        ExtractableResponse<Response> response = create(분당선_Red);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("이미 존재하는 노선의 이름으로 생성 요청 시 BAD_REQUEST를 응답한다.")
    @Test
    void createLineWhenDuplicateLineName() {
        // given
        Map<String, String> 분당선_Red = LineRequestForm("분당선", "bg-red-600");
        create(분당선_Red);

        // when
        ExtractableResponse<Response> response = create(분당선_Red);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("모든 노선을 조회한다.")
    @Test
    void getLines() {
        /// given
        Map<String, String> 분당선_Red = LineRequestForm("분당선", "bg-red-600");
        Map<String, String> 신분당선_Yellow = LineRequestForm("신분당선", "bg-yellow-600");
        ExtractableResponse<Response> 분당선생성 = create(분당선_Red);
        ExtractableResponse<Response> 신분당선생성 = create(신분당선_Yellow);

        // when
        ExtractableResponse<Response> response = getAll();
        List<Long> expectedLineIds = Stream.of(분당선생성, 신분당선생성)
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("노선 하나를 조회한다.")
    @Test
    void getLine() {
        /// given
        Map<String, String> 분당선_Red = LineRequestForm("분당선", "bg-red-600");
        ExtractableResponse<Response> 분당선생성 = create(분당선_Red);

        // when
        ExtractableResponse<Response> response = getOne("1");
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
        Map<String, String> 분당선_Red = LineRequestForm("분당선", "bg-red-600");
        Map<String, String> 신분당선_Yellow = LineRequestForm("신분당선", "bg-yellow-600");
        create(분당선_Red);

        // when
        ExtractableResponse<Response> expectedResponse = update(신분당선_Yellow, "1");
        ExtractableResponse<Response> updatedResponse = getOne("1");

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
        Map<String, String> 분당선_Red = LineRequestForm("분당선", "bg-red-600");
        Map<String, String> 신분당선_Yellow = LineRequestForm("신분당선", "bg-yellow-600");
        create(분당선_Red);
        create(신분당선_Yellow);

        // when
        ExtractableResponse<Response> response = update(신분당선_Yellow, "1");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        Map<String, String> 분당선_Red = LineRequestForm("분당선", "bg-red-600");
        ExtractableResponse<Response> 분당선생성 = create(분당선_Red);
        int originalSize = lineDao.findAll().size();

        // when
        String uri = 분당선생성.header("Location");
        ExtractableResponse<Response> response = delete(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(lineDao.findAll()).hasSize(originalSize - 1);
    }
}
