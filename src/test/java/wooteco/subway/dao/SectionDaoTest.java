package wooteco.subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Section;

import static org.assertj.core.api.Assertions.assertThat;

class SectionDaoTest {

    private SectionDao sectionDao;

    @BeforeEach
    void setUp() {
        sectionDao = new MemorySectionDao();
    }

    @DisplayName("Section 을 등록할 수 있다")
    @Test
    void create_section() {
        // given
        long savedSectionId = sectionDao.save(new Section(1L, 1L, 2L, 3, 1L));

        // when
        Section foundSection = sectionDao.findById(savedSectionId).get();

        // then
        assertThat(foundSection.getLineId()).isEqualTo(1L);
        assertThat(foundSection.getUpStationId()).isEqualTo(1L);
        assertThat(foundSection.getDownStationId()).isEqualTo(2L);
        assertThat(foundSection.getDistance()).isEqualTo(3);
    }
}
