package wooteco.subway.section.controller;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.line.controller.dto.LineRequest;
import wooteco.subway.line.controller.dto.LineResponse;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.section.controller.dto.SectionRequest;
import wooteco.subway.station.controller.dto.StationResponse;
import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.domain.Station;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 테스트")
class SectionAcceptanceTest extends AcceptanceTest {

    private static final String TEST_LINE_NAME = "2호선";
    private static final String TEST_COLOR_NAME = "orange darken-4";
    private static final Station STATION_1 = new Station(1L, "강남역");
    private static final Station STATION_2 = new Station(2L, "역삼역");
    private static final Station STATION_3 = new Station(3L, "잠실역");
    private static final Station STATION_4 = new Station(4L, "구의역");


    private static final LineRequest LINE_REQUEST = new LineRequest(TEST_LINE_NAME, TEST_COLOR_NAME,
            1L, 4L, 10);

    @Autowired
    private LineDao lineDao;

    @Autowired
    private StationDao stationDao;

    @BeforeEach
    void set() {
        stationDao.save(STATION_1);
        stationDao.save(STATION_2);
        stationDao.save(STATION_3);
        stationDao.save(STATION_4);
    }

    @AfterEach
    void cleanDB() {
        lineDao.deleteAll();
        stationDao.deleteAll();
    }

