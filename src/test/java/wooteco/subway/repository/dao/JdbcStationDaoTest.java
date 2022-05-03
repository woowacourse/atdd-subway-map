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
}