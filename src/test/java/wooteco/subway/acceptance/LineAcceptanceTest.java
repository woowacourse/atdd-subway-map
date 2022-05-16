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
import static wooteco.subway.acceptance.AcceptanceFixture.LINE_2_요청;
import static wooteco.subway.acceptance.AcceptanceFixture.LINE_2_인자;
import static wooteco.subway.acceptance.AcceptanceFixture.역삼역_인자;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationResponse;

public class LineAcceptanceTest extends AcceptanceTest {

    @BeforeEach
    void stationSetup() {
        postMethodRequest(강남역_인자, STATION_URL);
        postMethodRequest(역삼역_인자, STATION_URL);
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> LINE_2_응답 = postMethodRequest(LINE_2_요청, LINE_URL);

        // then
        assertAll(
                () -> assertThat(LINE_2_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(LINE_2_응답.header("Location")).isNotBlank(),
                () -> assertThat(LINE_2_응답.body().jsonPath().getString("name")).isEqualTo("2호선"),
                () -> assertThat(LINE_2_응답.body().jsonPath().getString("color")).isEqualTo("초록이"),
                () -> assertThat(LINE_2_응답.body().jsonPath().getList("stations").size()).isEqualTo(2)
        );
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성하면 에러를 응답한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        postMethodRequest(LINE_2_인자, LINE_URL);

        // when
        ExtractableResponse<Response> response = postMethodRequest(LINE_2_인자, LINE_URL);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        /// given
        ExtractableResponse<Response> createResponse1 = postMethodRequest(LINE_2_인자, LINE_URL);

        // when
        ExtractableResponse<Response> response = getMethodRequest(LINE_URL);

        // then
        List<Long> expectedLineIds = getExpectedLineIds(List.of(createResponse1));
        List<Long> resultLineIds = getResultLineIds(response);
        List<LineResponse> lineResponses = response.jsonPath().getList(".", LineResponse.class);
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(resultLineIds).containsAll(expectedLineIds),
                () -> assertThat(lineResponses.size()).isEqualTo(1),
                () -> {
                    LineResponse lineResponse = lineResponses.get(0);
                    assertThat(lineResponse.getName()).isEqualTo("2호선");
                    assertThat(lineResponse.getColor()).isEqualTo("초록이");
                },
                () -> {
                    List<StationResponse> stationResponses = lineResponses.get(0).getStations();
                    assertThat(stationResponses.size()).isEqualTo(2);
                    assertThat(stationResponses.get(0).getName()).isEqualTo("강남역");
                }
        );
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        //given
        ExtractableResponse<Response> createResponse = postMethodRequest(LINE_2_인자, LINE_URL);

        //when
        String url = LINE_URL + "/" + createResponse.header("Location").split("/")[2];
        ExtractableResponse<Response> response = getMethodRequest(url);

        //then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.body().jsonPath().getString("name")).isEqualTo("2호선"),
                () -> assertThat(response.body().jsonPath().getString("color")).isEqualTo("초록이")
        );
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void modifyLine() {
        //given
        ExtractableResponse<Response> createResponse = postMethodRequest(LINE_2_인자, LINE_URL);

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
        ExtractableResponse<Response> createResponse = postMethodRequest(LINE_2_인자, LINE_URL);

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
        postMethodRequest(LINE_2_인자, LINE_URL);

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
        ExtractableResponse<Response> createResponse = postMethodRequest(LINE_2_인자, LINE_URL);

        //when
        String url = LINE_URL + "/" + createResponse.header("Location").split("/")[2];
        ExtractableResponse<Response> response = putMethodRequest(LINE_2_인자, url);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("기존에 존재하지 않는 지하철 노선 ID로 지하철 노선을 제거하면 에러를 응답한다.")
    @Test
    void deleteLineWithNonExistId() {
        //given
        postMethodRequest(LINE_2_인자, LINE_URL);

        //when
        String url = LINE_URL + "/" + 100;
        ExtractableResponse<Response> response = deleteMethodRequest(url);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
