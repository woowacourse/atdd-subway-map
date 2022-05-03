package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Station;

class StationDaoTest {

    @BeforeEach
    void setUp() {
        StationDao.deleteAll();
    }

    @DisplayName("새로운 지하철 역을 저장한다")
    @Test
    void saveStation() {
        // given
        Station station = new Station(1L, "강남역");

        // when
        StationDao.save(station);

        // then
        List<Station> stations = StationDao.findAll();
        assertThat(stations.get(0)).isEqualTo(station);
    }

    @DisplayName("같은 지하철 역 이름이 있는 경우 예외를 발생시킨다")
    @Test
    void saveStationThrowException() {
        String name = "강남역";
        Station station1 = new Station(1L, name);
        Station station2 = new Station(2L, name);
        StationDao.save(station1);

        assertThatThrownBy(() -> StationDao.save(station2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("같은 이름");
    }

    @DisplayName("역 목록을 조회한다")
    @Test
    void findAll() {
        // given
        Station station1 = new Station(1L, "name1");
        Station station2 = new Station(2L, "name2");
        Station station3 = new Station(3L, "name3");
        StationDao.save(station1);
        StationDao.save(station2);
        StationDao.save(station3);

        // when
        List<Station> stations = StationDao.findAll();

        // then
        assertThat(stations).hasSize(3);
    }

    @DisplayName("저장된 모든 역을 삭제한다")
    @Test
    void deleteAll() {
        // given
        Station station = new Station(1L, "강남역");
        StationDao.save(station);

        // when
        StationDao.deleteAll();

        // then
        assertThat(StationDao.findAll()).isEmpty();
    }
}
