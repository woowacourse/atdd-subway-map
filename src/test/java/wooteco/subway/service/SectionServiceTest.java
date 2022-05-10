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
import wooteco.subway.mockDao.MockLineDao;
import wooteco.subway.mockDao.MockSectionDao;
import wooteco.subway.mockDao.MockStationDao;
import wooteco.subway.repository.entity.LineEntity;
import wooteco.subway.repository.entity.SectionEntity;

class SectionServiceTest {

    private final MockLineDao lineDao = new MockLineDao();
    private final MockSectionDao sectionDao = new MockSectionDao();
    private final MockStationDao stationDao = new MockStationDao();
    private final SectionService service = new SectionService(sectionDao, stationDao);

    @BeforeEach
    void initStore() {
        MockLineDao.removeAll();
        MockStationDao.removeAll();
        MockSectionDao.removeAll();
    }

    @DisplayName("노선 id, 상행역 id, 하행역 id, 길이를 입력받아 구간을 등록한다.")
    @Test
    void resister() {
        final Long upStationId = Fixture.saveStation("선릉역");
        final Long downStationId = Fixture.saveStation("잠실역");
        final LineEntity line = lineDao.save(new LineEntity(Line.createWithoutId("2호선", "bg-green-600")));

        final Section created = service.resister(line.getId(), upStationId, downStationId, 10);

        assertAll(
                () -> assertThat(created.getUpStation().getId()).isEqualTo(upStationId),
                () -> assertThat(created.getDownStation().getId()).isEqualTo(downStationId),
                () -> assertThat(created.getDistance()).isEqualTo(10)
        );
    }
}
