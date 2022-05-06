package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.StationDuplicateException;

@JdbcTest
public class StationDaoTest {

    @Autowired
    private DataSource dataSource;

    private StationDao stationDao;

    @BeforeEach
    void set() {
        stationDao = new JdbcStationDao(dataSource);

        stationDao.save(new Station("선릉역"));
    }

    @AfterEach
    void reset() {
        stationDao.deleteAll();
    }

    @Test
    @DisplayName("지하철역을 저장한다.")
    void save() {
        final Station expected = new Station("강남역");

        final Station actual = stationDao.save(expected);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("중복된 역을 저장할 경우 예외를 발생시킨다.")
    void save_duplicate() {
        final Station expected = new Station("선릉역");

        assertThatThrownBy(() -> stationDao.save(expected))
            .isInstanceOf(StationDuplicateException.class)
            .hasMessage("이미 존재하는 지하철역 이름입니다.");
    }

    @Test
    @DisplayName("모든 지하철 역을 조회한다")
    void findAll() {
        stationDao.save(new Station("잠실역"));

        List<Station> stations = stationDao.findAll();
        assertThat(stations).hasSize(2);
    }

    @Test
    @DisplayName("입력된 id의 지하철 역을 삭제한다")
    void deleteById() {
        stationDao.delete(new Station(1L, "선릉역"));

        assertThat(stationDao.findAll()).isEmpty();
    }
}


