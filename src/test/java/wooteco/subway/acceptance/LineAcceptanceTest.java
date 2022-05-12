package wooteco.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationRequest;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SuppressWarnings("NonAsciiCharacters")
class LineAcceptanceTest extends AcceptanceTest {

    private Long 선릉역_id;
    private Long 선정릉역_id;

    private final String basicPath = "/lines";

    @BeforeEach
    void setUpStations() {
        선릉역_id = RestAssuredConvenienceMethod.postLineAndGetId(new StationRequest("선릉역"), "/stations");
        선정릉역_id = RestAssuredConvenienceMethod.postLineAndGetId(new StationRequest("선정릉역"), "/stations");
    }

    @DisplayName("지하철 노선을 등록한다.")
    @Test
    void createLine() {
        // given
        LineRequest request = new LineRequest("분당선", "yellow", 선릉역_id, 선정릉역_id, 10);

        // when
        ExtractableResponse<Response> response = RestAssuredConvenienceMethod.postRequest(request, basicPath);

        // then
        Long responseLineId = response.jsonPath().getObject(".", LineResponse.class).getId();
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.header("Location")).isEqualTo("/lines/" + responseLineId)
        );
    }

    @DisplayName("비어있는 이름으로 역을 생성하면 400번 코드를 반환한다.")
    @Test
    void createLineWithInvalidNameDateSize() {
        // given
        LineRequest request = new LineRequest("", "yellow", 선릉역_id, 선정릉역_id, 10);

        // when
        ExtractableResponse<Response> response =
                RestAssuredConvenienceMethod.postRequest(request, basicPath);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("비어있는 색으로 역을 생성하면 400번 코드를 반환한다.")
    @Test
    void createLineWithInvalidColorDateSize() {
        // given
        LineRequest request = new LineRequest("분당선", "", 선릉역_id, 선정릉역_id, 10);

        // when
        ExtractableResponse<Response> response =
                RestAssuredConvenienceMethod.postRequest(request, basicPath);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("중복된 이름을 가진 지하철 노선을 등록할 때 400번 코드를 반환한다.")
    @Test
    void throwsExceptionWhenCreateDuplicatedName() {
        // given
        LineRequest request = new LineRequest("분당선", "yellow", 선릉역_id, 선정릉역_id, 10);
        RestAssuredConvenienceMethod.postRequest(request, basicPath);

        // when
        ExtractableResponse<Response> response =
                RestAssuredConvenienceMethod.postRequest(request, basicPath);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("상행, 역이 같은 가진 지하철 노선을 등록할 때 400번 코드를 반환한다.")
    @Test
    void throwsExceptionWhenCreateLineWithSameUpDownStation() {
        // given
        LineRequest request = new LineRequest("분당선", "yellow", 선릉역_id, 선릉역_id, 10);

        // when
        ExtractableResponse<Response> response =
                RestAssuredConvenienceMethod.postRequest(request, basicPath);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("존재하지 않는 역으로 지하철 노선을 등록할 때 400번 코드를 반환한다.")
    @Test
    void throwsExceptionWhenCreateLineWithNonExistStation() {
        // given
        LineRequest request = new LineRequest("분당선", "yellow", 선릉역_id, 100L, 10);

        // when
        ExtractableResponse<Response> response =
                RestAssuredConvenienceMethod.postRequest(request, basicPath);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("0이하의 거리를 가진 구간으로 지하철 노선을 등록할 때 400번 코드를 반환한다.")
    @Test
    void throwsExceptionWhenCreateLineWithInvalidDistance() {
        // given
        LineRequest request = new LineRequest("분당선", "yellow", 선릉역_id, 선정릉역_id, 0);

        // when
        ExtractableResponse<Response> response =
                RestAssuredConvenienceMethod.postRequest(request, basicPath);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        LineResponse createResponse1 = RestAssuredConvenienceMethod.postRequest(
                new LineRequest("분당선", "yellow", 선릉역_id, 선정릉역_id, 10), basicPath)
                .jsonPath().getObject(".", LineResponse.class);
        LineResponse createResponse2 = RestAssuredConvenienceMethod.postRequest(
                new LineRequest("신분당선", "yellow", 선릉역_id, 선정릉역_id, 10), basicPath)
                .jsonPath().getObject(".", LineResponse.class);

         // when
        ExtractableResponse<Response> response = RestAssuredConvenienceMethod.getRequest(basicPath);

        // then
        List<LineResponse> actual = response.jsonPath().getList(".", LineResponse.class);
        assertAll(
                () -> assertThat(actual.get(0).getId()).isEqualTo(createResponse1.getId()),
                () -> assertThat(actual.get(0).getName()).isEqualTo(createResponse1.getName()),
                () -> assertThat(actual.get(0).getColor()).isEqualTo(createResponse1.getColor()),

                () -> assertThat(actual.get(1).getId()).isEqualTo(createResponse2.getId()),
                () -> assertThat(actual.get(1).getName()).isEqualTo(createResponse2.getName()),
                () -> assertThat(actual.get(1).getColor()).isEqualTo(createResponse2.getColor())
        );
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        LineResponse createResponse = RestAssuredConvenienceMethod.postRequest(
                        new LineRequest("분당선", "yellow", 선릉역_id, 선정릉역_id, 10), basicPath)
                .jsonPath().getObject(".", LineResponse.class);

        // when
        ExtractableResponse<Response> response =
                RestAssuredConvenienceMethod.getRequest("/lines/" + createResponse.getId());

        // then
        LineResponse lineResponse = response.jsonPath().getObject(".", LineResponse.class);
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(lineResponse.getName()).isEqualTo(createResponse.getName()),
                () -> assertThat(lineResponse.getColor()).isEqualTo(createResponse.getColor())
        );
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        Long createdLineId = RestAssuredConvenienceMethod.postLineAndGetId(
                        new LineRequest("분당선", "yellow", 선릉역_id, 선정릉역_id, 10), basicPath);
        Line requestBody = new Line("다른분당선", "blue");

        // when
        ExtractableResponse<Response> response =
                RestAssuredConvenienceMethod.putRequest(requestBody, "/lines/" + createdLineId);


        // then
        LineResponse findResponse =
                RestAssuredConvenienceMethod.getRequest("/lines/" + createdLineId)
                        .jsonPath().getObject(".", LineResponse.class);
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(findResponse.getName()).isEqualTo("다른분당선"),
                () -> assertThat(findResponse.getColor()).isEqualTo("blue")
        );
    }

    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteLine() {
        // given
        Long createdLineId = RestAssuredConvenienceMethod.postLineAndGetId(
                        new LineRequest("분당선", "yellow", 선릉역_id, 선정릉역_id, 10), basicPath);

        // when
        ExtractableResponse<Response> response = RestAssuredConvenienceMethod.deleteRequest("/lines/" + createdLineId);

        // then
        List<LineResponse> lineResponses = RestAssuredConvenienceMethod.getRequest(basicPath)
                .jsonPath().getList(".", LineResponse.class);
        List<Long> lineIds = lineResponses.stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),
                () -> assertThat(lineIds).doesNotContain(createdLineId)
        );
    }

    @DisplayName("존재하지 않는 데이터를 삭제하려고 한다면 400번 코드를 반환한다.")
    @Test
    void deleteLineWithNotExistData() {
        // when
        ExtractableResponse<Response> response =
                RestAssuredConvenienceMethod.deleteRequest("/lines/" + 100L);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
