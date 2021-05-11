package wooteco.subway.station;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Sql("classpath:test-schema.sql")
class StationDaoTest {

    @Autowired
    private StationDao stationDao;

    @DisplayName("역 이름이 주어지면 저장하고 역 ID를 반환한다")
    @Test
    void save() {
        String stationName = "잠실역";
        assertThat(stationDao.save(stationName)).isInstanceOf(Long.class);
    }

    @DisplayName("역 이름이 중복되면, 저장되지 않는다")
    @Test
    void saveDuplicateException() {
        String stationName = "잠실역";
        stationDao.save(stationName);
        assertThatThrownBy(() -> stationDao.save(stationName))
            .isInstanceOf(DataAccessException.class);
    }

    @DisplayName("모든 역을 조회한다")
    @Test
    void findAll() {
        String station1 = "강남역";
        String station2 = "잠실역";
        String station3 = "신림역";

        stationDao.save(station1);
        stationDao.save(station2);
        stationDao.save(station3);

        assertThat(stationDao.findAll().size()).isEqualTo(3);
    }

    @DisplayName("존재하는 역을 id로 삭제한다")
    @Test
    void delete() {
        String station1 = "강남역";
        String station2 = "잠실역";
        String station3 = "신림역";

        stationDao.save(station1);
        stationDao.save(station2);
        long stationId3 = stationDao.save(station3);

        stationDao.delete(stationId3);

        assertThat(stationDao.findAll().size()).isEqualTo(2);
    }

    @DisplayName("id로 역을 조회한다")
    @Test
    void findById() {
        String station = "강남역";

        long stationId = stationDao.save(station);

        Station foundStation = stationDao.findById(stationId);

        assertThat(foundStation.getName()).isEqualTo(station);
    }
}