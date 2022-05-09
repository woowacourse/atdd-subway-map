package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.dao.memory.StationMemoryDao;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.NoSuchStationException;

class StationMemoryDaoTest {

    @BeforeEach
    void setUp() {
        StationMemoryDao.deleteAll();
    }

    @DisplayName("새로운 지하철 역을 저장한다")
    @Test
    void saveStation() {
        // given
        Station station = new Station(1L, "강남역");

        // when
        StationMemoryDao.save(station);

        // then
        List<Station> stations = StationMemoryDao.findAll();
        assertThat(stations.get(0)).isEqualTo(station);
    }

    @DisplayName("같은 지하철 역 이름이 있는 경우 예외를 발생시킨다")
    @Test
    void saveStationThrowException() {
        String name = "강남역";
        StationMemoryDao.save(new Station(1L, name));

        assertThatThrownBy(() -> StationMemoryDao.save(new Station(2L, name)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("같은 이름");
    }

    @DisplayName("역 목록을 조회한다")
    @Test
    void findAll() {
        // given
        StationMemoryDao.save(new Station(1L, "name1"));
        StationMemoryDao.save(new Station(2L, "name2"));
        StationMemoryDao.save(new Station(3L, "name3"));

        // when
        List<Station> stations = StationMemoryDao.findAll();

        // then
        assertThat(stations).hasSize(3);
    }

    @DisplayName("저장된 모든 역을 삭제한다")
    @Test
    void deleteAll() {
        // given
        Station station = new Station(1L, "강남역");
        StationMemoryDao.save(station);

        // when
        StationMemoryDao.deleteAll();

        // then
        assertThat(StationMemoryDao.findAll()).isEmpty();
    }

    @DisplayName("id로 역 하나를 삭제한다")
    @Test
    void deleteById() {
        // given
        Long savedId = StationMemoryDao.save(new Station(1L, "station"));

        // when
        StationMemoryDao.deleteById(savedId);

        // then
        assertThat(StationMemoryDao.findAll()).isEmpty();
    }

    @DisplayName("삭제하려는 지하철 역이 없는 경우 예외를 발생시킨다")
    @Test
    void throwsExceptionWhenTargetIdDoesNotExist() {
        assertThatThrownBy(() -> StationMemoryDao.deleteById(1))
                .isInstanceOf(NoSuchStationException.class);
    }
}
