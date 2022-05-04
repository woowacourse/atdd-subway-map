package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

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
    public JdbcStationDaoTest(JdbcTemplate jdbcTemplate) {
        this.stationDao = new JdbcStationDao(jdbcTemplate);
    }

    @DisplayName("지하철역을 저장한다.")
    @Test
    void 지하철역_저장() {
        String stationName = "서울대입구역";
        Station station = new Station(stationName);

        Station savedStation = stationDao.save(station);

        assertThat(savedStation.getName()).isEqualTo(stationName);
    }
}
