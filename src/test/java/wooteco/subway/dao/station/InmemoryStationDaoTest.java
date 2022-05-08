package wooteco.subway.dao.station;

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
        long savedStationId = stationDao.save(station);

        assertThat(savedStationId).isNotNull();
    }

    @Test
    @DisplayName("모든 Station을 조회할 수 있다.")
    void findAll() {
        stationDao.save(new Station("오리"));
        stationDao.save(new Station("배카라"));

        assertThat(stationDao.findAll()).hasSize(2);
    }

    @Test
    @DisplayName("id로 Station을 조회한다.")
    void findById() {
        long savedStationId = stationDao.save(new Station("오리"));

        assertThat(stationDao.findById(savedStationId)).isNotNull();
    }

    @Test
    @DisplayName("Station을 삭제할 수 있다.")
    void delete() {
        Long stationId = stationDao.save(new Station("오리"));

        assertThat(stationDao.delete(stationId)).isEqualTo(1);
    }

    @Test
    @DisplayName("Station 이름이 존재하는지 확인할 수 있다.")
    void existByName() {
        String name = "오리";
        stationDao.save(new Station(name));

        assertThat(stationDao.existByName(name)).isTrue();
    }

    @Test
    @DisplayName("id를 가진 Station이 존재하는지 확인할 수 있다.")
    void existById() {
        Long stationId = stationDao.save(new Station("오리"));

        assertThat(stationDao.existById(stationId)).isTrue();
    }
}
