package wooteco.subway.dao;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;

import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
class StationJdbcDaoTest {

    private StationJdbcDao stationJdbcDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        stationJdbcDao = new StationJdbcDao(jdbcTemplate);

        List<Object[]> splitStation = Arrays.asList("선릉역", "잠실역", "강남역").stream()
                .map(name -> name.split(" "))
                .collect(Collectors.toList());

        jdbcTemplate.batchUpdate("INSERT INTO station(name) VALUES (?)", splitStation);
    }

    @DisplayName("역 정보를 저장한다.")
    @Test
    void save() {
        StationRequest station = new StationRequest("역삼역");
        Station newStation = stationJdbcDao.save(station);

        assertThat(newStation.getName()).isEqualTo("역삼역");
    }

    @DisplayName("역 정보들을 가져온다.")
    @Test
    void findAll() {
        List<Station> stations = stationJdbcDao.findAll();

        assertThat(stations.size()).isEqualTo(3);
    }

    @DisplayName("역 정보를 삭제한다.")
    @Test
    void delete() {
        StationRequest station = new StationRequest("역삼역");
        Station newStation = stationJdbcDao.save(station);

        assertThat(stationJdbcDao.deleteStation(newStation.getId())).isEqualTo(1);
    }
}
