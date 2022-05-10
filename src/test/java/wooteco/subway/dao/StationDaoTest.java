package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import javax.sql.DataSource;
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

    @Test
    @DisplayName("지하철역을 저장한다.")
    void save() {
        final Station created = stationDao.save(STATION_강남);

        assertThat(STATION_강남.getId()).isEqualTo(STATION_강남.getId());

        stationDao.deleteById(created.getId());
    }

    @Test
    @DisplayName("중복된 역을 저장할 경우 예외를 발생시킨다.")
    void save_duplicate() {
        final Station created = stationDao.save(STATION_선릉);

        assertThatThrownBy(() -> stationDao.save(created))
            .isInstanceOf(DuplicateKeyException.class);

        stationDao.deleteById(created.getId());
    }

    @Test
    @DisplayName("모든 지하철 역을 조회한다")
    void findAll() {
        final Station created_1 = stationDao.save(STATION_선릉);
        final Station created_2 = stationDao.save(STATION_강남);

        assertThat(stationDao.findAll()).hasSize(2);

        stationDao.deleteById(created_1.getId());
        stationDao.deleteById(created_2.getId());
    }

    @Test
    @DisplayName("입력된 id의 지하철 역을 삭제한다")
    void deleteById() {
        final Station created = stationDao.save(STATION_선릉);

        stationDao.deleteById(created.getId());

        assertThat(stationDao.findAll()).isEmpty();

        stationDao.deleteById(created.getId());
    }
}
