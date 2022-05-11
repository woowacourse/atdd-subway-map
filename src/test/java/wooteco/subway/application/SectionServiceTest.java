package wooteco.subway.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.dao.MemorySectionDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

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
        // given
        long savedSectionId = sectionService.createSection(1L, 1L, 2L, 3);
        long savedSectionId2 = sectionService.createSection(1L, 3L, 1L, 4);

        // when
        Section foundSection = sectionDao.findById(savedSectionId).get();
        Section foundSection2 = sectionDao.findById(savedSectionId2).get();

        // then
        assertThat(foundSection.getUpStation().getId()).isEqualTo(1L);
        assertThat(foundSection.getDownStation().getId()).isEqualTo(2L);
        assertThat(foundSection2.getUpStation().getId()).isEqualTo(3L);
        assertThat(foundSection2.getDownStation().getId()).isEqualTo(1L);
    }

    @DisplayName("하행 종점 등록을 한다")
    @Test
    void create_last_terminal_station() {
        // given
        long savedSectionId = sectionService.createSection(1L, 1L, 2L, 3);
        long savedSectionId2 = sectionService.createSection(1L, 2L, 3L, 4);

        // when
        Section foundSection = sectionDao.findById(savedSectionId).get();
        Section foundSection2 = sectionDao.findById(savedSectionId2).get();

        // then
        assertThat(foundSection.getUpStation().getId()).isEqualTo(1L);
        assertThat(foundSection.getDownStation().getId()).isEqualTo(2L);
        assertThat(foundSection2.getUpStation().getId()).isEqualTo(2L);
        assertThat(foundSection2.getDownStation().getId()).isEqualTo(3L);
    }
    
    @DisplayName("[갈래길 방지] 상행역이 같을때 변경된 구간을 검증한다")
    @Test
    void prevent_forked_road_same_up_station() {
        // given
        long oldSectionId = sectionService.createSection(1L, 1L, 2L, 7);
        long newSectionId = sectionService.createSection(1L, 1L, 3L, 4);

        Section oldSection = sectionDao.findById(oldSectionId).get();
        Section newSection = sectionDao.findById(newSectionId).get();

        assertThat(oldSection.getUpStation().getId()).isEqualTo(3L);
        assertThat(oldSection.getDownStation().getId()).isEqualTo(2L);
        assertThat(oldSection.getDistance()).isEqualTo(3);

        assertThat(newSection.getUpStation().getId()).isEqualTo(1L);
        assertThat(newSection.getDownStation().getId()).isEqualTo(3L);
        assertThat(newSection.getDistance()).isEqualTo(4);
    }

    @DisplayName("[갈래길 방지] 하행역이 같을때 변경된 구간을 검증한다")
    @Test
    void prevent_forked_road_same_down_station() {
        // given
        long oldSectionId = sectionService.createSection(1L, 1L, 2L, 7);
        long newSectionId = sectionService.createSection(1L, 3L, 2L, 4);

        Section oldSection = sectionDao.findById(oldSectionId).get();
        Section newSection = sectionDao.findById(newSectionId).get();

        assertAll(
                () -> assertThat(oldSection.getUpStation().getId()).isEqualTo(1L),
                () -> assertThat(oldSection.getDownStation().getId()).isEqualTo(3L),
                () -> assertThat(oldSection.getDistance()).isEqualTo(3),

                () -> assertThat(newSection.getUpStation().getId()).isEqualTo(3L),
                () -> assertThat(newSection.getDownStation().getId()).isEqualTo(2L),
                () -> assertThat(newSection.getDistance()).isEqualTo(4)
        );

    }
}
