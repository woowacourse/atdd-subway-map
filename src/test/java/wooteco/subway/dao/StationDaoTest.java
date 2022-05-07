package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;

import wooteco.subway.domain.Station;

@JdbcTest
public class StationDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private StationDao stationDao;

    @BeforeEach
    void setUp() {
        stationDao = new StationDao(jdbcTemplate);
    }

    @Test
    @DisplayName("지하철역을 등록한다.")
    void saveTest() {
        Station station = stationDao.save(new Station("선릉역"));
        assertThat(station.getName()).isEqualTo("선릉역");
    }

    @Test
    @DisplayName("등록된 지하철역을 전체 조회한다.")
    void findAllTest() {
        stationDao.save(new Station("선릉역"));
        stationDao.save(new Station("역삼역"));
        stationDao.save(new Station("강남역"));

        List<String> stations = stationDao.findAll()
            .stream()
            .map(Station::getName)
            .collect(Collectors.toList());

        assertAll(
            () -> assertThat(stations).hasSize(3),
            () -> assertThat(stations).containsAll(List.of("선릉역", "역삼역", "강남역"))
        );
    }

    @Test
    @DisplayName("ID로 특정 지하철역을 조회한다.")
    void findByIdTest() {
        Station 짱구역 = stationDao.save(new Station("짱구역"));
        Station station = stationDao.findById(짱구역.getId()).get();

        assertAll(
            () -> assertThat(station.getId()).isEqualTo(짱구역.getId()),
            () -> assertThat(station.getName()).isEqualTo(짱구역.getName())
        );
    }

    @Test
    @DisplayName("ID로 특정 지하철역을 삭제한다.")
    void deleteTest() {
        final Station station = stationDao.save(new Station("강남역"));
        stationDao.deleteById(station.getId());

        assertThat(stationDao.findById(station.getId())).isEmpty();
    }
}
