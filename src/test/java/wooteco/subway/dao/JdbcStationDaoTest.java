package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;

import wooteco.subway.domain.StationEntity;

@JdbcTest
class JdbcStationDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private JdbcStationDao stationDao;

    @BeforeEach
    void setUp() {
        stationDao = new JdbcStationDao(jdbcTemplate);
    }

    @Test
    @DisplayName("Station 을 저장한다.")
    void save() {
        //given
        StationEntity station = new StationEntity("lala");

        //when
        StationEntity actual = stationDao.save(station);

        //then
        assertThat(actual.getName()).isEqualTo(station.getName());
    }

    @Test
    @DisplayName("모든 Station 을 검색한다.")
    void findAll() {
        //given
        StationEntity station1 = new StationEntity("lala");
        StationEntity station2 = new StationEntity("sojukang");
        stationDao.save(station1);
        stationDao.save(station2);

        //when
        List<StationEntity> actual = stationDao.findAll();

        //then
        assertAll(
            () -> assertThat(actual.get(0).getName()).isEqualTo(station1.getName()),
            () -> assertThat(actual.get(1).getName()).isEqualTo(station2.getName())
        );
    }

    @Test
    @DisplayName("이름으로 station 을 조회한다.")
    void findByName() {
        //given
        String name = "lala";
        stationDao.save(new StationEntity(name));

        //when
        StationEntity actual = stationDao.findByName(name).get();

        //then
        assertThat(actual.getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("존재하지 않는 station 의 이름으로 조회할 경우 빈 Optional 을 반환한다.")
    void findByNameNotExists() {
        //given
        String name = "lala";
        stationDao.save(new StationEntity(name));

        //when
        Optional<StationEntity> actual = stationDao.findByName("sojukang");

        //then
        assertThat(actual).isEmpty();
    }

    @Test
    @DisplayName("id로 station 을 조회한다.")
    void findById() {
        //given
        StationEntity station = stationDao.save(new StationEntity("lala"));

        //when
        StationEntity actual = stationDao.findById(station.getId()).get();

        //then
        assertThat(actual.getName()).isEqualTo(station.getName());
    }

    @Test
    @DisplayName("id 로 station 을 삭제한다.")
    void deleteById() {
        //given
        String name = "lala";
        StationEntity savedStation = stationDao.save(new StationEntity(name));

        //when
        stationDao.deleteById(savedStation.getId());

        //then
        assertThat(stationDao.findByName(name)).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 id 를 삭제하면 0을 반환한다.")
    void deleteByIdNotExists() {
        //when
        int actual = stationDao.deleteById(1L);

        //then
        assertThat(actual).isEqualTo(0);
    }
}
