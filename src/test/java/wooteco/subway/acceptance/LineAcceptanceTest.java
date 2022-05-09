package wooteco.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.http.HttpStatus;
import wooteco.subway.dto.LineResponse;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {


    @DisplayName("노선 생성을 관리한다")
    @TestFactory
    Stream<DynamicTest> dynamicTestsFromCollection() {
        return Stream.of(
                dynamicTest("새로운 노선 이름으로 노선을 생성한다.", () -> {
                    // when
                    ExtractableResponse<Response> response = createLine("2호선", "green");

                    // then
                    assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
                    assertThat(response.header("Location")).isNotBlank();
                }),

                dynamicTest("기존의 노선 이름으로 노선을 생성한다", () -> {
                    // when
                    ExtractableResponse<Response> response = createLine("2호선", "green");

                    // then
                    assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                })
        );
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        /// given
        ExtractableResponse<Response> createResponse1 = createLine("2호선", "green");
        ExtractableResponse<Response> createResponse2 = createLine("3호선", "orange");

        // when
        ExtractableResponse<Response> response = get("/lines");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2)
                .stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class)
                .stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        /// given
        ExtractableResponse<Response> createResponse = createLine("2호선", "green");

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
        //given
        ExtractableResponse<Response> response = createLine("2호선", "green");
        long savedLineId = Long.parseLong(response.header("Location").split("/")[2]);

        //when
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
        ExtractableResponse<Response> createResponse = createLine("2호선", "green");

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
        ExtractableResponse<Response> createResponse = createLine("2호선", "green");
        String uri = createResponse.header("Location");
        delete(uri);

        // when
        ExtractableResponse<Response> response = delete(uri);
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> createLine(String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);

        return post("/lines", params);
    }
}
