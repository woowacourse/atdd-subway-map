package wooteco.subway.controller;

import static org.junit.jupiter.api.Assertions.*;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
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
import wooteco.subway.domain.entity.LineEntity;
import wooteco.subway.domain.entity.SectionEntity;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.service.LineService;
import wooteco.subway.service.SectionService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@Sql("classpath:test-schema.sql")
class SectionControllerTest {

    @LocalServerPort
    int port;

    @Autowired
    StationDao stationDao;
    @Autowired
    SectionDao sectionDao;
    @Autowired
    LineDao lineDao;

    @Autowired
    SectionService sectionService;
    @Autowired
    LineService lineService;

    Station testStation1;
    Station testStation2;
    Station testStation3;
    Station testStation4;
    Station testStation5;
    Station testStation6;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;

        testStation1 = stationDao.save(new Station("testStation1"));
        testStation2 = stationDao.save(new Station("testStation2"));
        testStation3 = stationDao.save(new Station("testStation3"));
        testStation4 = stationDao.save(new Station("testStation4"));
        testStation5 = stationDao.save(new Station("testStation5"));
        testStation6 = stationDao.save(new Station("testStation6"));

        LineResponse lineResponse = lineService
                .createLineAndRegisterSection(new LineRequest("testLine", "color", testStation3.getId(), testStation4.getId(), 10L));

        sectionService.insertSection(lineResponse.getId(), new SectionRequest(testStation4.getId(), testStation5.getId(), 20L));
    }

    @DisplayName("구간을 저장한다")
    @Test
    void createSection_success() {
        SectionRequest sectionRequest = new SectionRequest(2L, 3L, 10L);
        RestAssured.
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
        RestAssured.
                given().log().all().
                when().
                    delete("/lines/1/sections?stationId=3").
                then().log().all().
                    statusCode(HttpStatus.OK.value()).
                    extract();
    }
}
