package wooteco.subway.station;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@Sql("classpath:schema.sql")
class StationDaoTest {

    @Autowired
    private StationDao stationDao;

    @DisplayName("역 이름이 주어지면 저장하고 역 ID를 반환한다")
    @Test
    void save() {
        Station station = new Station("잠실역");
        assertThat(stationDao.save(station)).isInstanceOf(Long.class);
    }

    @DisplayName("역 이름이 중복되면, 저장되지 않는다")
    @Test
    void saveDuplicateException() {
        Station station = new Station("잠실역");
        stationDao.save(station);
        assertThatThrownBy(() -> stationDao.save(station))
                .isInstanceOf(DataAccessException.class);
    }

    @DisplayName("모든 역을 조회한다")
    @Test
    void findAll() {
        Station station1 = new Station("강남역");
        Station station2 = new Station("잠실역");
        Station station3 = new Station("신림역");

        stationDao.save(station1);
        stationDao.save(station2);
        stationDao.save(station3);

        assertThat(stationDao.findAll().size()).isEqualTo(3);
    }

    @DisplayName("존재하는 역을 id로 삭제한다")
    @Test
    void delete() {
        Station station1 = new Station("강남역");
        Station station2 = new Station("잠실역");
        Station station3 = new Station("신림역");

        stationDao.save(station1);
        stationDao.save(station2);
        long stationId = stationDao.save(station3);

        stationDao.delete(stationId);

        assertThat(stationDao.findAll().size()).isEqualTo(2);
    }

    @DisplayName("id로 역을 조회한다")
    @Test
    void findById() {
        Station station = new Station("강남역");

        long stationId = stationDao.save(station);

        Station findStation = stationDao.findById(stationId);

        assertThat(station.getName()).isEqualTo(findStation.getName());
    }
}