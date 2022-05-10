package wooteco.subway.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.section.SectionRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;

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

    @Test
    @DisplayName("해당 노선 구간에 있는 전체 지하철역 반환")
    void findStationsByLineId() {
        given(sectionDao.findAllByLineId(1L)).willReturn(List.of(
                new Section(1L, 1L, 1L, 2L, 5),
                new Section(2L, 1L, 2L, 3L, 5)
        ));

        given(stationDao.findById(1L)).willReturn(new Station(3L, "name"));
        given(stationDao.findById(2L)).willReturn(new Station(2L, "name"));
        given(stationDao.findById(3L)).willReturn(new Station(1L, "name"));

        List<Station> stations = sectionService.findStationsByLineId(1L);

        assertThat(stations.get(0).getId()).isEqualTo(3L);
        assertThat(stations.get(1).getId()).isEqualTo(2L);
        assertThat(stations.get(2).getId()).isEqualTo(1L);
    }
}
