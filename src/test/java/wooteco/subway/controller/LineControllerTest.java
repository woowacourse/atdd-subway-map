package wooteco.subway.controller;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class LineControllerTest extends AcceptanceTest {

    @Autowired
    private LineDao lineDao;
    @Autowired
    private StationDao stationDao;

    private Station savedStation1;
    private Station savedStation2;

    private final String basicPath = "lines";

    @BeforeEach
    void setUpStations() {
        savedStation1 = stationDao.insert(new Station("선릉역"));
        savedStation2 = stationDao.insert(new Station("선정릉역"));
    }

    @DisplayName("지하철 노선을 등록한다.")
    @Test
    void createLine() {
        LineRequest request = new LineRequest("신분당선", "red", savedStation1.getId(), savedStation2.getId(),10);

        ExtractableResponse<Response> response =
                RestAssuredConvenienceMethod.postRequest(request, MediaType.APPLICATION_JSON_VALUE, basicPath);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("비어있는 이름으로 역을 생성하면 400번 코드를 반환한다.")
    @Test
    void createLineWithInvalidNameDateSize() {
        LineRequest request = new LineRequest("", "red", savedStation1.getId(), savedStation2.getId(), 10);

        ExtractableResponse<Response> response =
                RestAssuredConvenienceMethod.postRequest(request, MediaType.APPLICATION_JSON_VALUE, basicPath);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("비어있는 색으로 역을 생성하면 400번 코드를 반환한다.")
    @Test
    void createLineWithInvalidColorDateSize() {
        LineRequest request = new LineRequest("신분당선", "", savedStation1.getId(), savedStation2.getId(), 10);

        ExtractableResponse<Response> response =
                RestAssuredConvenienceMethod.postRequest(request, MediaType.APPLICATION_JSON_VALUE, basicPath);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("중복된 이름을 가진 지하철 노선을 등록할 때 400번 코드를 반환한다.")
    @Test
    void throwsExceptionWhenCreateDuplicatedName() {
        lineDao.insert(new Line("신분당선", "red"));
        LineRequest request = new LineRequest("신분당선", "red", savedStation1.getId(), savedStation2.getId(), 10);

        ExtractableResponse<Response> response =
                RestAssuredConvenienceMethod.postRequest(request, MediaType.APPLICATION_JSON_VALUE, basicPath);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("상행, 역이 같은 가진 지하철 노선을 등록할 때 400번 코드를 반환한다.")
    @Test
    void throwsExceptionWhenCreateLineWithSameUpDownStation() {
        lineDao.insert(new Line("신분당선", "red"));
        LineRequest request = new LineRequest("신분당선", "red", savedStation1.getId(), savedStation1.getId(), 10);

        ExtractableResponse<Response> response =
                RestAssuredConvenienceMethod.postRequest(request, MediaType.APPLICATION_JSON_VALUE, basicPath);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("존재하지 않는 역으로 지하철 노선을 등록할 때 400번 코드를 반환한다.")
    @Test
    void throwsExceptionWhenCreateLineWithNonExistStation() {
        lineDao.insert(new Line("신분당선", "red"));
        LineRequest request = new LineRequest("신분당선", "red", 100L, savedStation1.getId(), 10);

        ExtractableResponse<Response> response =
                RestAssuredConvenienceMethod.postRequest(request, MediaType.APPLICATION_JSON_VALUE, basicPath);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("0이하의 거리를 가진 구간으로 지하철 노선을 등록할 때 400번 코드를 반환한다.")
    @Test
    void throwsExceptionWhenCreateLineWithInvalidDistance() {
        lineDao.insert(new Line("신분당선", "red"));
        LineRequest request = new LineRequest("신분당선", "red", savedStation1.getId(), savedStation1.getId(), 0);

        ExtractableResponse<Response> response =
                RestAssuredConvenienceMethod.postRequest(request, MediaType.APPLICATION_JSON_VALUE, basicPath);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        Line savedLine1 = lineDao.insert(new Line("신분당선", "red"));
        Line savedLine2 = lineDao.insert(new Line("1호선", "blue"));

        ExtractableResponse<Response> response = RestAssuredConvenienceMethod.getRequest(basicPath);
        List<LineResponse> actual = response.jsonPath().getList(".", LineResponse.class);

        assertAll(
                () -> assertThat(actual.get(0).getId()).isEqualTo(savedLine1.getId()),
                () -> assertThat(actual.get(0).getName()).isEqualTo(savedLine1.getName()),
                () -> assertThat(actual.get(0).getColor()).isEqualTo(savedLine1.getColor()),

                () -> assertThat(actual.get(1).getId()).isEqualTo(savedLine2.getId()),
                () -> assertThat(actual.get(1).getName()).isEqualTo(savedLine2.getName()),
                () -> assertThat(actual.get(1).getColor()).isEqualTo(savedLine2.getColor())
        );
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        Line line = lineDao.insert(new Line("신분당선", "red"));

        ExtractableResponse<Response> response =
                RestAssuredConvenienceMethod.getRequest("/lines/" + line.getId());
        LineResponse lineResponse = response.jsonPath().getObject(".", LineResponse.class);

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(lineResponse.getName()).isEqualTo(line.getName()),
                () -> assertThat(lineResponse.getColor()).isEqualTo(line.getColor())
        );
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        Line line = lineDao.insert(new Line("신분당선", "red"));
        Line requestBody = new Line("다른분당선", "blue");

        ExtractableResponse<Response> response =
                RestAssuredConvenienceMethod.putRequest(requestBody, MediaType.APPLICATION_JSON_VALUE, "/lines/" + line.getId());

        Line updatedLine = lineDao.findById(line.getId());
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(updatedLine.getName()).isEqualTo("다른분당선"),
                () -> assertThat(updatedLine.getColor()).isEqualTo("blue")
        );
    }

    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteLine() {
        Line savedLine = lineDao.insert(new Line("신분당선", "red"));

        RestAssuredConvenienceMethod.deleteRequest("/lines/" + savedLine.getId());
        List<Line> lines = lineDao.findAll();

        assertThat(lines.contains(savedLine)).isFalse();
    }

    @DisplayName("존재하지 않는 데이터를 삭제하려고 한다면 400번 코드를 반환한다.")
    @Test
    void deleteLineWithNotExistData() {
        Line savedLine = lineDao.insert(new Line("신분당선", "red"));

        ExtractableResponse<Response> response =
                RestAssuredConvenienceMethod.deleteRequest("/lines/" + (savedLine.getId() + 1));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
