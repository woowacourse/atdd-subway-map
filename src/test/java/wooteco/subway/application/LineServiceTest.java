package wooteco.subway.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.BlankArgumentException;
import wooteco.subway.exception.DuplicateException;
import wooteco.subway.exception.NotFoundException;
import wooteco.subway.repository.LineRepository;

@SpringBootTest
@Transactional
public class LineServiceTest {

    @Autowired
    private LineService lineService;

    @Autowired
    private StationService stationService;

    @Autowired
    private LineRepository lineRepository;

    @DisplayName("지하철 노선 저장")
    @Test
    void saveLine() {
        Station upStation = stationService.save("강남역");
        Station downStation = stationService.save("역삼역");
        LineRequest request = new LineRequest("신분당선", "bg-red-600",
            upStation.getId(), downStation.getId(), 10);

        LineResponse response = lineService.save(request);

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
        Station upStation = stationService.save("강남역");
        Station downStation = stationService.save("역삼역");

        assertThatThrownBy(() -> lineService.save(
            new LineRequest(name, "bg-red-600", upStation.getId(), downStation.getId(), 10))
        ).isInstanceOf(BlankArgumentException.class);
    }

    @DisplayName("지하철 노선 빈 색깔로 저장")
    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "     "})
    void saveLineWithEmptyColor(String color) {
        Station upStation = stationService.save("강남역");
        Station downStation = stationService.save("역삼역");

        assertThatThrownBy(() -> lineService.save(
            new LineRequest("신분당선", color, upStation.getId(), downStation.getId(), 10))
        ).isInstanceOf(BlankArgumentException.class);
    }

    @DisplayName("중복된 지하철노선 저장")
    @Test
    void saveByDuplicateName() {
        String name = "신분당선";
        String color = "bg-red-600";
        Station upStation = stationService.save("강남역");
        Station downStation = stationService.save("역삼역");
        Station newStation = stationService.save("선릉역");

        lineService.save(new LineRequest(name, color, upStation.getId(), downStation.getId(), 10));

        assertThatThrownBy(() -> lineService
            .save(new LineRequest(name, color, newStation.getId(), upStation.getId(), 10))
        ).isInstanceOf(DuplicateException.class);
    }

    @DisplayName("존재하지 않는 역으로 노선을 등록할 수 없다.")
    @Test
    void saveWithNotExistStation() {
        LineRequest request = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);

        assertThatThrownBy(() -> lineService.save(request))
            .isInstanceOf(NotFoundException.class);
    }

    @DisplayName("존재하지 않는 지하철 노선 조회시 예외를 반환한다")
    @Test
    void showNotExistLine() {
        assertThatThrownBy(() -> lineService.queryById(50L))
            .isInstanceOf(NotFoundException.class);
    }

    @DisplayName("지하철 노선 조회")
    @Test
    void queryLine() {
        Station upStation = stationService.save("강남역");
        Station downStation = stationService.save("역삼역");
        LineResponse response = lineService.save(
            new LineRequest("신분당선", "bg-red-600", upStation.getId(), downStation.getId(), 10));

        LineResponse expected = lineService.queryById(response.getId());
        List<Long> expectedStationIds = expected.getStations().stream()
            .map(StationResponse::getId)
            .collect(Collectors.toList());

        assertThat(expected.getId()).isEqualTo(response.getId());
        assertThat(expected.getName()).isEqualTo("신분당선");
        assertThat(expected.getColor()).isEqualTo("bg-red-600");
        assertThat(expectedStationIds)
            .containsExactlyInAnyOrder(upStation.getId(), downStation.getId());
    }

    @Test
    void queryAll() {
        Station station1 = stationService.save("강남역");
        Station station2 = stationService.save("역삼역");
        Station station3 = stationService.save("선릉역");
        LineResponse response1 = lineService.save(
            new LineRequest("신분당선", "bg-red-600", station1.getId(), station2.getId(), 10));
        LineResponse response2 = lineService.save(
            new LineRequest("1호선", "bg-blue-600", station1.getId(), station3.getId(), 10));

        List<LineResponse> lineResponses = lineService.queryAll();

        assertThat(lineResponses).containsExactlyInAnyOrder(response1, response2);
    }

    @DisplayName("지하철 노선 빈 이름으로 수정")
    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "     "})
    void updateLineWithEmptyName(String name) {
        Station upStation = stationService.save("강남역");
        Station downStation = stationService.save("역삼역");
        LineRequest request = new LineRequest("신분당선", "bg-red-600",
            upStation.getId(), downStation.getId(), 10);

        LineResponse response = lineService.save(request);

        assertThatThrownBy(
            () -> lineService.update(response.getId(), new LineRequest(name, "bg-red-600")))
            .isInstanceOf(BlankArgumentException.class);
    }

    @DisplayName("지하철 노선 빈 색깔로 수정")
    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "     "})
    void updateLineWithEmptyColor(String color) {
        Station upStation = stationService.save("강남역");
        Station downStation = stationService.save("역삼역");
        LineRequest request = new LineRequest("신분당선", "bg-red-600",
            upStation.getId(), downStation.getId(), 10);

        LineResponse response = lineService.save(request);

        assertThatThrownBy(
            () -> lineService.update(response.getId(), new LineRequest("신분당선", color)))
            .isInstanceOf(BlankArgumentException.class);
    }

    @DisplayName("지하철 노선의 정보를 수정한다.")
    @Test
    void updateLine() {
        Station upStation = stationService.save("강남역");
        Station downStation = stationService.save("역삼역");
        LineRequest request = new LineRequest("신분당선", "bg-red-600",
            upStation.getId(), downStation.getId(), 10);

        LineResponse response = lineService.save(request);

        lineService.update(response.getId(), new LineRequest("1호선", "bg-blue-600"));

        Line expectedLine = lineRepository.findById(response.getId()).orElseThrow();
        assertThat(expectedLine.getName()).isEqualTo("1호선");
        assertThat(expectedLine.getColor()).isEqualTo("bg-blue-600");
    }

    @DisplayName("존재하지 않는 지하철 노선을 수정한다.")
    @Test
    void updateNotExistLine() {
        assertThatThrownBy(() -> lineService.update(50L, new LineRequest("1호선", "bg-red-600")))
            .isInstanceOf(NotFoundException.class);
    }

    @DisplayName("존재하지 않는 지하철 노선을 삭제 시도시 예외 반환")
    @Test
    void deleteNotExistLine() {
        assertThatThrownBy(() -> lineService.deleteById(50L))
            .isInstanceOf(NotFoundException.class);
    }

    @Disabled
    @DisplayName("지하철 노선을 삭제 시도")
    @Test
    void deleteLine() {
        Station upStation = stationService.save("강남역");
        Station downStation = stationService.save("역삼역");
        LineResponse response = lineService.save(
            new LineRequest("신분당선", "bg-red-600", upStation.getId(), downStation.getId(), 10));

        lineService.deleteById(response.getId());

        assertThat(lineRepository.findById(response.getId())).isEmpty();
    }
}
