package wooteco.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.line.service.LineService;
import wooteco.subway.section.dto.SectionRequest;
import wooteco.subway.section.service.SectionService;
import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.dto.StationResponse;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

//@Transactional
@Sql({"/init-line.sql", "/init-station.sql"})
@DisplayName("지하철 노선 관리 기능")
class LineAcceptanceTest extends AcceptanceTest {
    private static final String color = "초록색";
    private static final String name = "2호선";

    private LineRequest lineRequest;
    private LineRequest lineRequest2;

    @Autowired
    private StationDao stationDao;

    @Autowired
    private SectionService sectionService;

    private Station 잠실역;
    private Station 강남역;
    private Station 강변역;

    @BeforeEach
    void beforeEach() {
        잠실역 = stationDao.save("잠실역");
        강남역 = stationDao.save("강남역");
        강변역 = stationDao.save("강변역");
        lineRequest = new LineRequest("2호선", "초록색", 잠실역.getId(), 강남역.getId(), 3);
        lineRequest2 = new LineRequest("3호선", "갈색", 잠실역.getId(), 강변역.getId(), 2);
    }

    @Test
    @DisplayName("지하철 노선을 생성한다.")
    void createLine() {
        // given
        ExtractableResponse<Response> response = createLineResponseBy(lineRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @Test
    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성시 실패.")
    void createLineWithDuplicateName() {
        // given & when
        createLineResponseBy(lineRequest);
        ExtractableResponse<Response> response = createLineResponseBy(lineRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("지하철 전체 노선을 조회한다.")
    void getLines() {
        /// given
        ExtractableResponse<Response> createResponse1 = createLineResponseBy(lineRequest);
        ExtractableResponse<Response> createResponse2 = createLineResponseBy(lineRequest2);

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
        ExtractableResponse<Response> extract = createLineResponseBy(lineRequest);
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

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        assertThat(lineResponse.getColor()).isEqualTo(color);
        assertThat(lineResponse.getName()).isEqualTo(name);
        List<StationResponse> expectedPath = StationResponse.listOf(sectionService.sectionsByLineId(id).path());
        assertThat(lineResponse.getStations()).containsAll(expectedPath);
    }

    @Test
    @DisplayName("지하철 노선을 수정한다.")
    void modifyLine() {
        ExtractableResponse<Response> extract = createLineResponseBy(lineRequest);
        String uri = extract.header("Location");

        ExtractableResponse<Response> response = RestAssured.given()
                .body(lineRequest2)
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
        ExtractableResponse<Response> extract = createLineResponseBy(lineRequest);

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
        ExtractableResponse<Response> extract = createLineResponseBy(lineRequest);
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
        ExtractableResponse<Response> extract = createLineResponseBy(lineRequest);
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
