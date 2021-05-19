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
    private static final String BASE_URL = "/lines";
    private static final String BASE_URL_WITH_ID = "/lines/{id}";
    private static final String BASE_URL_WITH_ID_SECTION = "/lines/{id}/sections";

    @Autowired
    private StationDao stationDao;
    @Autowired
    private LineService lineService;

    private Station 백기역;
    private Station 흑기역;
    private Station 낙성대역;
    private Station 검프역;
    private Line 백기선;

    private static Stream<Arguments> stationIds() {
        return Stream.of(
                Arguments.arguments(1L, 2L),
                Arguments.arguments(2L, 3L),
                Arguments.arguments(1L, 3L),
                Arguments.arguments(3L, 1L)
        );
    }

    private static Stream<Arguments> exceptionStationIds() {
        return Stream.of(
                Arguments.arguments(3L, 4L),
                Arguments.arguments(4L, 3L),
                Arguments.arguments(5L, 6L),
                Arguments.arguments(0L, 6L)
        );
    }

    @BeforeEach
    void init() {
        this.백기역 = stationDao.save(new Station("백기역"));
        this.흑기역 = stationDao.save(new Station("흑기역"));
        this.낙성대역 = stationDao.save(new Station("낙성대역"));
        this.검프역 = stationDao.save(new Station("검프역"));
        this.백기선 = new Line("백기선", "bg-red-600");

        LineResponse lineResponse = lineService.save(new LineRequest(백기선.name(), 백기선.color(), 백기역.id(), 흑기역.id(), 7));
        this.백기선 = new Line(lineResponse.getId(), lineResponse.getName(), lineResponse.getColor());
    }

    @DisplayName("노선을 생성한다.")
    @Test
    void createStation() {
        // given
        String newLineName = "신분당선";
        String newLineColor = "bg-black-500";
        Long newUpStationId = 낙성대역.id();
        Long newDownStationId = 검프역.id();
        int newDistance = 5;
        LineRequest lineRequest = new LineRequest(newLineName, newLineColor, newUpStationId, newDownStationId, newDistance);

        // when
        ExtractableResponse<Response> response = post_요청을_보냄(BASE_URL, lineRequest);
        LineResponse lineResponse = response.body().as(LineResponse.class);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
        assertThat(stationResponsesToStrings(lineResponse.getStations())).containsExactly(낙성대역.name(), 검프역.name());
    }

    @DisplayName("기존에 존재하는 노선의 이름으로 노선을 생성하면 예외가 발생한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        String newLineName = 백기선.name();
        String newLineColor = "bg-black-500";
        Long newUpStationId = 낙성대역.id();
        Long newDownStationId = 검프역.id();
        int newDistance = 5;
        LineRequest lineRequest = new LineRequest(newLineName, newLineColor, newUpStationId, newDownStationId, newDistance);

        // when
        ExtractableResponse<Response> response = post_요청을_보냄(BASE_URL, lineRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("기존에 존재하는 노선의 색깔로 노선을 생성하면 예외가 발생한다.")
    @Test
    void createStationWithDuplicateColor() {
        // given
        String newLineName = "신분당선";
        String newLineColor = 백기선.color();
        Long newUpStationId = 낙성대역.id();
        Long newDownStationId = 검프역.id();
        int newDistance = 5;
        LineRequest lineRequest = new LineRequest(newLineName, newLineColor, newUpStationId, newDownStationId, newDistance);

        // when
        ExtractableResponse<Response> response = post_요청을_보냄(BASE_URL, lineRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("모든 지하철 노선을 조회한다.")
    @Test
    void getLines() {
        // given
        String newLineName = "신분당선";
        String newLineColor = "bg-black-500";
        Long newUpStationId = 낙성대역.id();
        Long newDownStationId = 검프역.id();
        int newDistance = 5;
        LineRequest lineRequest = new LineRequest(newLineName, newLineColor, newUpStationId, newDownStationId, newDistance);

        // when
        ExtractableResponse<Response> createResponse = post_요청을_보냄(BASE_URL, lineRequest);
        ExtractableResponse<Response> findResponse = get_요청을_보냄(BASE_URL);

        LineResponse lineResponse = createResponse.body().as(LineResponse.class);
        List<Long> resultLineIds = findResponse.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());

        // then
        assertThat(findResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(resultLineIds).containsExactly(백기선.id(), lineResponse.getId());
    }

    @DisplayName("단일 노선을 조회한다.")
    @Test
    void findLineByIdToHTTP() {
        // given
        String newLineName = "신분당선";
        String newLineColor = "bg-black-500";
        Long newUpStationId = 낙성대역.id();
        Long newDownStationId = 검프역.id();
        int newDistance = 5;
        LineRequest lineRequest = new LineRequest(newLineName, newLineColor, newUpStationId, newDownStationId, newDistance);

        // when
        ExtractableResponse<Response> createResponse = post_요청을_보냄(BASE_URL, lineRequest);
        LineResponse createdResponse = createResponse.body().as(LineResponse.class);

        ExtractableResponse<Response> findLineResponse = get_요청을_보냄(BASE_URL_WITH_ID, createdResponse.getId());
        LineResponse findResponse = findLineResponse.body().as(LineResponse.class);

        // then
        assertThat(findLineResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(findResponse.getId()).isEqualTo(createdResponse.getId());
        assertThat(findResponse.getName()).isEqualTo(createdResponse.getName());
        assertThat(findResponse.getColor()).isEqualTo(createdResponse.getColor());
        assertThat(stationResponsesToStrings(findLineResponse.jsonPath().getList("stations", StationResponse.class))).containsExactly(낙성대역.name(), 검프역.name());
    }

    @DisplayName("노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        String newLineName = "신분당선";
        String newLineColor = "bg-black-500";
        LineUpdateRequest lineUpdateRequest = new LineUpdateRequest(newLineName, newLineColor);

        // when
        ExtractableResponse<Response> updateResponse = put_요청을_보냄(BASE_URL_WITH_ID, lineUpdateRequest, 백기선.id());
        ExtractableResponse<Response> findLineResponse = get_요청을_보냄(BASE_URL_WITH_ID, 백기선.id());

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
        ExtractableResponse<Response> response = delete_요청을_보냄(BASE_URL_WITH_ID, 백기선.id());
        ExtractableResponse<Response> findLineResponse = get_요청을_보냄(BASE_URL_WITH_ID, 백기선.id());

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(findLineResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선 제거시 없는 노선이면 예외가 발생한다.")
    @Test
    void deleteStation() {
        //given

        //when
        ExtractableResponse<Response> response = delete_요청을_보냄(BASE_URL_WITH_ID, -1L);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("구간을 제거한다. (상행 종점역)")
    @Test
    void deleteUpwardEndPointStation() {
        int newDistance = 5;
        SectionRequest sectionRequest = new SectionRequest(흑기역.id(), 낙성대역.id(), newDistance);

        //when
        ExtractableResponse<Response> addResponse = post_요청을_보냄(BASE_URL_WITH_ID_SECTION, sectionRequest, 백기선.id());
        ExtractableResponse<Response> response = 지하철역_delete_요청을_보냄(백기선.id(), 백기역.id());
        ExtractableResponse<Response> findLineResponse = get_요청을_보냄(BASE_URL_WITH_ID, 백기선.id());

        LineResponse findResponse = findLineResponse.body().as(LineResponse.class);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(stationResponsesToStrings(findResponse.getStations())).containsExactly(흑기역.name(), 낙성대역.name());
    }

    @DisplayName("구간을 제거한다. (중간역)")
    @Test
    void deleteMiddlewardStation() {
        int newDistance = 5;
        SectionRequest sectionRequest = new SectionRequest(흑기역.id(), 낙성대역.id(), newDistance);

        //when
        ExtractableResponse<Response> addResponse = post_요청을_보냄(BASE_URL_WITH_ID_SECTION, sectionRequest, 백기선.id());
        ExtractableResponse<Response> response = 지하철역_delete_요청을_보냄(백기선.id(), 흑기역.id());
        ExtractableResponse<Response> findLineResponse = get_요청을_보냄(BASE_URL_WITH_ID, 백기선.id());

        LineResponse findResponse = findLineResponse.body().as(LineResponse.class);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(stationResponsesToStrings(findResponse.getStations())).containsExactly(백기역.name(), 낙성대역.name());
    }

    @DisplayName("구간을 제거한다. (하행 종점역)")
    @Test
    void deleteDownwardEndPointStation() {
        int newDistance = 5;
        SectionRequest sectionRequest = new SectionRequest(흑기역.id(), 낙성대역.id(), newDistance);

        //when
        ExtractableResponse<Response> addResponse = post_요청을_보냄(BASE_URL_WITH_ID_SECTION, sectionRequest, 백기선.id());
        ExtractableResponse<Response> response = 지하철역_delete_요청을_보냄(백기선.id(), 낙성대역.id());
        ExtractableResponse<Response> findLineResponse = get_요청을_보냄(BASE_URL_WITH_ID, 백기선.id());

        LineResponse findResponse = findLineResponse.body().as(LineResponse.class);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(stationResponsesToStrings(findResponse.getStations())).containsExactly(백기역.name(), 흑기역.name());
    }

    @ParameterizedTest
    @DisplayName("구간 등록시 상행역화 하행역이 이미 등록 되어있다면 예외가 발생한다. ")
    @MethodSource("stationIds")
    void registrationDuplicateException(Long newUpStationId, Long newDownStationId) {
        //given
        int newDistance = 3;
        SectionRequest acceptSectionRequest = new SectionRequest(흑기역.id(), 낙성대역.id(), 7);
        SectionRequest exceptionSectionRequest = new SectionRequest(newUpStationId, newDownStationId, newDistance);

        //when
        ExtractableResponse<Response> addResponse = post_요청을_보냄(BASE_URL_WITH_ID_SECTION, acceptSectionRequest, 백기선.id());
        ExtractableResponse<Response> exceptionResponse = post_요청을_보냄(BASE_URL_WITH_ID_SECTION, exceptionSectionRequest, 백기선.id());


        //then
        assertThat(exceptionResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @ParameterizedTest
    @DisplayName("구간 등록시 상행역화 하행역 둘다 노선에 등록 되어있지 않다면 예외가 발생한다. ")
    @MethodSource("exceptionStationIds")
    void registrationNotFoundException(Long newUpStationId, Long newDownStationId) {
        //given
        int newDistance = 3;
        SectionRequest exceptionSectionRequest = new SectionRequest(newUpStationId, newDownStationId, newDistance);

        //when
        ExtractableResponse<Response> exceptionResponse = post_요청을_보냄(BASE_URL_WITH_ID_SECTION, exceptionSectionRequest, 백기선.id());

        //then
        assertThat(exceptionResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("1개의 구간만 있을 때, 역을 삭제를 하려하면 예외가 발생한다")
    void deleteException() {
        //given

        //when
        ExtractableResponse<Response> findLineResponse = 지하철역_delete_요청을_보냄(백기선.id(), 백기역.id());

        //then
        assertThat(findLineResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("상행 종점역을 저장한다")
    void upwardEndPointRegistration() {
        //given
        int newDistance = 5;
        SectionRequest sectionRequest = new SectionRequest(낙성대역.id(), 백기역.id(), newDistance);

        //when
        ExtractableResponse<Response> addResponse = post_요청을_보냄(BASE_URL_WITH_ID_SECTION, sectionRequest, 백기선.id());
        ExtractableResponse<Response> findLineResponse = get_요청을_보냄(BASE_URL_WITH_ID, 백기선.id());
        LineResponse findResponse = findLineResponse.body().as(LineResponse.class);

        //then
        assertThat(addResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(findResponse.getId()).isEqualTo(백기선.id());
        assertThat(stationResponsesToStrings(findResponse.getStations())).containsExactly(낙성대역.name(), 백기역.name(), 흑기역.name());
    }

    @Test
    @DisplayName("허행 종점역을 저장한다")
    void downwardEndPointRegistration() {
        //given
        int newDistance = 5;
        SectionRequest sectionRequest = new SectionRequest(흑기역.id(), 낙성대역.id(), newDistance);

        //when
        ExtractableResponse<Response> addResponse = post_요청을_보냄(BASE_URL_WITH_ID_SECTION, sectionRequest, 백기선.id());
        ExtractableResponse<Response> findLineResponse = get_요청을_보냄(BASE_URL_WITH_ID, 백기선.id());
        LineResponse findResponse = findLineResponse.body().as(LineResponse.class);

        //then
        assertThat(addResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(findResponse.getId()).isEqualTo(백기선.id());
        assertThat(stationResponsesToStrings(findResponse.getStations())).containsExactly(백기역.name(), 흑기역.name(), 낙성대역.name());
    }

    private ExtractableResponse<Response> 지하철역_delete_요청을_보냄(Long lineId, Long stationId) {
        return RestAssured.given().log().all()
                .when()
                .queryParam("stationId", stationId)
                .delete(BASE_URL_WITH_ID_SECTION, lineId)
                .then().log().all()
                .extract();
    }

    private List<String> stationResponsesToStrings(final List<StationResponse> response) {
        return response.stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());
    }
}
