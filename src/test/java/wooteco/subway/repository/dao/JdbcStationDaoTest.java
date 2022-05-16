package wooteco.subway.repository.dao;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.domain.Station;
import wooteco.subway.repository.entity.StationEntity;

@Sql("/jdbcStationDaoTest.sql")
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
        Station station = Station.ofNullId("신대방역");
        StationEntity savedStationEntity = stationDao.save(new StationEntity(station));

        StationEntity stationEntity = stationDao.findById(savedStationEntity.getId());

        assertThat(stationEntity.getName()).isEqualTo("신대방역");
    }

    @DisplayName("모든 지하철역을 조회한다.")
    @Test
    void findAll() {
        assertThat(stationDao.findAll().size()).isEqualTo(2);
    }

    @DisplayName("id 로 노선을 삭제한다.")
    @Test
    void deleteById() {
        stationDao.deleteById(1L);

        assertThat(stationDao.findAll().size()).isEqualTo(1);
    }
}
