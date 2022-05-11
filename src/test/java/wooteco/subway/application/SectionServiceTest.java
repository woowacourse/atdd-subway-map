package wooteco.subway.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.dao.MemorySectionDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;

import static org.assertj.core.api.Assertions.assertThat;

class SectionServiceTest {

    private SectionDao sectionDao;
    private SectionService sectionService;

    @BeforeEach
    void setUp() {
        sectionDao = new MemorySectionDao();
        sectionService = new SectionService(sectionDao);
    }

    @DisplayName("Section 을 등록할 수 있다")
    @Test
    void create_section() {
        // given
        long savedSectionId = sectionService.createSection(1L, 1L, 2L, 3);

        // when
        Section foundSection = sectionDao.findById(savedSectionId).get();

        // then
        assertThat(foundSection.getLine().getId()).isEqualTo(1L);
        assertThat(foundSection.getUpStation().getId()).isEqualTo(1L);
        assertThat(foundSection.getDownStation().getId()).isEqualTo(2L);
        assertThat(foundSection.getDistance()).isEqualTo(3);
    }

/*
    @DisplayName("구간을 등록할 수 상행 종점 등록")
    @Test
    void test_create_section() {
        long lineId = 1L;
        long upStationId = 1L;
        long downStationId = 2L;
        int distance = 3;
        long savedSectionId = sectionService.createSection(lineId, upStationId, downStationId, distance);
    }
*/
}
