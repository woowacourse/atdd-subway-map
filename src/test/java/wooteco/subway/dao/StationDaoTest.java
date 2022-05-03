package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import wooteco.subway.domain.Station;

public class StationDaoTest {

    private StationDao stationDao;

    @BeforeEach
    void setUp() {
        stationDao = new StationDao();
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
}
