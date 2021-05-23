package wooteco.subway.line.section;


import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.exception.SubwayException;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.line.section.dto.SectionRequest;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationDao;
import wooteco.subway.station.dto.StationResponse;

@DisplayName("구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    private static final LineRequest LINE_2_REQUEST = new LineRequest("2호선", "bg-green-600", 1L, 4L,
        10);
    private static final Station GANGNAM_STATION = new Station(1L, "강남역");
    private static final Station JAMSIL_STATION = new Station(2L, "잠실역");
    private static final Station YEOKSAM_STATION = new Station(3L, "역삼역");
    private static final Station SILLIM_STATION = new Station(4L, "신림역");

    private final StationDao stationDao;

    public SectionAcceptanceTest(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    @BeforeEach
    void beforeSetUp() {
        stationDao.save(GANGNAM_STATION);
        stationDao.save(JAMSIL_STATION);
        stationDao.save(YEOKSAM_STATION);
        stationDao.save(SILLIM_STATION);
    }

    @Test
    @DisplayName("노선에 포함된 상행,하행이 아닌 역을 삭제하면 앞뒤 역이 연결되어야 한다.")
    void DeleteStationOnTheLinenWithDeleteOtherStation() {
        //given
        createLineAPI(LINE_2_REQUEST);
        createSectionAPI(new SectionRequest(1L, 3L, 5));
        int stationId = 3;

        List<StationResponse> stations = Arrays.asList(
            new StationResponse(GANGNAM_STATION),
            new StationResponse(SILLIM_STATION)
        );

        //when
        ExtractableResponse<Response> response = deleteSectionAPI(stationId);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        ExtractableResponse<Response> lineResponse = getLineAPI();

        thenCheckSection(stations, lineResponse);
    }

    @Test
    @DisplayName("노선에 포함된 구간이 1개일때 구간을을 삭제하면 에러가 발생한다.")
    void DeleteSectionWithSectionSizeOne() {
        //given
        createLineAPI(LINE_2_REQUEST);
        int stationId = 1;

        //when
        ExtractableResponse<Response> response = deleteSectionAPI(stationId);

        //then
        thenBadRequestException(response,
            SubwayException.ILLEGAL_SECTION_DELETE_EXCEPTION.message());
    }

    @Test
    @DisplayName("Section 을 추가한다.")
    void createSection() {
        //given
        createLineAPI(LINE_2_REQUEST);
        SectionRequest sectionRequest = new SectionRequest(2L, 1L, 5);

        //when
        ExtractableResponse<Response> extract = createSectionAPI(sectionRequest);

        //then
        assertThat(extract.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("노선에 상행 구간이 추가되면 순서가 변경되어야 한다.")
    void getLineWithAddSectionAtInitLocation() {
        //given
        createLineAPI(LINE_2_REQUEST);
        SectionRequest sectionRequest = new SectionRequest(2L, 1L, 5);

        List<StationResponse> stations = Arrays.asList(
            new StationResponse(2L, JAMSIL_STATION.getName()),
            new StationResponse(1L, GANGNAM_STATION.getName()),
            new StationResponse(4L, SILLIM_STATION.getName())
        );

        //when
        createSectionAPI(sectionRequest);
        ExtractableResponse<Response> response = getLineAPI();

        //then
        thenCheckSection(stations, response);
    }

    @Test
    @DisplayName("노선에 하행 구간이 추가되면 순서가 변경되어야 한다.")
    void getLineWithAddSectionAtLastLocation() {
        //given
        createLineAPI(LINE_2_REQUEST);
        SectionRequest sectionRequest = new SectionRequest(4L, 2L, 5);

        List<StationResponse> stations = Arrays.asList(
            new StationResponse(GANGNAM_STATION),
            new StationResponse(SILLIM_STATION),
            new StationResponse(JAMSIL_STATION)
        );

        //when
        createSectionAPI(sectionRequest);
        ExtractableResponse<Response> response = getLineAPI();

        //then
        thenCheckSection(stations, response);
    }

    @Test
    @DisplayName("노선에 하행 앞에 구간이 추가되면 순서가 변경되어야 한다.")
    void getLineWithAddSectionAtLastLocation2() {
        //given
        createLineAPI(LINE_2_REQUEST);
        createSectionAPI(new SectionRequest(1L, 2L, 5));

        List<StationResponse> stations = Arrays.asList(
            new StationResponse(GANGNAM_STATION),
            new StationResponse(JAMSIL_STATION),
            new StationResponse(YEOKSAM_STATION),
            new StationResponse(SILLIM_STATION)
        );

        //when
        createSectionAPI(new SectionRequest(3L, 4L, 3));
        ExtractableResponse<Response> response = getLineAPI();

        //then
        thenCheckSection(stations, response);
    }

    @Test
    @DisplayName("같은 구간을 추가하면 에러가 발생한다.")
    void addSectionWithEqualSection() {
        //given
        createLineAPI(LINE_2_REQUEST);
        SectionRequest sectionRequest = new SectionRequest(1L, 4L, 10);

        //when
        ExtractableResponse<Response> response = createSectionAPI(sectionRequest);

        //then
        thenBadRequestException(response, SubwayException.DUPLICATE_SECTION_EXCEPTION.message());
    }

    @Test
    @DisplayName("노선에 이미 포함된 역이 들어간 구간을 추가하면 에러가 발생한다.")
    void addSectionWithEqualTwoStation() {
        //given
        createLineAPI(LINE_2_REQUEST);
        createSectionAPI(new SectionRequest(4L, 3L, 10));

        SectionRequest sectionRequest = new SectionRequest(1L, 3L, 10);

        //when
        ExtractableResponse<Response> response = createSectionAPI(sectionRequest);

        //then
        thenBadRequestException(response, SubwayException.DUPLICATE_SECTION_EXCEPTION.message());
    }

    @ParameterizedTest
    @ValueSource(ints = {10, 11})
    @DisplayName("노선의 중간에 구간이 추가될때 기존구간보다 거리가 크거나 같으면 에러가 발생한다.")
    void addSectionWithOverDistance(int value) {
        //given
        createLineAPI(LINE_2_REQUEST);
        SectionRequest sectionRequest = new SectionRequest(1L, 2L, value);

        //when
        ExtractableResponse<Response> response = createSectionAPI(sectionRequest);

        //then
        thenBadRequestException(response,
            SubwayException.ILLEGAL_SECTION_DISTANCE_EXCEPTION.message());
    }

    @Test
    @DisplayName("노선에 포함되지 않은 역 2개가 포함된 구간을 추가하면 에러가 발생한다.")
    void addSectionWithNotHaveStation() {
        //given
        createLineAPI(LINE_2_REQUEST);

        SectionRequest sectionRequest = new SectionRequest(2L, 3L, 10);

        //when
        ExtractableResponse<Response> response = createSectionAPI(sectionRequest);

        //then
        thenBadRequestException(response, SubwayException.ILLEGAL_SECTION_EXCEPTION.message());
    }

    @Test
    @DisplayName("노선에 포함된 상행역을 삭제하면 다음역이 상행역이 되어야 한다.")
    void DeleteStationOnTheLinenWithDeleteUpStation() {
        //given
        createLineAPI(LINE_2_REQUEST);
        createSectionAPI(new SectionRequest(1L, 3L, 5));
        int stationId = 1;

        List<StationResponse> stations = Arrays.asList(
            new StationResponse(YEOKSAM_STATION),
            new StationResponse(SILLIM_STATION)
        );

        //when
        ExtractableResponse<Response> response = deleteSectionAPI(stationId);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        ExtractableResponse<Response> lineResponse = getLineAPI();

        thenCheckSection(stations, lineResponse);
    }

    private ExtractableResponse<Response> getLineAPI() {
        return RestAssured.given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/lines/1")
            .then()
            .extract();
    }

    @Test
    @DisplayName("노선에 포함된 하행역을 삭제하면 이전역이 하행역이 되어야 한다.")
    void DeleteStationOnTheLinenWithDeleteDownStation() {
        //given
        createLineAPI(LINE_2_REQUEST);
        createSectionAPI(new SectionRequest(1L, 3L, 5));
        int stationId = 4;

        List<StationResponse> stations = Arrays.asList(
            new StationResponse(GANGNAM_STATION),
            new StationResponse(YEOKSAM_STATION)
        );

        //when
        ExtractableResponse<Response> response = deleteSectionAPI(stationId);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        ExtractableResponse<Response> lineResponse = getLineAPI();

        thenCheckSection(stations, lineResponse);
    }

    private void thenCheckSection(List<StationResponse> stations,
        ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        LineResponse lineResponse = response.as(LineResponse.class);

        assertThat(lineResponse.getStations())
            .usingRecursiveComparison()
            .isEqualTo(stations);
    }

    private ExtractableResponse<Response> deleteSectionAPI(int deleteStationId) {
        return RestAssured.given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .delete("/lines/1/sections?stationId=" + deleteStationId)
            .then()
            .extract();
    }

    private void thenBadRequestException(ExtractableResponse<Response> response, String message) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString()).isEqualTo(message);
    }
}
