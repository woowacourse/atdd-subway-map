package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Station;

class StationDaoTest {

    @BeforeEach
    void clearAll() {
        StationDao.deleteAll();
    }

    @DisplayName("지하철역을 저장한다.")
    @Test
    void saveStation() {
        Station station = new Station("강남역");
        Station savedStation = StationDao.save(station);

        assertAll(
                () -> assertThat(savedStation.getId()).isNotZero(),
                () -> assertThat(savedStation.getName()).isEqualTo("강남역")
        );
    }

    @DisplayName("특정 지하철역을 이름으로 조회한다.")
    @Test
    void findByName() {
        Station station = new Station("강남역");
        StationDao.save(station);
        Optional<Station> wrappedStation = StationDao.findByName("강남역");
        assert (wrappedStation).isPresent();

        assertAll(
                () -> assertThat(wrappedStation.get().getId()).isNotZero(),
                () -> assertThat(wrappedStation.get().getName()).isEqualTo("강남역")
        );
    }

    @DisplayName("특정 지하철역을 삭제한다.")
    @Test
    void deleteById() {
        Station station = new Station("강남역");
        Station savedStation = StationDao.save(station);
        StationDao.deleteById(savedStation.getId());

        Optional<Station> wrappedStation = StationDao.findByName("강남역");
        assertThat(wrappedStation).isEmpty();
    }

    @DisplayName("특정 지하철역을 아이디로 조회한다.")
    @Test
    void findById() {
        Station station = new Station("강남역");
        Station savedStation = StationDao.save(station);
        Optional<Station> wrappedStation = StationDao.findById(savedStation.getId());
        assert(wrappedStation).isPresent();

        assertAll(
                () -> assertThat(wrappedStation.get().getId()).isEqualTo(savedStation.getId()),
                () -> assertThat(wrappedStation.get().getName()).isEqualTo(savedStation.getName())
        );
    }
}
