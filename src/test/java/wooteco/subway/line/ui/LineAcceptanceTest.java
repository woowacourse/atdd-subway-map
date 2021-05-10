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
import wooteco.subway.line.dto.LineUpdateRequest;
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
        ExtractableResponse<Response> findResponse = findAllLineToHTTP();

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
    void findLineByIdToHTTP() {
        // given
        String newLineName = "신분당선";
        String newLineColor = "bg-black-500";
        Long upStationId = 3L;
        Long downStationId = 4L;
        int distance = 5;
        LineRequest lineRequest = new LineRequest(newLineName, newLineColor, upStationId, downStationId, distance);

        // when
        ExtractableResponse<Response> createResponse = createLineToHTTP(lineRequest);
        LineResponse createdResponse = createResponse.body().as(LineResponse.class);

        ExtractableResponse<Response> findLineResponse = findLineByIdToHTTP(createdResponse.getId());
        LineResponse findResponse = findLineResponse.body().as(LineResponse.class);

        // then
        assertThat(findLineResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(findResponse.getId()).isEqualTo(createdResponse.getId());
        assertThat(findResponse.getName()).isEqualTo(createdResponse.getName());
        assertThat(findResponse.getColor()).isEqualTo(createdResponse.getColor());
        assertThat(stationResponsesToStrings(findLineResponse.jsonPath().getList("stations", StationResponse.class))).containsExactly("아마찌역", "검프역");
    }

    @DisplayName("노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        String newLineName = "신분당선";
        String newLineColor = "bg-black-500";
        LineUpdateRequest lineUpdateRequest = new LineUpdateRequest(newLineName, newLineColor);

        // when
        ExtractableResponse<Response> updateResponse = updateLineByIdToHTTP(lineUpdateRequest);
        ExtractableResponse<Response> findLineResponse = findLineByIdToHTTP(line.getId());

        LineResponse findResponse = findLineResponse.body().as(LineResponse.class);

        // then
        assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(findResponse.getName()).isEqualTo(newLineName);
        assertThat(findResponse.getColor()).isEqualTo(newLineColor);
    }

    @DisplayName("노선을 제거한다.")
    @Test
    void deleteLine() {
        // given

        // when
        ExtractableResponse<Response> response = deleteLineByIdToHTTP(line.getId());
        ExtractableResponse<Response> findLineResponse = findLineByIdToHTTP(line.getId());

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(findLineResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선 제거시 없는 노선이면 예외가 발생한다.")
    @Test
    void deleteStation() {
        //given

        //when
        ExtractableResponse<Response> response = deleteLineByIdToHTTP(-1L);

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

    private ExtractableResponse<Response> findAllLineToHTTP() {
        ExtractableResponse<Response> findResponse = RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();
        return findResponse;
    }

    private ExtractableResponse<Response> findLineByIdToHTTP(Long lineId) {
        ExtractableResponse<Response> findLineResponse = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines/{id}", lineId)
                .then().log().all()
                .extract();
        return findLineResponse;
    }

    private ExtractableResponse<Response> updateLineByIdToHTTP(LineUpdateRequest lineUpdateRequest) {
        ExtractableResponse<Response> updateResponse = RestAssured.given().log().all()
                .body(lineUpdateRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/{id}", line.getId())
                .then().log().all()
                .extract();
        return updateResponse;
    }

    private ExtractableResponse<Response> deleteLineByIdToHTTP(Long lineId) {
        return RestAssured.given().log().all()
                .when()
                .delete("/lines/{id}", lineId)
                .then().log().all()
                .extract();
    }

    private List<String> stationResponsesToStrings(final List<StationResponse> response) {
        return response.stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());
    }
}
