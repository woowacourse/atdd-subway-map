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

import wooteco.subway.domain.Station;

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
        Station station = new Station("lala");

        //when
        Station actual = stationDao.save(station);

        //then
        assertThat(actual.getName()).isEqualTo(station.getName());
    }

    @Test
    @DisplayName("모든 Station 을 조회한다.")
    void findAll() {
        //given
        Station station1 = new Station("lala");
        Station station2 = new Station("sojukang");
        stationDao.save(station1);
        stationDao.save(station2);

        //when
        List<Station> actual = stationDao.findAll();

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
        stationDao.save(new Station(name));

        //when
        Station actual = stationDao.findByName(name).get();

        //then
        assertThat(actual.getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("존재하지 않는 station 의 이름으로 조회할 경우 빈 Optional 을 반환한다.")
    void findByNameNotExists() {
        //given
        String name = "lala";
        stationDao.save(new Station(name));

        //when
        Optional<Station> actual = stationDao.findByName("sojukang");

        //then
        assertThat(actual).isEmpty();
    }

    @Test
    @DisplayName("id로 station 을 조회한다.")
    void findById() {
        //given
        Station station = stationDao.save(new Station("lala"));

        //when
        Station actual = stationDao.findById(station.getId()).get();

        //then
        assertThat(actual.getName()).isEqualTo(station.getName());
    }

    @Test
    @DisplayName("id 로 station 을 삭제한다.")
    void deleteById() {
        //given
        String name = "lala";
        Station savedStation = stationDao.save(new Station(name));

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
