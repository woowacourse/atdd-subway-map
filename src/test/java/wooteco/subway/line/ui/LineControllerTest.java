package wooteco.subway.line.ui;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.line.domain.LineRepository;
import wooteco.subway.line.service.LineService;
import wooteco.subway.line.ui.dto.LineCreateRequest;
import wooteco.subway.line.ui.dto.LineModifyRequest;
import wooteco.subway.line.ui.dto.LineResponse;
import wooteco.subway.line.ui.dto.SectionAddRequest;
import wooteco.subway.station.domain.StationRepository;
import wooteco.subway.station.ui.dto.StationResponse;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static wooteco.subway.station.StationAcceptanceTest.지하철역_등록되어_있음;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/truncate.sql")
class LineControllerTest {
    private static final String 봉천역 = "봉천역";
    private static final String 신림역 = "신림역";
    private static final String 강남역 = "강남역";
    private static final String 양재역 = "양재역";

    @Autowired
    private LineService lineService;

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private StationRepository stationRepository;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    public static LineResponse 지하철_노선_등록되어_있음(LineCreateRequest lineRequest) {
        return 지하철_노선_생성_요청(lineRequest).as(LineResponse.class);
    }

    public static void 지하철_구간_등록되어_있음(LineResponse lineResponse, SectionAddRequest sectionAddRequest) {
        ExtractableResponse<Response> response = 지하철_노선_구간_추가_요청(lineResponse, sectionAddRequest);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    public static ExtractableResponse<Response> 지하철_노선_생성_요청(LineCreateRequest params) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().post("/lines")
                .then().log().all().
                        extract();
    }

