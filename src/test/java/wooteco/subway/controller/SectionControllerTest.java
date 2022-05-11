package wooteco.subway.controller;

import static org.junit.jupiter.api.Assertions.*;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.SectionRequest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@Sql("classpath:test-schema.sql")
class SectionControllerTest {

    @LocalServerPort
    int port;

    @Autowired
    JdbcTemplate jdbcTemplate;

    SectionDao sectionDao;

    Station testStation1;
    Station testStation2;
    Station testStation3;
    Station testStation4;

    Section section1;
    Section section2;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        StationDao stationDao = new StationDao(jdbcTemplate);

        testStation1 = stationDao.save(new Station("testStation1"));
        testStation2 = stationDao.save(new Station("testStation2"));
        testStation3 = stationDao.save(new Station("testStation3"));
        testStation4 = stationDao.save(new Station("testStation4"));

        LineDao lineDao = new LineDao(jdbcTemplate);
        lineDao.save(new Line("testLine", "color", testStation3, testStation4, 10L));
    }

    @DisplayName("구간을 저장한다")
    @Test
    void createSection_success() {
        SectionRequest sectionRequest = new SectionRequest(1L, 2L, 10L);
        ExtractableResponse<Response> extract = RestAssured.
                given().log().all().
                    body(sectionRequest).
                    contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                    post("/lines/1/sections").
                then().log().all().
                    statusCode(HttpStatus.OK.value()).
                    extract();
    }

    @DisplayName("구간을 제거한다")
    @Test
    void deleteSection_success() {
        SectionRequest sectionRequest = new SectionRequest(1L, 2L, 10L);
        ExtractableResponse<Response> extract = RestAssured.
                given().log().all().
                when().
                    delete("/lines/1/sections?stationId=2").
                then().log().all().
                    statusCode(HttpStatus.OK.value()).
                    extract();
    }
}
