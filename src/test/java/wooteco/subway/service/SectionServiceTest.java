package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.dao.SectionMockDao;
import wooteco.subway.domain.Section;

public class SectionServiceTest {

    public static final Section SECTION = new Section(1L, 1L, 2L, 1);

    private final SectionMockDao sectionMockDao = new SectionMockDao();
    private final SectionService sectionService = new SectionService(sectionMockDao);

    @BeforeEach
    void setUp() {
        sectionMockDao.clear();
    }

    @DisplayName("지하철 구간을 생성한다.")
    @Test
    void save() {
        sectionService.save(SECTION);

        assertThat(sectionService.findAll()).hasSize(1);
    }
}
