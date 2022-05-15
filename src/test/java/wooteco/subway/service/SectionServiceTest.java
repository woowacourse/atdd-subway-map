package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.dto.SectionRequest;

class SectionServiceTest {

    private final SectionDao sectionDao = new MockSectionDao();

    private final SectionService sectionService = new SectionService(sectionDao);

    @DisplayName("구간을 추가한다")
    @Test
    void addSection() {
        // given
        Long lineId = 1L;
        sectionDao.save(new Section(lineId, 1L, 2L, 10));
        sectionDao.save(new Section(lineId, 2L, 3L, 10));
        SectionRequest request = new SectionRequest(1L, 4L, 5);

        // when
        sectionService.addSection(lineId, request);

        // then
        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        List<Long> sortedStationId = sections.getSortedStationId();
        assertAll(
                () -> assertThat(sortedStationId).hasSize(4),
                () -> assertThat(sortedStationId.get(0)).isEqualTo(1L),
                () -> assertThat(sortedStationId.get(1)).isEqualTo(4L),
                () -> assertThat(sortedStationId.get(2)).isEqualTo(2L),
                () -> assertThat(sortedStationId.get(3)).isEqualTo(3L)
        );
    }

    @DisplayName("구간을 제거하다")
    @Test
    void deleteStation() {
        // given
        Long lineId = 1L;
        sectionDao.save(new Section(lineId, 1L, 2L, 10));
        sectionDao.save(new Section(lineId, 2L, 3L, 10));

        // when
        sectionService.deleteStation(lineId, 2L);

        // then
        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        List<Long> sortedStationId = sections.getSortedStationId();
        assertAll(
                () -> assertThat(sortedStationId).hasSize(2),
                () -> assertThat(sortedStationId.get(0)).isEqualTo(1L),
                () -> assertThat(sortedStationId.get(1)).isEqualTo(3L)
        );
    }
}
