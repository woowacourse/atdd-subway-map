package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.dao.InmemorySectionDao;
import wooteco.subway.domain.Section;

class SectionServiceTest {

    private final InmemorySectionDao inmemorySectionDao = InmemorySectionDao.getInstance();
    private final SectionService sectionService = new SectionService(inmemorySectionDao);

    @AfterEach
    void afterEach() {
        inmemorySectionDao.clear();
    }

    @Test
    @DisplayName("Section을 저장할 때 이미 존재하는 section인 경우 예외 발생")
    void saveExceptionByExistSection() {
        Section section = new Section(null, 1L, 1L, 2L, 2);
        inmemorySectionDao.save(section);

        assertThatThrownBy(() -> sectionService.save(section))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 존재하는 Section입니다.");
    }
}
