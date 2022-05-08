package wooteco.subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.NotFoundException;

import javax.sql.DataSource;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
class StationDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private DataSource dataSource;
    private StationDao stationDao;

    @BeforeEach
    void setUp() {
        stationDao = new StationDao(jdbcTemplate, dataSource);
    }

    @DisplayName("역 정보를 저장한다.")
    @Test
    void save() {
        Station expected = new Station("강남역");
        Station actual = stationDao.save(expected);

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("모든 역 정보를 조회한다.")
    @Test
    void findAll() {
        Station station1 = new Station("강남역");
        Station station2 = new Station("신논현역");
        stationDao.save(station1);
        stationDao.save(station2);

        List<Station> actual = stationDao.findAll();

        assertThat(actual).containsExactly(station1, station2);
    }

    @DisplayName("역을 삭제한다.")
    @Test
    void delete() {
        Station station = stationDao.save(new Station("강남역"));

        stationDao.deleteById(station.getId());

        assertThatThrownBy(() -> stationDao.deleteById(station.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageMatching(station.getId() + "id를 가진 지하철 역을 찾을 수 없습니다.");
    }

    @DisplayName("역을 삭제할 때 id에 맞는 역이 없으면 예외를 발생시킨다.")
    @Test
    void deleteException() {
        assertThatThrownBy(() -> stationDao.deleteById(1L))
                .isInstanceOf(NotFoundException.class);
    }
}
