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
import org.springframework.http.HttpStatus;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.line.dto.LineUpdateRequest;
import wooteco.subway.line.dto.SectionRequest;
import wooteco.subway.station.dto.StationResponse;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static wooteco.subway.station.ui.StationAcceptanceTest.지하철역_생성_되어있음;

@DisplayName("노선역 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    private static final String BASE_URL = "/lines";
    private static final String BASE_URL_WITH_ID = "/lines/{id}";
    private static final String BASE_URL_WITH_ID_SECTION = "/lines/{id}/sections";

    private StationResponse 백기역;
    private StationResponse 흑기역;
    private StationResponse 낙성대역;
    private StationResponse 검프역;

    private LineRequest 인천1호선_Request;
    private LineRequest 인천2호선_Request;

    private SectionRequest sectionRequest;

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
        흑기역 = 지하철역_생성_되어있음("흑기역");
        백기역 = 지하철역_생성_되어있음("백기역");
        낙성대역 = 지하철역_생성_되어있음("낙성대역");
        검프역 = 지하철역_생성_되어있음("검프역");

        인천1호선_Request = new LineRequest("인천1호선", "bg-black-500", 흑기역.getId(), 백기역.getId(), 7);
        인천2호선_Request = new LineRequest("인천2호선", "bg-black-600", 흑기역.getId(), 백기역.getId(), 5);
        sectionRequest = new SectionRequest(백기역.getId(), 낙성대역.getId(), 5);
    }

    @DisplayName("노선을 생성한다.")
    @Test
    void createStation() {
        // given

        // when
        ExtractableResponse<Response> response = 지하철_노선_생청_요청(인천1호선_Request);

        // then
        지하철_노선_정상_생성됨(response);
    }

    @DisplayName("기존에 존재하는 노선의 이름으로 노선을 생성하면 예외가 발생한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        LineRequest lineRequest = new LineRequest("인천1호선", "bg-black-600", 흑기역.getId(), 백기역.getId(), 5);
        지하철_노선_생성_되어있음(인천1호선_Request);

        // when
        ExtractableResponse<Response> response = 지하철_노선_생청_요청(lineRequest);

        // then
        지하철_노선_생성_실패됨(response);
    }

    @DisplayName("기존에 존재하는 노선의 색깔로 노선을 생성하면 예외가 발생한다.")
    @Test
    void createStationWithDuplicateColor() {
        // given
        LineRequest lineRequest = new LineRequest("인천2호선", "bg-black-500", 흑기역.getId(), 백기역.getId(), 5);
        지하철_노선_생성_되어있음(인천1호선_Request);

        // when
        ExtractableResponse<Response> response = 지하철_노선_생청_요청(lineRequest);

        // then
        지하철_노선_생성_실패됨(response);
    }

    @DisplayName("모든 지하철 노선을 조회한다.")
    @Test
    void getLines() {
        // given
        LineResponse response1 = 지하철_노선_생성_되어있음(인천1호선_Request);
        LineResponse response2 = 지하철_노선_생성_되어있음(인천2호선_Request);

        // when
        ExtractableResponse<Response> findResponse = 지하철_목록_조회_요청();

        // then
        지하철_노선_목록_정상_요청됨(findResponse);
        지하철_노선_목록_정상_포함됨(findResponse, Arrays.asList(response1, response2));
    }


    @DisplayName("단일 노선을 조회한다.")
    @Test
    void findLineByIdToHTTP() {
        // given
        LineResponse response = 지하철_노선_생성_되어있음(인천1호선_Request);

        // when
        LineResponse findLineResponse = 지하철_노선_단일_조회_되어있음(response);

        // then
        지하철_노선_단일_조회_정상_포함됨(findLineResponse, response);
    }

    @DisplayName("노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        LineResponse response = 지하철_노선_생성_되어있음(인천1호선_Request);

        // when
        ExtractableResponse<Response> updateResponse = 지하철_노선_수정_요청함(response, new LineUpdateRequest(인천2호선_Request.getName(), 인천2호선_Request.getColor()));

        // then
        지하철_노선_정상_수정됨(updateResponse);
    }

    @DisplayName("노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        LineResponse response = 지하철_노선_생성_되어있음(인천1호선_Request);

        // when
        ExtractableResponse<Response> deleteResponse = 지하철_노선_삭제_요청을_보냄(response);

        // then
        지하철_노선_삭제_성공함(deleteResponse);
    }

    @DisplayName("노선 제거시 없는 노선이면 예외가 발생한다.")
    @Test
    void deleteStation() {
        //given

        //when
        ExtractableResponse<Response> deleteResponse = delete_요청을_보냄(BASE_URL_WITH_ID, -1L);

        // then
        지하철_노선_삭제_실패함(deleteResponse);
    }

    @DisplayName("구간을 제거한다. (상행 종점역)")
    @Test
    void deleteUpwardEndPointStation() {
        LineResponse response = 지하철_노선_생성_되어있음(인천1호선_Request);
        구간_등록_요청(response.getId(), sectionRequest);

        //when
        ExtractableResponse<Response> deleteResponse = 지하철_구간_지하철역_삭제_요청(response.getId(), 흑기역.getId());

        // then
        지하철_구간_지하철역_삭제됨(deleteResponse);
    }

    @DisplayName("구간을 제거한다. (중간역)")
    @Test
    void deleteMiddlewardStation() {
        LineResponse response = 지하철_노선_생성_되어있음(인천1호선_Request);
        구간_등록_요청(response.getId(), sectionRequest);

        //when
        ExtractableResponse<Response> deleteResponse = 지하철_구간_지하철역_삭제_요청(response.getId(), 백기역.getId());

        // then
        지하철_구간_지하철역_삭제됨(deleteResponse);
    }

    @DisplayName("구간을 제거한다. (하행 종점역)")
    @Test
    void deleteDownwardEndPointStation() {
        LineResponse response = 지하철_노선_생성_되어있음(인천1호선_Request);
        구간_등록_요청(response.getId(), sectionRequest);

        //when
        ExtractableResponse<Response> deleteResponse = 지하철_구간_지하철역_삭제_요청(response.getId(), 낙성대역.getId());

        // then
        지하철_구간_지하철역_삭제됨(deleteResponse);
    }

    @ParameterizedTest
    @DisplayName("구간 등록시 상행역화 하행역이 이미 등록 되어있다면 예외가 발생한다. ")
    @MethodSource("stationIds")
    void registrationDuplicateException(Long newUpStationId, Long newDownStationId) {
        //given
        LineResponse response = 지하철_노선_생성_되어있음(인천1호선_Request);
        구간_등록_요청(response.getId(), sectionRequest);

        //when
        ExtractableResponse<Response> addResponse = 구간_등록_요청(response.getId(), new SectionRequest(newUpStationId, newDownStationId, 3));

        //then
        지하철_노선_구간_등록_실패함(addResponse);
    }

    @ParameterizedTest
    @DisplayName("구간 등록시 상행역화 하행역 둘다 노선에 등록 되어있지 않다면 예외가 발생한다. ")
    @MethodSource("exceptionStationIds")
    void registrationNotFoundException(Long newUpStationId, Long newDownStationId) {
        //given
        LineResponse response = 지하철_노선_생성_되어있음(인천1호선_Request);

        //when
        ExtractableResponse<Response> addResponse = 구간_등록_요청(response.getId(), new SectionRequest(newUpStationId, newDownStationId, 3));

        //then
        지하철_노선_구간_등록_실패함(addResponse);
    }

    @Test
    @DisplayName("1개의 구간만 있을 때, 역을 삭제를 하려하면 예외가 발생한다")
    void deleteException() {
        //given
        LineResponse response = 지하철_노선_생성_되어있음(인천1호선_Request);

        //when
        ExtractableResponse<Response> deleteResponse = 지하철_구간_지하철역_삭제_요청(response.getId(), 흑기역.getId());

        //then
        지하철_노선_지하철역_삭제_실패함(deleteResponse);
    }

    @Test
    @DisplayName("상행 종점역을 저장한다")
    void upwardEndPointRegistration() {
        //given
        LineResponse response = 지하철_노선_생성_되어있음(인천1호선_Request);

        //when
        ExtractableResponse<Response> addResponse = 구간_등록_요청(response.getId(), new SectionRequest(낙성대역.getId(), 흑기역.getId(), 3));

        //then
        지하철_노선_구간_등록_성공함(addResponse);
    }

    @Test
    @DisplayName("허행 종점역을 저장한다")
    void downwardEndPointRegistration() {
        //given
        LineResponse response = 지하철_노선_생성_되어있음(인천1호선_Request);

        //when
        ExtractableResponse<Response> addResponse = 구간_등록_요청(response.getId(), new SectionRequest(백기역.getId(), 낙성대역.getId(), 3));

        //then
        지하철_노선_구간_등록_성공함(addResponse);
    }

    private ExtractableResponse<Response> 지하철역_delete_요청을_보냄(Long lineId, Long stationId) {
        return RestAssured.given().log().all()
                .when()
                .queryParam("stationId", stationId)
                .delete(BASE_URL_WITH_ID_SECTION, lineId)
                .then().log().all()
                .extract();
    }

    private LineResponse 지하철_노선_생성_되어있음(LineRequest request) {
        return 지하철_노선_생청_요청(request).as(LineResponse.class);
    }

    private LineResponse 지하철_노선_단일_조회_되어있음(LineResponse response) {
        ExtractableResponse<Response> findLineResponse = 지하철_노선_단일_조회_요청(response);
        지하철_노선_단일_조회_성공함(findLineResponse);

        return findLineResponse.as(LineResponse.class);
    }

    private ExtractableResponse<Response> 지하철_구간_지하철역_삭제_요청(Long lineId, Long stationId) {
        return 지하철역_delete_요청을_보냄(lineId, stationId);
    }

    private ExtractableResponse<Response> 지하철_노선_생청_요청(LineRequest request) {
        return post_요청을_보냄(BASE_URL, request);
    }

    private ExtractableResponse<Response> 지하철_노선_단일_조회_요청(LineResponse response) {
        return get_요청을_보냄(BASE_URL_WITH_ID, response.getId());
    }

    private ExtractableResponse<Response> 구간_등록_요청(Long lineId, SectionRequest sectionRequest) {
        return post_요청을_보냄(BASE_URL_WITH_ID_SECTION, sectionRequest, lineId);
    }

    private ExtractableResponse<Response> 지하철_노선_수정_요청함(LineResponse response, LineUpdateRequest lineUpdateRequest) {
        return put_요청을_보냄(BASE_URL_WITH_ID, lineUpdateRequest, response.getId());
    }

    private ExtractableResponse<Response> 지하철_노선_삭제_요청을_보냄(LineResponse response) {
        return delete_요청을_보냄(BASE_URL_WITH_ID, response.getId());
    }

    private ExtractableResponse<Response> 지하철_목록_조회_요청() {
        return get_요청을_보냄(BASE_URL);
    }

    private void 지하철_노선_목록_정상_포함됨(ExtractableResponse<Response> response, List<LineResponse> lineResponses) {
        List<Long> expectsLineIds = lineResponses.stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());

        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());

        assertThat(resultLineIds.containsAll(expectsLineIds)).isEqualTo(true);
    }

    private void 지하철_노선_단일_조회_정상_포함됨(LineResponse findLineResponse, LineResponse response) {
        assertThat(findLineResponse).isEqualTo(response);
    }

    private void 지하철_노선_단일_조회_성공함(ExtractableResponse<Response> findLineResponse) {
        assertThat(findLineResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void 지하철_노선_정상_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    private void 지하철_노선_목록_정상_요청됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_노선_정상_수정됨(ExtractableResponse<Response> updateResponse) {
        assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_노선_삭제_성공함(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private void 지하철_노선_삭제_실패함(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

    }

    private void 지하철_구간_지하철역_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private void 지하철_노선_지하철역_삭제_실패함(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void 지하철_노선_구간_등록_실패함(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void 지하철_노선_구간_등록_성공함(ExtractableResponse<Response> addResponse) {
        assertThat(addResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
