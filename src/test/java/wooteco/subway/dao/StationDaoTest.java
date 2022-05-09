package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import javax.sql.DataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.DuplicateKeyException;
import wooteco.subway.domain.Station;

@JdbcTest
public class StationDaoTest {

    private static final Station STATION_선릉 = new Station("선릉역");
    private static final Station STATION_강남 = new Station("강남역");

    @Autowired
    private DataSource dataSource;
    private StationDao stationDao;

    @BeforeEach
    void set() {
        stationDao = new JdbcStationDao(dataSource);
    }

    @AfterEach
    void reset() {
        for (final Station station : stationDao.findAll()) {
            stationDao.delete(station);
        }
    }

    @Test
    @DisplayName("지하철역을 저장한다.")
    void save() {
        final Station expected = STATION_강남;

        final Station actual = stationDao.save(expected);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("중복된 역을 저장할 경우 예외를 발생시킨다.")
    void save_duplicate() {
        final Station created = stationDao.save(STATION_선릉);

        assertThatThrownBy(() -> stationDao.save(created))
            .isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    @DisplayName("모든 지하철 역을 조회한다")
    void findAll() {
        stationDao.save(STATION_선릉);
        stationDao.save(STATION_강남);

        assertThat(stationDao.findAll()).hasSize(2);
    }

    @Test
    @DisplayName("입력된 id의 지하철 역을 삭제한다")
    void deleteById() {
        final Station created = stationDao.save(STATION_선릉);

        stationDao.delete(created);

        assertThat(stationDao.findAll()).isEmpty();
    }
}
