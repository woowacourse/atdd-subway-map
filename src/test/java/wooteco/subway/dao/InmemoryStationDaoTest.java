package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
        Long stationId = station.getId();

        assertThatCode(() -> inmemoryStationDao.delete(stationId)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("없는 id의 Station을 삭제할 수 없다.")
    void deleteByInvalidId() {
        Station station = inmemoryStationDao.save(new Station("오리"));
        Long stationId = station.getId() + 1;

        assertThatThrownBy(() -> inmemoryStationDao.delete(stationId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("없는 station 입니다.");
    }

    @Test
    @DisplayName("이미 삭제한 id의 Station을 삭제할 수 없다.")
    void deleteByDuplicatedId() {
        Station station = inmemoryStationDao.save(new Station("오리"));
        Long stationId = station.getId();
        inmemoryStationDao.delete(stationId);

        assertThatThrownBy(() -> inmemoryStationDao.delete(stationId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("없는 station 입니다.");
    }


    @Test
    @DisplayName("Station 이름이 존재하는지 확인할 수 있다.")
    void existByName() {
        String name = "오리";
        inmemoryStationDao.save(new Station(name));

        assertThat(inmemoryStationDao.existByName(name)).isTrue();
    }
}
