package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Station;

class StationDaoTest {

    @AfterEach
    void setUp() {
        StationDao.findAll().clear();
    }

    @Test
    @DisplayName("역을 저장한다.")
    void save() {
        final Station station = new Station("한성대입구역");

        final Station savedStation = StationDao.save(station);

        assertThat(savedStation.getName()).isEqualTo(station.getName());
    }

    @Test
    @DisplayName("같은 이름의 역을 저장하는 경우, 예외가 발생한다.")
    void saveDuplicateName() {
        final Station station = new Station("한성대입구역");
        StationDao.save(station);

        assertThatThrownBy(() -> StationDao.save(station))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("같은 이름의 역이 이미 존재합니다.");
    }

    @Test
    @DisplayName("모든 역을 조회한다.")
    void findAll() {
        final Station station1 = new Station("한성대입구역");
        final Station station2 = new Station("신대방역");
        StationDao.save(station1);
        StationDao.save(station2);

        final List<Station> stations = StationDao.findAll();

        assertThat(stations).hasSize(2);
    }

    @Test
    @DisplayName("역을 삭제한다.")
    void delete() {
        final Station station = new Station("한성대입구역");
        final Long id = StationDao.save(station).getId();

        StationDao.delete(id);

        final int actual = StationDao.findAll().size();
        assertThat(actual).isEqualTo(0);
    }
}
