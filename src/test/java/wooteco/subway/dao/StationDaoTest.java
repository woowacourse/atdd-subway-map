package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Station;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class StationDaoTest {

    @Autowired
    private StationDao stationDao;

    @DisplayName("역 저장")
    @Test
    void save() {
        // given
        Station station = new Station("정자역");
        Long expected = stationDao.findAll().size() + 1L;

        // when
        Long id = stationDao.save(station);

        // then
        assertThat(id).isEqualTo(expected);
    }

    @DisplayName("역 이름 존재하는지 확인")
    @Test
    void countByName() {
        // given

        // when
        boolean result = stationDao.existsByName("강남역");

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("역 id 존재하는지 확인")
    @Test
    void countById() {
        // given

        // when
        boolean result = stationDao.existsById(1L);

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("역 전체 조회")
    @Test
    void findAll() {
        // given

        // when
        List<Station> stations = stationDao.findAll();

        // then
        assertThat(stations.size()).isEqualTo(2);
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