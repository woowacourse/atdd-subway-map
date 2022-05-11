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

    @DisplayName("상행 종점 등록을 한다")
    @Test
    void create_first_terminal_station() {
        long savedSectionId = sectionService.createSection(1L, 1L, 2L, 3);
        long savedSectionId2 = sectionService.createSection(1L, 3L, 1L, 4);

        Section foundSection = sectionDao.findById(savedSectionId).get();
        Section foundSection2 = sectionDao.findById(savedSectionId2).get();

        assertThat(foundSection.getUpStation().getId()).isEqualTo(1L);
        assertThat(foundSection.getDownStation().getId()).isEqualTo(2L);
        assertThat(foundSection2.getUpStation().getId()).isEqualTo(3L);
        assertThat(foundSection2.getDownStation().getId()).isEqualTo(1L);
    }

    @DisplayName("하행 종점 등록을 한다")
    @Test
    void create_last_terminal_station() {
        long savedSectionId = sectionService.createSection(1L, 1L, 2L, 3);
        long savedSectionId2 = sectionService.createSection(1L, 2L, 3L, 4);

        Section foundSection = sectionDao.findById(savedSectionId).get();
        Section foundSection2 = sectionDao.findById(savedSectionId2).get();

        assertThat(foundSection.getUpStation().getId()).isEqualTo(1L);
        assertThat(foundSection.getDownStation().getId()).isEqualTo(2L);
        assertThat(foundSection2.getUpStation().getId()).isEqualTo(2L);
        assertThat(foundSection2.getDownStation().getId()).isEqualTo(3L);
    }
}
