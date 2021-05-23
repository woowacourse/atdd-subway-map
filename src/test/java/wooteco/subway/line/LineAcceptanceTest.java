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
import wooteco.subway.exception.SubwayException;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationDao;
import wooteco.subway.station.dto.StationResponse;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

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
        thenBadRequestException(response, SubwayException.DUPLICATE_LINE_EXCEPTION.message());
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
        thenBadRequestException(response,
            SubwayException.INVALID_INPUT_NAME_OR_COLOR_EXCEPTION.message());
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
        thenBadRequestException(response, SubwayException.INVALID_INPUT_NAME_OR_COLOR_EXCEPTION
            .message());
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
        thenBadRequestException(response,
            SubwayException.INVALID_INPUT_NAME_OR_COLOR_EXCEPTION.message());
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
        thenBadRequestException(response,
            SubwayException.INVALID_INPUT_NAME_OR_COLOR_EXCEPTION.message());
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
        thenBadRequestException(response,
            SubwayException.INVALID_INPUT_STATION_ID_EXCEPTION.message());
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
        thenBadRequestException(response,
            SubwayException.INVALID_INPUT_STATION_ID_EXCEPTION.message());
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
        thenBadRequestException(response,
            SubwayException.INVALID_INPUT_STATION_ID_EXCEPTION.message());
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
        thenBadRequestException(response,
            SubwayException.INVALID_INPUT_STATION_ID_EXCEPTION.message());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    @DisplayName("거리를 0이하로 노선을 생성하면 에러가 출력된다.")
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
        thenBadRequestException(response,
            SubwayException.INVALID_INPUT_DISTANCE_EXCEPTION.message());
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
    @DisplayName("없는 id를 이용하여 노선을 조회하면 에러가 출력된다.")
    public void getLineWithNotExistItem() {
        /// given

        // when
        ExtractableResponse<Response> response = getLineAPI();

        // then
        thenBadRequestException(response, SubwayException.NOT_EXIST_LINE_EXCEPTION.message());
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
        thenBadRequestException(response, SubwayException.NOT_EXIST_LINE_EXCEPTION.message());
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
        thenBadRequestException(response, SubwayException.DUPLICATE_LINE_EXCEPTION.message());
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
}