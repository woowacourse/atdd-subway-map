package wooteco.subway.web.api;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.dao.station.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.web.request.LineRequest;
import wooteco.subway.web.response.LineResponse;

@DisplayName("노선 관련 기능")
class LineApiControllerTest extends AcceptanceTest {

    private static final String UP_STATION_NAME = "강남역";
    private static final String DOWN_STATION_NAME = "잠실역";
    private static final String LINE_NAME = "신분당선";
    private static final String LINE_COLOR = "bg-red-600";
    private static final int DISTANCE = 10;

    @Autowired
    private StationDao stationDao;

    @DisplayName("노선 생성 - 성공")
    @Test
    void createLine() {
        // when
        final ExtractableResponse<Response> result = 기본_노선_생성();

        //then
        final LineResponse lineResponse = result.body().as(LineResponse.class);

        assertThat(result.header("Location")).isNotEmpty();
        assertThat(lineResponse.getName()).isEqualTo(LINE_NAME);
        assertThat(lineResponse.getColor()).isEqualTo(LINE_COLOR);
        assertThat(lineResponse.getStations()).hasSize(2);
        assertThat(lineResponse.getStations()).extracting("name")
            .containsExactlyInAnyOrder(UP_STATION_NAME, DOWN_STATION_NAME);
    }

    @DisplayName("노선 생성 - 실패(이름 중복)")
    @Test
    void createLine_duplicatedName() {
        // given
        final Long upStationId = 상행역().getId();
        final Long downStationId = 하행역().getId();
        노선_생성(LineRequest.create(LINE_NAME, LINE_COLOR, upStationId, downStationId, DISTANCE));

        // when
        final String newColor = "bg-green-600";
        final ExtractableResponse<Response> result =
            노선_생성(LineRequest.create(LINE_NAME, newColor, upStationId, downStationId, DISTANCE));

        // then
        assertThat(result.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(result.body().asString()).isEqualTo("이미 등록되어 있는 노선 정보입니다.");
    }

    @DisplayName("한 노선 조회 - 성공")
    @Test
    void getLineById() {
        // given
        final ExtractableResponse<Response> createResponse = 기본_노선_생성();
        int lineId = createResponse.body().path("id");

        // when
        ExtractableResponse<Response> response = 노선_조회((long) lineId);

        // then
        final LineResponse lineResponse = response.body().as(LineResponse.class);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(lineResponse.getName()).isEqualTo(LINE_NAME);
        assertThat(lineResponse.getColor()).isEqualTo(LINE_COLOR);
        assertThat(lineResponse.getStations()).hasSize(2);
        assertThat(lineResponse.getStations()).extracting("name")
            .containsExactlyInAnyOrder(UP_STATION_NAME, DOWN_STATION_NAME);
    }


    @DisplayName("노선 조회 - 실패(노선 정보 없음)")
    @Test
    void getStationById_notFound() {
        /// given
        ExtractableResponse<Response> response = 노선_조회(Long.MAX_VALUE);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("노선 수정 - 성공")
    @Test
    void updateLine() {
        // given
        final Long upStationId = 상행역().getId();
        final Long downStationId = 하행역().getId();
        final String uri = 노선_생성(LineRequest.create(LINE_NAME, LINE_COLOR, upStationId,
            downStationId, DISTANCE)).header("Location");

        String newLineName = "1호선";
        final LineRequest lineRequest = LineRequest
            .create(newLineName, LINE_COLOR, upStationId, downStationId, DISTANCE);

        // when
        final ExtractableResponse<Response> result = 노선_수정(uri, lineRequest);

        // then
        final LineResponse lineResponse = 노선_조회(uri).as(LineResponse.class);

        assertThat(result.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(lineResponse.getName()).isEqualTo(newLineName);
        assertThat(lineResponse.getName()).isNotEqualTo(LINE_NAME);
        assertThat(lineResponse.getColor()).isEqualTo(LINE_COLOR);
    }

    @DisplayName("노선 수정 - 실패(변경하려는 노선 이름 중복)")
    @Test
    void updateLine_duplicatedName() {
        // given
        final Station upStation = 상행역();
        final Station downStation = 하행역();
        노선_생성(LineRequest
            .create(LINE_NAME, LINE_COLOR, upStation.getId(), downStation.getId(), DISTANCE));

        String targetName = "구분당선";
        String targetColor = "bg-red-6000";
        final String uri = 노선_생성(LineRequest
            .create(targetName, targetColor, upStation.getId(), downStation.getId(), DISTANCE))
            .header("Location");

        // when
        final LineRequest lineRequest = LineRequest
            .create(LINE_NAME, "blue", upStation.getId(), downStation.getId(), DISTANCE);
        final ExtractableResponse<Response> result = 노선_수정(uri, lineRequest);

        // then
        assertThat(result.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(result.body().asString()).isEqualTo("이미 등록되어 있는 노선 정보입니다.");
    }

    @DisplayName("노선 수정 - 실패(존재 하지 않는 노선 수정)")
    @Test
    void updateLine_notFound() {
        /// given
        final LineRequest lineRequest = LineRequest
            .create(UP_STATION_NAME, LINE_COLOR, 상행역().getId(), 하행역().getId(), DISTANCE);

        // when
        final ExtractableResponse<Response> result = 노선_수정("/lines/" + Long.MAX_VALUE, lineRequest);

        // then
        assertThat(result.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("노선 삭제 - 성공")
    @Test
    void removeLine() {
        // given
        final String uri = 기본_노선_생성().header("Location");

        // when
        final ExtractableResponse<Response> result = 노선_삭제(uri);

        // then
        assertThat(result.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(노선_조회(uri).statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    private ExtractableResponse<Response> 노선_생성(LineRequest lineRequest) {
        return RestAssured
            .given()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 기본_노선_생성() {
        return 노선_생성(
            LineRequest.create(LINE_NAME, LINE_COLOR, 상행역().getId(), 하행역().getId(), DISTANCE));
    }

    private ExtractableResponse<Response> 노선_조회() {
        return RestAssured
            .given()
            .when()
            .get("/lines")
            .then()
            .extract();
    }

    private ExtractableResponse<Response> 노선_조회(Long lineId) {
        return RestAssured.given().log().all()
            .when()
            .get("/lines/" + lineId)
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 노선_조회(String uri) {
        return RestAssured.given().log().all()
            .when()
            .get(uri)
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 노선_수정(String uri, LineRequest lineRequest) {
        return RestAssured.given().log().all()
            .body(lineRequest)
            .contentType(ContentType.JSON)
            .when()
            .put(uri)
            .then().extract();
    }

    private ExtractableResponse<Response> 노선_삭제(String uri) {
        return RestAssured.given().log().all()
            .when()
            .delete(uri)
            .then()
            .extract();
    }

    private Station 역_생성(String name) {
        return stationDao.save(Station.create(name));
    }

    private Station 상행역() {
        return 역_생성(UP_STATION_NAME);
    }

    private Station 하행역() {
        return 역_생성(DOWN_STATION_NAME);
    }
}