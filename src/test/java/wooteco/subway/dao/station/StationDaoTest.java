package wooteco.subway.dao.station;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.linesOf;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.station.Station;

@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@Transactional
class StationDaoTest {

    @Autowired
    private StationDao stationDao;

    @Test
    void dependency() {
        assertThat(stationDao).isNotNull();
    }

    @Test
    void save() {
        // given
        Station station = new Station("잠실역");

        // when
        Station persistedStation = stationDao.save(station);

        // then
        assertAll(
            () -> assertThat(station.getName()).isEqualTo(persistedStation.getName())
        );
    }

    @Test
    void findAll() {
        // given
        Station station1 = new Station("잠실역");
        Station station2 = new Station("일원역");

        // when
        stationDao.save(station1);
        stationDao.save(station2);
        List<Station> stations = stationDao.findAll();

        // then
        assertAll(
            () -> assertThat(stations.get(0).getName()).isEqualTo(station1.getName()),
            () -> assertThat(stations.get(1).getName()).isEqualTo(station2.getName())
        );
    }

    @Test
    void findById() {
        // given
        Station station = new Station("잠실역");

        // when
        Station persistedStation = stationDao.save(station);
        Station selectedStaion = stationDao.findById(persistedStation.getId()).get();

        // then
        assertAll(
            () -> assertThat(selectedStaion.getId()).isEqualTo(persistedStation.getId()),
            () -> assertThat(selectedStaion.getName()).isEqualTo(persistedStation.getName())
        );
    }

    @Test
    void deleteById() {
        // given
        Station station = new Station("잠실역");
        Station persistedStation = stationDao.save(station);

        // when
        stationDao.deleteById(persistedStation.getId());

        // then
        assertThat(stationDao.findById(persistedStation.getId()).isPresent()).isFalse();
    }
}