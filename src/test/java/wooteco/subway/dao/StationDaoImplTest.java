package wooteco.subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Station;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
class StationDaoImplTest {

    private StationDaoImpl stationDaoImpl;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        stationDaoImpl = new StationDaoImpl(jdbcTemplate);

        jdbcTemplate.execute("DROP TABLE station IF EXISTS");
        jdbcTemplate.execute("CREATE TABLE station(" +
                "id bigint auto_increment not null,\n" +
                "name varchar(255) not null unique,\n" +
                "primary key(id));");

        List<Object[]> splitStation = Arrays.asList("선릉역", "잠실역", "강남역").stream()
                .map(name -> name.split(" "))
                .collect(Collectors.toList());

        jdbcTemplate.batchUpdate("INSERT INTO station(name) VALUES (?)", splitStation);
    }

    @DisplayName("역정보를 저장한다.")
    @Test
    void save() {
        Station station = new Station("역삼역");
        Station newStation = stationDaoImpl.save(station);

        assertThat(newStation.getName()).isEqualTo("역삼역");
    }

    @DisplayName("역정보들을 가져온다.")
    @Test
    void findAll() {
        List<Station> stations = stationDaoImpl.findAll();

        assertThat(stations.size()).isEqualTo(3);
    }

    @DisplayName("역정보를 삭제한다.")
    @Test
    void delete() {
        Station station = new Station("역삼역");
        Station newStation = stationDaoImpl.save(station);

        assertThat(stationDaoImpl.deleteStation(newStation.getId())).isEqualTo(1);
    }
}