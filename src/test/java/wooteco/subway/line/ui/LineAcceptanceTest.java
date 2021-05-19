package wooteco.subway.line.ui;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.line.application.LineService;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.line.dto.LineUpdateRequest;
import wooteco.subway.line.dto.SectionRequest;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.domain.StationDao;
import wooteco.subway.station.dto.StationResponse;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("노선역 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @Autowired
    private StationDao stationDao;

    @Autowired
    private LineService lineService;

    private Station station1;
    private Station station2;
    private Station station3;
    private Station station4;
    private Line line;

    @BeforeEach
    void init() {
        this.station1 = stationDao.save(new Station("백기역"));
        this.station2 = stationDao.save(new Station("흑기역"));
        this.station3 = stationDao.save(new Station("아마찌역"));
        this.station4 = stationDao.save(new Station("검프역"));
        this.line = new Line("백기선", "bg-red-600");

        LineResponse lineResponse = lineService.save(new LineRequest(line.name(), line.color(), station1.id(), station2.id(), 7));
        this.line = new Line(lineResponse.getId(), lineResponse.getName(), lineResponse.getColor());
    }

    @DisplayName("노선을 생성한다.")
    @Test
    void createStation() {
        // given
        String newLineName = "신분당선";
        String newLineColor = "bg-black-500";
        Long upStationId = 3L;
        Long downStationId = 4L;
        int distance = 5;
        LineRequest lineRequest = new LineRequest(newLineName, newLineColor, upStationId, downStationId, distance);

        // when
        ExtractableResponse<Response> response = createLineToHTTP(lineRequest);
        LineResponse lineResponse = response.body().as(LineResponse.class);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
        assertThat(stationResponsesToStrings(lineResponse.getStations())).containsExactly(station3.name(), station4.name());
    }

    @DisplayName("기존에 존재하는 노선의 이름으로 노선을 생성하면 예외가 발생한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        String newLineName = line.name();
        String newLineColor = "bg-black-500";
        Long upStationId = 3L;
        Long downStationId = 4L;
        int distance = 5;
        LineRequest lineRequest = new LineRequest(newLineName, newLineColor, upStationId, downStationId, distance);

        // when
        ExtractableResponse<Response> response = createLineToHTTP(lineRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("기존에 존재하는 노선의 색깔로 노선을 생성하면 예외가 발생한다.")
    @Test
    void createStationWithDuplicateColor() {
        // given
        String newLineName = "신분당선";
        String newLineColor = line.color();
        Long upStationId = 3L;
        Long downStationId = 4L;
        int distance = 5;
        LineRequest lineRequest = new LineRequest(newLineName, newLineColor, upStationId, downStationId, distance);

        // when
        ExtractableResponse<Response> response = createLineToHTTP(lineRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("모든 지하철 노선을 조회한다.")
    @Test
    void getLines() {
        // given
        String newLineName = "신분당선";
        String newLineColor = "bg-black-500";
        Long upStationId = station3.id();
        Long downStationId = station4.id();
        int distance = 5;
        LineRequest lineRequest = new LineRequest(newLineName, newLineColor, upStationId, downStationId, distance);

        // when
        ExtractableResponse<Response> createResponse = createLineToHTTP(lineRequest);
        ExtractableResponse<Response> findResponse = findAllLineToHTTP();

        LineResponse lineResponse = createResponse.body().as(LineResponse.class);
        List<Long> resultLineIds = findResponse.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());

        // then
        assertThat(findResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(resultLineIds).containsExactly(line.id(), lineResponse.getId());
    }

    @DisplayName("단일 노선을 조회한다.")
    @Test
    void findLineByIdToHTTP() {
        // given
        String newLineName = "신분당선";
        String newLineColor = "bg-black-500";
        Long upStationId = 3L;
        Long downStationId = 4L;
        int distance = 5;
        LineRequest lineRequest = new LineRequest(newLineName, newLineColor, upStationId, downStationId, distance);

        // when
        ExtractableResponse<Response> createResponse = createLineToHTTP(lineRequest);
        LineResponse createdResponse = createResponse.body().as(LineResponse.class);

        ExtractableResponse<Response> findLineResponse = findLineByIdToHTTP(createdResponse.getId());
        LineResponse findResponse = findLineResponse.body().as(LineResponse.class);

        // then
        assertThat(findLineResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(findResponse.getId()).isEqualTo(createdResponse.getId());
        assertThat(findResponse.getName()).isEqualTo(createdResponse.getName());
        assertThat(findResponse.getColor()).isEqualTo(createdResponse.getColor());
        assertThat(stationResponsesToStrings(findLineResponse.jsonPath().getList("stations", StationResponse.class))).containsExactly("아마찌역", "검프역");
    }

    @DisplayName("노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        String newLineName = "신분당선";
        String newLineColor = "bg-black-500";
        LineUpdateRequest lineUpdateRequest = new LineUpdateRequest(newLineName, newLineColor);

        // when
        ExtractableResponse<Response> updateResponse = updateLineByIdToHTTP(line.id(), lineUpdateRequest);
        ExtractableResponse<Response> findLineResponse = findLineByIdToHTTP(line.id());

        LineResponse findResponse = findLineResponse.body().as(LineResponse.class);

        // then
        assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(findResponse.getName()).isEqualTo(newLineName);
        assertThat(findResponse.getColor()).isEqualTo(newLineColor);
    }

    @DisplayName("노선을 제거한다.")
    @Test
    void deleteLine() {
        // given

        // when
        ExtractableResponse<Response> response = deleteLineByIdToHTTP(line.id());
        ExtractableResponse<Response> findLineResponse = findLineByIdToHTTP(line.id());

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(findLineResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선 제거시 없는 노선이면 예외가 발생한다.")
    @Test
    void deleteStation() {
        //given

        //when
        ExtractableResponse<Response> response = deleteLineByIdToHTTP(-1L);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("구간을 제거한다. (상행 종점역)")
    @Test
    void deleteUpwardEndPointStation() {
        int distance = 5;
        SectionRequest sectionRequest = new SectionRequest(station2.id(), station3.id(), distance);

        //when
        ExtractableResponse<Response> addResponse = addSectionToHTTP(line.id(), sectionRequest);
        ExtractableResponse<Response> response = deleteSectionByStationIdToHTTP(line.id(), station1.id());
        ExtractableResponse<Response> findLineResponse = findLineByIdToHTTP(line.id());

        LineResponse findResponse = findLineResponse.body().as(LineResponse.class);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(stationResponsesToStrings(findResponse.getStations())).containsExactly(station2.name(), station3.name());
    }

    @DisplayName("구간을 제거한다. (중간역)")
    @Test
    void deleteMiddlewardStation() {
        int distance = 5;
        SectionRequest sectionRequest = new SectionRequest(station2.id(), station3.id(), distance);

        //when
        ExtractableResponse<Response> addResponse = addSectionToHTTP(line.id(), sectionRequest);
        ExtractableResponse<Response> response = deleteSectionByStationIdToHTTP(line.id(), station2.id());
        ExtractableResponse<Response> findLineResponse = findLineByIdToHTTP(line.id());

        LineResponse findResponse = findLineResponse.body().as(LineResponse.class);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(stationResponsesToStrings(findResponse.getStations())).containsExactly(station1.name(), station3.name());
    }

    @DisplayName("구간을 제거한다. (하행 종점역)")
    @Test
    void deleteDownwardEndPointStation() {
        int distance = 5;
        SectionRequest sectionRequest = new SectionRequest(station2.id(), station3.id(), distance);

        //when
        ExtractableResponse<Response> addResponse = addSectionToHTTP(line.id(), sectionRequest);
        ExtractableResponse<Response> response = deleteSectionByStationIdToHTTP(line.id(), station3.id());
        ExtractableResponse<Response> findLineResponse = findLineByIdToHTTP(line.id());

        LineResponse findResponse = findLineResponse.body().as(LineResponse.class);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(stationResponsesToStrings(findResponse.getStations())).containsExactly(station1.name(), station2.name());
    }

    private static Stream<Arguments> stationIds() {
        return Stream.of(
                Arguments.arguments(1L, 2L),
                Arguments.arguments(2L, 3L),
                Arguments.arguments(1L, 3L),
                Arguments.arguments(3L, 1L)
        );
    }

    @ParameterizedTest
    @DisplayName("구간 등록시 상행역화 하행역이 이미 등록 되어있다면 예외가 발생한다. ")
    @MethodSource("stationIds")
    void registrationDuplicateException(Long upStationId, Long downStationId) {
        //given
        int distance = 3;
        SectionRequest acceptSectionRequest = new SectionRequest(station2.id(), station3.id(), 7);
        SectionRequest exceptionSectionRequest = new SectionRequest(upStationId, downStationId, distance);

        //when
        ExtractableResponse<Response> acceptSaveResponse = addSectionToHTTP(line.id(), acceptSectionRequest);

        ExtractableResponse<Response> exceptionR = addSectionToHTTP(line.id(), exceptionSectionRequest);


        //then
        assertThat(exceptionR.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private static Stream<Arguments> exceptionStationIds() {
        return Stream.of(
                Arguments.arguments(3L, 4L),
                Arguments.arguments(4L, 3L),
                Arguments.arguments(5L, 6L),
                Arguments.arguments(0L, 6L)
        );
    }

    @ParameterizedTest
    @DisplayName("구간 등록시 상행역화 하행역 둘다 노선에 등록 되어있지 않다면 예외가 발생한다. ")
    @MethodSource("exceptionStationIds")
    void registrationNotFoundException(Long upStationId, Long downStationId) {
        //given
        int distance = 3;
        SectionRequest exceptionSectionRequest = new SectionRequest(upStationId, downStationId, distance);

        //when
        ExtractableResponse<Response> exceptionResponse = addSectionToHTTP(line.id(), exceptionSectionRequest);

        //then
        assertThat(exceptionResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("1개의 구간만 있을 때, 역을 삭제를 하려하면 예외가 발생한다")
    void deleteException() {
        //given
        int distance = 5;

        //when
        ExtractableResponse<Response> findLineResponse = deleteSectionByStationIdToHTTP(line.id(), station1.id());

        //then
        assertThat(findLineResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("상행 종점역을 저장한다")
    void upwardEndPointRegistration() {
        //given
        int distance = 5;
        SectionRequest sectionRequest = new SectionRequest(station3.id(), station1.id(), distance);

        //when
        ExtractableResponse<Response> addResponse = addSectionToHTTP(line.id(), sectionRequest);
        ExtractableResponse<Response> findLineResponse = findLineByIdToHTTP(line.id());
        LineResponse findResponse = findLineResponse.body().as(LineResponse.class);

        //then
        assertThat(addResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(findResponse.getId()).isEqualTo(line.id());
        assertThat(stationResponsesToStrings(findResponse.getStations())).containsExactly(station3.name(), station1.name(), station2.name());
    }

    @Test
    @DisplayName("허행 종점역을 저장한다")
    void downwardEndPointRegistration() {
        //given
        int distance = 5;
        SectionRequest sectionRequest = new SectionRequest(station2.id(), station3.id(), distance);

        //when
        ExtractableResponse<Response> addResponse = addSectionToHTTP(line.id(), sectionRequest);
        ExtractableResponse<Response> findLineResponse = findLineByIdToHTTP(line.id());
        LineResponse findResponse = findLineResponse.body().as(LineResponse.class);

        //then
        assertThat(addResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(findResponse.getId()).isEqualTo(line.id());
        assertThat(stationResponsesToStrings(findResponse.getStations())).containsExactly(station1.name(), station2.name(), station3.name());
    }

    private ExtractableResponse<Response> createLineToHTTP(final LineRequest lineRequest) {
        return RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then()
                .log().all()
                .extract();
    }

    private ExtractableResponse<Response> findAllLineToHTTP() {
        return RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> findLineByIdToHTTP(Long lineId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines/{id}", lineId)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> updateLineByIdToHTTP(Long lineId, LineUpdateRequest lineUpdateRequest) {
        return RestAssured.given().log().all()
                .body(lineUpdateRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/{id}", lineId)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> deleteLineByIdToHTTP(Long lineId) {
        return RestAssured.given().log().all()
                .when()
                .delete("/lines/{id}", lineId)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> addSectionToHTTP(Long lineId, SectionRequest sectionRequest) {
        return RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/{id}/sections", lineId)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> deleteSectionByStationIdToHTTP(Long lineId, Long stationId) {
        return RestAssured.given().log().all()
                .when()
                .queryParam("stationId", stationId)
                .delete("/lines/{id}/sections", lineId)
                .then().log().all()
                .extract();
    }

    private List<String> stationResponsesToStrings(final List<StationResponse> response) {
        return response.stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());
    }
}
