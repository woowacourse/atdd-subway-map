package wooteco.subway.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import wooteco.subway.dao.FakeStationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.DataNotExistException;

public class StationServiceTest {

    private StationService stationService;

    @BeforeEach
    void setUp() {
        stationService = new StationService(new FakeStationDao());
    }

    @Test
    @DisplayName("station 을 저장한다.")
    void create() {
        //given
        Station station = new Station("lala");

        //when
        Station actual = stationService.createStation(station);

        //then
        assertThat(actual.getName()).isEqualTo(station.getName());
    }

    @Test
    @DisplayName("중복된 역을 저장할 수 없다.")
    void createDuplicateName() {
        //given
        Station station = new Station("lala");
        Station actual = stationService.createStation(station);

        //then
        assertThatThrownBy(() -> stationService.createStation(station))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("이미 등록된 역입니다.");
    }

    @Test
    @DisplayName("모든 station 을 검색한다.")
    void findAll() {
        //given
        Station station1 = new Station("lala");
        Station station2 = new Station("sojukang");
        stationService.createStation(station1);
        stationService.createStation(station2);

        //when
        List<Station> actual = stationService.findAll();

        //then
        assertAll(
            () -> assertThat(actual.get(0).getName()).isEqualTo(station1.getName()),
            () -> assertThat(actual.get(1).getName()).isEqualTo(station2.getName())
        );
    }

    @Test
    @DisplayName("id 로 Line 을 조회한다.")
    void findById() {
        //given
        Station station = new Station("lala");
        Long id = stationService.createStation(station).getId();

        //when
        Station actual = stationService.findById(id);

        //then
        assertThat(actual.getName()).isEqualTo(station.getName());
    }

    @Test
    @DisplayName("존재하지 않는 id 로 조회할 경우 예외를 던진다.")
    void findByIdNotExist() {
        //given
        Station station = new Station("lala");
        Long id = stationService.createStation(station).getId();

        //then
        assertThatThrownBy(() -> stationService.findById(id + 1))
            .isInstanceOf(DataNotExistException.class)
            .hasMessage("존재하지 않는 역입니다.");
    }

    @Test
    @DisplayName("Station 을 삭제한다.")
    void deleteById() {
        //given
        Station station = new Station("이수");
        Station createdStation = stationService.createStation(station);
        stationService.deleteById(createdStation.getId());

        //then
        assertThatThrownBy(() -> stationService.findById(createdStation.getId()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("존재하지 않는 역입니다.");
    }
}
