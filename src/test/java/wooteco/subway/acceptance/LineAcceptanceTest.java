package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import wooteco.subway.service.dto.LineResponse;
import wooteco.subway.ui.dto.LineCreateRequest;
import wooteco.subway.ui.dto.LineRequest;
import wooteco.subway.ui.dto.SectionRequest;
import wooteco.subway.utils.RestAssuredUtil;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private Long savedId1;
    private Long savedId2;
    private Long savedInsertId;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @BeforeEach
    void init() {
        jdbcTemplate.update("delete from LINE", new EmptySqlParameterSource());

        savedId1 = insertLine("신분당선", "bg-red-600");
        savedId2 = insertLine("분당선", "bg-green-600");

        savedInsertId = insertSection(savedId1, 1L, 2L);
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

    private Long insertSection(Long lineId, Long upStationId, Long downStationId) {
        String insertSql = "insert into section (line_id, up_station_id, down_station_id, distance) "
                + "values (:lineId, :upStationId, :downStationId, :distance)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource source = new MapSqlParameterSource();
        source.addValue("lineId", lineId);
        source.addValue("upStationId", upStationId);
        source.addValue("downStationId", downStationId);
        source.addValue("distance", 5);

        jdbcTemplate.update(insertSql, source, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();

    }

    @DisplayName("지하철 노선 생성")
    @Test
    void createLine() {
        // given
        LineCreateRequest lineCreateRequest = new LineCreateRequest("2호선", "bg-green-500", 1L, 2L, 20);

        // when
        ExtractableResponse<Response> response = RestAssuredUtil.post("/lines", lineCreateRequest);

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
        ExtractableResponse<Response> response = RestAssuredUtil.post("/lines", lineCreateRequest);

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
        List<Long> expectedLineIds = List.of(savedId1, savedId2);

        // when
        ExtractableResponse<Response> response = RestAssuredUtil.get("/lines");
        List<Long> resultLineIds = findLineIds(response);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    private List<Long> findLineIds(ExtractableResponse<Response> response) {
        return response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
    }

    @DisplayName("id 를 이용하여 노선을 조회한다.")
    @Test
    void findLine() {
        //given
        String id = String.valueOf(savedId1);

        //when
        ExtractableResponse<Response> response = RestAssuredUtil.get("/lines/" + id);

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
        ExtractableResponse<Response> response = RestAssuredUtil.get("/lines/" + id);

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

        ExtractableResponse<Response> response = RestAssuredUtil.put("/lines/" + id, lineRequest);

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

        ExtractableResponse<Response> response = RestAssuredUtil.put("/lines/" + id, lineRequest);

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
        RestAssuredUtil.delete("/lines/" + id, null);

        //then
        assertThat(expectedIds).isEqualTo(selectLines());
    }

    private List<Long> selectLines() {
        ExtractableResponse<Response> response = RestAssuredUtil.get("/lines");
        return findLineIds(response);
    }

    @DisplayName("구간 생성")
    @Test
    void createSection() {
        //given
        SectionRequest sectionRequest = new SectionRequest(1L, 2L, 5);
        String url = "/lines/" + savedId2 + "/sections";

        //when
        ExtractableResponse<Response> response = RestAssuredUtil.post(url, sectionRequest);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("구간 삭제")
    @Test
    void deleteSection() {
        //given
        String url = "/lines/" + savedId1 + "/sections";
        Map<String, String> source = new HashMap<>();
        source.put("stationId", savedInsertId.toString());

        //when
        ExtractableResponse<Response> response = RestAssuredUtil.delete(url, source);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
