package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Station;

class InmemoryStationDaoTest {

    private final InmemoryStationDao stationDao = InmemoryStationDao.getInstance();

    @AfterEach
    void afterEach() {
        stationDao.clear();
    }

    @Test
    @DisplayName("Station을 저장할 수 있다.")
    void save() {
        Station station = new Station("오리");
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
    @DisplayName("Station을 삭제할 수 있다.")
    void delete() {
        Station station = stationDao.save(new Station("오리"));
        stationDao.delete(station.getId());
        assertThat(stationDao.existById(station.getId())).isFalse();
    }

    @Test
    @DisplayName("Station 이름이 존재하는지 확인할 수 있다.")
    void existByName() {
        String name = "오리";
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
    @DisplayName("id를 통해 Station이 존재하는 지 확인한다.")
    void existById() {
        Station station = stationDao.save(new Station("배카라"));
        assertThat(stationDao.existById(station.getId())).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 id를 통해 Station이 존재하는 지 확인한다.")
    void existByInvalidId() {
        Station station = stationDao.save(new Station("배카라"));
        assertThat(stationDao.existById(station.getId() + 1)).isFalse();
    }
}
