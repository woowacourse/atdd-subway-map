package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Station;

class StationDaoTest {

    @BeforeEach
    void setUp() {
        StationDao.deleteAll();
    }

    @DisplayName("새로운 지하철 역을 저장한다")
    @Test
    void saveStation() {
        // given
        String name= "강남역";
        Station 강남역 = new Station(1L, name);

        // when
        StationDao.save(강남역);

        // then
        List<Station> stations = StationDao.findAll();
        assertThat(stations.get(0)).isEqualTo(강남역);
    }

    @DisplayName("같은 지하철 역 이름이 있는 경우 예외를 발생시킨다")
    @Test
    void saveStationThrowException() {
        String name= "강남역";
        Station 강남역1 = new Station(1L, name);
        Station 강남역2 = new Station(2L, name);
        StationDao.save(강남역1);

        assertThatThrownBy(() -> StationDao.save(강남역2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("같은 이름");
    }

    @DisplayName("저장된 모든 역을 삭제한다")
    @Test
    void deleteAll() {
        // given
        Station 강남역 = new Station(1L, "강남역");
        StationDao.save(강남역);

        // when
        StationDao.deleteAll();

        // then
        assertThat(StationDao.findAll()).isEmpty();
    }
}
