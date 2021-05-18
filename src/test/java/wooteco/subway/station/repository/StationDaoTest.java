package wooteco.subway.station.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.station.domain.Station;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Sql("classpath:tableInit.sql")
public class StationDaoTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private StationDao stationDao;

    @BeforeEach
    void setUp() {
        stationDao = new StationDao(jdbcTemplate);
        String query = "INSERT INTO STATION(name) VALUES (?)";

        jdbcTemplate.update(query, "잠실역");
        jdbcTemplate.update(query, "잠실새내역");
    }

    @DisplayName("station을 DB에 저장하면, id가 생성된 station을 반환한다.")
    @Test
    void saveStation() {
        Station station = new Station("석촌역");

        Station newStation = stationDao.save(station);
        assertThat(newStation.getId()).isEqualTo(3L);
    }

    @DisplayName("DB에 있는 station들을 조회하면, station을 담은 리스트를 반환한다.")
    @Test
    void findAll() {
        List<Station> stations = Arrays.asList(new Station(1L, "잠실역"), new Station(2L, "잠실새내역"));
        assertThat(stationDao.findAll()).isEqualTo(stations);
    }

    @DisplayName("전체 station을 조회할 때, DB에 존재하는 station이 없다면 빈 리스트를 반환한다.")
    @Test
    void findAll_noLinesSaved_emptyList() {
        jdbcTemplate.update("DELETE FROM station");
        assertThat(stationDao.findAll()).isEmpty();
    }

    @DisplayName("id를 통해 삭제 요청을 하면, DB에 있는 해당 id의 station을 삭제한다")
    @Test
    void deleteById() {
        Long id = 1L;

        String query = "SELECT EXISTS(SELECT * FROM station WHERE id = ?)";
        assertThat(jdbcTemplate.queryForObject(query, Boolean.class, id)).isTrue();

        stationDao.deleteById(id);
        assertThat(jdbcTemplate.queryForObject(query, Boolean.class, id)).isFalse();
    }
}