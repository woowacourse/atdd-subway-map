package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Station;

class StationDaoTest {

    @DisplayName("지하철 역을 저장한다.")
    @Test
    void save() {
        Station station = new Station("선릉역");
        Station savedStation = StationDao.save(station);

        assertThat(station.getName()).isEqualTo(savedStation.getName());
    }

    @DisplayName("모든 지하철 역을 조회한다.")
    @Test
    void findAll() {
        Station station1 = new Station("선릉역");
        Station station2 = new Station("잠실역");
        Station station3 = new Station("사우역");
        StationDao.save(station1);
        StationDao.save(station2);
        StationDao.save(station3);

        assertThat(StationDao.findAll().size()).isEqualTo(3);
    }

    @DisplayName("같은 이름의 지하철 역을 저장하는 경우 예외가 발생한다.")
    @Test
    void saveExistingName() {
        Station station = new Station("선릉역");
        StationDao.save(station);

        assertThatThrownBy(() -> {
            StationDao.save(station);
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("같은 이름의 역은 등록할 수 없습니다.");
    }

    @DisplayName("지하철 역을 삭제한다.")
    @Test
    void deleteById() {
        Station station = new Station("선릉역");
        Station savedStation = StationDao.save(station);

        StationDao.deleteById(savedStation.getId());

        assertThat(StationDao.findAll().size()).isZero();
    }

    @DisplayName("존재하지 않는 역을 삭제할 경우 예외가 발생한다.")
    @Test
    void deleteNotExistingStation() {
        assertThatThrownBy(() -> StationDao.deleteById(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 역입니다.");
    }

    @AfterEach
    void reset() {
        List<Station> stations = StationDao.findAll();
        stations.clear();
    }
}