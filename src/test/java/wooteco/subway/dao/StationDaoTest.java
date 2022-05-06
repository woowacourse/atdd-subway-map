package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Station;

class StationDaoTest extends DaoTest {

    @Test
    @DisplayName("역을 저장하면 저장된 역 정보를 반환한다.")
    void Save() {
        // given
        final String name = "선릉";
        final Station station = new Station(name);

        // when
        final Station savedStation = stationDao.save(station).orElseThrow();

        // then
        assertThat(savedStation.getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("모든 역 조회하기")
    void FindAll() {
        // given
        stationDao.save(new Station("선릉"));
        stationDao.save(new Station("노원"));

        // when
        final List<Station> stations = stationDao.findAll();

        // then
        assertThat(stations).hasSize(2);
    }

    @Test
    @DisplayName("id에 해당하는 역 삭제하기")
    void DeleteById() {
        // given
        final Station station = stationDao.save(new Station("선릉")).orElseThrow();

        // when
        final Integer affectedRows = stationDao.deleteById(station.getId());

        // then
        assertThat(affectedRows).isOne();
    }

    @Test
    @DisplayName("존재하지 않는 id의 역을 삭제하면 예외가 발생한다.")
    void DeleteById_InvalidId_ExceptionThrown() {
        assertThatThrownBy(() -> stationDao.deleteById(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("id가 일치하는 역이 존재하지 않습니다.");
    }
}