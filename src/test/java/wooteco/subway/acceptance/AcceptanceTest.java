package wooteco.subway.acceptance;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("classpath:test-schema.sql")
public class AcceptanceTest {
    @LocalServerPort
    protected int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;

        Map<String, String> stationParams = new HashMap<>();
        stationParams.put("name", "강남역");
        RestAssuredHelper.jsonPost(stationParams, "/stations");

        Map<String, String> stationParams2 = new HashMap<>();
        stationParams2.put("name", "역삼역");
        RestAssuredHelper.jsonPost(stationParams2, "/stations");

        Map<String, String> lineParams = new HashMap<>();
        lineParams.put("color", "bg-red-600");
        lineParams.put("name", "신분당선");
        lineParams.put("upStationId", "1");
        lineParams.put("downStationId", "2");
        lineParams.put("distance", "10");
        RestAssuredHelper.jsonPost(lineParams, "/lines");

        Map<String, String> stationParams3 = new HashMap<>();
        stationParams3.put("name", "선릉역");
        RestAssuredHelper.jsonPost(stationParams3, "/stations");

        Map<String, String> stationParams4 = new HashMap<>();
        stationParams4.put("name", "삼성역");
        RestAssuredHelper.jsonPost(stationParams4, "/stations");

        Map<String, String> lineParams2 = new HashMap<>();
        lineParams2.put("color", "bg-green-600");
        lineParams2.put("name", "2호선");
        lineParams2.put("upStationId", "3");
        lineParams2.put("downStationId", "4");
        lineParams2.put("distance", "10");
        RestAssuredHelper.jsonPost(lineParams2, "/lines");
    }
}
