package wooteco.subway.acceptance;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import wooteco.subway.dao.JdbcLineDao;
import wooteco.subway.dao.JdbcStationDao;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AcceptanceTest {

    @LocalServerPort
    int port;

    @Autowired
    private JdbcStationDao jdbcStationDao;

    @Autowired
    private JdbcLineDao jdbcLineDao;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        jdbcLineDao.deleteAll();
        jdbcStationDao.deleteAll();
    }
}
