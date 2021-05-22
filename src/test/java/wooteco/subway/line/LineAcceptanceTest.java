package wooteco.subway.line;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.line.section.dto.SectionRequest;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationDao;
import wooteco.subway.station.dto.StationResponse;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private static final String NOT_EXIST_ITEM_MESSAGE = "[ERROR] 해당 아이템이 존재하지 않습니다.";
    private static final String NO_INPUT_MESSAGE = "[ERROR] 입력값이 존재하지 않습니다.";
    private static final String DUPLICATE_MESSAGE = "[ERROR] 중복된 이름입니다.";
    private static final String ILLEGAL_USER_INPUT_MESSAGE = "[ERROR] 잘못된 입력입니다.";
    private static final LineRequest LINE_2_REQUEST = new LineRequest("2호선", "bg-green-600", 1L, 4L,
        10);
    private static final LineRequest LINE_3_REQUEST = new LineRequest("3호선", "bg-orange-600", 1L,
        3L, 13);
    private static final Station GANGNAM_STATION = new Station(1L, "강남역");
    private static final Station JAMSIL_STATION = new Station(2L, "잠실역");
    private static final Station YEOKSAM_STATION = new Station(3L, "역삼역");
    private static final Station SILLIM_STATION = new Station(4L, "신림역");

    private final StationDao stationDao;

    public LineAcceptanceTest(StationDao stationDao) {
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
    @DisplayName("지하철 노선 한개가 저장된다.")
    void create() {
        //given

        //when
        ExtractableResponse<Response> response = createLineAPI(LINE_2_REQUEST);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @Test
    @DisplayName("기존에 존재하는 노선 이름으로 노선을 생성한다.")
    void createLineWithDuplicateName() {
        createLineAPI(LINE_2_REQUEST);

        ExtractableResponse<Response> response = createLineAPI(LINE_2_REQUEST);
        thenBadRequestException(response, DUPLICATE_MESSAGE);
    }

    @Test
    @DisplayName("이름에 null 을 입력하여 노선을 생성하면 에러가 출력된다.")
    void createLineWithNameDataIsNull() {
        //given
        String name = null;
        String color = "bg-green-60";
        Long upStationId = 1L;
        Long downStationId = 2L;
        int distance = 10;

        //when
        ExtractableResponse<Response> response = createLineAPI(
            new LineRequest(name, color, upStationId, downStationId, distance));

        //then
        thenBadRequestException(response, NO_INPUT_MESSAGE);
    }

    @Test
    @DisplayName("이름에 공백을 입력하여 노선을 생성하면 에러가 출력된다.")
    void createLineWithNameDataIsSpace() {
        //given
        String name = "  ";
        String color = "bg-green-60";
        Long upStationId = 1L;
        Long downStationId = 2L;
        int distance = 10;

        //when
        ExtractableResponse<Response> response = createLineAPI(
            new LineRequest(name, color, upStationId, downStationId, distance));

        //then
        thenBadRequestException(response, NO_INPUT_MESSAGE);
    }

    @Test
    @DisplayName("컬러에 null 을 입력하여 노선을 생성하면 에러가 출력된다.")
    void createLineWithColorDataIsNull() {
        //given
        String name = "2호선";
        String color = null;
        Long upStationId = 1L;
        Long downStationId = 2L;
        int distance = 10;

        //when
        ExtractableResponse<Response> response = createLineAPI(
            new LineRequest(name, color, upStationId, downStationId, distance));

        //then
        thenBadRequestException(response, NO_INPUT_MESSAGE);
    }

    @Test
    @DisplayName("컬러에 공백을 입력하여 노선을 생성하면 에러가 출력된다.")
    void createLineWithColorDataIsSpace() {
        //given
        String name = "2호선";
        String color = " ";
        Long upStationId = 1L;
        Long downStationId = 2L;
        int distance = 10;

        //when
        ExtractableResponse<Response> response = createLineAPI(
            new LineRequest(name, color, upStationId, downStationId, distance));

        //then
        thenBadRequestException(response, NO_INPUT_MESSAGE);
    }

    @Test
    @DisplayName("upStationId에 null 을 입력하여 노선을 생성하면 에러가 출력된다.")
    void createLineWithUpStationIdDataIsNull() {
        //given
        String name = "2호선";
        String color = "bg-green-60";
        Long upStationId = null;
        Long downStationId = 2L;
        int distance = 10;

        //when
        ExtractableResponse<Response> response = createLineAPI(
            new LineRequest(name, color, upStationId, downStationId, distance));

        //then
        thenBadRequestException(response, NO_INPUT_MESSAGE);
    }

    @Test
    @DisplayName("upStationId에 0을 입력하여 노선을 생성하면 에러가 출력된다.")
    void createLineWithUpStationIdDataIsZero() {
        //given
        String name = "2호선";
        String color = "bg-green-60";
        Long upStationId = 0L;
        Long downStationId = 2L;
        int distance = 10;

        //when
        ExtractableResponse<Response> response = createLineAPI(
            new LineRequest(name, color, upStationId, downStationId, distance));

        //then
        thenBadRequestException(response, NO_INPUT_MESSAGE);
    }

    @Test
    @DisplayName("downStationId에 null 을 입력하여 노선을 생성하면 에러가 출력된다.")
    void createLineWithDownStationIdDataIsNull() {
        //given
        String name = "2호선";
        String color = "bg-green-60";
        Long upStationId = 1L;
        Long downStationId = null;
        int distance = 10;

        //when
        ExtractableResponse<Response> response = createLineAPI(
            new LineRequest(name, color, upStationId, downStationId, distance));

        //then
        thenBadRequestException(response, NO_INPUT_MESSAGE);
    }

    @Test
    @DisplayName("downStationId에 0을 입력하여 노선을 생성하면 에러가 출력된다.")
    void createLineWithDownStationIdDataIsZero() {
        //given
        String name = "2호선";
        String color = "bg-green-60";
        Long upStationId = 1L;
        Long downStationId = 0L;
        int distance = 10;

        //when
        ExtractableResponse<Response> response = createLineAPI(
            new LineRequest(name, color, upStationId, downStationId, distance));

        //then
        thenBadRequestException(response, NO_INPUT_MESSAGE);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    @DisplayName("downStationId에 0을 입력하여 노선을 생성하면 에러가 출력된다.")
    void createLineWithDistanceDataIsLessThanZero(int value) {
        //given
        String name = "2호선";
        String color = "bg-green-60";
        Long upStationId = 1L;
        Long downStationId = 2L;
        int distance = value;

        //when
        ExtractableResponse<Response> response = createLineAPI(
            new LineRequest(name, color, upStationId, downStationId, distance));

        //then
        thenBadRequestException(response, NO_INPUT_MESSAGE);
    }

    @Test
    @DisplayName("지하철역 목록을 조회한다.")
    void getLines() {
        /// given
        ExtractableResponse<Response> createResponse1 = createLineAPI(LINE_2_REQUEST);
        ExtractableResponse<Response> createResponse2 = createLineAPI(LINE_3_REQUEST);

        // when
        ExtractableResponse<Response> response = getLineAllAPI();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        List<Long> expectedLineIds = getExpectedLineIds(createResponse1, createResponse2);
        List<Long> resultLineIds = getResultLineIds(response);

        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @Test
    @DisplayName("id를 이용하여 지하철역을 조회한다.")
    public void getLine() {
        /// given
        createLineAPI(LINE_2_REQUEST);
        List<StationResponse> stations = Arrays
            .asList(new StationResponse(1L, "강남역"), new StationResponse(4L, "신림역"));

        // when
        ExtractableResponse<Response> response = getLineAPI();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        LineResponse lineResponse = response.as(LineResponse.class);
        assertThat(lineResponse).usingRecursiveComparison()
            .isEqualTo(new LineResponse(1L, "2호선", "bg-green-600", stations));
    }

    @Test
    @DisplayName("없는 id를 이용하여 지하철역을 조회하면 에러가 출력된다.")
    public void getLineWithNotExistItem() {
        /// given

        // when
        ExtractableResponse<Response> response = getLineAPI();

        // then
        thenBadRequestException(response, NOT_EXIST_ITEM_MESSAGE);
    }

    @Test
    @DisplayName("id를 기준으로 노선을 수정한다.")
    public void putLine() {
        /// given
        createLineAPI(LINE_2_REQUEST);

        // when
        ExtractableResponse<Response> response = updateLineAPI(LINE_3_REQUEST);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("없는 노선을 수정하려 하면 에러가 발생한다.")
    public void putLineWithNotExistItem() {
        // given

        // when
        ExtractableResponse<Response> response = updateLineAPI(LINE_3_REQUEST);

        //then
        thenBadRequestException(response, NOT_EXIST_ITEM_MESSAGE);
    }

    @Test
    @DisplayName("기존에 있는 이름으로 노선을 수정시 에러가 발생한다.")
    public void putLinWhitDuplicateName() {
        /// given
        createLineAPI(LINE_2_REQUEST);
        createLineAPI(LINE_3_REQUEST);

        // when
        ExtractableResponse<Response> response = updateLineAPI(LINE_3_REQUEST);

        //then
        thenBadRequestException(response, DUPLICATE_MESSAGE);
    }

    @Test
    @DisplayName("id를 이용해 노선을 삭제한다")
    public void deleteLine() {
        // given
        ExtractableResponse<Response> createResponse = createLineAPI(LINE_2_REQUEST);

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = deleteLineAPI(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
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
        thenBadRequestException(response, ILLEGAL_USER_INPUT_MESSAGE);
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
        thenBadRequestException(response, ILLEGAL_USER_INPUT_MESSAGE);
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
        thenBadRequestException(response, ILLEGAL_USER_INPUT_MESSAGE);
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
        thenBadRequestException(response, ILLEGAL_USER_INPUT_MESSAGE);
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
        thenBadRequestException(response, ILLEGAL_USER_INPUT_MESSAGE);
    }

    private ExtractableResponse<Response> deleteLineAPI(String uri) {
        return RestAssured.given()
            .when()
            .delete(uri)
            .then().log().all()
            .extract();
    }

    private List<Long> getResultLineIds(ExtractableResponse<Response> response) {
        return response.jsonPath().getList(".", LineResponse.class).stream()
            .map(LineResponse::getId)
            .collect(Collectors.toList());
    }

    private List<Long> getExpectedLineIds(ExtractableResponse<Response> createResponse1,
        ExtractableResponse<Response> createResponse2) {
        return Stream.of(createResponse1, createResponse2)
            .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
            .collect(Collectors.toList());
    }

    private ExtractableResponse<Response> getLineAllAPI() {
        return RestAssured.given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/lines")
            .then()
            .extract();
    }

    private ExtractableResponse<Response> updateLineAPI(LineRequest lineRequest) {
        return RestAssured.given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(lineRequest)
            .when()
            .put("/lines/1")
            .then()
            .extract();
    }

    private ExtractableResponse<Response> getLineAPI() {
        return RestAssured.given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/lines/1")
            .then()
            .extract();
    }

    private void thenBadRequestException(ExtractableResponse<Response> response, String message) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString()).isEqualTo(message);
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
}