package wooteco.subway.line.ui;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.line.application.LineService;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.LineDao;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.domain.StationDao;
import wooteco.subway.station.dto.StationResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("노선역 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @Autowired
    private StationDao stationDao;

    @Autowired
    private LineDao lineDao;

    @Autowired
    private LineService lineService;

    private Station station1;
    private Station station2;
    private Station station3;
    private Station station4;
    private Line line;

    @BeforeEach
    void init() {
        //given
        this.station1 = stationDao.save(new Station("백기역"));
        this.station2 = stationDao.save(new Station("흑기역"));
        this.station3 = stationDao.save(new Station("아마찌역"));
        this.station4 = stationDao.save(new Station("검프역"));
        LineResponse lineResponse = lineService.save(new LineRequest("백기선", "bg-red-600", station1.getId(), station2.getId(), 7));
        this.line = new Line(lineResponse.getId(), lineResponse.getName(), lineResponse.getColor());
    }

    @DisplayName("노선을 생성한다.")
    @Test
    void createStation() {
        // given
        String newLineName = "신분당선";
        String newLineColor = "bg-black-500";
        Long upStationId = 3L;
        Long downStationId = 4L;
        int distance = 5;
        LineRequest lineRequest = new LineRequest(newLineName, newLineColor, upStationId, downStationId, distance);

        // when
        ExtractableResponse<Response> response = createLineToHTTP(lineRequest);
        LineResponse lineResponse = response.body().as(LineResponse.class);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
        assertThat(stationResponsesToStrings(lineResponse.getStations())).containsExactly(station3.getName(), station4.getName());
    }

    @DisplayName("기존에 존재하는 노선의 이름으로 노선을 생성하면 예외가 발생한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        String newLineName = line.nameAsString();
        String newLineColor = "bg-black-500";
        Long upStationId = 3L;
        Long downStationId = 4L;
        int distance = 5;
        LineRequest lineRequest = new LineRequest(newLineName, newLineColor, upStationId, downStationId, distance);

        // when
        ExtractableResponse<Response> response = createLineToHTTP(lineRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("기존에 존재하는 노선의 색깔로 노선을 생성하면 예외가 발생한다.")
    @Test
    void createStationWithDuplicateColor() {
        // given
        String newLineName = "신분당선";
        String newLineColor = line.getColor();
        Long upStationId = 3L;
        Long downStationId = 4L;
        int distance = 5;
        LineRequest lineRequest = new LineRequest(newLineName, newLineColor, upStationId, downStationId, distance);

        // when
        ExtractableResponse<Response> response = createLineToHTTP(lineRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("모든 지하철 노선을 조회한다.")
    @Test
    void getLines() {
        // given
        String newLineName = "신분당선";
        String newLineColor = "bg-black-500";
        Long upStationId = 3L;
        Long downStationId = 4L;
        int distance = 5;
        LineRequest lineRequest = new LineRequest(newLineName, newLineColor, upStationId, downStationId, distance);

        // when
        ExtractableResponse<Response> createResponse = createLineToHTTP(lineRequest);
        ExtractableResponse<Response> findResponse = RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();

        LineResponse lineResponse = createResponse.body().as(LineResponse.class);
        List<Long> resultLineIds = findResponse.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());

        // then
        assertThat(findResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(resultLineIds).containsExactly(line.getId(), lineResponse.getId());
    }

    @DisplayName("단일 노선을 조회한다.")
    @Test
    void findLineByID() {
        // given
        String name = "신분당선";
        String color = "bg-red-600";
        Long id = 1L;
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", "1");
        params.put("downStationId", "2");
        params.put("distance", "3");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        ExtractableResponse<Response> findLineResponse = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines/{id}", id)
                .then().log().all()
                .extract();

        // then
        assertThat(findLineResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(findLineResponse.jsonPath().getLong("id")).isEqualTo(id);
        assertThat(findLineResponse.jsonPath().getString("name")).isEqualTo(name);
        assertThat(findLineResponse.jsonPath().getString("color")).isEqualTo(color);
        assertThat(stationResponsesToStrings(findLineResponse.jsonPath().getList("stations", StationResponse.class))).containsExactly("백기역", "흑기역");
    }

    @DisplayName("노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        Map<String, String> params1 = new HashMap<>();
        params1.put("name", "백기선");
        params1.put("color", "bg-red-600");

        // given
        Map<String, String> params2 = new HashMap<>();
        params2.put("name", "흑기선");
        params2.put("color", "bg-red-600");

        // when
        ExtractableResponse<Response> response1 = RestAssured.given().log().all()
                .body(params1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        long newId = response1.body().jsonPath().getLong("id");

        ExtractableResponse<Response> response2 = RestAssured.given().log().all()
                .body(params2)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/{id}", newId)
                .then().log().all()
                .extract();

        // then
        assertThat(response2.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "백기선");
        params.put("color", "bg-red-600");

        // when
        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        long deleteId = createResponse.body().jsonPath().getLong("id");
        String uri = createResponse.header("Location");

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(lineDao.findById(deleteId)).isEmpty();
    }

    @DisplayName("노선 제거시 없는 노선이면 예외가 발생한다.")
    @Test
    void deleteStation() {
        String uri = "/lines/{id}";

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete(uri, 0L)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("상행 종점역을 저장한다")
    void upwardEndPointRegistration() {
        //given
        String uri = "/lines/{id}/sections";
        String findUri = "/lines/{id}";

        Map<String, String> params = new HashMap<>();
        params.put("upStationId", String.valueOf(station3.getId()));
        params.put("downStationId", String.valueOf(station1.getId()));
        params.put("distance", "5");

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(uri, line.getId())
                .then().log().all()
                .extract();

        ExtractableResponse<Response> findResponse = RestAssured.given().log().all()
                .when()
                .get(findUri, line.getId())
                .then().log().all()
                .extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(findResponse.jsonPath().getLong("id")).isEqualTo(line.getId());
        assertThat(stationResponsesToStrings(findResponse.jsonPath().getList("stations", StationResponse.class))).containsExactly(station3.getName(), station1.getName(), station2.getName());
    }

    @Test
    @DisplayName("허향 종점역을 저장한다")
    void downwardEndPointRegistration() {
        //given
        String uri = "/lines/{id}/sections";
        String findUri = "/lines/{id}";

        Map<String, String> params = new HashMap<>();
        params.put("upStationId", String.valueOf(station2.getId()));
        params.put("downStationId", String.valueOf(station3.getId()));
        params.put("distance", "5");

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(uri, line.getId())
                .then().log().all()
                .extract();

        ExtractableResponse<Response> findResponse = RestAssured.given().log().all()
                .when()
                .get(findUri, line.getId())
                .then().log().all()
                .extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(findResponse.jsonPath().getLong("id")).isEqualTo(line.getId());
        assertThat(stationResponsesToStrings(findResponse.jsonPath().getList("stations", StationResponse.class))).containsExactly(station1.getName(), station2.getName(), station3.getName());
    }

    private ExtractableResponse<Response> createLineToHTTP(final LineRequest lineRequest) {
        return RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then()
                .log().all()
                .extract();
    }

    private List<String> stationResponsesToStrings(final List<StationResponse> response) {
        return response.stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());
    }
}
