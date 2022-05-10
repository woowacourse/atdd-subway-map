package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.dao.SectionMockDao;
import wooteco.subway.domain.Section;

public class SectionServiceTest {

    private final SectionMockDao sectionMockDao = new SectionMockDao();
    private final SectionService sectionService = new SectionService(sectionMockDao);

    @BeforeEach
    void setUp() {
        sectionMockDao.save(new Section(1L, 1L, 2L, 10));
    }

    @AfterEach
    void afterEach() {
        sectionMockDao.clear();
    }

    @DisplayName("상행종점 위에 있는 지하철 구간을 저장한다.")
    @Test
    void saveTopSection() {
        sectionService.save(new Section(1L, 3L, 1L, 4));

        assertThat(sectionService.findAll()).hasSize(2)
                .extracting(Section::getUpStationId, Section::getDownStationId, Section::getDistance)
                .contains(
                        tuple(3L, 1L, 4),
                        tuple(1L, 2L, 10)
                );
    }

    @DisplayName("하행종점 아래 있는 지하철 구간을 저장한다.")
    @Test
    void saveBottomSection() {
        sectionService.save(new Section(1L, 2L, 3L, 4));

        assertThat(sectionService.findAll()).hasSize(2)
                .extracting(Section::getUpStationId, Section::getDownStationId, Section::getDistance)
                .contains(
                        tuple(2L, 3L, 4),
                        tuple(1L, 2L, 10)
                );
    }

    @DisplayName("상행역이 일치하는 지하철 구간을 저장한다.")
    @Test
    void saveEqualsUpStation() {
        sectionService.save(new Section(1L, 1L, 3L, 4));

        assertThat(sectionService.findAll()).hasSize(2)
                .extracting(Section::getUpStationId, Section::getDownStationId, Section::getDistance)
                .contains(
                        tuple(1L, 3L, 4),
                        tuple(3L, 2L, 6)
                );
    }

    @DisplayName("하행역이 일치하는 지하철 구간을 저장한다.")
    @Test
    void saveEqualsDownStation() {
        sectionService.save(new Section(1L, 3L, 2L, 4));

        assertThat(sectionService.findAll()).hasSize(2)
                .extracting(Section::getUpStationId, Section::getDownStationId, Section::getDistance)
                .contains(
                        tuple(1L, 3L, 6),
                        tuple(3L, 2L, 4)
                );
    }
}
