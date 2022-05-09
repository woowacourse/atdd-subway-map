package wooteco.subway.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import wooteco.subway.domain.StationEntity;
import wooteco.subway.exception.DataDuplicationException;
import wooteco.subway.exception.DataNotExistException;

class StationServiceTest {

    private StationService stationService;

    @BeforeEach
    void setUp() {
        stationService = new StationService(new FakeStationDao());
    }

    @Test
    @DisplayName("station 을 저장한다.")
    void create() {
        //given
        StationEntity station = new StationEntity("lala");

        //when
        StationEntity actual = stationService.createStation(station);

        //then
        assertThat(actual.getName()).isEqualTo(station.getName());
    }

    @Test
    @DisplayName("중복된 역을 저장할 수 없다.")
    void createDuplicateName() {
        //given
        StationEntity station = new StationEntity("lala");
        stationService.createStation(station);

        //then
        assertThatThrownBy(() -> stationService.createStation(station))
            .isInstanceOf(DataDuplicationException.class)
            .hasMessageContaining("이미 등록된 역입니다.");
    }

    @Test
    @DisplayName("모든 station 목록을 조회한다.")
    void findAll() {
        //given
        StationEntity station1 = new StationEntity("lala");
        StationEntity station2 = new StationEntity("sojukang");
        stationService.createStation(station1);
        stationService.createStation(station2);

        //when
        List<StationEntity> actual = stationService.findAll();

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
        StationEntity station = new StationEntity("lala");
        Long id = stationService.createStation(station).getId();

        //when
        StationEntity actual = stationService.findById(id);

        //then
        assertThat(actual.getName()).isEqualTo(station.getName());
    }

    @Test
    @DisplayName("존재하지 않는 id 로 조회할 경우 예외를 던진다.")
    void findByIdNotExist() {
        //given
        StationEntity station = new StationEntity("lala");
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
        StationEntity station = new StationEntity("이수");
        StationEntity createdStation = stationService.createStation(station);
        stationService.deleteById(createdStation.getId());

        //then
        assertThatThrownBy(() -> stationService.findById(createdStation.getId()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("존재하지 않는 역입니다.");
    }

    @Test
    @DisplayName("존재하지 않는 id 로 삭제할 경우 예외를 던진다.")
    void deleteByIdWithIdNotExists() {
        //then
        assertThatThrownBy(() -> stationService.deleteById(1L))
            .isInstanceOf(DataNotExistException.class)
            .hasMessageContaining("존재하지 않는 역입니다.");
    }
}
