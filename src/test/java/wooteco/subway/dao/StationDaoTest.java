package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import wooteco.subway.domain.Station;

public class StationDaoTest {

    private final StationDao stationDao = StationDao.getInstance();

    @AfterEach
    void tearDown() {
        stationDao.deleteAll();
    }

    @Test
    @DisplayName("Station 을 저장한다.")
    void save() {
        //given
        Station station = new Station("lajukang");

        //when
        Station actual = stationDao.save(station);

        //then
        assertThat(actual.getName()).isEqualTo(station.getName());
    }

    @Test
    @DisplayName("모든 Station 을 검색한다.")
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
    @DisplayName("이름으로 station을 조회한다.")
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
    @DisplayName("모든 station 을 삭제한다.")
    void deleteAll() {
        //given
        Station station1 = new Station("lala");
        Station station2 = new Station("sojukang");
        stationDao.save(station1);
        stationDao.save(station2);

        //when
        stationDao.deleteAll();

        //then
        assertThat(stationDao.findAll()).hasSize(0);
    }
}
