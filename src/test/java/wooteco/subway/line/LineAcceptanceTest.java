package wooteco.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.line.repository.LineDao;
import wooteco.subway.station.dto.StationRequest;
import wooteco.subway.station.dto.StationResponse;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("노선 관련 기능")
@Sql("classpath:test.sql")
public class LineAcceptanceTest extends AcceptanceTest {
    private final long 강남역_id = 1L;
    private final long 잠실역_id = 2L;

    @Autowired
    private LineDao lineRepository;

    @BeforeEach
    void setUpStations() {
        StationRequest 강남역 = new StationRequest("강남역");
        StationRequest 잠실역 = new StationRequest("잠실역");

        post("/stations", 강남역);
        post("/stations", 잠실역);
    }

    @DisplayName("노선을 생성한다.")
    @Test
    void createLine() {
        // given
        LineRequest 분당선 =
                new LineRequest("분당선", "bg-red-600", 강남역_id, 잠실역_id, 0);

        // when
        ExtractableResponse<Response> response = post("/lines", 분당선);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("노선 생성 - 상행역과 하행역이 같은 경우 예외를 던진다.")
    @Test
    void createLineWhenSameStations() {
        // given
        LineRequest 분당선 =
                new LineRequest("분당선", "bg-red-600", 강남역_id, 강남역_id, 0);

        // when
        ExtractableResponse<Response> response = post("/lines", 분당선);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선 생성 - 등록되지 않은 역이 상행 혹은 하행역으로 등록 요청하는 경우 예외를 던진다.")
    @Test
    void createLineWhenNotExistStations() {
        // given
        LineRequest 분당선 =
                new LineRequest("분당선", "bg-red-600", 강남역_id, 3L, 0);
        LineRequest 신분당선 =
                new LineRequest("신분당선", "bg-red-600", 3L, 강남역_id, 0);
        LineRequest 이호선 =
                new LineRequest("이호선", "bg-red-600", 3L, 4L, 0);

        // when
        ExtractableResponse<Response> 분당선_생성_응답 = post("/lines", 분당선);
        ExtractableResponse<Response> 신부당선_생성_응답 = post("/lines", 신분당선);
        ExtractableResponse<Response> 이호선_생성_응답 = post("/lines", 이호선);

        // then
        assertThat(분당선_생성_응답.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(신부당선_생성_응답.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(이호선_생성_응답.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("값이 없는 경우 예외를 던진다.")
    void createLineWhenValueIsEmpty() {
        // given
        LineRequest 분당선 =
                new LineRequest("분당선", "bg-red-600", null, 2L, 0);
        LineRequest 신분당선 =
                new LineRequest("신분당선", "bg-red-600", 1L, null, 0);

        // when
        ExtractableResponse<Response> 분당선_생성_응답 = post("/lines", 분당선);
        ExtractableResponse<Response> 신분당선_생성_응답 = post("/lines", 신분당선);

        // then
        assertThat(분당선_생성_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(신분당선_생성_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("이미 존재하는 노선의 이름으로 생성 요청 시 BAD_REQUEST를 응답한다.")
    @Test
    void createLineWhenDuplicateLineName() {
        // given
        StationRequest 강남역 = new StationRequest("강남역");
        StationRequest 잠실역 = new StationRequest("잠실역");
        LineRequest 분당선 =
                new LineRequest("분당선", "bg-red-600", 1L, 2L, 0);

        // when
        post("/stations", 강남역);
        post("/stations", 잠실역);
        post("/lines", 분당선);
        ExtractableResponse<Response> response = post("/lines", 분당선);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("모든 노선을 조회한다.")
    @Test
    void getLines() {
        /// given
        LineRequest 분당선 =
                new LineRequest("분당선", "bg-red-600", 강남역_id, 잠실역_id, 0);

        LineRequest 신분당선 =
                new LineRequest("신분당선", "bg-red-600", 강남역_id, 잠실역_id, 0);

        // when
        ExtractableResponse<Response> 분당선_생성_응답 = post("/lines", 분당선);
        ExtractableResponse<Response> 신분당선_생성_응답 = post("/lines", 신분당선);

        ExtractableResponse<Response> 모든노선조회_응답 = get("/lines");

        // then
        assertThat(모든노선조회_응답.statusCode()).isEqualTo(HttpStatus.OK.value());

        List<Long> 생성시_응답된_노선ID_리스트 = Stream.of(분당선_생성_응답, 신분당선_생성_응답)
                .map(this::getIdFromResponse)
                .collect(Collectors.toList());
        List<Long> 조회시_응답된_노선ID_리스트 = 모든노선조회_응답.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());

        assertThat(조회시_응답된_노선ID_리스트).containsAll(생성시_응답된_노선ID_리스트);
    }

    @DisplayName("노선 하나를 조회한다.")
    @Test
    void getLine() {
        /// given
        LineRequest 분당선 =
                new LineRequest("분당선", "bg-red-600", 강남역_id, 잠실역_id, 0);

        // when
        ExtractableResponse<Response> 분당선_생성_응답 = post("/lines", 분당선);
        ExtractableResponse<Response> 노선_조회_응답 = get("/lines/1");

        // then
        assertThat(노선_조회_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
        Long expectedLineId = getIdFromResponse(분당선_생성_응답);
        Long resultLineId = 노선_조회_응답.as(LineResponse.class).getId();

        assertThat(resultLineId).isEqualTo(expectedLineId);
    }

    @DisplayName("노선 하나를 조회한다.")
    @Test
    void testGetLine() {
        /// given
        LineRequest 이호선 =
                new LineRequest("이호선", "bg-red-600", 강남역_id, 잠실역_id, 0);

        // when
        post("/lines", 이호선);
        ExtractableResponse<Response> 이호선_조회_응답 = get("/lines/1");

        // then
        assertThat(이호선_조회_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
        LineResponse 이호선_응답 = 이호선_조회_응답.as(LineResponse.class);

        assertThat(이호선_응답.getName()).isEqualTo("이호선");
        assertThat(이호선_응답.getColor()).isEqualTo("bg-red-600");
        assertThat(이호선_응답.getId()).isEqualTo(강남역_id);

        assertThat(이호선_응답.getStations()).usingRecursiveFieldByFieldElementComparator()
                .containsExactly(new StationResponse(강남역_id, "강남역"), new StationResponse(잠실역_id, "잠실역"));
    }

    @DisplayName("노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        LineRequest 이호선 =
                new LineRequest("이호선", "bg-blue-600", 강남역_id, 잠실역_id, 0);

        LineRequest 신분당선 =
                new LineRequest("신분당선", "bg-red-400", 강남역_id, 잠실역_id, 0);


        // when
        post("/lines", 이호선);
        ExtractableResponse<Response> 이호선_신분당선으로_수정 = putLine("/lines/1", 신분당선);
        ExtractableResponse<Response> 수정된_노선_응답 = get("/lines/1");

        // then
        assertThat(이호선_신분당선으로_수정.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(수정된_노선_응답.body().as(LineResponse.class).getName())
                .isEqualTo("신분당선");
        assertThat(수정된_노선_응답.body().as(LineResponse.class).getColor())
                .isEqualTo("bg-red-400");
    }

    @DisplayName("이미 존재하는 이름으로 수정 시 BAD_REQUEST를 응답한다.")
    @Test
    void updateLineWhenDuplicateName() {
        // given
        LineRequest 이호선 =
                new LineRequest("이호선", "bg-blue-600", 강남역_id, 잠실역_id, 0);

        LineRequest 신분당선 =
                new LineRequest("신분당선", "bg-red-600", 강남역_id, 잠실역_id, 0);


        // when
        post("/lines", 이호선);
        post("/lines", 신분당선);
        ExtractableResponse<Response> expectedResponse = putLine("/lines/1", 신분당선);

        // then
        assertThat(expectedResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        LineRequest 분당선 =
                new LineRequest("분당선", "bg-red-600", 강남역_id, 잠실역_id, 0);


        // when
        ExtractableResponse<Response> 분당선_생성_응답 = post("/lines", 분당선);
        int originalSize = lineRepository.findAll().size();
        String path = 분당선_생성_응답.header("Location");
        ExtractableResponse<Response> response = delete(path);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(lineRepository.findAll()).hasSize(originalSize - 1);
    }
}
