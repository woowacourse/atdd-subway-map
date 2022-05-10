package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private Long savedId1;
    private Long savedId2;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @BeforeEach
    void init() {
        jdbcTemplate.update("delete from STATION", new EmptySqlParameterSource());
        jdbcTemplate.update("delete from LINE", new EmptySqlParameterSource());
        jdbcTemplate.update("delete from SECTION", new EmptySqlParameterSource());
        insertStationData("강남역");
        insertStationData("선릉역");
        savedId1 = insertData("신분당선", "red");
        savedId2 = insertData("분당선", "yellow");
    }

    private Long insertData(String name, String color) {
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

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        LineRequest request = new LineRequest("2호선", "green", 1L, 2L, 10);
        String path = "/lines";

        // when
        ExtractableResponse<Response> response = execute(request, path);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.header("Location")).isNotBlank()
        );
    }

    private ExtractableResponse<Response> execute(LineRequest request, String path) {
        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(path)
                .then().log().all()
                .extract();
    }

    @DisplayName("기존에 존재하는 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        LineRequest request = new LineRequest("신분당선", "red", 1L, 2L, 10);
        String path = "/lines";

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(path)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
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

        List<Long> expectedLineIds = List.of(savedId1, savedId2);
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(resultLineIds).containsAll(expectedLineIds)
        );
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

        Integer findId = response.jsonPath().get("id");
        //then
        assertThat(findId).isEqualTo(Integer.parseInt(id));
    }

    @DisplayName("존재하지 않는 id 를 이용하여 노선을 조회한다.")
    @Test
    void findLineWithNoneId() {
        //given
        String id = "-1";

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

        Map<String, String> parameter = new HashMap<>();
        parameter.put("name", "다른분당선");
        parameter.put("color", "bg-black-600");
        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(parameter)
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

        Map<String, String> parameter = new HashMap<>();
        parameter.put("name", "분당선");
        parameter.put("color", "bg-red-600");
        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(parameter)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + id)
                .then().log().all()
                .extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선을 중복된 색으로 수정한다.")
    @Test
    void updateLineWithDuplicatedColor() {
        //given
        String id = String.valueOf(savedId1);

        Map<String, String> parameter = new HashMap<>();
        parameter.put("name", "신분당선");
        parameter.put("color", "bg-red-600");
        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(parameter)
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

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete("/lines/" + id)
                .then().log().all()
                .extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
