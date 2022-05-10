package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static wooteco.subway.acceptance.AcceptanceFixture.LINE_URL;
import static wooteco.subway.acceptance.AcceptanceFixture.STATION_URL;
import static wooteco.subway.acceptance.AcceptanceFixture.deleteMethodRequest;
import static wooteco.subway.acceptance.AcceptanceFixture.getExpectedLineIds;
import static wooteco.subway.acceptance.AcceptanceFixture.getMethodRequest;
import static wooteco.subway.acceptance.AcceptanceFixture.getResultLineIds;
import static wooteco.subway.acceptance.AcceptanceFixture.postMethodRequest;
import static wooteco.subway.acceptance.AcceptanceFixture.putMethodRequest;
import static wooteco.subway.acceptance.AcceptanceFixture.강남역_인자;
import static wooteco.subway.acceptance.AcceptanceFixture.경의중앙선_인자;
import static wooteco.subway.acceptance.AcceptanceFixture.분당선_요청;
import static wooteco.subway.acceptance.AcceptanceFixture.분당선_인자;
import static wooteco.subway.acceptance.AcceptanceFixture.역삼역_인자;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> 강남역_응답 = postMethodRequest(강남역_인자, STATION_URL);
        ExtractableResponse<Response> 역삼역_응답 = postMethodRequest(역삼역_인자, STATION_URL);
        ExtractableResponse<Response> 분당선_응답 = postMethodRequest(분당선_요청, LINE_URL);

        // then
        assertAll(
                () -> assertThat(분당선_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(분당선_응답.header("Location")).isNotBlank(),
                () -> assertThat(분당선_응답.body().jsonPath().getString("name")).isEqualTo("분당선"),
                () -> assertThat(분당선_응답.body().jsonPath().getString("color")).isEqualTo("노랑이"),
                () -> assertThat(분당선_응답.body().jsonPath().getList("stations").size()).isEqualTo(2)
        );
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성하면 에러를 응답한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        postMethodRequest(분당선_인자, LINE_URL);

        // when
        ExtractableResponse<Response> response = postMethodRequest(분당선_인자, LINE_URL);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        /// given
        ExtractableResponse<Response> createResponse1 = postMethodRequest(분당선_인자, LINE_URL);
        ExtractableResponse<Response> createResponse2 = postMethodRequest(경의중앙선_인자, LINE_URL);

        // when
        ExtractableResponse<Response> response = getMethodRequest(LINE_URL);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = getExpectedLineIds(List.of(createResponse1, createResponse2));
        List<Long> resultLineIds = getResultLineIds(response);
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        //given
        ExtractableResponse<Response> createResponse = postMethodRequest(분당선_인자, LINE_URL);

        //when
        String url = LINE_URL + "/" + createResponse.header("Location").split("/")[2];
        ExtractableResponse<Response> response = getMethodRequest(url);

        //then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.body().jsonPath().getString("name")).isEqualTo("분당선"),
                () -> assertThat(response.body().jsonPath().getString("color")).isEqualTo("노랑이")
        );
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void modifyLine() {
        //given
        ExtractableResponse<Response> createResponse = postMethodRequest(분당선_인자, LINE_URL);

        //when
        String url = LINE_URL + "/" + createResponse.header("Location").split("/")[2];
        ExtractableResponse<Response> response = putMethodRequest(경의중앙선_인자, url);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> createResponse = postMethodRequest(분당선_인자, LINE_URL);

        // when
        String url = LINE_URL + "/" + createResponse.header("Location").split("/")[2];
        ExtractableResponse<Response> response = deleteMethodRequest(url);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("기존에 존재하지 않는 지하철 노선 ID로 지하철 노선을 조회하면 에러를 응답한다.")
    @Test
    void getLineWithNonExistId() {
        //when
        String url = LINE_URL + "/100";
        ExtractableResponse<Response> response = getMethodRequest(url);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("기존에 존재하지 않는 지하철 노선 ID로 지하철 노선을 수정하면 에러를 응답한다.")
    @Test
    void updateLineWithNonExistId() {
        //given
        postMethodRequest(분당선_인자, LINE_URL);

        //when
        String url = LINE_URL + "/" + 100;
        ExtractableResponse<Response> response = putMethodRequest(경의중앙선_인자, url);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 수정하면 에러를 응답한다.")
    @Test
    void updateLineWithDuplicatedName() {
        //given
        ExtractableResponse<Response> createResponse = postMethodRequest(분당선_인자, LINE_URL);

        //when
        String url = LINE_URL + "/" + createResponse.header("Location").split("/")[2];
        ExtractableResponse<Response> response = putMethodRequest(분당선_인자, url);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("기존에 존재하지 않는 지하철 노선 ID로 지하철 노선을 제거하면 에러를 응답한다.")
    @Test
    void deleteLineWithNonExistId() {
        //given
        postMethodRequest(분당선_인자, LINE_URL);

        //when
        String url = LINE_URL + "/" + 100;
        ExtractableResponse<Response> response = deleteMethodRequest(url);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
