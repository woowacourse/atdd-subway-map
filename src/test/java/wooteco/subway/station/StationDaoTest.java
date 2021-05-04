package wooteco.subway.station;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StationDaoTest {

    @DisplayName("중복된 역은 허용하지 않는다.")
    @Test
    void duplicateStation() {
        Station station = new Station("옥수역");
        Station newStation = new Station("옥수역");
        StationDao.save(station);
        assertThatThrownBy(() -> {
            StationDao.save(newStation);
        }).isInstanceOf(IllegalArgumentException.class);
    }

}