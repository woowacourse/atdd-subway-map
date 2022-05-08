package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.http.HttpStatus;

import wooteco.subway.acceptance.fixture.SimpleResponse;
import wooteco.subway.acceptance.fixture.SimpleRestAssured;
import wooteco.subway.dto.LineResponse;

public class LineAcceptanceTest extends AcceptanceTest {

    @Test
    @DisplayName("노선을 생성한다.")
    public void createLine() {
        // given
        Map<String, String> params =
                Map.of("name", "신분당선", "color", "bg-red-600");
        // when
        final SimpleResponse response = SimpleRestAssured.post("/lines", params);
        // then
        Assertions.assertAll(
                () -> assertThat(response.hasStatus(HttpStatus.CREATED)).isTrue(),
                () -> assertThat(response.getHeader("Location")).isNotBlank()
        );
    }

    @Test
    @DisplayName("입력값이 비어있는 경우 노선을 생성할 수 없다.")
    public void createLine_throwsExceptionWithBlankInput() {
        // given
        Map<String, String> params =
                Map.of("name", "신분당선", "color", "");
        // when
        final SimpleResponse response = SimpleRestAssured.post("/lines", params);
        // then
        Assertions.assertAll(
                () -> assertThat(response.hasStatus(HttpStatus.BAD_REQUEST)).isTrue(),
                () -> assertThat(response.containsExceptionMessage("필수 입력")).isTrue()
        );
    }

    @Test
    @DisplayName("기존에 존재하는 노선 이름으로 노선을 생성할 수 없다.")
    public void createLine_throwsExceptionWithDuplicatedName() {
        // given
        Map<String, String> params1 =
                Map.of("name", "신분당선", "color", "bg-red-600");
        SimpleRestAssured.post("/lines", params1);
        // when
        Map<String, String> params2 =
                Map.of("name", "신분당선", "color", "bg-red-800");
        final SimpleResponse response = SimpleRestAssured.post("/lines", params2);
        // then
        Assertions.assertAll(
                () -> assertThat(response.hasStatus(HttpStatus.BAD_REQUEST)).isTrue(),
                () -> assertThat(response.containsExceptionMessage("이미 존재")).isTrue()
        );
    }

    @Test
    @DisplayName("전체 노선 목록을 조회한다.")
    void getLines() {
        /// given
        Map<String, String> params1 = Map.of("name", "신분당선", "color", "bg-red-600");
        Map<String, String> params2 = Map.of("name", "경의중앙선", "color", "bg-red-800");

        SimpleResponse createResponse1 = SimpleRestAssured.post("/lines", params1);
        SimpleResponse createResponse2 = SimpleRestAssured.post("/lines", params2);
        // when
        SimpleResponse response = SimpleRestAssured.get("/lines");
        // then
        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
                .map(SimpleResponse::getIdFromLocation)
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.toList(LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());

        Assertions.assertAll(
                () -> assertThat(response.hasStatus(HttpStatus.OK)).isTrue(),
                () -> assertThat(resultLineIds).containsAll(expectedLineIds)
        );
    }

    @Test
    @DisplayName("ID값으로 노선을 조회한다.")
    public void getLine() {
        // given
        Map<String, String> params1 = Map.of("name", "신분당선", "color", "bg-red-600");
        SimpleResponse createdResponse = SimpleRestAssured.post("/lines", params1);
        // when
        final String uri = createdResponse.getHeader("Location");
        final SimpleResponse foundResponse = SimpleRestAssured.get(uri);
        final LineResponse createdLineResponse = createdResponse.toObject(LineResponse.class);
        final LineResponse foundLineResponse = foundResponse.toObject(LineResponse.class);
        // then
        Assertions.assertAll(
                () -> assertThat(foundResponse.hasStatus(HttpStatus.OK)).isTrue(),
                () -> assertThat(foundLineResponse.getId()).isEqualTo(createdLineResponse.getId())
        );
    }

    @Test
    @DisplayName("존재하지 않는 ID값으로 노선을 조회할 수 없다.")
    public void getLine_throwExceptionWithInvalidId() {
        // given
        Map<String, String> params = Map.of("name", "신분당선", "color", "bg-red-600");
        SimpleRestAssured.post("/lines", params);
        // when
        final SimpleResponse response = SimpleRestAssured.get("/lines/99");
        // then
        Assertions.assertAll(
                () -> assertThat(response.hasStatus(HttpStatus.BAD_REQUEST)).isTrue(),
                () -> assertThat(response.containsExceptionMessage("존재하지 않습니다")).isTrue()
        );
    }

    @Test
    @DisplayName("ID값으로 노선을 수정한다.")
    public void modifyLine() {
        // given
        Map<String, String> params1 = Map.of("name", "신분당선", "color", "bg-red-600");
        SimpleResponse createdResponse = SimpleRestAssured.post("/lines", params1);
        // when
        final Map<String, String> modificationParam =
                Map.of("name", "구분당선", "color", "bg-red-800");
        final String uri = createdResponse.getHeader("Location");
        final SimpleResponse modifiedResponse = SimpleRestAssured.put(uri, modificationParam);
        // then
        assertThat(modifiedResponse.hasStatus(HttpStatus.OK)).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 ID값의 노선을 수정할 수 없다.")
    public void modifyLine_throwExceptionWithInvalidId() {
        // given
        Map<String, String> params = Map.of("name", "신분당선", "color", "bg-red-600");
        SimpleRestAssured.post("/lines", params);
        // when
        final Map<String, String> modificationParam =
                Map.of("name", "구분당선", "color", "bg-red-800");
        final SimpleResponse response = SimpleRestAssured.put("/lines/99", modificationParam);
        // then
        Assertions.assertAll(
                () -> assertThat(response.hasStatus(HttpStatus.BAD_REQUEST)).isTrue(),
                () -> assertThat(response.containsExceptionMessage("존재하지 않습니다")).isTrue()
        );
    }

    @Test
    @DisplayName("ID값으로 노선을 제거한다.")
    public void deleteLine() {
        // given
        Map<String, String> params = Map.of("name", "신분당선", "color", "bg-red-600");
        SimpleResponse createdResponse = SimpleRestAssured.post("/lines", params);
        // when
        final String uri = createdResponse.getHeader("Location");
        final SimpleResponse deleteResponse = SimpleRestAssured.delete(uri);
        // then
        assertThat(deleteResponse.hasStatus(HttpStatus.NO_CONTENT)).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 ID값의 노선을 제거할 수 없다.")
    public void deleteLine_throwExceptionWithInvalidId() {
        // given
        Map<String, String> params = Map.of("name", "신분당선", "color", "bg-red-600");
        SimpleRestAssured.post("/lines", params);
        // when
        final SimpleResponse response = SimpleRestAssured.delete("/lines/99");
        // then
        Assertions.assertAll(
                () -> assertThat(response.hasStatus(HttpStatus.BAD_REQUEST)).isTrue(),
                () -> assertThat(response.containsExceptionMessage("존재하지 않습니다")).isTrue()
        );
    }
}
