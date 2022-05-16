package wooteco.subway.repository;

import static org.assertj.core.api.Assertions.*;
import static wooteco.subway.domain.fixture.StationFixture.*;

import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import wooteco.subway.dao.JdbcStationDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.station.Station;
import wooteco.subway.domain.station.StationSeries;
import wooteco.subway.entity.StationEntity;

@SpringBootTest
@Sql(value = "classpath:schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class StationRepositoryTest {

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private StationDao stationDao;

    @BeforeEach
    void setUp() {
        stationDao = new JdbcStationDao(dataSource, jdbcTemplate);
    }

    @Test
    @DisplayName("persist를 통해 저장한다.")
    public void saveByPersist() {
        // given
        StationSeries stationSeries = new StationSeries(List.of());
        // when
        stationSeries.add(new Station("새로운역"));
        stationRepository.persist(stationSeries);

        // then
        assertThat(stationRepository.findAllStations()).hasSize(1);
    }

    @Test
    @DisplayName("persist를 통해 삭제한다.")
    public void deleteByPersist() {
        // given
        stationDao.save(StationEntity.from(getStationA()));
        stationDao.save(StationEntity.from(getStationB()));
        stationDao.save(StationEntity.from(getStationC()));

        // when
        StationSeries stationSeries = new StationSeries(stationRepository.findAllStations());
        stationSeries.delete(getStationA().getId());
        stationRepository.persist(stationSeries);

        // then
        assertThat(stationRepository.findAllStations()).hasSize(2);
    }
}