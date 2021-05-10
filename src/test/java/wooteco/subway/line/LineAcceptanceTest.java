package wooteco.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.dto.request.LineCreateRequest;
import wooteco.subway.line.dto.request.LineUpdateRequest;
import wooteco.subway.line.dto.response.LineCreateResponse;
import wooteco.subway.line.dto.response.LineResponse;
import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.station.Station;
import wooteco.subway.station.dto.StationRequest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static wooteco.subway.line.LineRequestForm.*;

@DisplayName("노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @Autowired
    private LineDao lineDao;

    @Autowired
    private SectionDao sectionDao;

    @DisplayName("노선을 생성한다.")
    @Test
    void createLine() {
        // given
        StationRequest 강남역 = new StationRequest("강남역");
        StationRequest 잠실역 = new StationRequest("잠실역");
        LineCreateRequest 분당선_RED =
                new LineCreateRequest("분당선", "bg-red-600", 1L, 2L, 3);

        // when
        createRequest("/stations", 강남역);
        createRequest("/stations", 잠실역);
        ExtractableResponse<Response> response = createRequest("/lines", 분당선_RED);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
        assertThat(response.as(LineCreateResponse.class))
                .usingRecursiveComparison()
                .isEqualTo(new LineCreateResponse(1L, "분당선", "bg-red-600", 1L, 2L, 3));
    }

    @DisplayName("upStationId를 포함하지 않고 노선을 생성할 경우 BAD_REQUEST 반환")
    @Test
    void createLineNoUpStationId() {
        // given
        Map<String, Object> noUpStationId = new HashMap<>();
        noUpStationId.put("name", "분당선");
        noUpStationId.put("color", "bg-red-600");
        noUpStationId.put("downStationId", 1L);
        noUpStationId.put("distance", 3);

        // when
        ExtractableResponse<Response> noUpStationIdResponse = createRequest("/lines", noUpStationId);

        // then
        assertThat(noUpStationIdResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("downStationId를 포함하지 않고 노선을 생성할 경우 BAD_REQUEST 반환")
    @Test
    void createLineNoDownStationId() {
        // given
        Map<String, Object> noDownStationId = new HashMap<>();
        noDownStationId.put("name", "분당선");
        noDownStationId.put("color", "bg-red-600");
        noDownStationId.put("upStationId", 1L);
        noDownStationId.put("distance", 3);

        // when
        ExtractableResponse<Response> noDownStationIdResponse = createRequest("/lines", noDownStationId);

        // then
        assertThat(noDownStationIdResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("distance를 포함하지 않고 노선을 생성할 경우 BAD_REQUEST 반환")
    @Test
    void createLineNoDistance() {
        // given
        Map<String, Object> noDistance = new HashMap<>();
        noDistance.put("name", "분당선");
        noDistance.put("color", "bg-red-600");
        noDistance.put("upStationId", 1L);
        noDistance.put("downStationId", 2L);

        // when
        ExtractableResponse<Response> noDistanceResponse = createRequest("/lines", noDistance);

        // then
        assertThat(noDistanceResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("이미 존재하는 노선의 이름으로 생성 요청 시 BAD_REQUEST를 응답한다.")
    @Test
    void createLineWhenDuplicateLineName() {
        // given
        LineCreateRequest 분당선_RED =
                new LineCreateRequest("분당선", "bg-red-600", 1L, 2L, 3);

        // when
        createRequest("/lines", 분당선_RED);
        ExtractableResponse<Response> response = createRequest("/lines", 분당선_RED);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("초기 구간의 자하철 역 id가 같은 경우 BAD_REQUEST를 응답한다.")
    @Test
    void createLineWithDuplicateStation() {
        // given
        Station 잠실역 = new Station("잠실역");
        Station 왕십리역 = new Station("왕십리역");
        LineCreateRequest 분당선_RED =
                new LineCreateRequest("분당선", "bg-red-600", 1L, 1L, 3);

        // when
        createRequest("/stations", 잠실역);
        ExtractableResponse<Response> response = createRequest("/lines", 분당선_RED);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("초기 구간의 자하철 역 id가 존재하지 않는 역인 경우 BAD_REQUEST를 응답한다.")
    @Test
    void createLineWhenNoExistStation() {
        // given
        Station 잠실역 = new Station("잠실역");
        LineCreateRequest 분당선_RED =
                new LineCreateRequest("분당선", "bg-red-600", 1L, 3L, 3);

        // when
        createRequest("/stations", 잠실역);
        ExtractableResponse<Response> response = createRequest("/lines", 분당선_RED);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("모든 노선을 조회한다.")
    @Test
    void getLines() {
        // given
        Station 잠실역 = new Station("잠실역");
        Station 왕십리역 = new Station("왕십리역");
        LineCreateRequest 분당선_RED =
                new LineCreateRequest("분당선", "bg-red-600", 1L, 2L, 3);
        LineCreateRequest 신분당선_YELLOW =
                new LineCreateRequest("신분당선", "bg-yellow-600", 1L, 2L, 3);

        // when
        createRequest("/stations", 잠실역);
        createRequest("/stations", 왕십리역);
        ExtractableResponse<Response> 분당선생성 = createRequest("/lines", 분당선_RED);
        ExtractableResponse<Response> 신분당선생성 = createRequest("/lines", 신분당선_YELLOW);
        ExtractableResponse<Response> response = findAllRequest("/lines");
        List<Long> expectedLineIds = expectedLineIdsList(Arrays.asList(분당선생성, 신분당선생성));
        List<Long> resultLineIds = resultLineIdsList(response);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    private List<Long> expectedLineIdsList(List<ExtractableResponse<Response>> lines) {
        return Stream.of(lines.get(0), lines.get(1))
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
    }

    private List<Long> resultLineIdsList(ExtractableResponse<Response> response) {
        return response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
    }

    @DisplayName("노선 하나를 조회한다.")
    @Test
    void getLine() {
        // given
        Station 잠실역 = new Station("잠실역");
        Station 왕십리역 = new Station("왕십리역");
        LineCreateRequest 분당선_RED =
                new LineCreateRequest("분당선", "bg-red-600", 1L, 2L, 3);

        // when
        createRequest("/stations", 잠실역);
        createRequest("/stations", 왕십리역);
        ExtractableResponse<Response> 분당선생성 = createRequest("/lines", 분당선_RED);
        ExtractableResponse<Response> response = findByIdRequest("1");
        Long expectedLineId = Long.parseLong(분당선생성.header("Location").split("/")[2]);
        Long resultLineId = response.as(LineResponse.class).getId();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(resultLineId).isEqualTo(expectedLineId);
    }

    @DisplayName("노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        Station 잠실역 = new Station("잠실역");
        Station 왕십리역 = new Station("왕십리역");
        LineCreateRequest 분당선_RED =
                new LineCreateRequest("분당선", "bg-red-600", 1L, 2L, 3);
        LineUpdateRequest 신분당선_YELLOW =
                new LineUpdateRequest("신분당선", "bg-yellow-600");

        // when
        createRequest("/stations", 잠실역);
        createRequest("/stations", 왕십리역);
        createRequest("/lines", 분당선_RED);
        ExtractableResponse<Response> expectedResponse = updateRequest("1", 신분당선_YELLOW);
        ExtractableResponse<Response> updatedResponse = findByIdRequest("1");

        // then
        assertThat(expectedResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(updatedResponse.body().jsonPath().getString("name"))
                .isEqualTo("신분당선");
        assertThat(updatedResponse.body().jsonPath().getString("color"))
                .isEqualTo("bg-yellow-600");
    }

    @DisplayName("이미 존재하는 이름으로 수정 시 BAD_REQUEST를 응답한다.")
    @Test
    void updateLineWhenDuplicateName() {
        // given
        LineCreateRequest 분당선_RED =
                new LineCreateRequest("분당선", "bg-red-600", 1L, 2L, 3);
        LineCreateRequest 신분당선_YELLOW =
                new LineCreateRequest("신분당선", "bg-yellow-600", 1L, 2L, 3);

        // when
        createRequest("/lines", 분당선_RED);
        createRequest("/lines", 신분당선_YELLOW);
        ExtractableResponse<Response> response = updateRequest("1", new LineUpdateRequest("신분당선", "bg-yellow-600"));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        Station 잠실역 = new Station("잠실역");
        Station 왕십리역 = new Station("왕십리역");
        LineCreateRequest 분당선_RED =
                new LineCreateRequest("분당선", "bg-red-600", 1L, 2L, 3);
        int originalSize;

        // when
        createRequest("/stations", 잠실역);
        createRequest("/stations", 왕십리역);
        ExtractableResponse<Response> 분당선생성 = createRequest("/lines", 분당선_RED);
        String uri = 분당선생성.header("Location");
        originalSize = lineDao.findAll().size();
        ExtractableResponse<Response> response = deleteRequest(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(lineDao.findAll()).hasSize(originalSize - 1);
    }
}
