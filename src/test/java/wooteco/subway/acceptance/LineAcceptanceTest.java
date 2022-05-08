package wooteco.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import wooteco.subway.dto.LineResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("노선 관련 기능")
class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("라인을 등록한다.")
    @Test
    void createLine() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "rgb-red-600");

        // when
        final ExtractableResponse<Response> response = AcceptanceTestFixture.post("/lines", params);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.header("Location")).isNotBlank(),
                () -> assertThat(response.body().jsonPath().getLong("id")).isNotNull(),
                () -> assertThat(response.body().jsonPath().getString("name")).isEqualTo("신분당선"),
                () -> assertThat(response.body().jsonPath().getString("color")).isEqualTo("rgb-red-600")
        );
    }

    @DisplayName("라인을 등록 할 때 입력값이 잘못되면 예외를 발생한다.")
    @ParameterizedTest
    @MethodSource("badLineRequest")
    void createLineWithBadInput(String name, String color, String errorMessage) {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);

        // when
        final ExtractableResponse<Response> response = AcceptanceTestFixture.post("/lines", params);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response.body().jsonPath().getString("message")).isEqualTo(errorMessage)
        );
    }

    private static Stream<Arguments> badLineRequest() {
        return Stream.of(
                Arguments.of(new String(new char[256]), new String(new char[20]), "이름은 255자를 초과할 수 없습니다."),
                Arguments.of(new String(new char[255]), new String(new char[21]), "색은 20자를 초과할 수 없습니다."),
                Arguments.of(null, new String(new char[21]), "이름은 비어있을 수 없습니다."),
                Arguments.of(new String(new char[255]), "", "색은 비어있을 수 없습니다.")
        );
    }

    @DisplayName("기존에 존재하는 노선 이름으로 노선을 생성하면 예외를 발생한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "경의중앙선");
        params.put("color", "rgb-mint-600");
        AcceptanceTestFixture.post("/lines", params);

        // when
        final ExtractableResponse<Response> response = AcceptanceTestFixture.post("/lines", params);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response.body().jsonPath().getString("message")).isEqualTo("이미 같은 이름의 노선이 존재합니다.")
        );
    }

    @DisplayName("전체 노선들을 조회한다.")
    @Test
    void findAllLines() {
        /// given
        Map<String, String> params1 = new HashMap<>();
        params1.put("name", "5호선");
        params1.put("color", "rgb-purple-600");
        final ExtractableResponse<Response> createResponse1 = AcceptanceTestFixture.post("/lines", params1);

        Map<String, String> params2 = new HashMap<>();
        params2.put("name", "2호선");
        params2.put("color", "rgb-green-600");
        final ExtractableResponse<Response> createResponse2 = AcceptanceTestFixture.post("/lines", params2);

        // when
        final ExtractableResponse<Response> response = AcceptanceTestFixture.get("/lines");

        // then
        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(resultLineIds).containsAll(expectedLineIds)
        );
    }

    @DisplayName("특정 노선을 조회한다.")
    @Test
    void findLine() {
        /// given
        Map<String, String> params = new HashMap<>();
        params.put("name", "1호선");
        params.put("color", "rgb-blue-600");

        final ExtractableResponse<Response> createResponse = AcceptanceTestFixture.post("/lines", params);
        long id = Long.parseLong(createResponse.header("Location").split("/")[2]);

        // when
        final ExtractableResponse<Response> response = AcceptanceTestFixture.get("/lines/" + id);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getLong("id")).isEqualTo(id),
                () -> assertThat(response.jsonPath().getString("name")).isEqualTo("1호선"),
                () -> assertThat(response.jsonPath().getString("color")).isEqualTo("rgb-blue-600")
        );
    }

    @DisplayName("특정 노선을 수정한다.")
    @Test
    void updateLine() {
        /// given
        Map<String, String> params1 = new HashMap<>();
        params1.put("name", "3호선");
        params1.put("color", "rgb-orange-600");

        final ExtractableResponse<Response> createResponse = AcceptanceTestFixture.post("/lines", params1);
        long id = Long.parseLong(createResponse.header("Location").split("/")[2]);

        // when
        Map<String, String> params2 = new HashMap<>();
        params2.put("name", "6호선");
        params2.put("color", "rgb-brown-600");

        final ExtractableResponse<Response> response = AcceptanceTestFixture.put("/lines/" + id, params2);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "7호선");
        params.put("color", "rgb-darkgreen-600");
        final ExtractableResponse<Response> createResponse = AcceptanceTestFixture.post("/lines", params);
        String uri = createResponse.header("Location");

        // when
        final ExtractableResponse<Response> response = AcceptanceTestFixture.delete(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
