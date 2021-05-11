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
import wooteco.subway.web.request.SectionRequest;
import wooteco.subway.web.response.LineResponse;

@DisplayName("구간 등록 테스트")
class SectionApiControllerTest extends AcceptanceTest {

    private static final String UP_STATION_NAME = "강남역";
    private static final String DOWN_STATION_NAME = "잠실역";
    private static final String LINE_NAME = "신분당선";
    private static final String LINE_COLOR = "bg-red-600";
    private static final int DISTANCE = 10;

    @Autowired
    private StationDao stationDao;


    @DisplayName("구간 등록 - 성공(상행 종점 등록)")
    @Test
    void insertSection_success_upStation() {
        // given
        final Station upStation = 상행역();
        final Station downStation = 하행역();
        final String uri =
            노선_생성(LineRequest
                .create(LINE_NAME, LINE_COLOR, upStation.getId(), downStation.getId(), DISTANCE))
                .header("Location");

        final Station newStation = 역_생성("대림역");
        final int newDistance = 5;
        final SectionRequest sectionRequest =
            SectionRequest.create(newStation.getId(), upStation.getId(), newDistance);

        // when
        final ExtractableResponse<Response> result = 구간_등록(uri, sectionRequest);
        구간_등록(uri, SectionRequest.create(역_생성("테스트").getId(), upStation.getId(), 4));

        // then
        final LineResponse lineResponse = 노선_조회(uri).body().as(LineResponse.class);

        assertThat(result.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(lineResponse.getStations()).hasSize(4);
        assertThat(lineResponse.getStations()).extracting("name")
            .containsExactlyInAnyOrder("대림역", "테스트", UP_STATION_NAME, DOWN_STATION_NAME);
    }

    @Test
    @DisplayName("구간 등록 - 성공(하행 종점 등록)")
    void insertSection_success_downStation() {
        // given
        final Station upStation = 상행역();
        final Station downStation = 하행역();
        final String uri =
            노선_생성(LineRequest
                .create(LINE_NAME, LINE_COLOR, upStation.getId(), downStation.getId(), DISTANCE))
                .header("Location");

        final Station newStation = 역_생성("홍대역");
        final int newDistance = 5;
        final SectionRequest sectionRequest =
            SectionRequest.create(newStation.getId(), upStation.getId(), newDistance);

        // when
        final ExtractableResponse<Response> result = 구간_등록(uri, sectionRequest);

        // then
        final LineResponse lineResponse = 노선_조회(uri).body().as(LineResponse.class);

        assertThat(result.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(lineResponse.getStations()).hasSize(3);
        assertThat(lineResponse.getStations()).extracting("name")
            .containsExactlyInAnyOrder(UP_STATION_NAME, DOWN_STATION_NAME, "홍대역");
    }

    @Test
    @DisplayName("구간 등록 - 성공(상행 기준 중간에 구간 등록)")
    void insertSection_success_middle_up() {
        // given
        final Station upStation = 상행역();
        final Station downStation = 하행역();
        final String uri =
            노선_생성(LineRequest
                .create(LINE_NAME, LINE_COLOR, upStation.getId(), downStation.getId(), DISTANCE))
                .header("Location");

        final Station newStation = 역_생성("홍대역");
        final int newDistance = 5;
        final SectionRequest sectionRequest =
            SectionRequest.create(upStation.getId(), newStation.getId(), newDistance);

        // when
        final ExtractableResponse<Response> result = 구간_등록(uri, sectionRequest);

        // then
        final LineResponse lineResponse = 노선_조회(uri).body().as(LineResponse.class);

        assertThat(result.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(lineResponse.getStations()).hasSize(3);
        assertThat(lineResponse.getStations()).extracting("name")
            .containsExactlyInAnyOrder(UP_STATION_NAME, "홍대역", DOWN_STATION_NAME);
    }

    @Test
    @DisplayName("구간 등록 - 성공(하행 기준 중간에 구간 등록)")
    void insertSection_success_middle_down() {
        // given
        final Station upStation = 상행역();
        final Station downStation = 하행역();
        final String uri =
            노선_생성(LineRequest
                .create(LINE_NAME, LINE_COLOR, upStation.getId(), downStation.getId(), DISTANCE))
                .header("Location");

        final Station newStation = 역_생성("홍대역");
        final int newDistance = 5;
        final SectionRequest sectionRequest =
            SectionRequest.create(newStation.getId(), downStation.getId(), newDistance);

        // when
        final ExtractableResponse<Response> result = 구간_등록(uri, sectionRequest);

        // then
        final LineResponse lineResponse = 노선_조회(uri).body().as(LineResponse.class);

        assertThat(result.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(lineResponse.getStations()).hasSize(3);
        assertThat(lineResponse.getStations()).extracting("name")
            .containsExactlyInAnyOrder(UP_STATION_NAME, "홍대역", DOWN_STATION_NAME);
    }

    @Test
    @DisplayName("구간 등록 - 실패(새로 추가할 거리가 기존 거리보다 같거나 큰 경우)")
    public void insertSection_fail_overDistance() {
        //given
        final Station upStation = 상행역();
        final Station downStation = 하행역();
        final String uri = 노선_생성(LineRequest
            .create(LINE_NAME, LINE_COLOR, upStation.getId(), downStation.getId(), DISTANCE))
            .header("Location");

        final Station newStation = stationDao.save(Station.create("홍대역"));

        //when
        final ExtractableResponse<Response> result =
            구간_등록(uri, SectionRequest.create(upStation.getId(), newStation.getId(), DISTANCE));

        //then
        assertThat(result.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("구간 등록 - 실패(존재하지 않는 역을 등록할 경우)")
    public void insertSection_fail_notExistStation() {
        // given
        final Station upStation = 상행역();
        final Station downStation = 하행역();
        final String uri = 노선_생성(LineRequest
            .create(LINE_NAME, LINE_COLOR, upStation.getId(), downStation.getId(), DISTANCE))
            .header("Location");

        // when
        final ExtractableResponse<Response> result =
            구간_등록(uri, SectionRequest.create(upStation.getId(), Long.MAX_VALUE, 1));

        // then
        assertThat(result.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("구간 등록 - 실패(이미 구간이 연결되어있을 경우)")
    public void insertSection_fail_alreadyConnected() {
        // given
        final Station upStation = 상행역();
        final Station downStation = 하행역();
        final String uri =
            노선_생성(LineRequest
                .create(LINE_NAME, LINE_COLOR, upStation.getId(), downStation.getId(), DISTANCE))
                .header("Location");

        final Station newStation = 역_생성("대림역");
        final int newDistance = 5;
        final SectionRequest sectionRequest =
            SectionRequest.create(newStation.getId(), upStation.getId(), newDistance);
        구간_등록(uri, sectionRequest);

        // when
        final ExtractableResponse<Response> result =
            구간_등록(uri, SectionRequest.create(upStation.getId(), downStation.getId(), DISTANCE));

        // then
        assertThat(result.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("구간 삭제 - 성공")
    public void dropSection() {
        // given
        final Station upStation = 상행역();
        final Station downStation = 하행역();
        final String uri =
            노선_생성(LineRequest
                .create(LINE_NAME, LINE_COLOR, upStation.getId(), downStation.getId(), DISTANCE))
                .header("Location");

        final Station newStation = 역_생성("대림역");
        final int newDistance = 5;
        final SectionRequest sectionRequest =
            SectionRequest.create(newStation.getId(), upStation.getId(), newDistance);
        구간_등록(uri, sectionRequest);

        // when
        final ExtractableResponse<Response> result = 구간_삭제(uri, upStation.getId());

        // then
        final LineResponse lineResponse = 노선_조회(uri).body().as(LineResponse.class);

        assertThat(result.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(lineResponse.getStations()).hasSize(2);
        assertThat(lineResponse.getStations()).extracting("name")
            .containsExactlyInAnyOrder(newStation.getName(), downStation.getName());
    }

    @Test
    @DisplayName("구간 삭제 - 실패(존재하지 않는 역 정보)")
    public void dropSection_fail_wrongStationId() {
        //given
        final String uri = 기본_노선_생성().header("Location");
        //when
        final ExtractableResponse<Response> result = 구간_삭제(uri, Long.MAX_VALUE);
        //then
        assertThat(result.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
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

    private ExtractableResponse<Response> 노선_조회(String uri) {
        return RestAssured.given().log().all()
            .when()
            .get(uri)
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 구간_등록(String uri, SectionRequest sectionRequest) {
        return RestAssured.given()
            .body(sectionRequest)
            .contentType(ContentType.JSON)
            .when()
            .post(uri + "/sections")
            .then()
            .extract();
    }

    private ExtractableResponse<Response> 구간_삭제(String uri, Long stationId) {
        return RestAssured.given()
            .contentType(ContentType.JSON)
            .when()
            .delete(uri + "/sections?stationId="+stationId)
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