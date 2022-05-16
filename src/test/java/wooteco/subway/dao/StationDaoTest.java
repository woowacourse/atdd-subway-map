package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static wooteco.subway.Fixtures.HYEHWA;
import static wooteco.subway.Fixtures.SINSA;

import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import wooteco.subway.dao.entity.StationEntity;
import wooteco.subway.exception.notfound.NotFoundStationException;

@JdbcTest
class StationDaoTest {

    @Autowired
    private DataSource dataSource;

    private StationDao stationDao;

    @BeforeEach
    void setUp() {
        stationDao = new StationDao(dataSource);
    }

    @Test
    @DisplayName("역을 저장한다.")
    void save() {
        // when
        final Long id = stationDao.save(new StationEntity(HYEHWA));

        // then
        assertThat(stationDao.findById(id).getName()).isEqualTo(HYEHWA);
    }

    @Test
    @DisplayName("역을 조회한다.")
    void find() {
        // given
        final long id = stationDao.save(new StationEntity(HYEHWA));

        // when
        final StationEntity foundStation = stationDao.findById(id);

        // then
        assertThat(foundStation.getName()).isEqualTo(HYEHWA);
    }

    @Test
    @DisplayName("존재하지 않는 Id 조회 시, 예외를 발생한다.")
    void findNotExistId() {
        // when & then
        assertThatThrownBy(() -> stationDao.findById(1L))
                .isInstanceOf(NotFoundStationException.class);
    }

    @Test
    @DisplayName("모든 역을 조회한다.")
    void findAll() {
        // given
        stationDao.save(new StationEntity(HYEHWA));
        stationDao.save(new StationEntity(SINSA));

        // when
        final List<StationEntity> stations = stationDao.findAll();

        // then
        assertThat(stations).hasSize(2);
    }

    @Test
    @DisplayName("역을 삭제한다.")
    void delete() {
        // given
        final Long id = stationDao.save(new StationEntity(HYEHWA));

        // when
        stationDao.deleteById(id);

        // then
        assertThat(stationDao.findAll()).hasSize(0);
    }
}
