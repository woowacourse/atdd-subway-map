package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.SubwayException;
import wooteco.subway.domain.Station;

@JdbcTest
class StationDaoTest {

    private final StationDao stationDao;

    @Autowired
    private StationDaoTest(JdbcTemplate jdbcTemplate) {
        this.stationDao = new StationDao(jdbcTemplate);
    }

    @DisplayName("중복되는 역 이름이 없을 때 성공적으로 저장되는지 테스트")
    @Test
    void save_success() {
        Station station = stationDao.save(new Station("testName"));

        assertThat(stationDao.findAll().size()).isEqualTo(1);
    }

    @DisplayName("중복되는 역 이름이 있을 때 예외 반환 테스트")
    @Test
    void save_fail() {
        Station station = stationDao.save(new Station("testName"));
        assertThatThrownBy(() -> stationDao.save(new Station("testName")))
                .isInstanceOf(SubwayException.class);
    }

    @DisplayName("존재하는 역 id가 있으면 삭제되는지 테스트")
    @Test
    void deleteById_exist() {
        Station station = stationDao.save(new Station("testName"));
        stationDao.deleteById(station.getId());
        assertThat(stationDao.findAll().size()).isEqualTo(0);
    }

    @DisplayName("존재하는 역 id가 없으면 삭제되지 않는지 테스트")
    @Test
    void deleteById_not_exist() {
        Station station = stationDao.save(new Station("testName"));
        stationDao.deleteById(-1L);
        assertThat(stationDao.findAll().size()).isEqualTo(1);
    }

    @DisplayName("존재하는 역 id가 있으면 null이 아닌지 테스트")
    @Test
    void findById_exist() {
        Station station = stationDao.save(new Station("testName"));
        Station result = stationDao.findById(station.getId());
        assertThat(result).isNotNull();
    }

    @DisplayName("존재하는 역 id가 없으면 예외가 발생하는지 테스트")
    @Test
    void findById_not_exist() {
        Station station = stationDao.save(new Station("testName"));
        assertThatThrownBy(() -> stationDao.findById(-1L))
                .isInstanceOf(EmptyResultDataAccessException.class);
    }
}
