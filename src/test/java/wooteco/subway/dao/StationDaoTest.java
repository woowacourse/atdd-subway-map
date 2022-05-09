package wooteco.subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Station;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
public class StationDaoTest {
    private StationDao stationDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void init() {
        stationDao = new StationDao(jdbcTemplate);

        jdbcTemplate.execute("DROP TABLE Station IF EXISTS");
        jdbcTemplate.execute("CREATE TABLE Station(" +
                "id bigint auto_increment not null,\n" +
                "name varchar(255) not null unique,\n" +
                "primary key(id))"
        );
    }

    @DisplayName("Station 객체의 정보가 제대로 저장되는 것을 확인한다.")
    @Test
    void save() {
        final Station station = stationDao.save(new Station(null, "강남역"));

        assertThat(station.getName()).isEqualTo("강남역");
    }

    @DisplayName("전달된 이름을 가지고 있는 Station의 개수를 제대로 반환하는 지 확인한다.")
    @Test
    void counts_one() {
        jdbcTemplate.update("insert into Station (name) values (?)", "선릉역");
        final int actual = stationDao.counts("선릉역");

        assertThat(actual).isEqualTo(1);
    }

    @DisplayName("전달된 이름을 가지고 있는 Station의 개수를 제대로 반환하는 지 확인한다.")
    @Test
    void counts_zero() {
        jdbcTemplate.update("insert into Station (name) values (?)", "선릉역");
        final int actual = stationDao.counts("강남역");

        assertThat(actual).isEqualTo(0);
    }

    @DisplayName("모든 Station을 가져오는 것을 확인한다.")
    @Test
    void findAll() {
        jdbcTemplate.update("insert into Station (name) values (?)", "강남역");
        jdbcTemplate.update("insert into Station (name) values (?)", "선릉역");
        jdbcTemplate.update("insert into Station (name) values (?)", "잠실역");
        final List<Station> stations = stationDao.findAll();
        final int actual = stations.size();

        assertThat(actual).isEqualTo(3);
    }

    @DisplayName("인자로 전달된 id를 가지는 레코드가 삭제되는 것을 확인한다.")
    @Test
    void deleteById() {
        jdbcTemplate.update("insert into Station (name) values (?)", "강남역");
        stationDao.deleteById(1L);
        final int actual = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM Station WHERE name = '강남역'", Integer.class);

        assertThat(actual).isEqualTo(0);
    }

}