    private static ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return RestAssured
                .given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_조회_요청(LineResponse response) {
        return RestAssured
                .given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines/{lineId}", response.getId())
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_수정_요청(LineResponse response,
                                                             LineModifyRequest params) {

        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().put("/lines/" + response.getId())
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_제거_요청(LineResponse lineResponse) {
        return RestAssured
                .given().log().all()
                .when().delete("/lines/" + lineResponse.getId())
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_구간_추가_요청(LineResponse lineResponse,
                                                                SectionAddRequest sectionAddRequest) {

        return RestAssured
                .given().log().all()
                .accept(MediaType.ALL_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(sectionAddRequest)
                .post("/lines/" + lineResponse.getId() + "/sections")
                .then()
                .extract();
    }


    public static ExtractableResponse<Response> 지하철_노선_구간_삭제_요청(LineResponse lineResponse, StationResponse stationResponse) {
        return RestAssured
                .given().log().all()
                .accept(MediaType.ALL_VALUE)
                .when()
                .delete("/lines/" + lineResponse.getId() + "/sections?stationId=" + stationResponse.getId())
                .then()
                .extract();
    }


    public static void 지하철_노선_생성됨(LineCreateRequest request, ExtractableResponse response) {
        LineResponse lineResponse = response.body().as(LineResponse.class);

        assertThat(request.getName()).isEqualTo(lineResponse.getName());
        assertThat(request.getColor()).isEqualTo(lineResponse.getColor());

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    public static void 지하철_노선_관련_요청_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public static void 지하철_노선_목록_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 지하철_노선_응답됨(ExtractableResponse<Response> response,
                                  LineResponse createdLineResponse) {

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        LineResponse resultResponse = response.as(LineResponse.class);
        assertThat(resultResponse.getId()).isEqualTo(createdLineResponse.getId());
        assertThat(resultResponse.getName()).isEqualTo(createdLineResponse.getName());
        assertThat(resultResponse.getColor()).isEqualTo(createdLineResponse.getColor());
    }

    public static void 지하철_노선_목록_포함됨(ExtractableResponse<Response> response,
                                     List<LineCreateRequest> createdLines) {

        LineResponse[] responses = response.as(LineResponse[].class);
        assertThat(responses)
                .hasSize(2)
                .extracting(LineResponse::getName)
                .contains(createdLines.stream()
                        .map(LineCreateRequest::getName)
                        .collect(Collectors.toList())
                        .toArray(new String[createdLines.size()]));

        assertThat(responses)
                .hasSize(2)
                .extracting(LineResponse::getColor)
                .contains(createdLines.stream()
                        .map(LineCreateRequest::getColor)
                        .collect(Collectors.toList())
                        .toArray(new String[createdLines.size()]));
    }

    public static void 지하철_노선_수정됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    public static void 지하철_노선_삭제됨(LineResponse lineResponse, ExtractableResponse<Response> response) {

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        ExtractableResponse<Response> getResponse = 지하철_노선_조회_요청(lineResponse);
        assertThat(getResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public static void 지하철_노선_구간_변경됨(ExtractableResponse<Response> response,
                                     LineResponse beforeAddSectionLine, List<StationResponse> expected) {

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        ExtractableResponse<Response> afterResponse = 지하철_노선_조회_요청(beforeAddSectionLine);
        LineResponse afterAddSectionLine = afterResponse.as(LineResponse.class);

        assertThat(afterAddSectionLine.getStations())
                .containsExactly(expected.toArray(new StationResponse[0]));
    }

    @DisplayName("새로운 노선을 생성한다.")
    @Test
    void createNewline_createNewLineFromUserInputs() {
        //given
        StationResponse station1 = 지하철역_등록되어_있음(봉천역);
        StationResponse station2 = 지하철역_등록되어_있음(신림역);
        LineCreateRequest request = new LineCreateRequest("bg-red-600", "신분당선",
                station1.getId(), station2.getId(), 10);

        //when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(request);

        //then
        지하철_노선_생성됨(request, response);
    }

    @DisplayName("노선 이름 중복 체크")
    @Test
    void createNewLine_checkDuplicatedLineName() {
        //given
        StationResponse station1 = 지하철역_등록되어_있음(봉천역);
        StationResponse station2 = 지하철역_등록되어_있음(신림역);

        final LineCreateRequest request = new LineCreateRequest("bg-red-600", "2호선",
                station1.getId(), station2.getId(), 10);

        //when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(request);

        //then
        지하철_노선_관련_요청_실패됨(response);
    }


    @DisplayName("모든 노선을 조회한다.")
    @Test
    void allLines() {
        //given
        StationResponse station1 = 지하철역_등록되어_있음(봉천역);
        StationResponse station2 = 지하철역_등록되어_있음(신림역);
        StationResponse station3 = 지하철역_등록되어_있음(양재역);
        StationResponse station4 = 지하철역_등록되어_있음(강남역);

        final LineCreateRequest requestLine2 = new LineCreateRequest("bg-red-600", "2호선",
                station1.getId(), station2.getId(), 10);
        지하철_노선_등록되어_있음(requestLine2);


        final LineCreateRequest requestLineNew = new LineCreateRequest("bg-red-500", "신분당선",
                station3.getId(), station4.getId(), 10);
        지하철_노선_등록되어_있음(requestLineNew);

        //when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        //then
        지하철_노선_목록_응답됨(response);
        지하철_노선_목록_포함됨(response, Arrays.asList(requestLine2, requestLineNew));
    }

    @DisplayName("노선을 검색한다")
    @Test
    void findById_findLineById() {
        //given
        StationResponse station1 = 지하철역_등록되어_있음(봉천역);
        StationResponse station2 = 지하철역_등록되어_있음(신림역);

        final LineCreateRequest requestLine2 = new LineCreateRequest("bg-red-600", "2호선",
                station1.getId(), station2.getId(), 10);
        LineResponse lineResponse = 지하철_노선_등록되어_있음(requestLine2);

        //when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(lineResponse);


        //then
        지하철_노선_응답됨(response, lineResponse);
    }

    @DisplayName("노선이 없다면 400에러 발생")
    @Test
    void findById_canNotFindLineById() {
        //given
        //when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(new LineResponse(0L, "", "",
                Collections.emptyList()));

        //then
        지하철_노선_관련_요청_실패됨(response);
    }

    @DisplayName("노션을 수정한다.")
    @Test
    void modifyById_modifyLineFromUserInputs() {
        //given
        StationResponse station1 = 지하철역_등록되어_있음(봉천역);
        StationResponse station2 = 지하철역_등록되어_있음(신림역);

        final LineCreateRequest requestLine2 = new LineCreateRequest("bg-red-600", "2호선",
                station1.getId(), station2.getId(), 10);
        LineResponse lineResponse = 지하철_노선_등록되어_있음(requestLine2);

        LineModifyRequest lineModifyRequest = new LineModifyRequest("bg-red-600", "구분당선");

        //when
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(lineResponse, lineModifyRequest);

        //then
        지하철_노선_수정됨(response);
    }

    @DisplayName("노션을 삭제한다.")
    @Test
    void deleteById_deleteLineFromUserInputs() {
        //given
        StationResponse station1 = 지하철역_등록되어_있음(봉천역);
        StationResponse station2 = 지하철역_등록되어_있음(신림역);

        LineCreateRequest requestLine2 = new LineCreateRequest("bg-red-600", "2호선",
                station1.getId(), station2.getId(), 10);
        LineResponse lineResponse = 지하철_노선_등록되어_있음(requestLine2);

        //when
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(lineResponse);

        //then
        지하철_노선_삭제됨(lineResponse, response);
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @Test
    void addSectionInLine_sectionLengthHaveToLessThanStationLength() {
        //given
        StationResponse station1 = 지하철역_등록되어_있음(봉천역);
        StationResponse station2 = 지하철역_등록되어_있음(신림역);
        StationResponse station3 = 지하철역_등록되어_있음(강남역);

        LineCreateRequest requestLine2 = new LineCreateRequest("bg-red-600", "2호선",
                station1.getId(), station2.getId(), 10);
        LineResponse lineResponse = 지하철_노선_등록되어_있음(requestLine2);

        SectionAddRequest sectionAddRequest = new SectionAddRequest(station1.getId(), station3.getId(), 10);

        //when
        ExtractableResponse<Response> response = 지하철_노선_구간_추가_요청(lineResponse, sectionAddRequest);

        //then
        지하철_노선_관련_요청_실패됨(response);
    }


    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void addSectionInLine_sectionHasToNewStationInLine() {
        //given
        StationResponse station1 = 지하철역_등록되어_있음(봉천역);
        StationResponse station2 = 지하철역_등록되어_있음(신림역);

        LineCreateRequest requestLine2 = new LineCreateRequest("bg-red-600", "2호선",
                station1.getId(), station2.getId(), 10);
        LineResponse lineResponse = 지하철_노선_등록되어_있음(requestLine2);

        SectionAddRequest sectionAddRequest = new SectionAddRequest(station1.getId(), station2.getId(), 5);

        //when
        ExtractableResponse<Response> response = 지하철_노선_구간_추가_요청(lineResponse, sectionAddRequest);

        //then
        지하철_노선_관련_요청_실패됨(response);

    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void addSectionInLine_sectionHaveOnlyOneNewStation() {
        //given
        StationResponse station1 = 지하철역_등록되어_있음(봉천역);
        StationResponse station2 = 지하철역_등록되어_있음(신림역);
        StationResponse station3 = 지하철역_등록되어_있음(강남역);
        StationResponse station4 = 지하철역_등록되어_있음(양재역);

        LineCreateRequest requestLine2 = new LineCreateRequest("bg-red-600", "2호선",
                station1.getId(), station2.getId(), 10);
        LineResponse lineResponse = 지하철_노선_등록되어_있음(requestLine2);

        SectionAddRequest sectionAddRequest = new SectionAddRequest(station3.getId(), station4.getId(), 5);

        //when
        ExtractableResponse<Response> response = 지하철_노선_구간_추가_요청(lineResponse, sectionAddRequest);

        //then
        지하철_노선_관련_요청_실패됨(response);
    }

    @DisplayName("새로 등록할 구간의 상행역과 하행역 중 노선에 이미 등록되어있는 역을 기준으로 새로운 구간을 추가한다. - 노선의 끝 추가")
    @Test
    void addSectionInLine() {
        //given
        StationResponse station1 = 지하철역_등록되어_있음(봉천역);
        StationResponse station2 = 지하철역_등록되어_있음(신림역);
        StationResponse station3 = 지하철역_등록되어_있음(강남역);
        StationResponse station4 = 지하철역_등록되어_있음(양재역);

        LineCreateRequest requestLine2 = new LineCreateRequest("bg-red-600", "2호선",
                station1.getId(), station2.getId(), 10);
        LineResponse lineResponse = 지하철_노선_등록되어_있음(requestLine2);

        SectionAddRequest sectionUpAddRequest = new SectionAddRequest(station3.getId(), station1.getId(), 20);

        //when
        ExtractableResponse<Response> response = 지하철_노선_구간_추가_요청(lineResponse, sectionUpAddRequest);

        //then
        지하철_노선_구간_변경됨(response, lineResponse, Arrays.asList(station3, station1, station2));


        //given
        sectionUpAddRequest = new SectionAddRequest(station2.getId(), station4.getId(), 20);

        //when
        response = 지하철_노선_구간_추가_요청(lineResponse, sectionUpAddRequest);

        //then
        지하철_노선_구간_변경됨(response, lineResponse, Arrays.asList(station3, station1, station2, station4));
    }

    @DisplayName("하나의 노선에는 갈래길이 허용되지 않기 때문에 구간이 추가되기 전에 갈래길이 생기지 않도록 기존 구간을 변경한다.")
    @Test
    void addSectionInLine_blockTwoWay() {
        //given
        StationResponse station1 = 지하철역_등록되어_있음(봉천역);
        StationResponse station2 = 지하철역_등록되어_있음(신림역);
        StationResponse station3 = 지하철역_등록되어_있음(강남역);

        LineCreateRequest requestLine2 = new LineCreateRequest("bg-red-600", "2호선",
                station1.getId(), station2.getId(), 10);
        LineResponse lineResponse = 지하철_노선_등록되어_있음(requestLine2);

        SectionAddRequest sectionAddRequest = new SectionAddRequest(station1.getId(), station3.getId(), 5);

        //when
        ExtractableResponse<Response> response = 지하철_노선_구간_추가_요청(lineResponse, sectionAddRequest);

        //then
        지하철_노선_구간_변경됨(response, lineResponse, Arrays.asList(station1, station3, station2));


        //given
        StationResponse station4 = 지하철역_등록되어_있음(양재역);
        sectionAddRequest = new SectionAddRequest(station3.getId(), station4.getId(), 3);

        //when
        response = 지하철_노선_구간_추가_요청(lineResponse, sectionAddRequest);

        //then
        지하철_노선_구간_변경됨(response, lineResponse, Arrays.asList(station1, station3, station4, station2));

      /*  assertThat(savedLine.getSections().toList())
                .extracting(Section::getDistance)
                .containsExactly(5, 3, 2);*/
    }

    @DisplayName("종점이 제거될 경우 다음으로 오던 역이 종점이 됨 - 상행 제거")
    @Test
    void deleteSection_endPointUp() {
        //given
        StationResponse station1 = 지하철역_등록되어_있음(봉천역);
        StationResponse station2 = 지하철역_등록되어_있음(신림역);
        StationResponse station3 = 지하철역_등록되어_있음(강남역);

        LineCreateRequest requestLine2 = new LineCreateRequest("bg-red-600", "2호선",
                station1.getId(), station2.getId(), 10);
        LineResponse lineResponse = 지하철_노선_등록되어_있음(requestLine2);

        SectionAddRequest sectionAddRequest = new SectionAddRequest(station1.getId(), station3.getId(), 5);
        지하철_구간_등록되어_있음(lineResponse, sectionAddRequest);

        //when
        ExtractableResponse<Response> response = 지하철_노선_구간_삭제_요청(lineResponse, station1);

        //then
        지하철_노선_구간_변경됨(response, lineResponse, Arrays.asList(station3, station2));
    }

    @DisplayName("종점이 제거될 경우 다음으로 오던 역이 종점이 됨 - 하행 제거")
    @Test
    void deleteSection_endPointDown() {
        //given
        StationResponse station1 = 지하철역_등록되어_있음(봉천역);
        StationResponse station2 = 지하철역_등록되어_있음(신림역);
        StationResponse station3 = 지하철역_등록되어_있음(강남역);

        LineCreateRequest requestLine2 = new LineCreateRequest("bg-red-600", "2호선",
                station1.getId(), station2.getId(), 10);
        LineResponse lineResponse = 지하철_노선_등록되어_있음(requestLine2);

        SectionAddRequest sectionAddRequest = new SectionAddRequest(station1.getId(), station3.getId(), 5);
        지하철_구간_등록되어_있음(lineResponse, sectionAddRequest);

        //when
        ExtractableResponse<Response> response = 지하철_노선_구간_삭제_요청(lineResponse, station2);

        //then
        지하철_노선_구간_변경됨(response, lineResponse, Arrays.asList(station1, station3));
    }

    @DisplayName("구간이 하나인 노선에서 마지막 구간을 제거 할 때 제거 할 수 없음")
    @Test
    void deleteSection_whenOneSection() {
        //given
        StationResponse station1 = 지하철역_등록되어_있음(봉천역);
        StationResponse station2 = 지하철역_등록되어_있음(신림역);

        LineCreateRequest requestLine2 = new LineCreateRequest("bg-red-600", "2호선",
                station1.getId(), station2.getId(), 10);
        LineResponse lineResponse = 지하철_노선_등록되어_있음(requestLine2);

        //when
        ExtractableResponse<Response> response = 지하철_노선_구간_삭제_요청(lineResponse, station2);

        //then
        지하철_노선_관련_요청_실패됨(response);
    }
}