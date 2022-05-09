package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import wooteco.subway.ui.dto.LineCreateRequest;
import wooteco.subway.ui.dto.LineRequest;
import wooteco.subway.service.dto.LineResponse;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private Long savedId1;
    private Long savedId2;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @BeforeEach
    void init() {
        jdbcTemplate.update("delete from LINE", new EmptySqlParameterSource());

        savedId1 = insertLine("신분당선", "bg-red-600");
        savedId2 = insertLine("분당선", "bg-green-600");

        insertSection(savedId1, 1L, 2L);
        insertSection(savedId2, 1L, 2L);
    }

    private Long insertLine(String name, String color) {
        String insertSql = "insert into LINE (name, color) values (:name, :color)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource source = new MapSqlParameterSource();
        source.addValue("name", name);
        source.addValue("color", color);

        jdbcTemplate.update(insertSql, source, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    private void insertSection(Long lineId, Long upStationId, Long downStationId) {
        String insertSql = "insert into section (line_id, up_station_id, down_station_id, distance) "
                + "values (:lineId, :upStationId, :downStationId, :distance)";

        MapSqlParameterSource source = new MapSqlParameterSource();
        source.addValue("lineId", lineId);
        source.addValue("upStationId", upStationId);
        source.addValue("downStationId", downStationId);
        source.addValue("distance", 5);

        jdbcTemplate.update(insertSql, source);
    }

    @DisplayName("지하철 노선 생성")
    @Test
    void createLine() {
        // given
        LineCreateRequest lineCreateRequest = new LineCreateRequest("2호선", "bg-green-500", 1L, 2L, 20);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(lineCreateRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("잘못된 지하철 노선 생성")
    @ParameterizedTest(name = "{0}")
    @MethodSource("parameterProvider")
    void createLineFailed(String displayName, LineCreateRequest lineCreateRequest) {
        // given

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(lineCreateRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private static Stream<Arguments> parameterProvider() {
        return Stream.of(
                Arguments.arguments(
                        "이미 존재하는 노선 이름으로 생성",
                        new LineCreateRequest("신분당선", "bg-red-600", 1L, 2L, 10)
                ),
                Arguments.arguments(
                        "구간 거리가 음수",
                        new LineCreateRequest("2호선", "bg-green-500", 1L, 2L, -10)
                ),
                Arguments.arguments(
                        "구간 거리가 0",
                        new LineCreateRequest("2호선", "bg-green-500", 1L, 2L, 0)
                ),
                Arguments.arguments(
                        "존재하지 않는 역 등록 시도",
                        new LineCreateRequest("2호선", "bg-green-500", 1L, 6L, 10)
                )
        );
    }

    @DisplayName("노선 목록을 조회한다.")
    @Test
    void getLines() {
        /// given

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = List.of(savedId1, savedId2);
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("id 를 이용하여 노선을 조회한다.")
    @Test
    void findLine() {
        //given
        String id = String.valueOf(savedId1);

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines/" + id)
                .then().log().all()
                .extract();

        //then
        Integer findId = response.jsonPath().get("id");
        assertThat(findId).isEqualTo(Integer.parseInt(id));
    }

    @DisplayName("존재하지 않는 id 를 이용하여 노선을 조회한다.")
    @Test
    void findLineWithNoneId() {
        //given
        String id = "999999";

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines/" + id)
                .then().log().all()
                .extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("id 를 이용하여 노선을 수정한다.")
    @Test
    void updateLine() {
        //given
        String id = String.valueOf(savedId1);

        //when
        String name = "다른분당선";
        LineRequest lineRequest = new LineRequest(name, "bg-red-600");

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + id)
                .then().log().all()
                .extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("노선을 중복된 이름으로 수정한다.")
    @Test
    void updateLineWithDuplicatedName() {
        //given
        String id = String.valueOf(savedId1);

        //when
        String name = "분당선";
        LineRequest lineRequest = new LineRequest(name, "bg-red-600");

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + id)
                .then().log().all()
                .extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("id 를 이용하여 노선을 삭제한다.")
    @Test
    void deleteLineById() {
        //given
        String id = String.valueOf(savedId1);
        List<Long> expectedIds = selectLines();
        expectedIds.remove(Long.parseLong(id));

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete("/lines/" + id)
                .then().log().all()
                .extract();

        //then
        assertThat(expectedIds).isEqualTo(selectLines());
    }

    private List<Long> selectLines() {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();

        return response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
    }
}
