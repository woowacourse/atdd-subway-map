package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wooteco.subway.domain.Station;

class StationDaoTest {

    @Autowired
    private StationDao stationDao;

    @DisplayName("역 저장")
    @Test
    void save() {
        // given
        Station station = new Station("정자역");

        // when
        Long id = stationDao.save(station);

        // then
        assertThat(id).isEqualTo(2L);
    }

    @DisplayName("역 이름으로 개수 검색")
    @Test
    void countByName() {
        // given

        // when
        int count = stationDao.countByName("강남역");

        // then
        assertThat(count).isEqualTo(1);
    }

    @DisplayName("역 전체 조회")
    @Test
    void findAll() {
        // given

        // when
        List<Station> stations = stationDao.findAll();

        // then
        assertThat(stations.size()).isEqualTo(1);
    }

    @DisplayName("역 삭제")
    @Test
    void deleteById() {
        // given
        Long id = 1L;
        List<Station> before = stationDao.findAll();

        // when
        stationDao.deleteById(id);
        List<Station> after = stationDao.findAll();

        // then
        assertThat(before.size() - 1).isEqualTo(after.size());
    }
}