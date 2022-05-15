package wooteco.subway.acceptance;

import static wooteco.subway.acceptance.TestFixtures.extractPostResponse;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.StationRequest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/schema.sql")
public class AcceptanceTest {

    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;

        extractPostResponse(new StationRequest("강남역"), "/stations");
        extractPostResponse(new StationRequest("선릉역"), "/stations");
        extractPostResponse(new StationRequest("역삼역"), "/stations");

        LineRequest lineRequest = new LineRequest(
                "신분당선", "red", 1L, 2L, 5);
        extractPostResponse(lineRequest, "/lines");
    }
}
