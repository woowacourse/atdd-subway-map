package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Station;

@JdbcTest
class JdbcStationDaoTest {

    private StationDao stationDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        stationDao = new JdbcStationDao(jdbcTemplate);
    }

    @Test
    @DisplayName("Station을 저장할 수 있다.")
    void save() {
        Station station = new Station("배카라");
        Station savedStation = stationDao.save(station);

        assertThat(savedStation.getId()).isNotNull();
    }

    @Test
    @DisplayName("모든 Station을 조회할 수 있다.")
    void findAll() {
        stationDao.save(new Station("오리"));
        stationDao.save(new Station("배카라"));

        assertThat(stationDao.findAll()).hasSize(2);
    }

    @Test
    @DisplayName("Station 이름이 존재하는 경우 확인한다.")
    void existByName() {
        String name = "배카라";
        stationDao.save(new Station(name));

        assertThat(stationDao.existByName(name)).isTrue();
    }

    @Test
    @DisplayName("Station 이름이 존재하지 않는 경우 확인한다.")
    void nonExistByName() {
        String name = "배카라";
        stationDao.save(new Station(name));

        assertThat(stationDao.existByName("오리")).isFalse();
    }

    @Test
    @DisplayName("Station을 삭제할 수 있다.")
    void delete() {
        Station station = stationDao.save(new Station("오리"));
        Long stationId = station.getId();

        assertThatCode(() -> stationDao.delete(stationId)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("없는 id의 Station을 삭제할 수 없다.")
    void deleteByInvalidId() {
        Station station = stationDao.save(new Station("오리"));
        Long stationId = station.getId() + 1;

        assertThatThrownBy(() -> stationDao.delete(stationId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("없는 station 입니다.");
    }

    @Test
    @DisplayName("이미 삭제한 id의 Station을 삭제할 수 없다.")
    void deleteByDuplicatedId() {
        Station station = stationDao.save(new Station("오리"));
        Long stationId = station.getId();
        stationDao.delete(stationId);

        assertThatThrownBy(() -> stationDao.delete(stationId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("없는 station 입니다.");
    }
}
