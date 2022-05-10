package wooteco.subway.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.Fixture;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.mockDao.MockLineDao;
import wooteco.subway.mockDao.MockSectionDao;
import wooteco.subway.mockDao.MockStationDao;
import wooteco.subway.repository.entity.LineEntity;
import wooteco.subway.repository.entity.SectionEntity;

class SectionServiceTest {

    private final MockLineDao lineDao = new MockLineDao();
    private final MockStationDao stationDao = new MockStationDao();
    private final MockSectionDao sectionDao = new MockSectionDao();
    private final LineService lineService = new LineService(lineDao, stationDao, sectionDao);
    private final StationService stationService  = new StationService(stationDao);
    private final SectionService service = new SectionService(sectionDao, stationService);

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
        final Line line = lineService.register("2호선", "bg-green-600", upStationId, downStationId, 10);

        final Section created = service.resisterFirst(line.getId(), upStationId, downStationId, 10);

        assertAll(
                () -> assertThat(created.getUpStation().getId()).isEqualTo(upStationId),
                () -> assertThat(created.getDownStation().getId()).isEqualTo(downStationId),
                () -> assertThat(created.getDistance()).isEqualTo(10)
        );
    }

    @DisplayName("노선 id, 상행역 id, 하행역 id, 길이를 입력받아 새 구간을 등록한다.")
    @Test
    void resister() {
        final Long upStationId = Fixture.saveStation("선릉역");
        final Long downStationId = Fixture.saveStation("잠실역");
        final Line line = lineService.register("2호선", "bg-green-600", upStationId, downStationId, 10);
        service.resisterFirst(line.getId(), upStationId, downStationId, 10);

        final Long newDownStationId = Fixture.saveStation("삼성역");
        service.resister(line.getId(), upStationId, newDownStationId, 5);

        final Sections sections = service.findSectionsByLineId(line.getId());
        assertThat(sections.getValue().size()).isEqualTo(3);
    }
}
