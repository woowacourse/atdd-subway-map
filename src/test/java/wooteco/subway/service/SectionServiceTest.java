package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.dao.FakeSectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;

public class SectionServiceTest {

    private SectionService sectionService;
    private Section section;
    private Sections sections;

    @BeforeEach
    void setUp() {
        sectionService = new SectionService(new FakeSectionDao());
        section = sectionService.save(new Section(1L, 1L, 2L, 10));
    }

    @DisplayName("새로운 구간을 추가한다.")
    @Test
    void save() {
        Section newSection = new Section(1L, 2L, 3L, 5);

        assertThat(sectionService.save(newSection).getUpStationId()).isEqualTo(2L);
    }

    @DisplayName("lineId에 해당되는 모든 구간들을 반환한다.")
    @Test
    void findByLineId() {
        Section newSection = new Section(1L, 2L, 3L, 5);
        sectionService.save(newSection);

        Sections sections = new Sections(sectionService.findByLineId(1L), 1L);

        assertThat(sections.getSections()).hasSize(2);
    }

    @DisplayName("lineId에 해당되는 모든 구간들을 반환한다.")
    @Test
    void update() {
        Section newSection = new Section(1L, 2L, 3L, 5);
        sectionService.save(newSection);

        Sections sections = new Sections(sectionService.findByLineId(1L), 1L);

        assertThat(sections.getSections()).hasSize(2);
    }

}
