package wooteco.subway.station;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StationDaoTest {

    @AfterEach
    void clean() {
        StationDao.clear();
    }

    @Test
    @DisplayName("이름으로 역을 찾는다.")
    void findByName() {
        String name = "검프역";
        StationDao.save(new Station(1L, name));
        Station station = StationDao.findByName(name).get();

        assertThat(station.getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("같은 이름 역을 저장할 시 예외가 발생한다.")
    void saveDuplicatedName() {
        String name = "검프역";
        StationDao.save(new Station(1L, name));
        assertThatThrownBy(() ->
                StationDao.save(new Station(2L, name)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}