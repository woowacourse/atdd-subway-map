package wooteco.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.section.SectionRequest;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationDao;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@Transactional
@Sql("/init-line.sql")
@DisplayName("지하철 노선 관리 기능")
class LineAcceptanceTest extends AcceptanceTest {
    private static final String color1 = "초록색";
    private static final String name1 = "2호선";

    private static final Station JAMSIL_STATION = new Station(1L, "잠실역");
    private static final Station GANGNAM_STATION = new Station(2L, "강남역");
    private static final Station GANGBYUN_STATION = new Station(3L, "강변역");
    private static final LineRequest LINE_2_REQUEST = new LineRequest("2호선", "초록색", 1L, 2L, 3);
    private static final LineRequest LINE_1_REQUEST = new LineRequest("1호선", "파란색", 1L, 2L, 0);

    @MockBean
    private StationDao stationDao;

    @Autowired
    private LineService lineService;

    @BeforeEach
    void beforeEach() {
        given(stationDao.findById(1L)).willReturn(Optional.of(JAMSIL_STATION));
        given(stationDao.findById(2L)).willReturn(Optional.of(GANGNAM_STATION));
        given(stationDao.findById(3L)).willReturn(Optional.of(GANGBYUN_STATION));
    }

    @Test
    @DisplayName("지하철 노선을 생성한다.")
    void createLine() {
        // given
        ExtractableResponse<Response> response = createLineResponseBy(LINE_2_REQUEST);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @Test
    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성시 실패.")
    void createLineWithDuplicateName() {
        // given & when
        createLineResponseBy(LINE_2_REQUEST);
        ExtractableResponse<Response> response = createLineResponseBy(LINE_2_REQUEST);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("지하철 전체 노선을 조회한다.")
    void getLines() {
        /// given
        ExtractableResponse<Response> createResponse1 = createLineResponseBy(LINE_2_REQUEST);
        ExtractableResponse<Response> createResponse2 = createLineResponseBy(LINE_1_REQUEST);

        // when
        ExtractableResponse<Response> response = RestAssured.given()
                                                            .when()
                                                            .get("/lines")
                                                            .then()
                                                            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
                                           .map(it -> Long.parseLong(it.header("Location")
                                                                       .split("/")[2]))
                                           .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath()
                                           .getList("", LineResponse.class)
                                           .stream()
                                           .map(LineResponse::getId)
                                           .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @Test
    @DisplayName("지하철 노선 1개를 조회한다.")
    void getLine() {
        ExtractableResponse<Response> extract = createLineResponseBy(LINE_2_REQUEST);
        String uri = extract.header("Location");

        ExtractableResponse<Response> response = RestAssured.given()
                                                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                                                            .when()
                                                            .get(uri)
                                                            .then()
                                                            .extract();
        LineResponse lineResponse = response.body()
                                            .as(LineResponse.class);

        Long id = Long.valueOf(uri.split("/")[2]);

        assertThat(lineResponse).usingRecursiveComparison()
                                .isEqualTo(lineService.findById(id));
        assertThat(lineResponse.getColor()).isEqualTo(color1);
        assertThat(lineResponse.getName()).isEqualTo(name1);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("지하철 노선을 수정한다.")
    void modifyLine() {
        ExtractableResponse<Response> extract = createLineResponseBy(LINE_2_REQUEST);
        String uri = extract.header("Location");

        ExtractableResponse<Response> response = RestAssured.given()
                                                            .body(LINE_1_REQUEST)
                                                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                                                            .when()
                                                            .put(uri)
                                                            .then()
                                                            .extract();


        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("지하철 노선을 삭제한다")
    void deleteLine() {
        ExtractableResponse<Response> extract = createLineResponseBy(LINE_2_REQUEST);

        String uri = extract.header("Location");
        ExtractableResponse<Response> response = RestAssured.given()
                                                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                                                            .when()
                                                            .delete(uri)
                                                            .then()
                                                            .extract();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("구간을 추가한다")
    void addSection() {
        ExtractableResponse<Response> extract = createLineResponseBy(LINE_2_REQUEST);
        String uri = extract.header("Location");

        SectionRequest sectionRequest = new SectionRequest(2L, 3L, 5);
        ExtractableResponse<Response> response = RestAssured.given()
                                                            .body(sectionRequest)
                                                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                                                            .when()
                                                            .post(uri + "/sections")
                                                            .then()
                                                            .extract();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("구간을 삭제한다")
    void deleteSection() {
        ExtractableResponse<Response> extract = createLineResponseBy(LINE_2_REQUEST);
        String uri = extract.header("Location");

        SectionRequest sectionRequest = new SectionRequest(2L, 3L, 5);
        RestAssured.given()
                   .body(sectionRequest)
                   .contentType(MediaType.APPLICATION_JSON_VALUE)
                   .when()
                   .post(uri + "/sections")
                   .then()
                   .extract();

        ExtractableResponse<Response> response = RestAssured.given()
                                                            .param("stationId", 2L)
                                                            .when()
                                                            .delete(uri + "/sections")
                                                            .then()
                                                            .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> createLineResponseBy(LineRequest lineRequest) {
        return RestAssured.given()
                          .body(lineRequest)
                          .contentType(MediaType.APPLICATION_JSON_VALUE)
                          .when()
                          .post("/lines")
                          .then()
                          .extract();
    }
}