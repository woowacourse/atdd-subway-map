package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Station;

class StationDaoTest {

    @BeforeEach
    public void setUp() {
        delete_all();
    }

    @DisplayName("중복되는 역 이름이 없을 때 성공적으로 저장되는지 테스트")
    @Test
    void save_success() {
        Station station = StationDao.save(new Station("testName"));

        assertThat(StationDao.findAll().size()).isEqualTo(1);
    }

    @DisplayName("중복되는 역 이름이 있을 때 예외 반환 테스트")
    @Test
    void save_fail() {
        Station station = StationDao.save(new Station("testName"));
        assertThatThrownBy(() -> StationDao.save(new Station("testName")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("존재하는 역 id가 있으면 삭제되는지 테스트")
    @Test
    void deleteById_exist() {
        Station station = StationDao.save(new Station("testName"));
        StationDao.deleteById(station.getId());
        assertThat(StationDao.findAll().isEmpty()).isTrue();
    }

    @DisplayName("존재하는 역 id가 없으면 삭제되지 않는지 테스트")
    @Test
    void deleteById_not_exist() {
        Station station = StationDao.save(new Station("testName"));
        StationDao.deleteById(-1L);
        assertThat(StationDao.findAll().isEmpty()).isFalse();
    }

    @DisplayName("존재하는 역 id가 있으면 Optional이 비지 않았는지 테스트")
    @Test
    void findById_exist() {
        Station station = StationDao.save(new Station("testName"));
        Optional<Station> result = StationDao.findById(station.getId());
        assertThat(result.isPresent()).isTrue();
    }

    @DisplayName("존재하는 역 id가 없으면 Optional이 비었는지 테스트")
    @Test
    void findById_not_exist() {
        Station station = StationDao.save(new Station("testName"));
        Optional<Station> result = StationDao.findById(-1L);
        assertThat(result.isPresent()).isFalse();
    }

    void delete_all() {
        List<Station> stations = StationDao.findAll();
        stations.clear();

    }
}
