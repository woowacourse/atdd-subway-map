package wooteco.subway.repository.dao;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import wooteco.subway.domain.Station;
import wooteco.subway.repository.entity.StationEntity;

@JdbcTest
class JdbcStationDaoTest {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private StationDao stationDao;

    @BeforeEach
    void setUp() {
        stationDao = new JdbcStationDao(namedParameterJdbcTemplate);
    }

    @DisplayName("지하철역을 저장하고 id로 지하철역을 찾는다.")
    @Test
    void saveAndFindById() {
        Station station = new Station("잠실역");
        StationEntity savedStationEntity = stationDao.save(new StationEntity(station));

        StationEntity stationEntity = stationDao.findById(savedStationEntity.getId()).get();

        assertThat(stationEntity.getName()).isEqualTo("잠실역");
    }

    @DisplayName("모든 지하철역을 조회한다.")
    @Test
    void findAll() {
        Station station1 = new Station("잠실역");
        stationDao.save(new StationEntity(station1));
        Station station2 = new Station("선릉역");
        stationDao.save(new StationEntity(station2));

        assertThat(stationDao.findAll().size()).isEqualTo(2);
    }

    @DisplayName("이름으로 노선을 찾는다.")
    @Test
    void findByName() {
        Station station = new Station("잠실역");
        StationEntity savedStationEntity = stationDao.save(new StationEntity(station));

        StationEntity stationEntity = stationDao.findByName(savedStationEntity.getName()).get();

        assertThat(stationEntity.getName()).isEqualTo(station.getName());
    }

    @DisplayName("id 로 노선을 삭제한다.")
    @Test
    void deleteById() {
        Station station = new Station("잠실역");
        StationEntity savedStationEntity = stationDao.save(new StationEntity(station));

        stationDao.deleteById(savedStationEntity.getId());

        assertThat(stationDao.findAll().size()).isEqualTo(0);
    }
}