    @DisplayName("하행 종점 구간을 추가한다.")
    @Test
    public void addUpLastSection() {
        // given
        ExtractableResponse<Response> createResponse = getPostResponse(LINE_REQUEST, "/lines");

        // when
        String uri = createResponse.header("Location");
        SectionRequest sectionRequest = new SectionRequest(4L, 2L, 5);
        ExtractableResponse<Response> response = getPostResponse(sectionRequest, uri + "/sections");

        ExtractableResponse<Response> getResponse = getResponse(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        final LineResponse lineResponse = getResponse.body().as(LineResponse.class);
        assertThat(lineResponse.getStations()).containsExactly(new StationResponse(STATION_1),
                new StationResponse(STATION_4),
                new StationResponse(STATION_2));
    }

    @DisplayName("상행 종점 구간을 추가한다.")
    @Test
    public void addDownLastSection() {
        // given
        ExtractableResponse<Response> createResponse = getPostResponse(LINE_REQUEST, "/lines");

        // when
        String uri = createResponse.header("Location");
        SectionRequest sectionRequest = new SectionRequest(3L, 1L, 5);
        ExtractableResponse<Response> response = getPostResponse(sectionRequest, uri + "/sections");

        ExtractableResponse<Response> getResponse = getResponse(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        final LineResponse lineResponse = getResponse.body().as(LineResponse.class);
        assertThat(lineResponse.getStations()).containsExactly(new StationResponse(STATION_3),
                new StationResponse(STATION_1),
                new StationResponse(STATION_4));
    }

    @DisplayName("노선에 구간을 추가한다. (상행역이 같음)")
    @Test
    public void addSection1() {
        // given
        ExtractableResponse<Response> createResponse = getPostResponse(LINE_REQUEST, "/lines");
        // when
        String uri = createResponse.header("Location");
        SectionRequest sectionRequest = new SectionRequest(1L, 3L, 4);
        ExtractableResponse<Response> response = getPostResponse(sectionRequest, uri + "/sections");

        ExtractableResponse<Response> getResponse = getResponse(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        final LineResponse lineResponse = getResponse.body().as(LineResponse.class);
        assertThat(lineResponse.getStations()).containsExactly(new StationResponse(STATION_1),
                new StationResponse(STATION_3),
                new StationResponse(STATION_4));
    }

    @DisplayName("노선에 구간을 추가한다. (하행역이 같음)")
    @Test
    public void addSection2() {
        // given
        ExtractableResponse<Response> createResponse = getPostResponse(LINE_REQUEST, "/lines");
        // when
        String uri = createResponse.header("Location");
        SectionRequest sectionRequest = new SectionRequest(3L, 4L, 4);
        ExtractableResponse<Response> response = getPostResponse(sectionRequest, uri + "/sections");

        ExtractableResponse<Response> getResponse = getResponse(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        final LineResponse lineResponse = getResponse.body().as(LineResponse.class);
        assertThat(lineResponse.getStations()).containsExactly(new StationResponse(STATION_1),
                new StationResponse(STATION_3),
                new StationResponse(STATION_4));
    }

    @DisplayName("노선 중간에 구간을 추가한다.")
    @Test
    public void addSection3() {
        // given
        ExtractableResponse<Response> createResponse = getPostResponse(LINE_REQUEST, "/lines");
        String uri = createResponse.header("Location");
        SectionRequest sectionRequest1 = new SectionRequest(1L, 2L, 4);
        ExtractableResponse<Response> response1 = getPostResponse(sectionRequest1, uri + "/sections");

        // when
        SectionRequest sectionRequest2 = new SectionRequest(2L, 3L, 3);
        ExtractableResponse<Response> response2 = getPostResponse(sectionRequest2, uri + "/sections");
        ExtractableResponse<Response> getResponse = getResponse(uri);

        // then
        assertThat(response2.statusCode()).isEqualTo(HttpStatus.OK.value());
        final LineResponse lineResponse = getResponse.body().as(LineResponse.class);
        assertThat(lineResponse.getStations()).containsExactly(new StationResponse(STATION_1),
                new StationResponse(STATION_2),
                new StationResponse(STATION_3),
                new StationResponse(STATION_4));
    }

    @DisplayName("노선 중간에 구간 추가할 때 거리가 원래 구간보다 같거나 클 경우 예외")
    @Test
    public void addSectionOverDistance() {
        // given
        ExtractableResponse<Response> createResponse = getPostResponse(LINE_REQUEST, "/lines");

        // when
        String uri = createResponse.header("Location");
        SectionRequest sectionRequest = new SectionRequest(1L, 3L, 15);
        ExtractableResponse<Response> response = getPostResponse(sectionRequest, uri + "/sections");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선 중간에 구간 추가 시 상,하행역 모두 존재할 경우")
    @Test
    public void addSectionIfSameStations() {
        // given
        ExtractableResponse<Response> createResponse = getPostResponse(LINE_REQUEST, "/lines");

        // when
        String uri = createResponse.header("Location");
        SectionRequest sectionRequest = new SectionRequest(1L, 4L, 4);
        ExtractableResponse<Response> response = getPostResponse(sectionRequest, uri + "/sections");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선 중간에 구간 추가 시 상,하행역 모두 존재할 경우 (상,하행이 반대인 상황)")
    @Test
    public void addSectionIfSameStationsReverse() {
        // given
        ExtractableResponse<Response> createResponse = getPostResponse(LINE_REQUEST, "/lines");

        // when
        String uri = createResponse.header("Location");
        SectionRequest sectionRequest = new SectionRequest(4L, 1L, 4);
        ExtractableResponse<Response> response = getPostResponse(sectionRequest, uri + "/sections");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선 중간에 구간 추가 시 상,하행역 모두 존재 X")
    @Test
    public void addSectionIfNotExistSameStations() {
        // given
        ExtractableResponse<Response> createResponse = getPostResponse(LINE_REQUEST, "/lines");

        // when
        String uri = createResponse.header("Location");
        SectionRequest sectionRequest = new SectionRequest(2L, 3L, 4);
        ExtractableResponse<Response> response = getPostResponse(sectionRequest, uri + "/sections");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선에 추가할 구간의 상,하행역이 같을 경우")
    @Test
    public void addSectionIfEachStationIsSame() {
        // given
        ExtractableResponse<Response> createResponse = getPostResponse(LINE_REQUEST, "/lines");

        // when
        String uri = createResponse.header("Location");
        SectionRequest sectionRequest = new SectionRequest(1L, 1L, 4);
        ExtractableResponse<Response> response = getPostResponse(sectionRequest, uri + "/sections");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> getPostResponse(Object object, String url) {
        return RestAssured.given().log().all()
                .body(object)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(url)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> getResponse(String uri) {
        return RestAssured.given().log().all()
                .when()
                .get(uri)
                .then().log().all()
                .extract();
    }

}