package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.dao.FakeSectionDao;
import wooteco.subway.dao.FakeStationDao;
import wooteco.subway.dto.SectionRequest;

public class SectionServiceTest {

    private SectionService sectionService;

    @BeforeEach
    void setUp() {
        FakeSectionDao.init();
        FakeStationDao.init();
        sectionService = new SectionService(new FakeSectionDao(), new FakeStationDao());
    }

    @DisplayName("Section 정보를 저장한다.")
    @Test()
    void save() {
        Long lineId = 1L;
        SectionRequest sectionRequest = new SectionRequest(1L, 3L, 3);

        assertThat(sectionService.save(lineId, sectionRequest)).isEqualTo(2);
    }

    @DisplayName("Section 정보를 삭제한다.")
    @Test()
    void delete() {
        Long lineId = 1L;
        Long stationId = 1L;
        SectionRequest sectionRequest = new SectionRequest(stationId, 3L, 3);

        sectionService.save(lineId, sectionRequest);

        assertThat(sectionService.delete(lineId, stationId)).isEqualTo(1);
    }
}
