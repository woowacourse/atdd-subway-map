package wooteco.subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Station;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
public class JdbcStationDaoTest {
    private JdbcStationDao stationDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        stationDao = new JdbcStationDao(jdbcTemplate);

        jdbcTemplate.execute("create table if not exists STATION(\n"
                + "    id bigint auto_increment not null,\n"
                + "    name varchar(255) not null unique,\n"
                + "    primary key(id)\n"
                + ");");

        final Station station1 = new Station("지하철역이름");
        final Station station2 = new Station("새로운지하철역이름");
        stationDao.save(station1);
        stationDao.save(station2);
    }

    @Test
    @DisplayName("지하철역을 저장한다.")
    void save() {
        final String sql = "select count(*) from STATION";
        final int expected = 2;

        final int actual = jdbcTemplate.queryForObject(sql, Integer.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("지하철역을 조회한다.")
    void findAll() {
        final int expected = 2;

        final int actual = stationDao.findAll().size();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("지하철역을 삭제한다.")
    void delete() {
        final Station station3 = new Station("또다른지하철역이름");
        final Long stationId = stationDao.save(station3);
        stationDao.deleteById(stationId);
        final int expected = 2;

        final int actual = stationDao.findAll().size();

        assertThat(actual).isEqualTo(expected);
    }
}
