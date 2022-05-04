package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.domain.Station;

@JdbcTest
@Sql("/schema.sql")
class JdbcStationDaoTest {

    private final JdbcStationDao stationDao;

    @Autowired
    public JdbcStationDaoTest(JdbcTemplate jdbcTemplate) {
        this.stationDao = new JdbcStationDao(jdbcTemplate);
    }

    @Test
    @DisplayName("지하철 역을 생성, 조회, 삭제한다.")
    void StationCRDTest() {
        Long stationId = stationDao.save(new Station("선릉역"));

        List<Station> stations1 = stationDao.findAll();
        assertThat(stations1).hasSize(1)
                .extracting("name")
                .containsExactly("선릉역");

        stationDao.deleteById(stationId);

        List<Station> stations2 = stationDao.findAll();
        assertThat(stations2).hasSize(0);
    }
}
