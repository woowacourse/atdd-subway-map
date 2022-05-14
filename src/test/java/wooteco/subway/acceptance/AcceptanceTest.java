package wooteco.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.dto.line.LineRequest;
import wooteco.subway.dto.section.SectionRequest;
import wooteco.subway.dto.station.StationRequest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class AcceptanceTest {

    protected static final String STATION_PATH_PREFIX = "/stations";
    protected static final String LINE_PATH_PREFIX = "/lines";
    protected static final String SECTION_PATH_PREFIX = "/sections";
    protected static final String LOCATION = "Location";
    protected static final String SLASH = "/";

    @LocalServerPort
    int port;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @AfterEach
    void cleanUp() {
        jdbcTemplate.execute("DELETE FROM section");
        jdbcTemplate.execute("DELETE FROM station");
        jdbcTemplate.execute("DELETE FROM line");
    }

    protected long createAndGetStationId(StationRequest request) {
        final ExtractableResponse<Response> response = createStation(request);
        return extractId(response);
    }

    protected long createAndGetLineId(LineRequest request) {
        final ExtractableResponse<Response> response = createLine(request);
        return extractId(response);
    }

    protected ExtractableResponse<Response> createStation(final StationRequest request) {
        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(STATION_PATH_PREFIX)
                .then().log().all()
                .extract();
    }

    protected ExtractableResponse<Response> createLine(final LineRequest request) {
        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(LINE_PATH_PREFIX)
                .then().log().all()
                .extract();
    }

    protected ExtractableResponse<Response> createSection(final SectionRequest request, final long lineId) {
        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(LINE_PATH_PREFIX + SLASH + lineId + SECTION_PATH_PREFIX)
                .then().log().all()
                .extract();
    }

    protected long extractId(final ExtractableResponse<Response> response) {
        return Long.parseLong(response.header(LOCATION).split(SLASH)[2]);
    }
}
