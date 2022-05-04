package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Station;

@SpringBootTest
class StationDaoTest {

    @Autowired
    private StationDao stationDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @DisplayName("지하철역을 생성한다.")
    @Test
    void save() {
        Station station = new Station("신분당선");
        stationDao.save(station);

        Integer count = jdbcTemplate.queryForObject("select count(*) from STATION", Integer.class);

        assertThat(count).isEqualTo(1);
    }
}
