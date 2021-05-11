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

    private ExtractableResponse<Response> createResponse;

    @BeforeEach
    void set() {
        stationDao.save(STATION_1);
        stationDao.save(STATION_2);
        stationDao.save(STATION_3);
        stationDao.save(STATION_4);

        createResponse = postResponse(LINE_REQUEST, "/lines");
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
        // when
        String uri = createResponse.header("Location");
        SectionRequest sectionRequest = new SectionRequest(4L, 2L, 5);
        ExtractableResponse<Response> response = postResponse(sectionRequest, uri + "/sections");

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
        // when
        String uri = createResponse.header("Location");
        SectionRequest sectionRequest = new SectionRequest(3L, 1L, 5);
        ExtractableResponse<Response> response = postResponse(sectionRequest, uri + "/sections");

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
        // when
        String uri = createResponse.header("Location");
        SectionRequest sectionRequest = new SectionRequest(1L, 3L, 4);
        ExtractableResponse<Response> response = postResponse(sectionRequest, uri + "/sections");

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
        // when
        String uri = createResponse.header("Location");
        SectionRequest sectionRequest = new SectionRequest(3L, 4L, 4);
        ExtractableResponse<Response> response = postResponse(sectionRequest, uri + "/sections");

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
        String uri = createResponse.header("Location");
        SectionRequest sectionRequest1 = new SectionRequest(1L, 2L, 4);
        ExtractableResponse<Response> response1 = postResponse(sectionRequest1, uri + "/sections");

        // when
        SectionRequest sectionRequest2 = new SectionRequest(2L, 3L, 3);
        ExtractableResponse<Response> response2 = postResponse(sectionRequest2, uri + "/sections");
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
        // when
        String uri = createResponse.header("Location");
        SectionRequest sectionRequest = new SectionRequest(1L, 3L, 10);
        ExtractableResponse<Response> response = postResponse(sectionRequest, uri + "/sections");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선 중간에 구간 추가 시 상,하행역 모두 존재할 경우")
    @Test
    public void addSectionIfSameStations() {
        // given
        // when
        String uri = createResponse.header("Location");
        SectionRequest sectionRequest = new SectionRequest(1L, 4L, 4);
        ExtractableResponse<Response> response = postResponse(sectionRequest, uri + "/sections");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선 중간에 구간 추가 시 상,하행역 모두 존재할 경우 (상,하행이 반대인 상황)")
    @Test
    public void addSectionIfSameStationsReverse() {
        // given
        // when
        String uri = createResponse.header("Location");
        SectionRequest sectionRequest = new SectionRequest(4L, 1L, 4);
        ExtractableResponse<Response> response = postResponse(sectionRequest, uri + "/sections");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선 중간에 구간 추가 시 상,하행역 모두 존재 X")
    @Test
    public void addSectionIfNotExistSameStations() {
        // given
        // when
        String uri = createResponse.header("Location");
        SectionRequest sectionRequest = new SectionRequest(2L, 3L, 4);
        ExtractableResponse<Response> response = postResponse(sectionRequest, uri + "/sections");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선에 추가할 구간의 상,하행역이 같을 경우")
    @Test
    public void addSectionIfEachStationIsSame() {
        // given
        // when
        String uri = createResponse.header("Location");
        SectionRequest sectionRequest = new SectionRequest(1L, 1L, 4);
        ExtractableResponse<Response> response = postResponse(sectionRequest, uri + "/sections");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선에 등록된 상행 종점역을 삭제한다.")
    @Test
    public void deleteUpLastStation() {
        // given
        String uri = createResponse.header("Location");
        SectionRequest sectionRequest = new SectionRequest(1L, 2L, 4);
        postResponse(sectionRequest, uri + "/sections");

        // when
        ExtractableResponse<Response> deleteResponse = deleteResponse(uri + "/sections", 1L);
        ExtractableResponse<Response> getResponse = getResponse(uri);

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        final LineResponse lineResponse = getResponse.body().as(LineResponse.class);
        assertThat(lineResponse.getStations()).containsExactly(new StationResponse(STATION_2),
                new StationResponse(STATION_4));
    }

    @DisplayName("노선에 등록된 하행 종점역을 삭제")
    @Test
    public void deleteDownLastStation() {
        // given
        String uri = createResponse.header("Location");
        SectionRequest sectionRequest = new SectionRequest(1L, 2L, 4);
        postResponse(sectionRequest, uri + "/sections");

        // when
        ExtractableResponse<Response> deleteResponse = deleteResponse(uri + "/sections", 4L);
        ExtractableResponse<Response> getResponse = getResponse(uri);

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        final LineResponse lineResponse = getResponse.body().as(LineResponse.class);
        assertThat(lineResponse.getStations()).containsExactly(new StationResponse(STATION_1),
                new StationResponse(STATION_2));
    }

    @DisplayName("노선에 등록된 역 중 상,하행 종점역을 제외한 나머지 역 하나 삭제")
    @Test
    public void deleteStation() {
        // given
        String uri = createResponse.header("Location");
        SectionRequest sectionRequest = new SectionRequest(1L, 2L, 4);
        postResponse(sectionRequest, uri + "/sections");

        // when
        ExtractableResponse<Response> deleteResponse = deleteResponse(uri + "/sections", 2L);
        ExtractableResponse<Response> getResponse = getResponse(uri);

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        final LineResponse lineResponse = getResponse.body().as(LineResponse.class);
        assertThat(lineResponse.getStations()).containsExactly(new StationResponse(STATION_1),
                new StationResponse(STATION_4));
    }

    @DisplayName("노선에 구간이 한 개 밖에 없을 때 역 삭제 시 예외")
    @Test
    public void deleteWhenOnlyOneSection() {
        // given
        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> deleteResponse = deleteResponse(uri + "/sections", 1L);

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> postResponse(Object object, String url) {
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

    private ExtractableResponse<Response> deleteResponse(String url, Long stationId) {
        return RestAssured.given().log().all()
                .param("stationId", stationId)
                .when()
                .delete(url)
                .then().log().all()
                .extract();
    }

}