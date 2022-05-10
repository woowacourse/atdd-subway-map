package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;

public class SectionAcceptanceTest extends AcceptanceTest {

    private StationDao stationDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void init() {
        stationDao = new StationDao(jdbcTemplate);
    }

    ExtractableResponse<Response> givenLineRequest() {
        stationDao.save(new Station("강남역"));
        stationDao.save(new Station("선릉역"));
        stationDao.save(new Station("잠실역"));
        stationDao.save(new Station("낙성대역"));

        Map<String, String> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "bg-red-600");
        params.put("upStationId", "1");
        params.put("downStationId", "2");
        params.put("distance", "10");

        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
    }

    ExtractableResponse<Response> givenLineRequestWithParams(Map<String, String> params) {
        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines/1/sections")
            .then().log().all()
            .extract();
    }

    @DisplayName("지하철 구간을 생성한다.")
    @Test
    void createSection() {
        givenLineRequest();

        Map<String, String> params = new HashMap<>();
        params.put("upStationId", "3");
        params.put("downStationId", "1");
        params.put("distance", "10");

        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines/1/sections")
            .then().log().all()
            .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 구간을 삭제한다.")
    @Test
    void deleteSection() {
        givenLineRequest();

        Map<String, String> params = new HashMap<>();
        params.put("upStationId", "3");
        params.put("downStationId", "1");
        params.put("distance", "10");

        givenLineRequestWithParams(params);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .delete("/lines/1/sections?stationId=1")
            .then().log().all()
            .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
