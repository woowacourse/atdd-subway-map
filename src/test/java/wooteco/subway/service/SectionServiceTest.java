package wooteco.subway.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.Fixture;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.mockDao.MockLineDao;
import wooteco.subway.mockDao.MockSectionDao;
import wooteco.subway.mockDao.MockStationDao;
import wooteco.subway.repository.entity.LineEntity;

class SectionServiceTest {

    private final MockLineDao lineDao = new MockLineDao();
    private final MockStationDao stationDao = new MockStationDao();
    private final MockSectionDao sectionDao = new MockSectionDao();
    private final StationService stationService  = new StationService(stationDao);
    private final SectionService sectionService = new SectionService(sectionDao, stationService);
    private final LineService lineService = new LineService(lineDao, sectionService);

    @BeforeEach
    void initStore() {
        MockLineDao.removeAll();
        MockStationDao.removeAll();
        MockSectionDao.removeAll();
    }

    @DisplayName("노선 id, 상행역 id, 하행역 id, 길이를 입력받아 첫 구간을 등록한다.")
    @Test
    void resisterFirst() {
        final Long upStationId = Fixture.saveStation("선릉역");
        final Long downStationId = Fixture.saveStation("잠실역");
        final LineEntity line = lineDao.save(new LineEntity(null, "2호선", "bg-green-600"));

        final Section created = sectionService.resisterFirst(line.getId(), upStationId, downStationId, 10);

        assertAll(
                () -> assertThat(created.getUpStation().getId()).isEqualTo(upStationId),
                () -> assertThat(created.getDownStation().getId()).isEqualTo(downStationId),
                () -> assertThat(created.getDistance()).isEqualTo(10)
        );
    }

    @Test
    @DisplayName("노선 id로 구간목록을 불러온다.")
    void searchSectionsByLineId() {
        final Long upStationId = Fixture.saveStation("선릉역");
        final Long downStationId = Fixture.saveStation("잠실역");
        final Line line = lineService.register("2호선", "bg-green-600", upStationId, downStationId, 10);

        final Long newDownStationId = Fixture.saveStation("삼성역");
        sectionService.resister(line.getId(), upStationId, newDownStationId, 5);

        final Sections sections = sectionService.searchSectionsByLineId(line.getId());

        assertThat(sections.getValue()).isEqualTo(List.of(
                Section.createWithoutId(
                        stationService.searchById(upStationId),
                        stationService.searchById(newDownStationId),
                        5
                ),
                Section.createWithoutId(
                        stationService.searchById(newDownStationId),
                        stationService.searchById(downStationId),
                        5
                )
        ));
    }

    @DisplayName("노선 id, 상행역 id, 하행역 id, 길이를 입력받아 새 구간을 등록한다.")
    @Test
    void resister() {
        final Long upStationId = Fixture.saveStation("선릉역");
        final Long downStationId = Fixture.saveStation("잠실역");
        final Line line = lineService.register("2호선", "bg-green-600", upStationId, downStationId, 10);

        final Long newDownStationId = Fixture.saveStation("삼성역");
        sectionService.resister(line.getId(), upStationId, newDownStationId, 5);

        final Sections sections = sectionService.searchSectionsByLineId(line.getId());
        assertThat(sections.getValue().size()).isEqualTo(2);
    }

    @DisplayName("노선 id와 지하철역 id를 입력받아서 해당 노선에서 역을 제거한다. - 맨 뒤")
    @Test
    void removeStationBack() {
        final Long upStationId = Fixture.saveStation("선릉역");
        final Long downStationId = Fixture.saveStation("잠실역");
        final Line line = lineService.register("2호선", "bg-green-600", upStationId, downStationId, 10);
        final Long newDownStationId = Fixture.saveStation("삼성역");
        sectionService.resister(line.getId(), upStationId, newDownStationId, 5);

        sectionService.removeStation(line.getId(), downStationId);

        final Sections sections = sectionService.searchSectionsByLineId(line.getId());

        assertThat(sections.getValue()).isEqualTo(List.of(
                Section.createWithoutId(Station.createWithoutId("선릉역"), Station.createWithoutId("삼성역"), 5)
        ));
    }

    @DisplayName("노선 id와 지하철역 id를 입력받아서 해당 노선에서 역을 제거한다. - 맨 앞")
    @Test
    void removeStationFront() {
        final Long upStationId = Fixture.saveStation("선릉역");
        final Long downStationId = Fixture.saveStation("잠실역");
        final Line line = lineService.register("2호선", "bg-green-600", upStationId, downStationId, 10);
        final Long newDownStationId = Fixture.saveStation("삼성역");
        sectionService.resister(line.getId(), upStationId, newDownStationId, 5);

        sectionService.removeStation(line.getId(), upStationId);

        final Sections sections = sectionService.searchSectionsByLineId(line.getId());
        assertThat(sections.getValue()).isEqualTo(List.of(
                Section.createWithoutId(Station.createWithoutId("삼성역"), Station.createWithoutId("잠실역"), 5)
        ));
    }

    @DisplayName("노선 id와 지하철역 id를 입력받아서 해당 노선에서 역을 제거한다. - 사이")
    @Test
    void removeStation() {
        final Long upStationId = Fixture.saveStation("선릉역");
        final Long downStationId = Fixture.saveStation("잠실역");
        final Line line = lineService.register("2호선", "bg-green-600", upStationId, downStationId, 10);
        final Long newDownStationId = Fixture.saveStation("삼성역");
        sectionService.resister(line.getId(), upStationId, newDownStationId, 5);

        sectionService.removeStation(line.getId(), newDownStationId);

        final Sections sections = sectionService.searchSectionsByLineId(line.getId());
        assertThat(sections.getValue()).isEqualTo(List.of(
                Section.createWithoutId(Station.createWithoutId("선릉역"), Station.createWithoutId("잠실역"), 10)
        ));
    }

    @DisplayName("노선에 구간이 한개인 경우 삭제 요청시 예외가 발생한다.")
    @Test
    void removeStationOneSection() {
        final Long upStationId = Fixture.saveStation("선릉역");
        final Long downStationId = Fixture.saveStation("잠실역");
        final Line line = lineService.register("2호선", "bg-green-600", upStationId, downStationId, 10);

        assertThatThrownBy(() -> sectionService.removeStation(line.getId(), downStationId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("[ERROR] 구간이 한개일 경우엔 삭제할 수 없습니다.");

        final Sections sections = sectionService.searchSectionsByLineId(line.getId());
        assertThat(sections.getValue().size()).isEqualTo(1);
    }
}
