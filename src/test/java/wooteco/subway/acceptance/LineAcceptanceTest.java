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

    private Long insertSectionData(Long lineId, Long upStationId, Long downStationId, int distance) {
        String insertSql = "insert into SECTION (line_id, up_station_id, down_station_id, distance) values (:lineId, :upStationId, :downStationId, :distance)";
        SqlParameterSource source = new BeanPropertySqlParameterSource(
                new Section(lineId, upStationId, downStationId, distance));
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(insertSql, source, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        LineRequest request = new LineRequest("2호선", "green", stationId1, stationId2, 10);
        String path = "/lines";
        // when
        ExtractableResponse<Response> response = executePostApi(request, path);
        List<Station> expectStations = response.body().jsonPath().getList("stations", StationResponse.class)
                .stream().map(it -> new Station(it.getId(), it.getName()))
                .collect(Collectors.toList());
        // then
        assertAll(() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.header("Location")).isNotBlank(),
                () -> assertThat(expectStations).isEqualTo(
                        List.of(new Station(stationId1, "강남역"), new Station(stationId2, "선릉역"))));
    }

    @DisplayName("기존에 존재하는 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        LineRequest request = new LineRequest("신분당선", "red", stationId1, stationId2, 10);
        String path = "/lines";
        // when
        ExtractableResponse<Response> response = executePostApi(request, path);
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선 목록을 조회한다.")
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
        assertAll(() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(actualLineIds).containsAll(expectedLineIds));
    }

    @DisplayName("id 를 이용하여 노선을 조회한다.")
    @Test
    void findLine() {
        //given
        String id = String.valueOf(lineId1);
        String path = "/lines/" + id;
        //when
        ExtractableResponse<Response> response = executeGetApi(path);
        Integer findId = response.jsonPath().get("id");
        List<Station> expectStations = response.body().jsonPath().getList("stations", StationResponse.class)
                .stream().map(it -> new Station(it.getId(), it.getName()))
                .collect(Collectors.toList());
        //then
        assertAll(() -> assertThat(findId).isEqualTo(Integer.parseInt(id)),
                () -> assertThat(expectStations).isEqualTo(
                        List.of(new Station(stationId1, "강남역"), new Station(stationId2, "선릉역"))));
    }

    @DisplayName("존재하지 않는 id 를 이용하여 노선을 조회한다.")
    @Test
    void findLineWithNoneId() {
        //given
        String id = "-1";
        String path = "/lines/" + id;
        //when
        ExtractableResponse<Response> response = executeGetApi(path);
        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("id 를 이용하여 노선을 수정한다.")
    @Test
    void updateLine() {
        //given
        String id = String.valueOf(lineId1);
        Line line = new Line("다른분당선", "black");
        String path = "/lines/" + id;
        //when
        ExtractableResponse<Response> response = executePutApi(line, path);
        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("노선을 중복된 이름으로 수정한다.")
    @Test
    void updateLineWithDuplicatedName() {
        //given
        String id = String.valueOf(lineId1);
        Line line = new Line("분당선", "blue");
        String path = "/lines/" + id;
        //when
        ExtractableResponse<Response> response = executePutApi(line, path);
        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선을 중복된 색으로 수정한다.")
    @Test
    void updateLineWithDuplicatedColor() {
        //given
        String id = String.valueOf(lineId1);
        Line line = new Line("신분당선", "red");
        String path = "/lines/" + id;
        //when
        ExtractableResponse<Response> response = executePutApi(line, path);
        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("id 를 이용하여 노선을 삭제한다.")
    @Test
    void deleteLineById() {
        //given
        String id = String.valueOf(lineId1);
        String path = "/lines/" + id;
        //when
        ExtractableResponse<Response> response = executeDeleteApi(path);
        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> executeGetApi(String path) {
        return RestAssured.given().log().all()
                .when().get(path)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> executePostApi(LineRequest request, String path) {
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
