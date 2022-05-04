package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import wooteco.subway.domain.Station;

public class StationDaoTest {

    private final StationDao stationDao = new StationDao();

    @AfterEach
    void tearDown() {
        new StationDao().deleteAll();
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
    void deleteByName() {
        //given
        String name = "lala";
        Station savedStation = stationDao.save(new Station(name));

        //when
        stationDao.deleteById(savedStation.getId());

        //then
        assertThat(stationDao.findByName(name)).isEmpty();
    }

    @Test
    @DisplayName("역 id 가 존재하지 않을 경우 삭제하면 예외를 던진다.")
    void deleteByNameNotExists() {
        //given
        String name = "lala";
        Station savedStation = stationDao.save(new Station(name));

        //then
        assertThatThrownBy(() -> stationDao.deleteById(savedStation.getId() + 1))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("존재하지 않는 역 입니다.");
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
