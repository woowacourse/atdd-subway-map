package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

    @Nested
    @DisplayName("id로 Station을 조회한다.")
    class FindById {

        @Test
        @DisplayName("값이 존재하는 경우 값이 반환된다.")
        void isNotEmpty() {
            Station station = stationDao.save(new Station("오리"));

            assertThat(stationDao.findById(station.getId())).isNotEmpty();
        }

        @Test
        @DisplayName("값이 존재하지 않는 경우 empty가 반환된다.")
        void empty() {
            Station station = stationDao.save(new Station("오리"));

            assertThat(stationDao.findById(station.getId() + 1)).isEmpty();
        }
    }

    @Test
    @DisplayName("Station을 삭제할 수 있다.")
    void delete() {
        Station station = stationDao.save(new Station("오리"));
        Long stationId = station.getId();

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
        Station station = stationDao.save(new Station("오리"));

        assertThat(stationDao.existById(station.getId())).isTrue();
    }
}
