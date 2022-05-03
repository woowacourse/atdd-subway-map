package wooteco.subway.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;

public class StationServiceTest {

    private final StationService stationService = StationService.getInstance();

    @AfterEach
    void tearDown() {
        StationDao.getInstance().deleteAll();
    }

    @Test
    @DisplayName("station 을 저장한다.")
    void save() {
        //given
        Station station = new Station("lala");

        //when
        Station actual = stationService.save(station);

        //then
        assertThat(actual.getName()).isEqualTo(station.getName());
    }

    @Test
    @DisplayName("중복된 역을 저장할 수 없다.")
    void saveDuplicateName() {
        //given
        Station station = new Station("lala");
        Station actual = stationService.save(station);

        //then
        assertThatThrownBy(() -> stationService.save(station))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("이미 등록된 역입니다.");
    }

    @Test
    @DisplayName("모든 station 을 검색한다.")
    void findAll() {
        //given
        Station station1 = new Station("lala");
        Station station2 = new Station("sojukang");
        stationService.save(station1);
        stationService.save(station2);

        //when
        List<Station> actual = stationService.findAll();

        //then
        assertAll(
            () -> assertThat(actual.get(0).getName()).isEqualTo(station1.getName()),
            () -> assertThat(actual.get(1).getName()).isEqualTo(station2.getName())
        );
    }
}
