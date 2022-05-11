package wooteco.subway.service;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.SectionEntity;
import wooteco.subway.service.dto.SectionDto;

public class SectionServiceTest {

    private SectionDao sectionDao;
    private SectionService sectionService;

    @BeforeEach
    void setUp() {
        sectionDao = new FakeSectionDao();
        sectionService = new SectionService(sectionDao, new StationService(new FakeStationDao()));
    }

    @Test
    @DisplayName("구간을 추가한다.")
    void createSection() {
        //given
        long lineId = 1L;
        sectionService.createSection(new SectionDto(lineId, 1L, 2L, 10));

        //when
        List<SectionEntity> sectionEntities = sectionDao.findByLineId(lineId);

        //then
        assertThat(sectionEntities).contains(new SectionEntity(1L, lineId, 1L, 2L, 10));
    }
}
