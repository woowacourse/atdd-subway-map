package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationResponse;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private Long lineId1;
    private Long lineId2;
    private Long stationId1;
    private Long stationId2;
    private Long stationId3;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @BeforeEach
    void init() {
        jdbcTemplate.update("delete from STATION", new EmptySqlParameterSource());
        jdbcTemplate.update("delete from LINE", new EmptySqlParameterSource());
        jdbcTemplate.update("delete from SECTION", new EmptySqlParameterSource());
        stationId1 = insertStationData("강남역");
        stationId2 = insertStationData("선릉역");
        stationId3 = insertStationData("잠실역");
        lineId1 = insertLineData("신분당선", "red");
        lineId2 = insertLineData("분당선", "yellow");
        insertSectionData(lineId1, stationId1, stationId2, 10);
        insertSectionData(lineId2, stationId1, stationId3, 10);
    }

    @DisplayName("line 을 저장한다.")
    @Test
    void saveLine() {
        // given
        LineRequest request = new LineRequest("2호선", "green", stationId1, stationId2, 10);
        String path = "/lines";
        // when
        ExtractableResponse<Response> response = executePostLineApi(request, path);
        List<Station> expectStations = response.body().jsonPath().getList("stations", StationResponse.class)
                .stream().map(it -> new Station(it.getId(), it.getName()))
                .collect(Collectors.toList());
        // then
        assertAll(() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.header("Location")).isNotBlank(),
                () -> assertThat(expectStations).isEqualTo(
                        List.of(new Station(stationId1, "강남역"), new Station(stationId2, "선릉역"))));
    }

    @DisplayName("기존에 존재하는 line name 으로 line 을 저장한다.")
    @Test
    void saveLineWithDuplicateName() {
        // given
        LineRequest request = new LineRequest("신분당선", "red", stationId1, stationId2, 10);
        String path = "/lines";
        // when
        ExtractableResponse<Response> response = executePostLineApi(request, path);
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("line 목록을 조회한다.")
    @Test
    void getLines() {
        /// given
        String path = "/lines";
        // when
        ExtractableResponse<Response> response = executeGetApi(path);

        List<Long> expectedLineIds = List.of(lineId1, lineId2);
        List<Long> actualLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId).collect(Collectors.toList());
        // then
        List<List<Station>> linesStations = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(its -> its.getStations().stream()
                        .map(it -> new Station(it.getId(), it.getName())).collect(Collectors.toList()))
                .collect(Collectors.toList());

        assertAll(() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(actualLineIds).containsAll(expectedLineIds),
                () -> assertThat(linesStations).isEqualTo(
                        List.of(List.of(new Station(stationId1, "강남역"), new Station(stationId2, "선릉역")),
                                List.of(new Station(stationId1, "강남역"), new Station(stationId3, "잠실역")))));
    }

    @DisplayName("id 를 이용하여 line 을 조회한다.")
    @Test
    void findLineById() {
        //given
        String path = "/lines/" + lineId1;
        //when
        ExtractableResponse<Response> response = executeGetApi(path);

        Integer findId = response.jsonPath().get("id");
        List<Station> expectStations = response.body().jsonPath().getList("stations", StationResponse.class)
                .stream().map(it -> new Station(it.getId(), it.getName()))
                .collect(Collectors.toList());
        //then
        assertAll(() -> assertThat(findId).isEqualTo(lineId1.intValue()),
                () -> assertThat(expectStations).isEqualTo(
                        List.of(new Station(stationId1, "강남역"), new Station(stationId2, "선릉역"))));
    }

    @DisplayName("존재하지 않는 id 를 이용하여 line 을 조회한다.")
    @Test
    void findLineWithNoneId() {
        //given
        String unSavedId = "-1";
        String path = "/lines/" + unSavedId;
        //when
        ExtractableResponse<Response> response = executeGetApi(path);
        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("id 를 이용하여 line 을 수정한다.")
    @Test
    void updateLine() {
        //given
        Line line = new Line("다른분당선", "black");
        String path = "/lines/" + lineId1;
        //when
        ExtractableResponse<Response> response = executePutApi(line, path);
        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("line 을 중복된 name 으로 수정한다.")
    @Test
    void updateLineWithDuplicatedName() {
        //given
        Line line = new Line("분당선", "blue");
        String path = "/lines/" + lineId1;
        //when
        ExtractableResponse<Response> response = executePutApi(line, path);
        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("line 을 중복된 color 으로 수정한다.")
    @Test
    void updateLineWithDuplicatedColor() {
        //given
        Line line = new Line("신분당선", "red");
        String path = "/lines/" + lineId1;
        //when
        ExtractableResponse<Response> response = executePutApi(line, path);
        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("id 를 이용하여 line 을 삭제한다.")
    @Test
    void deleteLineById() {
        //given
        String path = "/lines/" + lineId1;
        //when
        ExtractableResponse<Response> response = executeDeleteApi(path);
        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("line id를 이용하여 section 을 저장한다.")
    @Test
    void saveSection() {
        //given
        SectionRequest sectionRequest = new SectionRequest(stationId2, stationId3, 10);
        String path = "lines/" + lineId1 + "/sections";
        //when
        ExtractableResponse<Response> response = executePostSectionApi(sectionRequest, path);
        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private Long insertLineData(String name, String color) {
        String insertSql = "insert into LINE (name, color) values (:name, :color)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource source = new MapSqlParameterSource();
        source.addValue("name", name);
        source.addValue("color", color);
        jdbcTemplate.update(insertSql, source, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    private Long insertStationData(String name) {
        String insertSql = "insert into STATION (name) values (:name)";
        SqlParameterSource source = new MapSqlParameterSource("name", name);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(insertSql, source, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    private void insertSectionData(Long lineId, Long upStationId, Long downStationId, int distance) {
        String insertSql = "insert into SECTION (line_id, up_station_id, down_station_id, distance) values (:lineId, :upStationId, :downStationId, :distance)";
        SqlParameterSource source = new BeanPropertySqlParameterSource(
                new Section(lineId, upStationId, downStationId, distance));
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(insertSql, source, keyHolder);
        Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    private ExtractableResponse<Response> executeGetApi(String path) {
        return RestAssured.given().log().all()
                .when().get(path)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> executePostLineApi(LineRequest request, String path) {
        return RestAssured.given().log().all().body(request).contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(path)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> executePostSectionApi(SectionRequest request, String path) {
        return RestAssured.given().log().all().body(request).contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(path)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> executePutApi(Line line, String path) {
        return RestAssured.given().log().all()
                .body(line).contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(path)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> executeDeleteApi(String path) {
        return RestAssured.given().log().all()
                .when().delete(path)
                .then().log().all().extract();
    }
}
