package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.ExceptionMessage;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private static final String LOCATION = "Location";

    @Autowired
    private StationDao stationDao;

    @Test
    @DisplayName("지하철 노선을 생성한다.")
    void createLine() {
        // given
        String lineName = "7호선";
        String lineColor = "bg-red-600";

        Station 강남역 = stationDao.save(new Station("강남역"));
        Station 노원역 = stationDao.save(new Station("노원역"));

        // when
        LineRequest requestBody = new LineRequest(lineName, lineColor, 노원역.getId(), 강남역.getId(), 10);
        ExtractableResponse<Response> response = postWithBody("/lines", requestBody);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header(LOCATION)).isNotBlank();

        LineResponse lineResponse = response.body().as(LineResponse.class);
        List<Long> stationIds = lineResponse.getStations().stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        assertAll(() -> {
            assertThat(lineResponse.getId()).isNotNull();
            assertThat(lineResponse.getName()).isEqualTo(lineName);
            assertThat(lineResponse.getColor()).isEqualTo(lineColor);
            assertThat(stationIds).containsExactly(노원역.getId(), 강남역.getId());
        });
    }

    @Test
    @DisplayName("이미 존재하는 이름의 호선을 생성하려고 하면 BAD REQUEST를 반환한다.")
    void createLine_duplicatedName() {
        // given
        String lineName = "7호선";
        String redColor = "bg-red-600";
        String blueColor = "bg-blue-600";

        Station 강남역 = stationDao.save(new Station("강남역"));
        Station 노원역 = stationDao.save(new Station("노원역"));

        LineRequest lineRequest = new LineRequest(lineName, redColor, 노원역.getId(), 강남역.getId(), 10);
        postWithBody("/lines", lineRequest);

        // when
        LineRequest duplicatedNameRequest = new LineRequest(lineName, blueColor, 노원역.getId(), 강남역.getId(), 10);
        ExtractableResponse<Response> response = postWithBody("/lines", duplicatedNameRequest);


        // then
        String bodyMessage = response.jsonPath().get("message");

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(bodyMessage).isEqualTo(ExceptionMessage.DUPLICATED_LINE_NAME.getContent());
    }

    @DisplayName("모든 노선을 조회한다.")
    @Test
    void getLines() {
        /// given
        Station 강남역 = stationDao.save(new Station("강남역"));
        Station 노원역 = stationDao.save(new Station("노원역"));

        LineRequest requestBody1 = new LineRequest("7호선", "bg-green-600", 강남역.getId(), 노원역.getId(), 10);
        ExtractableResponse<Response> createResponse1 = postWithBody("/lines", requestBody1);

        LineRequest requestBody2 = new LineRequest("5호선", "bg-red-600", 노원역.getId(), 강남역.getId(), 10);
        ExtractableResponse<Response> createResponse2 = postWithBody("/lines", requestBody2);

        // when
        ExtractableResponse<Response> response = get("/lines");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
                .map(this::getIdFromLocation)
                .collect(Collectors.toList());

        List<LineResponse> lineResponses = response.jsonPath().getList(".", LineResponse.class);

        List<Long> resultLineIds = lineResponses.stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());

        long validResponsesCount = lineResponses.stream()
                .filter(it -> it.getStations().size() == 2)
                .count();

        assertThat(resultLineIds).containsAll(expectedLineIds);
        assertThat(validResponsesCount).isEqualTo(2);
    }

    @DisplayName("id로 노선을 조회한다.")
    @Test
    void findById() {
        /// given
        String lineName = "7호선";
        String lineColor = "bg-green-600";

        Station 강남역 = stationDao.save(new Station("강남역"));
        Station 노원역 = stationDao.save(new Station("노원역"));

        LineRequest lineRequest = new LineRequest(lineName, lineColor, 강남역.getId(), 노원역.getId(), 5);

        ExtractableResponse<Response> createResponse = postWithBody("/lines", lineRequest);
        long id = getIdFromLocation(createResponse);

        // when
        ExtractableResponse<Response> response = get("/lines/" + id);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        LineResponse lineResponse = response.body().as(LineResponse.class);
        assertAll(() -> {
            assertThat(lineResponse.getId()).isEqualTo(id);
            assertThat(lineResponse.getName()).isEqualTo(lineName);
            assertThat(lineResponse.getColor()).isEqualTo(lineColor);
            assertThat(lineResponse.getStations()).hasSize(2);
        });
    }

    @Test
    @DisplayName("존재하지 않은 id로 조회하면 NOT_FOUND를 반환한다.")
    void findById_invalidId() {
        // given
        long notExistsId = 1;

        // when
        ExtractableResponse<Response> response = get("/lines/" + notExistsId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("지하철 노선 정보를 수정한다.")
    void updateLine() {
        // given
        Station 강남역 = stationDao.save(new Station("강남역"));
        Station 노원역 = stationDao.save(new Station("노원역"));

        LineRequest requestBody = new LineRequest("7호선", "bg-red-600", 강남역.getId(), 노원역.getId(), 10);
        ExtractableResponse<Response> response = postWithBody("/lines", requestBody);

        long id = getIdFromLocation(response);

        // when
        LineRequest updateBody = new LineRequest("5호선", "bg-green-600", null, null, 10);

        ExtractableResponse<Response> updateResponse = putWithBody("/lines/" + id, updateBody);

        // then
        assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("지하철 노선 정보를 삭제한다.")
    void deleteLine() {
        // given
        Station 강남역 = stationDao.save(new Station("강남역"));
        Station 노원역 = stationDao.save(new Station("노원역"));

        LineRequest requestBody = new LineRequest("7호선", "bg-red-600", 강남역.getId(), 노원역.getId(), 0);
        ExtractableResponse<Response> response = postWithBody("/lines", requestBody);

        long id = getIdFromLocation(response);

        // when
        ExtractableResponse<Response> deleteResponse = delete("/lines/" + id);

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
