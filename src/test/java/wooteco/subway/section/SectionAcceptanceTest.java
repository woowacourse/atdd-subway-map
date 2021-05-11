package wooteco.subway.section;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.station.dto.StationResponse;

@DisplayName("구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @BeforeEach
    void clear() {
        jdbcTemplate.update("TRUNCATE TABLE line;");
        jdbcTemplate.update("TRUNCATE TABLE section;");
        jdbcTemplate.update("TRUNCATE TABLE station;");

        jdbcTemplate.update("INSERT INTO station(name) VALUES('강남역');");
        jdbcTemplate.update("INSERT INTO station(name) VALUES('역삼역');");
        jdbcTemplate.update("INSERT INTO station(name) VALUES('잠실역');");
        jdbcTemplate.update("INSERT INTO station(name) VALUES('선릉역');");

        jdbcTemplate.update("INSERT INTO line(name, color) VALUES('1호선', 'black')");

        jdbcTemplate.update(
            "INSERT INTO section(line_id, up_station_id, down_station_id, distance) VALUES('1', '1', '3', '3')");
        jdbcTemplate.update(
            "INSERT INTO section(line_id, up_station_id, down_station_id, distance) VALUES('1', '3', '2', '3')");
    }

    @Test
    @DisplayName("구간 추가 - 성공")
    void createSection() {
        RestAssured.defaultParser = Parser.JSON;

        Map<String, String> params = new HashMap<>();
        params.put("distance", "1");
        params.put("upStationId", "4");
        params.put("downStationId", "1");
        params.put("lineId", "1");

        // when
        ExtractableResponse<Response> response = RestAssured
            .given()
            .log()
            .all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines/1/sections")
            .then()
            .log()
            .all()
            .extract();

        // when
        LineResponse lineResponse = RestAssured
            .given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/lines/1")
            .then()
            .log().all()
            .extract()
            .as(LineResponse.class);

        List<Long> stationIds = lineResponse.getStations()
            .stream()
            .map(StationResponse::getId)
            .collect(Collectors.toList());

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(stationIds).containsExactly(4L, 1L , 3L, 2L);
    }

    @ParameterizedTest(name = "구간 추가 - 실패(양쪽 종점일 경우, 구간의 상행 하행 모두 노선 내에 존재할 경우")
    @CsvSource({"1, 3, 2", "1, 1, 2", "4, 5, 2"})
    void createSectionFailures(String upStationId, String downStationId, String distance) {
        Map<String, String> params = new HashMap<>();
        params.put("distance", distance);
        params.put("downStationId", downStationId);
        params.put("upStationId", upStationId);
        params.put("lineId", "1");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines/1/sections")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @ParameterizedTest(name = "구간 추가 - 실패(추가하려는 구간의 거리가 잘못된 경우")
    @CsvSource({"1, 2, 3", "1, 2, 4", "2, 3, 4"})
    void createSectionFailuresWithDistance(String upStationId, String downStationId,
        String distance) {
        Map<String, String> params = new HashMap<>();
        params.put("distance", distance);
        params.put("downStationId", downStationId);
        params.put("upStationId", upStationId);
        params.put("lineId", "1");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines/1/sections")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @ParameterizedTest(name = "구간 추가 - 실패(null)")
    @CsvSource(value = {"null, null, null", "1, null, 2", "null, 1, 2",
        "1, 2, null"}, nullValues = {"null"})
    void createSectionFailuresWhenNull(String upStationId, String downStationId, String distance) {
        Map<String, String> params = new HashMap<>();
        params.put("distance", distance);
        params.put("downStationId", downStationId);
        params.put("upStationId", upStationId);
        params.put("lineId", "1");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines/1/sections")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @ParameterizedTest(name = "구간 추가 - 실패(empty)")
    @CsvSource(value = {",,", "1,,2", ",1,2", "1,2,"})
    void createSectionFailuresWhenEmpty(String upStationId, String downStationId, String distance) {
        Map<String, String> params = new HashMap<>();
        params.put("distance", distance);
        params.put("downStationId", downStationId);
        params.put("upStationId", upStationId);
        params.put("lineId", "1");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines/1/sections")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @ParameterizedTest(name = "구간을 제거(노선의 역 삭제) - 성공")
    @CsvSource({"1", "2", "3"})
    void deleteSection(String stationId) {

        ExtractableResponse<Response> deleteResponse = RestAssured.given().log().all()
            .param("stationId", stationId)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .delete("/lines/1/sections")
            .then().log().all()
            .extract();

        // when
        LineResponse lineResponse = RestAssured
            .given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/lines/1")
            .then()
            .log().all()
            .extract()
            .as(LineResponse.class);

        List<Long> stationIds = lineResponse.getStations()
            .stream()
            .map(StationResponse::getId)
            .collect(Collectors.toList());

        List<Long> containsId = new ArrayList<>(Arrays.asList(1L, 3L, 2L));
        containsId.remove(Long.parseLong(stationId));
        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(stationIds).containsExactly(containsId.toArray(new Long[0]));
    }

    @ParameterizedTest(name = "구간을 제거(노선의 역 삭제) - 실패")
    @CsvSource({"1,2", "2,4", "3,3"})
    void deleteSection(String stationId1, String stationId2) {

        ExtractableResponse<Response> beforeDeleteResponse = RestAssured.given().log().all()
            .param("stationId", stationId1)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .delete("/lines/1/sections")
            .then().log().all()
            .extract();

        ExtractableResponse<Response> deleteResponse = RestAssured.given().log().all()
            .param("stationId", stationId2)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .delete("/lines/1/sections")
            .then().log().all()
            .extract();

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
