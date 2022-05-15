package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import wooteco.subway.dao.entity.StationEntity;

@JdbcTest
@Import(StationDao.class)
public class StationDaoTest {

    @Autowired
    private StationDao stationDao;

    @Test
    @DisplayName("지하철 역 저장")
    void save() {
        StationEntity station = new StationEntity("호호역");
        StationEntity savedStation = stationDao.save(station);

        assertThat(savedStation.getId()).isNotNull();
        assertThat(savedStation.getName()).isEqualTo(station.getName());
    }

    @Test
    @DisplayName("지하철 역 이름 중복 여부 조회")
    void duplicateName() {
        StationEntity station = new StationEntity("호호역");
        stationDao.save(station);
        assertThat(stationDao.existByName("호호역")).isTrue();
    }

    @Test
    @DisplayName("지하철 역 전체 조회")
    void findAll() {
        StationEntity station1 = new StationEntity("호호역");
        StationEntity station2 = new StationEntity("수달역");
        stationDao.save(station1);
        stationDao.save(station2);

        List<StationEntity> stations = stationDao.findAll();

        assertThat(stations).hasSize(2);
    }

    @Test
    @DisplayName("id로 지하철 역을 삭제")
    void deleteById() {
        StationEntity station1 = new StationEntity("호호역");
        StationEntity savedStation = stationDao.save(station1);

        stationDao.deleteById(savedStation.getId());

        assertThat(stationDao.findAll()).hasSize(0);
    }

}
