package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

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
class StationDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private StationDao stationDao;

    @BeforeEach
    void setUp() {
        stationDao = new StationDao(jdbcTemplate);
    }

    @Test
    @DisplayName("새로운 지하철 역을 등록할 수 있다.")
    void save() {
        final Station station = new Station("선릉역");
        final Station savedStation = stationDao.save(station);

        assertThat(savedStation).isNotNull();
    }

    @Test
    @DisplayName("등록된 지하철 역들을 반환한다.")
    void findAll() {
        final Station station1 = new Station("강남역");
        final Station station2 = new Station("역삼역");
        final Station station3 = new Station("선릉역");

        stationDao.save(station1);
        stationDao.save(station2);
        stationDao.save(station3);

        final List<String> actual = stationDao.findAll().stream()
                .map(Station::getName)
                .collect(Collectors.toList());
        final List<String> expected = List.of("강남역", "역삼역", "선릉역");

        assertThat(actual).containsAll(expected);
    }

    @Test
    @DisplayName("등록된 지하철을 삭제한다.")
    void deleteById() {
        final Station station = new Station("선릉역");
        final Station savedStation = stationDao.save(station);
        final Long id = savedStation.getId();

        stationDao.deleteById(id);

        assertThat(stationDao.findAll()).hasSize(0);
    }
}
