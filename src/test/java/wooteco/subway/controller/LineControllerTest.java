package wooteco.subway.controller;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static wooteco.subway.controller.AcceptanceFixture.*;

@DisplayName("지하철 노선 관련 기능")
class LineControllerTest extends AcceptanceTest {

    private static final String URL = "/lines";

    @Autowired
    private LineDao lineDao;
    @Autowired
    private SectionDao sectionDao;
    @Autowired
    private StationDao stationDao;

    @DisplayName("등록 - `POST /lines`")
    @ParameterizedTest
    @MethodSource("params")
    void createLine(String name, String color, Long upStationId, Long downStationId,
                    int distance, int httpStatusCode) {
        stationDao.save(new Station("동천역"));
        stationDao.save(new Station("판교역"));
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        ExtractableResponse<Response> response = post(params, URL);

        assertThat(response.statusCode()).isEqualTo(httpStatusCode);
    }

    private static Stream<Arguments> params() {
        return Stream.of(
                Arguments.of("신분당선", "red", 1L, 2L, 1, 201),
                Arguments.of("신분당선", " ", 1L, 2L, 1, 400),
                Arguments.of(" ", "red", 1L, 2L, 1, 400),
                Arguments.of("신분당선", "red", 2L, 3L, 1, 404),
                Arguments.of("신분당선", "red", 1L, 1L, 1, 400),
                Arguments.of("신분당선", "red", 1L, 2L, 0, 400)
        );
    }

    @DisplayName("목록 - `GET /lines`")
    @Test
    void getLines() {
        Station upStation = stationDao.save(new Station("동천역"));
        Station downStation = stationDao.save(new Station("판교역"));
        Line line = lineDao.save(new Line("신분당선", "red"));
        sectionDao.save(new Section(upStation.getId(), downStation.getId(), line.getId(), 5));

        ExtractableResponse<Response> response = get(URL);
        List<LineResponse> actual = response.jsonPath().getList(".", LineResponse.class);

        assertThat(actual.size()).isEqualTo(1);
    }

    @DisplayName("조회 - `GET /lines/{id}`")
    @ParameterizedTest
    @CsvSource({"1,200", "2,404"})
    void getLine(Long pathParameter, int httpStatusCode) {
        Station upStation = stationDao.save(new Station("동천역"));
        Station downStation = stationDao.save(new Station("판교역"));
        Line line = lineDao.save(new Line("신분당선", "red"));
        sectionDao.save(new Section(upStation.getId(), downStation.getId(), line.getId(), 5));

        ExtractableResponse<Response> response = get(URL + "/" + pathParameter);

        assertThat(response.statusCode()).isEqualTo(httpStatusCode);
    }

    @DisplayName("수정 - `PUT /lines/{id}`")
    @Nested
    class Put {

        @DisplayName("성공")
        @Test
        void updateLine() {
            Line line = lineDao.save(new Line("신분당선", "red"));

            ExtractableResponse<Response> response = put(new Line("다른분당선", "blue"),
                    URL + "/" + line.getId());

            Line updatedLine = lineDao.findById(line.getId()).get();
            assertAll(
                    () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                    () -> assertThat(updatedLine.getName()).isEqualTo("다른분당선"),
                    () -> assertThat(updatedLine.getColor()).isEqualTo("blue")
            );
        }

        @DisplayName("노선에서 수정하려는 이름을 가진 노선이 존재한다면 400 상태코드로 응답한다.")
        @Test
        void updateLineResponse400() {
            lineDao.save(new Line("다른분당선", "blue"));
            Line line = lineDao.save(new Line("신분당선", "red"));

            ExtractableResponse<Response> response = put(new Line("다른분당선", "blue"),
                    URL + "/" + line.getId());

            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }

        @DisplayName("지하철 노선을 수정할 때 id에 맞는 노선이 없으면 404 상태코드로 응답한다.")
        @Test
        void updateLineResponse404() {
            ExtractableResponse<Response> response = put(new Line("다른분당선", "blue"), URL + 1);

            assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        }
    }

    @DisplayName("삭제 - `DELETE /lines/{id}`")
    @Nested
    class Delete {

        @DisplayName("성공")
        @Test
        void deleteLine() {
            Line line = lineDao.save(new Line("신분당선", "red"));

            ExtractableResponse<Response> response = delete(URL + "/" + line.getId());

            assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        }

        @DisplayName("지하철 노선을 삭제할 때 id에 맞는 노선이 없으면 404 상태코드로 응답한다.")
        @Test
        void deleteLineResponse404() {
            ExtractableResponse<Response> response = delete(URL + "/" + 1);

            assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        }
    }
}
