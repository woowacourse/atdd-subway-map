package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

class StationServiceTest {

    private final StationDao stationDao = new MockStationDao();
    private final SectionDao sectionDao = new MockSectionDao();

    private final SectionService sectionService = new SectionService(sectionDao);
    private final StationService stationService = new StationService(stationDao, sectionDao, sectionService);

    @DisplayName("역을 저장한다")
    @Test
    void save() {
        // given
        String name = "name";
        StationRequest stationRequest = new StationRequest(name);

        // when
        StationResponse stationResponse = stationService.save(stationRequest);

        // then
        assertThat(stationResponse.getName()).isEqualTo(name);
    }

    @DisplayName("모든 역을 찾는다")
    @Test
    void findAll() {
        // given
        stationDao.save(new Station(1L, "name1"));
        stationDao.save(new Station(2L, "name2"));
        stationDao.save(new Station(3L, "name3"));

        // when
        List<StationResponse> results = stationService.findAll();

        // then
        assertThat(results).hasSize(3);
    }

    @DisplayName("역 id에 맞는 역을 제거한다")
    @Test
    void deleteById() {
        // given
        Long id1 = stationDao.save(new Station("name1"));
        Long id2 = stationDao.save(new Station("name2"));

        // when
        stationService.deleteById(id1);

        // then
        assertThat(stationService.findAll()).hasSize(1);
    }
}
