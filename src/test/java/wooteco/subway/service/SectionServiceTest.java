package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    @DisplayName("지하철 구간의 상행역과 하행역이 모두 노선에 존재하지 않은 경우 예외가 발생한다.")
    @Test
    void saveNotExistStations() {
        sectionService.save(SECTION);

        assertThatThrownBy(() -> sectionService.save(new Section(1L, 3L, 4L, 1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역과 하행역이 모두 노선에 포함되어있지 않습니다.");
    }

    @DisplayName("지하철 구간의 상행역과 하행역이 이미 모두 노선에 포함되어 있는 경우 예외가 발생한다.")
    @Test
    void saveExistAllStations() {
        sectionService.save(SECTION);

        assertThatThrownBy(() -> sectionService.save(new Section(1L, 2L, 1L, 1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역과 하행역이 이미 모두 노선에 포함되어 있습니다.");
    }
}
