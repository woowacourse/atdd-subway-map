package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Station;

class InmemoryStationDaoTest {

    private final InmemoryStationDao inmemoryStationDao = InmemoryStationDao.getInstance();

    @AfterEach
    void afterEach() {
        inmemoryStationDao.clear();
    }

    @Test
    @DisplayName("Station을 저장할 수 있다.")
    void save() {
        Station station = new Station("오리");
        Station savedStation = inmemoryStationDao.save(station);

        assertThat(savedStation.getId()).isNotNull();
    }

    @Test
    @DisplayName("모든 Station을 조회할 수 있다.")
    void findAll() {
        inmemoryStationDao.save(new Station("오리"));
        inmemoryStationDao.save(new Station("배카라"));

        assertThat(inmemoryStationDao.findAll()).hasSize(2);
    }

    @Test
    @DisplayName("Station을 삭제할 수 있다.")
    void delete() {
        Station station = inmemoryStationDao.save(new Station("오리"));
        inmemoryStationDao.delete(station.getId());
        assertThat(inmemoryStationDao.existById(station.getId())).isFalse();
    }

    @Test
    @DisplayName("Station 이름이 존재하는지 확인할 수 있다.")
    void existByName() {
        String name = "오리";
        inmemoryStationDao.save(new Station(name));

        assertThat(inmemoryStationDao.existByName(name)).isTrue();
    }

    @Test
    @DisplayName("Station 이름이 존재하지 않는 경우 확인한다.")
    void nonExistByName() {
        String name = "배카라";
        inmemoryStationDao.save(new Station(name));

        assertThat(inmemoryStationDao.existByName("오리")).isFalse();
    }
}
