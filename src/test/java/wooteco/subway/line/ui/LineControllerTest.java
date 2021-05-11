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
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.LineRepository;
import wooteco.subway.line.domain.Section;
import wooteco.subway.line.domain.Sections;
import wooteco.subway.line.service.LineService;
import wooteco.subway.line.ui.dto.LineCreateRequest;
import wooteco.subway.line.ui.dto.LineResponse;
import wooteco.subway.line.ui.dto.SectionAddRequest;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.domain.StationRepository;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.is;
import static wooteco.subway.line.domain.Sections.ERROR_SECTION_GRATER_OR_EQUALS_LINE_DISTANCE;
import static wooteco.subway.line.domain.Sections.ERROR_SECTION_HAVE_TO_ONE_STATION_IN_LINE;
import static wooteco.subway.line.service.LineService.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/truncate.sql")
class LineControllerTest {

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

    @DisplayName("새로운 노선을 생성한다.")
    @Test
    void createNewline_createNewLineFromUserInputs() {
        //given
        Station station1 = setDummyStation("봉천역");
        Station station2 = setDummyStation("신림역");
        final LineCreateRequest request = new LineCreateRequest("bg-red-600", "신분당선", station1.getId(), station2.getId(), 10);

        //when
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .accept(MediaType.ALL_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(request)
                .post("/lines")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Location", "/lines/1")
                .extract();

        //then
        final LineResponse lineResponse = response.as(LineResponse.class);
        assertThat(request.getName()).isEqualTo(lineResponse.getName());
        assertThat(request.getColor()).isEqualTo(lineResponse.getColor());
    }

    @DisplayName("노선 이름 중복 체크")
    @Test
    void createNewLine_checkDuplicatedLineName() {
        //given
        Station kangnam = setDummyStation("강남역");
        Station yangjae = setDummyStation("양재역");

        Line newLine = setDummyLine(kangnam, yangjae, 10, "신분당선", "bg-red-600");
        final LineCreateRequest request = new LineCreateRequest("bg-red-600", "신분당선", kangnam.getId(), yangjae.getId(), 10);

        RestAssured
                .given().log().all()
                .accept(MediaType.ALL_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(request)
                .post("/lines")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(is(ERROR_DUPLICATED_LINE_NAME));
    }


    @DisplayName("모든 노선을 조회한다.")
    @Test
    void allLines() {
        //given
        Station kangnam = setDummyStation("강남역");
        Station yangjae = setDummyStation("양재역");
        Station bongchun = setDummyStation("봉천역");
        Station sinlim = setDummyStation("신림역");

        Line newLine = setDummyLine(kangnam, yangjae, 10, "신분당선", "bg-red-600");
        Line twoLine = setDummyLine(bongchun, sinlim, 10, "2호선", "bg-red-500");

        //when
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract();

        //then
        LineResponse[] responses = response.as(LineResponse[].class);
        assertThat(responses)
                .hasSize(2)
                .extracting(LineResponse::getName)
                .contains(newLine.getName(), twoLine.getName());

        assertThat(responses)
                .hasSize(2)
                .extracting(LineResponse::getColor)
                .contains(newLine.getColor(), twoLine.getColor());
    }

    @DisplayName("노선을 검색한다")
    @Test
    void findById_findLineById() {
        //given
        Station kangnam = setDummyStation("강남역");
        Station yangjae = setDummyStation("양재역");
        Line line = setDummyLine(kangnam, yangjae, 10, "신분당선", "bg-red-600");

        //when
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines/" + line.getId())
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract();

        //then
        LineResponse lineResponse = response.as(LineResponse.class);
        assertThat(line.getId()).isEqualTo(lineResponse.getId());
        assertThat(line.getName()).isEqualTo(lineResponse.getName());
        assertThat(line.getColor()).isEqualTo(lineResponse.getColor());
    }

    @DisplayName("노선이 없다면 400에러 발생")
    @Test
    void findById_canNotFindLineById() {
        //given

        //when
        //then
        RestAssured
                .given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines/1")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노션을 수정한다.")
    @Test
    void modifyById_modifyLineFromUserInputs() {
        //given
        Station kangnam = setDummyStation("강남역");
        Station yangjae = setDummyStation("양재역");
        Line line = setDummyLine(kangnam, yangjae, 10, "신분당선", "bg-red-600");

        //when
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .accept(MediaType.ALL_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(new LineCreateRequest("bg-red-600", "구분당선", kangnam.getId(), yangjae.getId(), 10))
                .put("/lines/" + line.getId())
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .extract();

        //then
        final Line updatedLine = lineRepository.findById(line.getId());
        assertThat(updatedLine.getName()).isEqualTo("구분당선");
    }

    @DisplayName("노션을 삭제한다.")
    @Test
    void deleteById_deleteLineFromUserInputs() {
        //given
        Station kangnam = setDummyStation("강남역");
        Station yangjae = setDummyStation("양재역");
        Line line = setDummyLine(kangnam, yangjae, 10, "신분당선", "bg-red-600");

        //when
        RestAssured
                .given().log().all()
                .accept(MediaType.ALL_VALUE)
                .when()
                .delete("/lines/" + line.getId())
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        //then
        assertThatThrownBy(() -> {
            lineRepository.findById(line.getId());
        }, "", EmptyResultDataAccessException.class);

    }

    @DisplayName("노선에 포함된 역들을 순서대로 조회한다.")
    @Test
    void showSectionsInLine() {
        //given
        Station kangnam = setDummyStation("강남역");
        Station yangjae = setDummyStation("양재역");
        Line line = setDummyLine(kangnam, yangjae, 10, "신분당선", "bg-red-600");
        final List<Station> stations = lineService.getStations(line.getId());
        LineResponse testResponse = new LineResponse(line, stations);

        //when
        LineResponse resultResponse = RestAssured
                .given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines/" + line.getId())
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(LineResponse.class);

        //then
        assertThat(resultResponse.getStations()).hasSize(2);
        assertThat(resultResponse).isEqualTo(testResponse);
    }


    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @Test
    void addSectionInLine_sectionLengthHaveToLessThanStationLength() {
        //given
        Station kangnam = setDummyStation("강남역");
        Station yangjae = setDummyStation("양재역");
        Station pankyo = setDummyStation("판교역");

        Line line = setDummyLine(kangnam, yangjae, 10, "신분당선", "bg-red-600");
        SectionAddRequest sectionAddRequest = new SectionAddRequest(kangnam.getId(), pankyo.getId(), 10);
        //when
        //then
        RestAssured
                .given().log().all()
                .accept(MediaType.ALL_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(sectionAddRequest)
                .post("/lines/" + line.getId() + "/sections")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(is(ERROR_SECTION_GRATER_OR_EQUALS_LINE_DISTANCE));
    }


    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void addSectionInLine_sectionHasToNewStationInLine() {
        //given
        Station kangnam = setDummyStation("강남역");
        Station yangjae = setDummyStation("양재역");
        Line line = setDummyLine(kangnam, yangjae, 10, "신분당선", "bg-red-600");
        SectionAddRequest sectionAddRequest = new SectionAddRequest(kangnam.getId(), yangjae.getId(), 5);

        //when
        //then
        RestAssured
                .given().log().all()
                .accept(MediaType.ALL_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(sectionAddRequest)
                .post("/lines/" + line.getId() + "/sections")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(is(ERROR_SECTION_HAVE_TO_ONE_STATION_IN_LINE));

    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void addSectionInLine_sectionHaveOnlyOneNewStation() {
        //given
        Station kangnam = setDummyStation("강남역");
        Station yangjae = setDummyStation("양재역");
        Station bongchun = setDummyStation("봉천역");
        Station sinlim = setDummyStation("신림역");

        Line line = setDummyLine(kangnam, yangjae, 10, "신분당선", "bg-red-600");
        SectionAddRequest sectionAddRequest = new SectionAddRequest(bongchun.getId(), sinlim.getId(), 5);

        //when
        //then
        RestAssured
                .given().log().all()
                .accept(MediaType.ALL_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(sectionAddRequest)
                .post("/lines/" + line.getId() + "/sections")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(is(ERROR_SECTION_HAVE_TO_ONE_STATION_IN_LINE));
    }

    @DisplayName("새로 등록할 구간의 상행역과 하행역 중 노선에 이미 등록되어있는 역을 기준으로 새로운 구간을 추가한다.")
    @Test
    void addSectionInLine() {
        //given
        Station kangnam = setDummyStation("강남역");
        Station yangjae = setDummyStation("양재역");
        Station pankyo = setDummyStation("판교역");
        Station kwangkyo = setDummyStation("광교");

        Line line = setDummyLine(kangnam, yangjae, 10, "신분당선", "bg-red-600");
        SectionAddRequest sectionUpAddRequest = new SectionAddRequest(pankyo.getId(), kangnam.getId(), 10);

        //when
        RestAssured
                .given().log().all()
                .accept(MediaType.ALL_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(sectionUpAddRequest)
                .post("/lines/" + line.getId() + "/sections")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .extract();

        //then
        Line savedLine = lineRepository.findById(line.getId());

        assertThat(savedLine.getSections().sumSectionDistance()).isEqualTo(20);

        assertThat(savedLine.getSections().toList())
                .hasSize(2);

        assertThat(savedLine.getSections().toList())
                .extracting(Section::getUpStationId)
                .containsExactly(pankyo.getId(), kangnam.getId());

        assertThat(savedLine.getSections().toList())
                .extracting(Section::getDownStationId)
                .containsExactly(kangnam.getId(), yangjae.getId());

        //given
        sectionUpAddRequest = new SectionAddRequest(yangjae.getId(), kwangkyo.getId(), 10);

        //when
        RestAssured
                .given().log().all()
                .accept(MediaType.ALL_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(sectionUpAddRequest)
                .post("/lines/" + line.getId() + "/sections")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .extract();

        //then
        savedLine = lineRepository.findById(line.getId());

        assertThat(savedLine.getSections().sumSectionDistance()).isEqualTo(30);

        assertThat(savedLine.getSections().toList())
                .hasSize(3);

        assertThat(savedLine.getSections().toList())
                .extracting(Section::getUpStationId)
                .containsExactly(pankyo.getId(), kangnam.getId(), yangjae.getId());

        assertThat(savedLine.getSections().toList())
                .extracting(Section::getDownStationId)
                .containsExactly(kangnam.getId(), yangjae.getId(), kwangkyo.getId());
    }


    private Station setDummyStation(String stationName) {
        Station station = new Station(stationName);
        return stationRepository.save(station);
    }

    private Line setDummyLine(Station upStation, Station downStation, int distance, String lineName, String lineColor) {
        Sections sections = new Sections(
                Collections.singletonList(
                        new Section(upStation.getId(), downStation.getId(), distance)
                )
        );

        return lineRepository.save(new Line(lineName, lineColor, sections));
    }
}