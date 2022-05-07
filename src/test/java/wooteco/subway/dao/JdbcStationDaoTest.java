package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Station;

@JdbcTest
class JdbcStationDaoTest {

    private final StationDao stationDao;

    @Autowired
    public JdbcStationDaoTest(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.stationDao = new JdbcStationDao(jdbcTemplate, dataSource);
    }

    @Test
    @DisplayName("지하철 역을 생성한다.")
    void StationCreateTest() {
        Long stationId = stationDao.save(new Station("선릉역"));

        assertThat(stationDao.findAll()).hasSize(1)
            .extracting("name")
            .containsExactly("선릉역");
    }

    @Test
    @DisplayName("지하철 역을 조회한다.")
    void StationReadTest() {
        Long stationId = stationDao.save(new Station("잠실역"));

        List<Station> stations1 = stationDao.findAll();

        assertThat(stations1).hasSize(1)
            .extracting("name")
            .containsExactly("잠실역");
    }

    @Test
    @DisplayName("지하철 역을 삭제한다.")
    void StationDeleteTest() {
        Long stationId = stationDao.save(new Station("선릉역"));

        stationDao.deleteById(stationId);

        assertThat(stationDao.findAll()).isEmpty();
    }
}
