package wooteco.subway.acceptance;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.service.LineService;
import wooteco.subway.service.SectionService;

@Sql("classpath:test-schema.sql")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AcceptanceTest {
    @LocalServerPort
    int port;

    @Autowired
    protected SectionService sectionService;
    @Autowired
    protected LineService lineService;
    @Autowired
    protected StationDao stationDao;

    protected LineRequest testLine1;
    protected LineRequest testLine2;
    protected LineRequest testLine3;

    protected Station testStation1;
    protected Station testStation2;
    protected Station testStation3;
    protected Station testStation4;
    protected Station testStation5;
    protected Station testStation6;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;

        testStation1 = stationDao.save(new Station("testStation1"));
        testStation2 = stationDao.save(new Station("testStation2"));
        testStation3 = stationDao.save(new Station("testStation3"));
        testStation4 = stationDao.save(new Station("testStation4"));
        testStation5 = stationDao.save(new Station("testStation5"));
        testStation6 = stationDao.save(new Station("testStation6"));

        testLine1 = new LineRequest("신분당선", "bg-red-600", testStation1.getId(), testStation2.getId(), 10L);
        testLine2  = new LineRequest("분당선", "bg-red-600", testStation3.getId(), testStation4.getId(), 20L);
        testLine3 = new LineRequest("2호선", "bg-green-500", testStation5.getId(), testStation6.getId(), 30L);

        LineResponse lineResponse = lineService
                .createLineAndRegisterSection(new LineRequest("testLine", "color", testStation3.getId(), testStation4.getId(), 10L));

        sectionService.insertSection(lineResponse.getId(), new SectionRequest(testStation4.getId(), testStation5.getId(), 20L));
    }
}
