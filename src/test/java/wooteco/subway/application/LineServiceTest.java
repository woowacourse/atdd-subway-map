package wooteco.subway.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.application.exception.DuplicateLineNameException;
import wooteco.subway.application.exception.NotFoundLineException;
import wooteco.subway.application.exception.NotFoundStationException;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;
import wooteco.subway.domain.exception.BlankArgumentException;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.repository.LineRepository;
import wooteco.subway.repository.StationRepository;

@SpringBootTest
@Transactional
public class LineServiceTest {

    @Autowired
    private LineService lineService;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineRepository lineRepository;

    private Station upStation;
    private Station downStation;

    @BeforeEach
    void setUp() {
        upStation = stationRepository.save(new Station("강남역"));
        downStation = stationRepository.save(new Station("역삼역"));
    }

    @DisplayName("지하철 노선 저장")
    @Test
    void saveLine() {
        LineRequest request = new LineRequest("신분당선", "bg-red-600",
            upStation.getId(), downStation.getId(), 10);

        Line line = lineService.save(request);

        LineResponse response = lineService.getById(line.getId());
        List<Long> actualIds = response.getStations().stream()
            .map(StationResponse::getId)
            .collect(Collectors.toList());

        assertThat(lineRepository.findById(response.getId())).isNotEmpty();
        assertThat(actualIds).containsExactlyInAnyOrder(upStation.getId(), downStation.getId());
    }

    @DisplayName("지하철 노선 빈 이름으로 저장")
    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "     "})
    void saveLineWithEmptyName(String name) {
        assertThatThrownBy(() -> lineService.save(
            new LineRequest(name, "bg-red-600", upStation.getId(), downStation.getId(), 10))
        ).isInstanceOf(BlankArgumentException.class);
    }

    @DisplayName("지하철 노선 빈 색깔로 저장")
    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "     "})
    void saveLineWithEmptyColor(String color) {
        assertThatThrownBy(() -> lineService.save(
            new LineRequest("신분당선", color, upStation.getId(), downStation.getId(), 10))
        ).isInstanceOf(BlankArgumentException.class);
    }

    @DisplayName("중복된 지하철노선 저장")
    @Test
    void saveByDuplicateName() {
        String name = "신분당선";
        String color = "bg-red-600";
        Station newStation = stationRepository.save(new Station("선릉역"));

        lineService.save(new LineRequest(name, color, upStation.getId(), downStation.getId(), 10));

        assertThatThrownBy(() -> lineService
            .save(new LineRequest(name, color, newStation.getId(), upStation.getId(), 10))
        ).isInstanceOf(DuplicateLineNameException.class);
    }

    @DisplayName("존재하지 않는 역으로 노선을 등록할 수 없다.")
    @Test
    void saveWithNotExistStation() {
        long notFoundStationId = Math.max(upStation.getId(), downStation.getId()) + 10;
        LineRequest request = new LineRequest("신분당선", "bg-red-600", 1L, notFoundStationId, 10);

        assertThatThrownBy(() -> lineService.save(request))
            .isInstanceOf(NotFoundStationException.class);
    }

    @DisplayName("존재하지 않는 지하철 노선 조회시 예외를 반환한다")
    @Test
    void showNotExistLine() {
        assertThatThrownBy(() -> lineService.getById(50L))
            .isInstanceOf(NotFoundLineException.class);
    }

    @DisplayName("지하철 노선 조회")
    @Test
    void queryLine() {
        Line line = lineService.save(
            new LineRequest("신분당선", "bg-red-600", upStation.getId(), downStation.getId(), 10));

        LineResponse expected = lineService.getById(line.getId());
        List<Long> expectedStationIds = expected.getStations().stream()
            .map(StationResponse::getId)
            .collect(Collectors.toList());

        assertThat(expected.getId()).isEqualTo(line.getId());
        assertThat(expected.getName()).isEqualTo("신분당선");
        assertThat(expected.getColor()).isEqualTo("bg-red-600");
        assertThat(expectedStationIds)
            .containsExactlyInAnyOrder(upStation.getId(), downStation.getId());
    }

    @Test
    void queryAll() {
        Station station3 = stationRepository.save(new Station("선릉역"));
        Line line1 = lineService.save(
            new LineRequest("신분당선", "bg-red-600", upStation.getId(), downStation.getId(), 10));
        Line line2 = lineService.save(
            new LineRequest("1호선", "bg-blue-600", upStation.getId(), station3.getId(), 10));

        List<Long> lineIds = lineService.getAll().stream()
            .map(LineResponse::getId)
            .collect(Collectors.toList());

        assertThat(lineIds).containsExactlyInAnyOrder(line1.getId(), line2.getId());
    }

    @DisplayName("지하철 노선 빈 이름으로 수정")
    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "     "})
    void updateLineWithEmptyName(String name) {
        LineRequest request = new LineRequest("신분당선", "bg-red-600",
            upStation.getId(), downStation.getId(), 10);

        Line line = lineService.save(request);

        assertThatThrownBy(
            () -> lineService.update(line.getId(), new LineRequest(name, "bg-red-600")))
            .isInstanceOf(BlankArgumentException.class);
    }

    @DisplayName("지하철 노선 빈 색깔로 수정")
    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "     "})
    void updateLineWithEmptyColor(String color) {
        LineRequest request = new LineRequest("신분당선", "bg-red-600",
            upStation.getId(), downStation.getId(), 10);

        Line line = lineService.save(request);

        assertThatThrownBy(
            () -> lineService.update(line.getId(), new LineRequest("신분당선", color)))
            .isInstanceOf(BlankArgumentException.class);
    }

    @DisplayName("지하철 노선의 정보를 수정한다.")
    @Test
    void updateLine() {
        LineRequest request = new LineRequest("신분당선", "bg-red-600",
            upStation.getId(), downStation.getId(), 10);

        Line line = lineService.save(request);

        lineService.update(line.getId(), new LineRequest("1호선", "bg-blue-600"));

        Line expectedLine = lineRepository.findById(line.getId()).orElseThrow();
        assertThat(expectedLine.getName()).isEqualTo("1호선");
        assertThat(expectedLine.getColor()).isEqualTo("bg-blue-600");
    }

    @DisplayName("존재하지 않는 지하철 노선을 수정한다.")
    @Test
    void updateNotExistLine() {
        assertThatThrownBy(() -> lineService.update(50L, new LineRequest("1호선", "bg-red-600")))
            .isInstanceOf(NotFoundLineException.class);
    }

    @DisplayName("존재하지 않는 지하철 노선을 삭제 시도시 예외 반환")
    @Test
    void deleteNotExistLine() {
        assertThatThrownBy(() -> lineService.deleteById(50L))
            .isInstanceOf(NotFoundLineException.class);
    }

    @DisplayName("지하철 노선을 삭제 시도")
    @Test
    void deleteLine() {
        Line line = lineService.save(
            new LineRequest("신분당선", "bg-red-600", upStation.getId(), downStation.getId(), 10));

        lineService.deleteById(line.getId());

        assertThat(lineRepository.findById(line.getId())).isEmpty();
    }
}
