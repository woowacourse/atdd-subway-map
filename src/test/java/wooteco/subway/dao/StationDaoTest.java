package wooteco.subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.station.Station;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
class StationDaoTest {

    private StationDao stationDao;
    private long testStationId;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        stationDao = new StationDao(jdbcTemplate);
        String schemaQuery = "create table if not exists STATION ( id bigint auto_increment not null, nam varchar(255) " +
                "not null unique, primary key(id))";
        jdbcTemplate.execute(schemaQuery);
        testStationId = stationDao.save(new Station("testStation"));
    }

    @DisplayName("역을 등록한다.")
    @Test
    void save() {
        Station station = new Station("testStation2");

        stationDao.save(station);
        List<Station> stations = stationDao.findAll();

        assertThat(stations).hasSize(2);
    }

    @DisplayName("역을 ID로 조회한다.")
    @Test
    void findById() {
        Station station = stationDao.findById(testStationId);

        assertThat(station).isEqualTo(new Station(testStationId, "testStation"));
    }

    @DisplayName("역을 삭제한다.")
    @Test
    void delete() {
        long id = stationDao.save(new Station("dummy"));
        int beforeLineCounts = stationDao.findAll().size();

        stationDao.deleteById(id);
        int afterLineCounts = stationDao.findAll().size();

        assertThat(beforeLineCounts - 1).isEqualTo(afterLineCounts);
    }
}
