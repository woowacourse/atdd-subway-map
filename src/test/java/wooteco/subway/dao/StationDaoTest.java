package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.station.NoSuchStationException;

class StationDaoTest extends DaoTest {

    @Test
    @DisplayName("역을 저장하면 저장된 역 정보를 반환한다.")
    void Save() {
        // given
        final String name = "선릉";
        final Station station = new Station(name);

        // when
        final Station savedStation = stationDao.insert(station).orElseThrow();

        // then
        assertThat(savedStation.getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("id에 해당하는 노선을 조회한다.")
    void FindById() {
        // given
        final String name = "선릉";
        final Long id = stationDao.insert(new Station(name))
                .orElseThrow()
                .getId();

        final Station expected = new Station(id, name);

        // when
        final Optional<Station> actual = stationDao.findById(id);

        // then
        assertThat(actual).isPresent()
                .contains(expected);
    }

    @Test
    @DisplayName("id에 해당하는 노선이 존재하지 않으면 빈 Optional 을 반환한다.")
    void FindById_NotExistId_EmptyOptionalReturned() {
        // given
        final Long id = 999L;

        // when
        final Optional<Station> actual = stationDao.findById(id);

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    @DisplayName("모든 역 조회하기")
    void FindAll() {
        // given
        stationDao.insert(new Station("선릉"));
        stationDao.insert(new Station("노원"));

        // when
        final List<Station> stations = stationDao.findAll();

        // then
        assertThat(stations).hasSize(2);
    }

    @Test
    @DisplayName("id에 해당하는 역 삭제하기")
    void DeleteById() {
        // given
        final Station station = stationDao.insert(new Station("선릉")).orElseThrow();

        // when
        final Integer affectedRows = stationDao.deleteById(station.getId());

        // then
        assertThat(affectedRows).isOne();
    }

    @Test
    @DisplayName("존재하지 않는 id의 역을 삭제하면 예외가 발생한다.")
    void DeleteById_InvalidId_ExceptionThrown() {
        assertThatThrownBy(() -> stationDao.deleteById(999L))
                .isInstanceOf(NoSuchStationException.class);
    }
}