package wooteco.subway.controller;

import static org.junit.jupiter.api.Assertions.*;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.SectionRequest;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SectionControllerTest {


    @LocalServerPort
    int port;

    @Autowired
    JdbcTemplate jdbcTemplate;

    Station testStation1;
    Station testStation2;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        StationDao stationDao = new StationDao(jdbcTemplate);
        testStation1 = stationDao.save(new Station("testStation1"));
        testStation2 = stationDao.save(new Station("testStation2"));

        LineDao lineDao = new LineDao(jdbcTemplate);
        lineDao.save(new Line("testLine", "color", 10L));
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
}
