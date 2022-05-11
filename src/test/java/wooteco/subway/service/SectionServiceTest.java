package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Section;
import wooteco.subway.dto.SectionRequest;

@SpringBootTest
@Transactional
public class SectionServiceTest {

    @Autowired
    private SectionService sectionService;

    @DisplayName("Section 요청 정보를 받아 Section을 저장한다.")
    @Test
    void save() {
        SectionRequest sectionRequest = new SectionRequest(1L, 2L, 3);
        Section section = sectionService.save(1L, sectionRequest);
        assertAll(
                () -> assertThat(section.getUpStationId()).isEqualTo(1L),
                () -> assertThat(section.getDownStationId()).isEqualTo(2L),
                () -> assertTrue(section.getDistance() == 3)
        );
    }

    @DisplayName("노선의 Id로 Section을 조회한다.")
    @Test
    void findSectionsByLineId() {
        sectionService.save(1L, new SectionRequest(1L, 2L, 3));
        sectionService.save(1L, new SectionRequest(3L, 1L, 5));
        sectionService.save(1L, new SectionRequest(2L, 4L, 7));

        List<Section> sections = sectionService.getSectionsByLineId(1L);
        assertThat(sections).containsExactly(new Section(1L, 1L, 2L, 3), new Section(1L, 3L, 1L, 5), new Section(1L, 2L, 4L, 7));
    }
}

