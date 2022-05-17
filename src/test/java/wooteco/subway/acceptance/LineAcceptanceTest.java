package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import wooteco.subway.exception.ExceptionMessage;
import wooteco.subway.repository.dao.StationDao;
import wooteco.subway.repository.entity.StationEntity;
import wooteco.subway.service.dto.LineRequest;
import wooteco.subway.service.dto.LineResponse;
import wooteco.subway.service.dto.StationResponse;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private static final String LOCATION = "Location";

    @Autowired
    private StationDao stationDao;

    private StationEntity 강남역;
    private StationEntity 노원역;

    @Override
    @BeforeEach
    void setUp() {
        super.setUp();
        강남역 = stationDao.save(new StationEntity(null, "강남역"));
        노원역 = stationDao.save(new StationEntity(null, "노원역"));
    }

    @Test
    @DisplayName("지하철 노선을 생성한다.")
    void createLine() {
        // given
        String lineName = "7호선";
        String lineColor = "bg-red-600";

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
    @DisplayName("잘못된 값 입력해서 노선 생성 시도")
    void createLine_invalid() {
        // given
        LineRequest lineRequest = new LineRequest(null, null, null, null, null);

        // when
        ExtractableResponse<Response> response = postWithBody("/lines", lineRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("이미 존재하는 이름의 호선을 생성하려고 하면 BAD REQUEST를 반환한다.")
    void createLine_duplicatedName() {
        // given
        String lineName = "7호선";
        String redColor = "bg-red-600";
        String blueColor = "bg-blue-600";

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

        LineRequest requestBody = new LineRequest("7호선", "bg-red-600", 강남역.getId(), 노원역.getId(), 10);
        ExtractableResponse<Response> response = postWithBody("/lines", requestBody);

        long id = getIdFromLocation(response);

        // when
        LineRequest updateBody = new LineRequest("5호선", "bg-green-600", 노원역.getId(), 강남역.getId(), 10);

        ExtractableResponse<Response> updateResponse = putWithBody("/lines/" + id, updateBody);

        // then
        assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("지하철 노선 정보를 삭제한다.")
    void deleteLine() {
        // given

        LineRequest requestBody = new LineRequest("7호선", "bg-red-600", 강남역.getId(), 노원역.getId(), 0);
        ExtractableResponse<Response> response = postWithBody("/lines", requestBody);

        long id = getIdFromLocation(response);

        // when
        ExtractableResponse<Response> deleteResponse = delete("/lines/" + id);

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
