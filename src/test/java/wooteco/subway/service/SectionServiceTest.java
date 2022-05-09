package wooteco.subway.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.dto.section.SectionRequest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class SectionServiceTest {

    StationDao stationDao;
    SectionDao sectionDao;
    SectionService sectionService;

    @BeforeEach
    public void setUp() {
        stationDao = Mockito.mock(StationDao.class);
        sectionDao = Mockito.mock(SectionDao.class);
        sectionService = new SectionService(sectionDao, stationDao);
    }

    @Test
    @DisplayName("구간 저장")
    void save() {
        assertDoesNotThrow(() -> sectionService.save(1L, new SectionRequest(1L, 2L, 5)));
    }
}
