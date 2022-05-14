package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import wooteco.subway.acceptance.fixture.SimpleRestAssured;
import wooteco.subway.dto.response.ExceptionResponse;
import wooteco.subway.dto.response.LineResponse;
import wooteco.subway.dto.response.StationResponse;
import wooteco.subway.exception.RowNotFoundException;

public class LineAcceptanceTest extends AcceptanceTest {

    @Test
    @DisplayName("노선을 생성한다.")
    public void createLine() {
        // given
        SimpleRestAssured.post("/stations", Map.of("name", "선정릉역"));
        SimpleRestAssured.post("/stations", Map.of("name", "선릉역"));
        Map<String, String> params = Map.of(
            "name", "신분당선",
            "color", "bg-red-600",
            "upStationId", "1",
            "downStationId", "2",
            "distance", "10");
        // when
        final ExtractableResponse<Response> response = SimpleRestAssured.post("/lines", params);
        // then
        Assertions.assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
            () -> assertThat(response.header("Location")).isNotBlank()
        );
    }

    @Test
    @DisplayName("입력값이 비어있는 경우 노선을 생성할 수 없다.")
    public void createLine_throwsExceptionWithBlankInput() {
        // given
        Map<String, String> params =
            Map.of("name", "신분당선", "color", "");
        // when
        final ExtractableResponse<Response> response = SimpleRestAssured.post("/lines", params);
        // then
        Assertions.assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
            () -> assertThat(SimpleRestAssured.toObject(response, ExceptionResponse.class)
                .getMessage())
                .contains("필수 입력")
        );
    }

    @Test
    @DisplayName("기존에 존재하는 노선 이름으로 노선을 생성할 수 없다.")
    public void createLine_throwsExceptionWithDuplicatedName() {
        // given
        setLineAsSaved("장한평역", "군자역", "5호선");
        // when
        final ExtractableResponse<Response> response =
            setLineAsSaved("다른역", "또다른역", "5호선");
        // then
        Assertions.assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
            () -> assertThat(SimpleRestAssured.toObject(response, ExceptionResponse.class)
                .getMessage())
                .contains("이미 존재")
        );
    }

    @Test
    @DisplayName("전체 노선 목록을 조회한다.")
    void getLines() {
        /// given
        final ExtractableResponse<Response> createResponse1 = setLineAsSaved("장한평역", "군자역", "5호선");
        final ExtractableResponse<Response> createResponse2 = setLineAsSaved("선정릉역", "선릉역", "수인분당선");

        // when
        ExtractableResponse<Response> response = SimpleRestAssured.get("/lines");

        // then
        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
            .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
            .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
            .map(LineResponse::getId)
            .collect(Collectors.toList());
        Assertions.assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(resultLineIds).containsAll(expectedLineIds)
        );
    }

    @Test
    @DisplayName("ID값으로 노선을 조회한다.")
    public void getLine() {
        // given
        final ExtractableResponse<Response> createdResponse = setLineAsSaved("선정릉역", "선릉역", "수인분당선");

        // when
        final String uri = createdResponse.header("Location");
        final ExtractableResponse<Response> foundResponse = SimpleRestAssured.get(uri);

        final LineResponse createdLineResponse = SimpleRestAssured.toObject(createdResponse, LineResponse.class);
        final LineResponse foundLineResponse = SimpleRestAssured.toObject(foundResponse, LineResponse.class);
        // then
        Assertions.assertAll(
            () -> assertThat(foundResponse.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(foundLineResponse.getId()).isEqualTo(createdLineResponse.getId())
        );
    }

    @Test
    @DisplayName("존재하지 않는 ID값으로 노선을 조회할 수 없다.")
    public void getLine_throwExceptionWithInvalidId() {
        // given & when
        setLineAsSaved("선정릉역", "선릉역", "수인분당선");

        final ExtractableResponse<Response> response = SimpleRestAssured.get("/lines/99");

        // then
        Assertions.assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
            () -> assertThat(SimpleRestAssured.toObject(response, ExceptionResponse.class).getException())
                .isEqualTo(RowNotFoundException.class)
        );

    }

    @Test
    @DisplayName("ID값으로 노선을 수정한다.")
    public void modifyLine() {
        // given
        final ExtractableResponse<Response> createdResponse =
            setLineAsSaved("선정릉역", "선릉역", "수인분당선");

        // when
        final Map<String, String> modificationParam =
            Map.of("name", "구분당선", "color", "bg-red-800");
        final String uri = createdResponse.header("Location");
        final ExtractableResponse<Response> modifiedResponse = SimpleRestAssured.put(uri, modificationParam);

        // then
        assertThat(modifiedResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("존재하지 않는 ID값의 노선을 수정할 수 없다.")
    public void modifyLine_throwExceptionWithInvalidId() {
        // given
        setLineAsSaved("장한평역", "마장역", "5호선");
        // when
        final Map<String, String> modificationParam =
            Map.of("name", "구분당선", "color", "bg-red-800");
        final ExtractableResponse<Response> response = SimpleRestAssured.put("/lines/99", modificationParam);
        // then
        Assertions.assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
            () -> assertThat(SimpleRestAssured.toObject(response, ExceptionResponse.class).getException())
                .isEqualTo(RowNotFoundException.class)
        );

    }

    @Test
    @DisplayName("ID값으로 노선을 제거한다.")
    public void deleteLine() {
        // given
        final ExtractableResponse<Response> createdResponse = setLineAsSaved("선정릉역", "선릉역", "수인분당선");

        // when
        final String uri = createdResponse.header("Location");
        final ExtractableResponse<Response> deleteResponse = SimpleRestAssured.delete(uri);

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("존재하지 않는 ID값의 노선을 제거할 수 없다.")
    public void deleteLine_throwExceptionWithInvalidId() {
        // given
        setLineAsSaved("선릉역", "선정릉역", "분당선");
        // when

        final ExtractableResponse<Response> response = SimpleRestAssured.delete("/lines/99");
        // then
        Assertions.assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
            () -> assertThat(SimpleRestAssured.toObject(response, ExceptionResponse.class).getException())
                .isEqualTo(RowNotFoundException.class)
        );

    }

    private ExtractableResponse<Response> setLineAsSaved(String upStationName, String downStationName,
        String lineName) {
        final Long upStationId = SimpleRestAssured.toObject(
            SimpleRestAssured.post("/stations", Map.of("name", upStationName)), StationResponse.class).getId();

        final Long downStationId = SimpleRestAssured.toObject(
            SimpleRestAssured.post("/stations", Map.of("name", downStationName)), StationResponse.class).getId();
        Map<String, String> params = Map.of(
            "name", lineName,
            "color", "bg-red-600",
            "upStationId", upStationId + "",
            "downStationId", downStationId + "",
            "distance", "10");
        // when
        return SimpleRestAssured.post("/lines", params);
    }

}
