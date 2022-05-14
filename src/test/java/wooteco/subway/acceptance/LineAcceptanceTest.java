package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.line.LineRequest;
import wooteco.subway.dto.line.LineResponse;
import wooteco.subway.dto.section.SectionRequest;
import wooteco.subway.dto.station.StationRequest;

@DisplayName("지하철 노선 관련 기능")
class LineAcceptanceTest extends AcceptanceTest {

    private static final String LINE_ONE_NAME = "1호선";
    private static final String LINE_ONE_COLOR = "bg-red-600";
    private static final String LINE_TWO_NAME = "2호선";
    private static final String LINE_TWO_COLOR = "bg-green-600";

    private Station seolleung;
    private Station yeoksam;
    private Station wangsimni;
    private Station dapsimni;

    private LineRequest lineOneRequest;
    private LineRequest lineTwoRequest;

    @BeforeEach
    void setUpData() {
        seolleung = createStation(new StationRequest("선릉역")).as(Station.class);
        yeoksam = createStation(new StationRequest("역삼역")).as(Station.class);
        wangsimni = createStation(new StationRequest("왕십리역")).as(Station.class);
        dapsimni = createStation(new StationRequest("답십리역")).as(Station.class);

        lineOneRequest = new LineRequest(
                LINE_ONE_NAME,
                LINE_ONE_COLOR,
                seolleung.getId(),
                yeoksam.getId(),
                10
        );
        lineTwoRequest = new LineRequest(
                LINE_TWO_NAME,
                LINE_TWO_COLOR,
                wangsimni.getId(),
                dapsimni.getId(),
                7
        );
    }

    @Test
    @DisplayName("지하철 노선과 구간을 생성한다.")
    void CreateLine_WithSection_Success() {
        // when
        final ExtractableResponse<Response> actual = createLine(lineOneRequest);
        final long lineId = extractId(actual);

        final LineResponse expected = LineResponse.of(
                new Line(lineId, LINE_ONE_NAME, LINE_ONE_COLOR),
                List.of(seolleung, yeoksam)
        );

        // then
        assertThat(actual.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(actual.header(LOCATION)).isEqualTo(LINE_PATH_PREFIX + SLASH + lineId);

        final LineResponse actualResponse = actual.body().as(LineResponse.class);
        assertThat(actualResponse).isEqualTo(expected);
    }

    @DisplayName("모든 노선을 조회한다.")
    @Test
    void Show_Lines() {
        // given
        final LineResponse expectedLineOne = createLine(lineOneRequest).as(LineResponse.class);
        final LineResponse expectedLineTwo = createLine(lineTwoRequest).as(LineResponse.class);

        // when
        final ExtractableResponse<Response> actual = RestAssured.given().log().all()
                .when()
                .get(LINE_PATH_PREFIX)
                .then().log().all()
                .extract();
        final List<LineResponse> actualResponse = actual.jsonPath().getList(".", LineResponse.class);

        // then
        assertThat(actual.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actualResponse).containsExactly(expectedLineOne, expectedLineTwo);
    }

    @DisplayName("id로 노선을 조회한다.")
    @Test
    void ShowLine() {
        // given
        final long id = createAndGetLineId(lineOneRequest);

        final LineResponse expected = LineResponse.of(
                new Line(id, LINE_ONE_NAME, LINE_ONE_COLOR),
                List.of(seolleung, yeoksam)
        );

        // when
        final ExtractableResponse<Response> actual = RestAssured.given().log().all()
                .when()
                .get(LINE_PATH_PREFIX + SLASH + id)
                .then().log().all()
                .extract();
        final LineResponse actualResponse = actual.body().as(LineResponse.class);

        // then
        assertThat(actual.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actualResponse).isEqualTo(expected);
    }

    @DisplayName("id로 5개의 역을 포함한 노선을 상행에서 하행 순서로 정렬해서 조회한다.")
    @Test
    void ShowLine_5StationsOrderByUpStation_OK() {
        // given
        final long id = createAndGetLineId(lineOneRequest);

        final long samseongId = createAndGetStationId(new StationRequest("삼성역"));

        createSection(new SectionRequest(dapsimni.getId(), yeoksam.getId(), 5), (int) id);
        createSection(new SectionRequest(yeoksam.getId(), wangsimni.getId(), 5), (int) id);
        createSection(new SectionRequest(samseongId, yeoksam.getId(), 3), (int) id);

        final LineResponse expected = LineResponse.of(
                new Line(id, LINE_ONE_NAME, LINE_ONE_COLOR),
                List.of(seolleung, dapsimni, new Station(samseongId, "삼성역"), yeoksam, wangsimni)
        );

        // when
        final ExtractableResponse<Response> actual = RestAssured.given().log().all()
                .when()
                .get(LINE_PATH_PREFIX + SLASH + id)
                .then().log().all()
                .extract();
        final LineResponse actualResponse = actual.body().as(LineResponse.class);

        // then
        // 선릉 - 답십리 - 삼성 - 역삼 - 왕십리
        assertThat(actual.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actualResponse).isEqualTo(expected);
    }

    @Test
    @DisplayName("존재하지 않은 id로 조회하면 NOT_FOUND를 반환한다.")
    void ShowLine_NotExistId_NotFound() {
        // when
        final ExtractableResponse<Response> actual = RestAssured.given().log().all()
                .when()
                .get(LINE_PATH_PREFIX + SLASH + 999)
                .then().log().all()
                .extract();

        // then
        assertThat(actual.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("지하철 노선 정보를 수정한다.")
    void UpdateLine() {
        // given
        final long id = createAndGetLineId(lineOneRequest);

        // when
        final ExtractableResponse<Response> actual = RestAssured.given().log().all()
                .body(lineTwoRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(LINE_PATH_PREFIX + SLASH + id)
                .then().log().all()
                .extract();

        // then
        assertThat(actual.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("수정하려는 노선 이름이 중복되면 BAD_REQUEST를 반환한다.")
    void UpdateLine_DuplicateName_BadRequest() {
        // given
        createLine(lineOneRequest);
        final long id = createAndGetLineId(lineTwoRequest);

        // when
        final ExtractableResponse<Response> actual = RestAssured.given().log().all()
                .body(lineOneRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(LINE_PATH_PREFIX + SLASH + id)
                .then().log().all()
                .extract();

        // then
        assertThat(actual.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("수정하려는 노선 id가 존재하지 않으면 404를 반환한다.")
    void UpdateLine_NotExistId_BadRequest() {
        // when
        final ExtractableResponse<Response> actual = RestAssured.given().log().all()
                .body(lineOneRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(LINE_PATH_PREFIX + SLASH + 999)
                .then().log().all()
                .extract();

        // then
        assertThat(actual.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("지하철 노선 정보를 삭제한다.")
    void DeleteLine() {
        // given
        final long id = createAndGetLineId(lineOneRequest);

        // when
        final ExtractableResponse<Response> updateResponse = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete(LINE_PATH_PREFIX + SLASH + id)
                .then().log().all()
                .extract();

        // then
        assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("존재하지 않는 노선을 제거하면 404를 반환한다.")
    void DeleteLine_NotExistId_BadRequest() {
        // when
        final ExtractableResponse<Response> updateResponse = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete(LINE_PATH_PREFIX + SLASH + 999)
                .then().log().all()
                .extract();

        // then
        assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }
}
