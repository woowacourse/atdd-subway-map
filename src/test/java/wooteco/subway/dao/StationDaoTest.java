package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.repository.dao.JdbcStationDao;
import wooteco.subway.repository.dao.StationDao;
import wooteco.subway.repository.entity.StationEntity;

@JdbcTest
class StationDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private StationDao stationDao;

    @BeforeEach
    void setUp() {
        stationDao = new JdbcStationDao(jdbcTemplate);
    }

    @Test
    @DisplayName("역을 저장하면 저장된 역 정보를 반환한다.")
    void save() {
        // given
        final String name = "선릉";
        final StationEntity entity = new StationEntity(null, name);

        // when
        final StationEntity savedEntity = stationDao.save(entity);

        // then
        assertThat(savedEntity.getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("모든 역 조회하기")
    void findAll() {
        // given
        stationDao.save(new StationEntity(null, "선릉"));
        stationDao.save(new StationEntity(null, "노원"));

        // when
        List<StationEntity> stations = stationDao.findAll();

        // then
        assertThat(stations).hasSize(2);
    }

    @Test
    @DisplayName("id에 해당하는 역 삭제하기")
    void deleteById() {
        // given
        StationEntity entity = stationDao.save(new StationEntity(null, "선릉"));

        // when
        Integer affectedRows = stationDao.deleteById(entity.getId());

        // then
        assertThat(affectedRows).isOne();
    }

    @Test
    @DisplayName("id들에 해당하는 역 모두 가져오기")
    void findByIds() {
        // given
        Long gangNamId = stationDao.save(new StationEntity(null, "강남역")).getId();
        Long jamSilId = stationDao.save(new StationEntity(null, "잠실역")).getId();

        // when
        List<StationEntity> entities = stationDao.findByIds(List.of(gangNamId, jamSilId));

        // then
        assertThat(entities).hasSize(2);
    }
}
