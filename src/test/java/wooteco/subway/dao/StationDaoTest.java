package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.domain.Station;

@JdbcTest
@Sql({"/schema.sql", "/test-data.sql"})
class StationDaoTest {

    private final StationDao stationDao;

    @Autowired
    public StationDaoTest(JdbcTemplate jdbcTemplate) {
        this.stationDao = new StationDao(jdbcTemplate);
    }

    @Test
    void save() {
        final Station newStation = stationDao.save(new Station("석촌고분역"));
        assertThat(newStation.getId()).isEqualTo(4L);
    }

    @Test
    void findAll() {
        final List<Station> stations = stationDao.findAll();
        assertThat(stations.size()).isEqualTo(3);
    }

    @Test
    void existsByName() {
        final boolean result = stationDao.existsByName("삼전역");
        assertThat(result).isTrue();
    }

    @Test
    void deleteById() {
        stationDao.deleteById(1L);

        final List<Station> stations = stationDao.findAll();
        assertThat(stations.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("존재하지 않는 id값을 삭제할 때 예외 발생")
    void deleteNonExistentId() {
        assertThatThrownBy(() -> stationDao.deleteById(4L)).isInstanceOf(IllegalArgumentException.class);
    }
}
