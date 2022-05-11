package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.Fixture;
import wooteco.subway.domain.Line;
import wooteco.subway.exception.DuplicateNameException;
import wooteco.subway.mockDao.MockLineDao;
import wooteco.subway.mockDao.MockSectionDao;
import wooteco.subway.mockDao.MockStationDao;

class LineServiceTest {

    private final MockLineDao lineDao = new MockLineDao();
    private final MockStationDao stationDao = new MockStationDao();
    private final MockSectionDao sectionDao = new MockSectionDao();
    private final StationService stationService = new StationService(stationDao);
    private final SectionService sectionService = new SectionService(sectionDao, stationService);
    private final LineService lineService = new LineService(lineDao, stationService, sectionService);


    @BeforeEach
    void initStore() {
        MockLineDao.removeAll();
        MockStationDao.removeAll();
        MockSectionDao.removeAll();
    }

    @DisplayName("노선 이름과 색깔, 초기 구간정보를 입력받아서 해당 이름과 색깔을 가진 노선을 등록한다.")
    @Test
    void register() {
        final Long station1Id = Fixture.saveStation("선릉역");
        final Long station2Id = Fixture.saveStation("잠실역");
        final Line created = lineService.register("2호선", "bg-green-600", station1Id, station2Id, 10);

        assertAll(
                () -> assertThat(created.getName()).isEqualTo("2호선"),
                () -> assertThat(created.getColor()).isEqualTo("bg-green-600")
        );
    }

    @DisplayName("이미 존재하는 노선이름으로 등록하려할 시 예외가 발생한다.")
    @Test
    void registerDuplicateName() {
        final Long station1Id = Fixture.saveStation("선릉역");
        final Long station2Id = Fixture.saveStation("잠실역");
        lineService.register("2호선", "bg-green-600", station1Id, station2Id, 10);

        assertThatThrownBy(() -> lineService.register("2호선", "bg-green-600", station1Id, station2Id, 10))
                .isInstanceOf(DuplicateNameException.class)
                .hasMessage("[ERROR] 이미 존재하는 노선 이름입니다.");
    }

    @DisplayName("등록된 모든 노선 리스트를 조회한다.")
    @Test
    void searchAll() {
        final Long station1Id = Fixture.saveStation("선릉역");
        final Long station2Id = Fixture.saveStation("잠실역");
        lineService.register("2호선", "bg-green-600", station1Id, station2Id, 10);
        lineService.register("신분당선", "bg-red-600", station1Id, station2Id, 10);
        lineService.register("분당선", "bg-yellow-600", station1Id, station2Id, 10);

        List<Line> lines = lineService.searchAll();
        List<String> names = lines.stream()
                .map(Line::getName)
                .collect(Collectors.toList());
        List<String> colors = lines.stream()
                .map(Line::getColor)
                .collect(Collectors.toList());

        assertAll(
                () -> assertThat(names.containsAll(List.of("2호선", "신분당선", "분당선"))).isTrue(),
                () -> assertThat(colors.containsAll(List.of("bg-green-600", "bg-red-600", "bg-yellow-600"))).isTrue()
        );
    }

    @DisplayName("id 로 노선을 조회한다.")
    @Test
    void searchById() {
        final Long station1Id = Fixture.saveStation("선릉역");
        final Long station2Id = Fixture.saveStation("잠실역");
        final Line savedLine = lineService.register("2호선", "bg-green-600", station1Id, station2Id, 10);

        Line searchedLine = lineService.searchById(savedLine.getId());

        assertAll(
                () -> assertThat(searchedLine.getName()).isEqualTo(savedLine.getName()),
                () -> assertThat(searchedLine.getColor()).isEqualTo(savedLine.getColor())
        );
    }

    @DisplayName("노선을 수정한다.")
    @Test
    void modify() {
        final Long station1Id = Fixture.saveStation("선릉역");
        final Long station2Id = Fixture.saveStation("잠실역");
        final Line savedLine = lineService.register("2호선", "bg-green-600", station1Id, station2Id, 10);

        lineService.modify(savedLine.getId(), "신분당선", "bg-red-600");
        Line searchedLine = lineService.searchById(savedLine.getId());

        assertAll(
                () -> assertThat(searchedLine.getName()).isEqualTo("신분당선"),
                () -> assertThat(searchedLine.getColor()).isEqualTo("bg-red-600")
        );
    }

    @DisplayName("존재하지 않는 노선을 수정한다.")
    @Test
    void modifyMissingLine() {
        final Long station1Id = Fixture.saveStation("선릉역");
        final Long station2Id = Fixture.saveStation("잠실역");
        final Line savedLine = lineService.register("2호선", "bg-green-600", station1Id, station2Id, 10);

        lineService.remove(savedLine.getId());
        assertThatThrownBy(() -> lineService.modify(savedLine.getId(), "신분당선", "bg-red-600"))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("[ERROR] 노선이 존재하지 않습니다");
    }

    @DisplayName("id 로 노선을 삭제한다.")
    @Test
    void removeById() {
        final Long station1Id = Fixture.saveStation("선릉역");
        final Long station2Id = Fixture.saveStation("잠실역");
        lineService.register("2호선", "bg-green-600", station1Id, station2Id, 10);
        Line line = lineService.register("신분당선", "bg-red-600", station1Id, station2Id, 10);

        lineService.remove(line.getId());

        assertThat(lineService.searchAll().size()).isEqualTo(1);
    }
}