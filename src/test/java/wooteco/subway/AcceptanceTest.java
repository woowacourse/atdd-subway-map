package wooteco.subway;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/cleanUp.sql")
public class AcceptanceTest {

    @LocalServerPort
    int port;

    @BeforeEach
    protected void setUp() {
        RestAssured.port = port;
    }
}
